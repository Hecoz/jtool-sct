package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReplaceCallMethodVisitor extends MethodVisitor implements Opcodes
{
  private final ClassLoader classLoader;
  
  public ReplaceCallMethodVisitor(final int api, final MethodVisitor mv,
      final String className, final ClassLoader classLoader)
  {
    super(api, mv);
    this.classLoader = classLoader;
  }
  
  @Override
  public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc)
  {
    if (opcode == INVOKEVIRTUAL)
    {
      if (name.equals("notify") && desc.equals("()V"))
      {
        super.visitMethodInsn(INVOKESTATIC,
            "org/jtool/runtime/InstrumentationPoints", "calledNotify",
            "(Ljava/lang/Object;)V");
        return;
      }
      else if (name.equals("notifyAll") && desc.equals("()V"))
      {
        super.visitMethodInsn(INVOKESTATIC,
            "org/jtool/runtime/InstrumentationPoints", "calledNotifyAll",
            "(Ljava/lang/Object;)V");
        return;
      }
      else if (name.equals("wait"))
      {
        if (desc.equals("()V"))
        {
          super.visitMethodInsn(INVOKESTATIC,
              "org/jtool/runtime/InstrumentationPoints", "calledWait",
              "(Ljava/lang/Object;)V");
          return;
        }
        else if (desc.equals("(J)V"))
        {
          super.visitMethodInsn(INVOKESTATIC,
              "org/jtool/runtime/InstrumentationPoints", "calledWait",
              "(Ljava/lang/Object;J)V");
          return;
        }
        else if (desc.equals("(JI)V"))
        {
          super.visitMethodInsn(INVOKESTATIC,
              "org/jtool/runtime/InstrumentationPoints", "calledWait",
              "(Ljava/lang/Object;JI)V");
          return;
        }
        
      }
      else if (name.equals("join"))
      {
        try
        {
          final ClassInfo ci = ClassManager.getAndStoreClassInfo(owner,
              classLoader);
          if (ci.getSupernames().contains("java/lang/Thread"))
          {
            if (desc.equals("()V"))
            {
              super.visitMethodInsn(INVOKESTATIC,
                  "org/jtool/runtime/InstrumentationPoints", "calledJoin",
                  "(Ljava/lang/Thread;)V");
              return;
            }
            else if (desc.equals("(J)V"))
            {
              super.visitMethodInsn(INVOKESTATIC,
                  "org/jtool/runtime/InstrumentationPoints", "calledJoin",
                  "(Ljava/lang/Thread;J)V");
              return;
            }
            else if (desc.equals("(JI)V"))
            {
              super.visitMethodInsn(INVOKESTATIC,
                  "org/jtool/runtime/InstrumentationPoints", "calledJoin",
                  "(Ljava/lang/Thread;JI)V");
              return;
            }
          }
        }
        catch (final ClassNotFoundException e)
        {
          e.printStackTrace();
          System.exit(1);
        }
        
      }
    }
    else if (opcode == INVOKESTATIC)
    {
      if (owner.equals("java/lang/Thread"))
      {
        if (name.equals("yield")
          && desc.equals("()V"))
        {
          super.visitMethodInsn(
              INVOKESTATIC,
              "org/jtool/runtime/InstrumentationPoints",
              "calledYield",
              "()V");
        }
        else if (name.equals("sleep") && desc.equals("(J)V"))
        {
          super.visitMethodInsn(
              INVOKESTATIC,
              "org/jtool/runtime/InstrumentationPoints",
              "calledSleep",
              "(J)V");
          return;
        }
      }
      else if (owner.equals("java/lang/System")
          && name.equals("arraycopy")
          && desc.equals("(Ljava/lang/Object;ILjava/lang/Object;II)V"))
      {
        super.visitMethodInsn(
            INVOKESTATIC,
            "org/jtool/runtime/rpl/Static",
            "arraycopy",
            "(Ljava/lang/Object;ILjava/lang/Object;II)V");
        return;
      }
    }
    super.visitMethodInsn(opcode, owner, name, desc);
  }
  
  // mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Object", "notify", "()V");
  
}

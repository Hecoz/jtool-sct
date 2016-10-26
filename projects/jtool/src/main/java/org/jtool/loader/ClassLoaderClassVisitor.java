package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClassLoaderClassVisitor extends ClassVisitor implements Opcodes
{
  public ClassLoaderClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions)
  {
    MethodVisitor mv =
        super.visitMethod(access, name, desc, signature, exceptions);
    // mv = cw.visitMethod(
    // ACC_PUBLIC, 
    // "loadClass",
    // "(Ljava/lang/String;)Ljava/lang/Class;", 
    // "(Ljava/lang/String;)Ljava/lang/Class<*>;", 
    // new String[] { "java/lang/ClassNotFoundException" });
    if (access == ACC_PUBLIC
        && "loadClass".equals(name)
        && "(Ljava/lang/String;)Ljava/lang/Class;".equals(desc))
    {
      mv = new ClassLoaderMethodVisitor(api, mv);
    }
    return mv;
  }


  
}


class ClassLoaderMethodVisitor extends MethodVisitor implements Opcodes
{
  public ClassLoaderMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }

  @Override
  public void visitCode()
  {
    super.visitCode();
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Thread",
        "currentThread",
        "()Ljava/lang/Thread;");
    // currThread, 
    mv.visitInsn(DUP);
    // currThread, currThread
    mv.visitFieldInsn(GETFIELD, "java/lang/Thread", "instrumented", "Z");
    // instr0or1, currThread, 
    mv.visitInsn(SWAP);
    // currThread, instr0or1,  
    mv.visitInsn(ICONST_0);
    // 0, currThread, instr0or1,
    mv.visitFieldInsn(PUTFIELD, "java/lang/Thread", "instrumented", "Z");
    // instr0or1,
  }
  
  @Override
  public void visitInsn(final int opcode)
  {
    if (opcode == ARETURN)
    {
      // obj, instr0or1,
      mv.visitInsn(SWAP);
      // instr0or1, obj, 
      mv.visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Thread",
          "currentThread",
          "()Ljava/lang/Thread;");
      // currThread, instr0or1, obj,
      mv.visitInsn(SWAP);
      // instr0or1, currThread, obj,
      mv.visitFieldInsn(PUTFIELD, "java/lang/Thread", "instrumented", "Z");
      // obj
    }
    super.visitInsn(opcode);
  }
  
  
  
}
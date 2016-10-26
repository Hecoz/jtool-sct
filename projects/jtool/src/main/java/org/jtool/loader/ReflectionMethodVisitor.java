package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReflectionMethodVisitor extends MethodVisitor implements Opcodes
{
  public ReflectionMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }
  
  @Override
  public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc)
  {
    super.visitMethodInsn(opcode, owner, name, desc);
    // , "java/lang/Class", "getMethods", "()[Ljava/lang/reflect/Method;");
    if (owner.equals("java/lang/Class"))
    {
      if (name.equals("getMethods") || name.equals("getDeclaredMethods"))
      {
        visitMethodInsn(
            INVOKESTATIC,
            "org/jtool/runtime/InstrumentationPoints",
            "filterMethods",
            "([Ljava/lang/reflect/Method;)[Ljava/lang/reflect/Method;");
      }
    }
    else if (owner.equals("java/lang/reflect/Method"))
    {
      if (name.equals("getName"))
      {
        visitMethodInsn(
            INVOKESTATIC,
            "org/jtool/runtime/InstrumentationPoints",
            "translateMethodName",
            "(Ljava/lang/String;)Ljava/lang/String;");
      }
    }
  }

  
}

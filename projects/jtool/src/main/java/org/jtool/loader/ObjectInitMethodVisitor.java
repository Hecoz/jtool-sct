package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ObjectInitMethodVisitor extends MethodVisitor implements Opcodes
{
  
  public ObjectInitMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }

  @Override
  public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc)
  {
    if (opcode == INVOKESPECIAL
        && owner.equals("java/lang/Object")
        && name.equals("<init>")
        && desc.equals("()V"))
    {
      mv.visitInsn(DUP);
      super.visitMethodInsn(opcode, owner, name, desc);
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "newObject",
          "(Ljava/lang/Object;)V");
      return;
    }
    super.visitMethodInsn(opcode, owner, name, desc);
  }

  
}

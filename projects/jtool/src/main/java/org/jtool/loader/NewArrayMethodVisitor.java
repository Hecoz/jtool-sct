package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NewArrayMethodVisitor extends MethodVisitor implements Opcodes
{
  public NewArrayMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }

  @Override
  public void visitIntInsn(final int opcode, final int operand)
  {
    super.visitIntInsn(opcode, operand);
    if (opcode == NEWARRAY)
    {
      mv.visitInsn(DUP);
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "newArray",
          "(Ljava/lang/Object;)V");
    }
  }

  @Override
  public void visitTypeInsn(final int opcode, final String type)
  {
    super.visitTypeInsn(opcode, type);
    if (opcode == ANEWARRAY)
    {
      mv.visitInsn(DUP);
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "newArray",
          "(Ljava/lang/Object;)V");
    }
  }

  @Override
  public void visitMultiANewArrayInsn(final String desc, final int dims)
  {
    super.visitMultiANewArrayInsn(desc, dims);
    mv.visitInsn(DUP);
    mv.visitMethodInsn(
        INVOKESTATIC,
        "org/jtool/runtime/InstrumentationPoints",
        "newMultiArray",
        "(Ljava/lang/Object;)V");
  }




  
}

package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ConstructorCallMethodVisitor extends MethodVisitor implements
    Opcodes
{
  public ConstructorCallMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }

  @Override
  public void visitMethodInsn(final int opcode, final String owner,
      final String name, String desc)
  {
    if (opcode == INVOKESPECIAL && name.equals("<init>")
        && !owner.equals("java/lang/Object"))
    {
      final String type = "Lorg/jtool/loader/ConstrInstrMarker;";
      desc = desc.replace(")", type + ")");
      visitInsn(ACONST_NULL);
    }
    super.visitMethodInsn(opcode, owner, name, desc);
  }
  
}

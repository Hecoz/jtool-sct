package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RewriterMethodVisitor extends MethodVisitor implements Opcodes
{
  public RewriterMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }
  
  @Override
  public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc)
  {
    if (opcode == INVOKESTATIC && owner.equals("org/jtool/util/Util"))
    {
      if (name.equals("monitorEnter"))
      {
        super.visitInsn(MONITORENTER);
        return;
      }
      else if (name.equals("monitorExit"))
      {
        super.visitInsn(MONITOREXIT);
        return;
      }
    }
    super.visitMethodInsn(opcode, owner, name, desc);
  }
  
}

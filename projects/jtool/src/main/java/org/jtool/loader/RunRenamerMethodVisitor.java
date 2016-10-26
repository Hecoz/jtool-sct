package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;

public class RunRenamerMethodVisitor extends MethodVisitor
{
  
  public RunRenamerMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }
  
  @Override
  public void visitMethodInsn(final int opcode, final String owner,
      String name, final String desc)
  {
    if (name.equals("run") && desc.equals("()V"))
    {
      name += "$orig";
    }
    super.visitMethodInsn(opcode, owner, name, desc);
  }


  
}

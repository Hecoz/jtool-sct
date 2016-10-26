package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CloneMethodVisitor extends MethodVisitor implements Opcodes
{
  
  public CloneMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }

  @Override
  public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc)
  {
    super.visitMethodInsn(opcode, owner, name, desc);
    if (opcode == INVOKEVIRTUAL)
    {
      if (name.equals("clone") && desc.equals("()Ljava/lang/Object;"))
      {
        mv.visitInsn(DUP);
        mv.visitMethodInsn(
            INVOKESTATIC,
            "org/jtool/runtime/InstrumentationPoints",
            "cloneCalled",
            "(Ljava/lang/Object;)V");
      }
    }
    
  }


  
}

package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SelfInstrumentationClassVisitor extends ClassVisitor
{
  
  public SelfInstrumentationClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions)
  {
    MethodVisitor mv =
        super.visitMethod(access, name, desc, signature, exceptions);
    if (name.equals("instrumentedExecute"))
    {
      mv = new SelfInstrumentationMethodVisitor(Opcodes.ASM4, mv);
    }
    return mv;
  }
  
  
  
}

class SelfInstrumentationMethodVisitor extends MethodVisitor implements Opcodes
{
  
  public SelfInstrumentationMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }

  @Override
  public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc)
  {
    // mv.visitMethodInsn(INVOKEINTERFACE, "org/jtool/test/ConcurrencyTestCase", "execute", "()V");
    if (opcode == INVOKEINTERFACE
        && owner.equals("org/jtool/test/ConcurrencyTestCase"))
    {
      super.visitMethodInsn(opcode, owner, name + "$instr", desc);
      return;
    }
    super.visitMethodInsn(opcode, owner, name, desc);
  }

  

}

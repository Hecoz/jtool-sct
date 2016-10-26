package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ArrayAccessClassVisitor extends ClassVisitor implements Opcodes
{
  
  public ArrayAccessClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions)
  {
    MethodVisitor mv =
        super.visitMethod(access, name, desc, signature, exceptions);
    mv = new ArrayAccessMethodVisitor(ASM4, mv);
    return mv;
  }

}

package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class ConstructorCallClassVisitor extends ClassVisitor
{
  public ConstructorCallClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }

  @Override
  public MethodVisitor visitMethod(final int access, final String name,
      final String desc, final String signature, final String[] exceptions)
  {
    return new ConstructorCallMethodVisitor(api, super.visitMethod(access,
        name, desc, signature, exceptions));
  }

  

}

package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class InitClassVisitor extends ClassVisitor
{
  public InitClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions)
  {
    MethodVisitor mv =
        super.visitMethod(access, name, desc, signature, exceptions);
    if (name.equals("<init>"))
    {
      mv = new InitMethodVisitor(api, mv);
    }
    return mv;
  }


  
}

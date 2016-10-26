package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class CloneClassVisitor extends ClassVisitor
{
  public CloneClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions)
  {
    MethodVisitor mv =
        super.visitMethod(access, name, desc, signature, exceptions);
    mv = new CloneMethodVisitor(api, mv);
    return mv;
  }
  


}

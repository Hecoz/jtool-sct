package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class RunRenamerClassVisitor extends ClassVisitor
{
  public RunRenamerClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, String name, final String desc,
      final String signature, final String[] exceptions)
  {
    
    if (name.equals("run") && desc.equals("()V"))
    {
      name += "$orig";
    }
    final MethodVisitor mv = super.visitMethod(access, name, desc,
        signature, exceptions);
      
    return new RunRenamerMethodVisitor(api, mv);
  }

  
}

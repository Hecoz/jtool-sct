package org.jtool.loader;

import java.util.regex.Pattern;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

public class MethodRenameClassVisitor extends ClassVisitor
{
  private final Pattern leaveInstrumentedWorldPattern;
  public MethodRenameClassVisitor(final int api, final ClassVisitor cv,
      final Pattern leaveInstrumentedWorldPattern)
  {
    super(api, cv);
    this.leaveInstrumentedWorldPattern = leaveInstrumentedWorldPattern;
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions)
  {
    MethodVisitor mv = super.visitMethod(access, name, desc, signature,
        exceptions);
    
    mv = new MethodRenameMethodVisitor(api, mv, leaveInstrumentedWorldPattern);

    return mv;
  }


  
}

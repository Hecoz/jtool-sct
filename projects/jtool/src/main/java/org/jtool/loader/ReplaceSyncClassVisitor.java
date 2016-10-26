package org.jtool.loader;


import static org.objectweb.asm.Opcodes.ACC_SYNCHRONIZED;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReplaceSyncClassVisitor extends ClassVisitor
{
  private final String className;

  public ReplaceSyncClassVisitor(final int api, final ClassVisitor cv,
      final String className)
  {
    super(api, cv);
    this.className = className;
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, final String name,
      final String desc,
      final String signature, final String[] exceptions)
  {
    MethodVisitor mv;
    
    if ((access & ACC_SYNCHRONIZED) != 0)
    {
      mv = cv.visitMethod(access & (~ACC_SYNCHRONIZED), name, desc, signature,
          exceptions);

      mv = new ReplaceSyncMethodVisitor(Opcodes.ASM4, mv, access
          & (~ACC_SYNCHRONIZED), name, desc, className);
    }
    else
    {
      mv = cv.visitMethod(access, name, desc, signature, exceptions);
    }
    
    return mv;
  }
  
  

}

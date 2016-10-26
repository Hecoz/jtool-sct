package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ReplaceCallClassVisitor extends ClassVisitor
{
  private final String className;
  private final ClassLoader classLoader;
  
  public ReplaceCallClassVisitor(final int api, final ClassVisitor cv,
      final String className, final ClassLoader classLoader)
  {
    super(api, cv);
    this.className = className;
    this.classLoader = classLoader;
  }

  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions)
  {
    MethodVisitor mv = super.visitMethod(access, name, desc, signature,
        exceptions);
    
    // if (!className.equals("java/lang/ThreadGroup"))
    // {
    mv = new ReplaceCallMethodVisitor(Opcodes.ASM4, mv, className, classLoader);
    // }
    
    return mv;
  }



}

package org.jtool.loader;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RunClassVisitor extends ClassVisitor {

  
  public RunClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }

  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions) {
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
    if(ACC_PUBLIC == access && "run".equals(name) && "()V".equals(desc))
    {
      // System.out.println("Found run method.");
      mv = new RunMethodVisitor(Opcodes.ASM4, mv, access, name, desc);
    }
    
    return mv;
  }
  
  

}

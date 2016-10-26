package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ThreadStart extends ClassVisitor implements Opcodes
{

  public ThreadStart(final int api, final ClassVisitor cv) {
    super(api, cv);
  }

  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions) {
    
    MethodVisitor mv = cv.visitMethod(access, name, desc, signature, exceptions);
    
    // TODO: Check signature and exceptions?
    if ((ACC_PUBLIC & access) != 0 && "start$instr".equals(name)
        && "()V".equals(desc))
    {
      // System.out.println("Found start method.");
      mv = new InstrumentThreadStartMethod(Opcodes.ASM4, mv, access, name, desc);
    }
    // ACC_PRIVATE + ACC_STATIC + ACC_SYNCHRONIZED, "nextThreadNum", "()I"
    if (
        //((ACC_PRIVATE + ACC_STATIC + ACC_SYNCHRONIZED) == access)
        "nextThreadNum$instr".equals(name)
    //&& "()I".equals(desc)
    )
    {
      System.out.println("Instrumented ---- nextThreadNum");
      mv.visitCode();
      mv.visitInsn(ICONST_1);
      mv.visitInsn(IRETURN);
      mv.visitEnd();
      return new MethodVisitor(ASM4)
      {
        
      };
    }
    
    return mv;
  }
}

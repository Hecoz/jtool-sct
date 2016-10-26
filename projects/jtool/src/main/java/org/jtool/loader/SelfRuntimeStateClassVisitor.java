package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class SelfRuntimeStateClassVisitor extends ClassVisitor implements Opcodes
{
  public SelfRuntimeStateClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }
  
  // mv = cw.visitMethod(ACC_PUBLIC + ACC_STATIC, "setThreadInRuntimeState", "(Z)V", null, null);
  @Override
  public MethodVisitor visitMethod(final int access, final String name, final String desc,
      final String signature, final String[] exceptions)
  {
    final MethodVisitor mv =
        super.visitMethod(access, name, desc, signature, exceptions);
    if (name.equals("setThreadInRuntimeState"))
    {
      mv.visitCode();
      mv.visitMethodInsn(
          INVOKESTATIC,
          "java/lang/Thread",
          "currentThread",
          "()Ljava/lang/Thread;");
      mv.visitVarInsn(ILOAD, 0);
      final Label l3 = new Label();
      mv.visitJumpInsn(IFEQ, l3);
      mv.visitInsn(ICONST_0);
      final Label l4 = new Label();
      mv.visitJumpInsn(GOTO, l4);
      mv.visitLabel(l3);
      mv.visitInsn(ICONST_1);
      mv.visitLabel(l4);
      mv.visitFieldInsn(PUTFIELD, "java/lang/Thread", "instrumented", "Z");
      mv.visitInsn(RETURN);
      mv.visitEnd();
      return null;
    }
    
    if (name.equals("getThreadData"))
    {
      // mv.visitFieldInsn(GETFIELD, "org/jtool/test/Test2", "threadData", "Lorg/jtool/runtime/ThreadData;");
      mv.visitCode();
      mv.visitVarInsn(ALOAD, 0);
      // Thread
      mv.visitFieldInsn(
          GETFIELD,
          "java/lang/Thread",
          "threadData",
          "Lorg/jtool/runtime/ThreadData;");
      // ThreadData
      mv.visitInsn(ARETURN);
      mv.visitEnd();
      return null;
    }
    
    if (name.equals("setThreadData"))
    {
      // mv.visitFieldInsn(GETFIELD, "org/jtool/test/Test2", "threadData", "Lorg/jtool/runtime/ThreadData;");
      mv.visitCode();
      mv.visitVarInsn(ALOAD, 0);
      // Thread
      mv.visitVarInsn(ALOAD, 1);
      // ThreadData, Thread
      mv.visitFieldInsn(
          PUTFIELD,
          "java/lang/Thread",
          "threadData",
          "Lorg/jtool/runtime/ThreadData;");
      // 
      mv.visitInsn(RETURN);
      mv.visitEnd();
      return null;
    }
    
    if (name.equals("throwException"))
    {
      mv.visitCode();
      mv.visitVarInsn(ALOAD, 0);
      mv.visitInsn(ATHROW);
      mv.visitEnd();
      return null;
    }

    return mv;
  }
  
}


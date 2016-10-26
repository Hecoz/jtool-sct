package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class InstrumentThreadStartMethod extends AdviceAdapter {

  protected InstrumentThreadStartMethod(final int api, final MethodVisitor mv, final int access,
      final String name, final String desc) {
    super(api, mv, access, name, desc);
  }

  @Override
  protected void onMethodEnter() {
    // System.out.println("Instrumented enter of start method.");
    mv.visitVarInsn(ALOAD, 0);
    visitMethodInsn(INVOKESTATIC, "org/jtool/runtime/InstrumentationPoints",
        "preStartThread", "(Ljava/lang/Thread;)V");
  }

  @Override
  protected void onMethodExit(final int opcode) {
    mv.visitVarInsn(ALOAD, 0);
    if(opcode == ATHROW)
    {
      visitMethodInsn(INVOKESTATIC, "org/jtool/runtime/InstrumentationPoints",
          "failedStartThread", "(Ljava/lang/Thread;)V");
    }
    else
    {
      visitMethodInsn(INVOKESTATIC, "org/jtool/runtime/InstrumentationPoints",
          "successStartThread", "(Ljava/lang/Thread;)V");
    }
  }
  
  
  
}

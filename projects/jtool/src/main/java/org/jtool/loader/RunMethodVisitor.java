package org.jtool.loader;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.AdviceAdapter;

public class RunMethodVisitor extends AdviceAdapter {
  private final Label startTry = new Label();
  protected RunMethodVisitor(final int api, final MethodVisitor mv, final int access,
 final String name, final String desc)
  {
    super(api, mv, access, name, desc);
  }

  @Override
  public void visitCode()
  {
    super.visitCode();
    mv.visitLabel(startTry);
  }
  
  @Override
  public void visitMaxs(final int maxStack, final int maxLocals)
  {
    final Label endTry = new Label();
    mv.visitTryCatchBlock(startTry, endTry, endTry, null);
    mv.visitLabel(endTry);
    onFinally(ATHROW);
    mv.visitInsn(ATHROW);
    mv.visitMaxs(maxStack, maxLocals);
  }

  private void onFinally(final int opcode)
  {
    // System.out.println("Instrumented exit of run method.");
    mv.visitVarInsn(ALOAD, 0);
    visitMethodInsn(INVOKESTATIC, "org/jtool/runtime/InstrumentationPoints",
        "exitRun", "(Ljava/lang/Object;)V");
  }
  
  @Override
  protected void onMethodExit(final int opcode)
  {
    if (opcode != ATHROW)
    {
      onFinally(opcode);
    }
  }

  @Override
  protected void onMethodEnter()
  {
    // System.out.println("Instrumented enter of run method.");
    
    mv.visitVarInsn(ALOAD, 0);
    visitMethodInsn(INVOKESTATIC, "org/jtool/runtime/InstrumentationPoints",
        "executedRun", "(Ljava/lang/Object;)V");
  }



}

package org.jtool.loader;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class ReplaceSyncMethodVisitor extends AdviceAdapter
{
  private final Label startTry = new Label();
  
  private final boolean isStatic;
  private final String className;
  

  protected ReplaceSyncMethodVisitor(final int api, final MethodVisitor mv, final int access,
 final String name, final String desc,
      final String className)
  {
    super(api, mv, access, name, desc);
    isStatic = (access & Opcodes.ACC_STATIC) != 0;
    this.className = className;
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
    if (isStatic)
    {
      mv.visitLdcInsn(Type.getObjectType(className));
    }
    else
    {
      mv.visitVarInsn(ALOAD, 0);
    }
    mv.visitInsn(MONITOREXIT);
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
    mv.visitLabel(startTry);
    if (isStatic)
    {
      mv.visitLdcInsn(Type.getObjectType(className));
    }
    else
    {
      mv.visitVarInsn(ALOAD, 0);
    }

    mv.visitInsn(MONITORENTER);
  }

}

package org.jtool.loader.alt;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FirstMethodVisitor extends MethodVisitor implements Opcodes
{
  private final Label secondMethodLabel = new Label();
  private Label firstLabel = new Label();
  
  public FirstMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }
  
  @Override
  public void visitCode()
  {
    super.visitCode();
    mv.visitLabel(firstLabel);
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Thread",
        "currentThread",
        "()Ljava/lang/Thread;");
    mv.visitFieldInsn(GETFIELD, "java/lang/Thread", "instrumented", "Z");
    mv.visitJumpInsn(IFNE, secondMethodLabel);
  }
  
  @Override
  public void visitLabel(final Label label)
  {
    if (firstLabel == null)
    {
      firstLabel = label;
    }
    super.visitLabel(label);
  }
  
  @Override
  public void visitMaxs(final int maxStack, final int maxLocals)
  {
    // ignore
  }
  
  @Override
  public void visitEnd()
  {
    // ignore
    // but visit the label for code of the next method
    mv.visitLabel(secondMethodLabel);
  }
  
  @Override
  public void visitLocalVariable(final String name, final String desc,
      final String signature, final Label start, final Label end,
      final int index)
  {
    //    // Only visit if not 0 (this) and not parameters.
    //    // e.g. 3 params:
    //    // [0 (this), 1, 2] (params), [3,4] (other local vars)
    //    if (index >= numParameters)
    //    {
    //      super.visitLocalVariable(name, desc, signature, start, end, index);
    //    }
  }
  
  public Label getSecondMethodLabel()
  {
    return secondMethodLabel;
  }

  public Label getFirstLabel()
  {
    return firstLabel;
  }


}

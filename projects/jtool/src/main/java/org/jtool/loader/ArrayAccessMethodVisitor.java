package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ArrayAccessMethodVisitor extends MethodVisitor implements Opcodes
{
  public ArrayAccessMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }
  
  @Override
  public void visitInsn(final int opcode)
  {
    switch (opcode)
{
    case LALOAD:
    case DALOAD:
    case IALOAD:
    case FALOAD:
    case AALOAD:
    case BALOAD:
    case CALOAD:
    case SALOAD:
      // stack: index, arrayref
      mv.visitInsn(DUP2);
      // stack: index, arrayref, index, arrayref
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "arrayLoad",
          "(Ljava/lang/Object;I)V");
      // stack: index, arrayref
      break;
    
    
    case LASTORE:
    case DASTORE:
      // stack: v1, v2, index, arrayref
      mv.visitInsn(DUP2_X2);
      // stack: v1, v2, index, arrayref, v1, v2
      mv.visitInsn(POP2);
      // stack: index, arrayref, v1, v2
      mv.visitInsn(DUP2_X2);
      // stack: index, arrayref, v1, v2, index, arrayref
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "arrayStore",
          "(Ljava/lang/Object;I)V");
      // stack: v1, v2, index, arrayref
      break;
    case IASTORE:
    case FASTORE:
    case AASTORE:
    case BASTORE:
    case CASTORE:
    case SASTORE:
      // stack: value, index, arrayref
      mv.visitInsn(DUP_X2);
      // stack: value, index, arrayref, value
      mv.visitInsn(POP);
      // stack:  index, arrayref, value
      mv.visitInsn(DUP2_X1);
      // stack:  index, arrayref, value, index, arrayref
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "arrayStore",
          "(Ljava/lang/Object;I)V");
      // stack:  value, index, arrayref
      break;
    default:
      break;
    }
    super.visitInsn(opcode);
  }

}

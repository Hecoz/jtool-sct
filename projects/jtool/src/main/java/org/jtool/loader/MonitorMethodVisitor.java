package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MonitorMethodVisitor extends MethodVisitor implements Opcodes
{
  public MonitorMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }

  @Override
  public void visitInsn(final int opcode)
  {
    if (opcode == MONITORENTER)
    {
      // super.visitInsn(DUP);
      visitMethodInsn(INVOKESTATIC, "org/jtool/runtime/InstrumentationPoints",
          "enterMonitor", "(Ljava/lang/Object;)V");
      return;
    }
    else if (opcode == MONITOREXIT)
    {
      // super.visitInsn(DUP);
      visitMethodInsn(INVOKESTATIC, "org/jtool/runtime/InstrumentationPoints",
          "exitMonitor", "(Ljava/lang/Object;)V");
      return;
    }
    super.visitInsn(opcode);
  }


  
}

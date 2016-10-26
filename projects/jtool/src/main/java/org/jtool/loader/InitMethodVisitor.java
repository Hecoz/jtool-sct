package org.jtool.loader;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class InitMethodVisitor extends MethodVisitor implements Opcodes
{
  
  private int initVsNewCount = 0;
  private boolean done = false;
  
  public InitMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, mv);
  }

  @Override
  public void visitMethodInsn(final int opcode, final String owner,
      final String name, final String desc)
  {
    if (name.equals("<init>"))
    {
      initVsNewCount++;
      // this is probably the call to the super() or this()
      // if initVsNewCount > 0
      
      if (initVsNewCount > 0 && !done)
      {
        done = true;
        mv.visitMethodInsn(opcode, owner, name, desc); // original init
        
        // TODO: Why can't find class sometimes?
        //        if (classInfo != null)
        //        {
        //          final ClassNode cn = classInfo.getClassNode();
        //
        //          for (final FieldNode field : (List<FieldNode>) cn.fields)
        //          {
        //            // ignore static fields
        //            if ((field.access & ACC_STATIC) != 0)
        //            {
        //              continue;
        //            }
        //            // ignore final fields
        //            if ((field.access & ACC_FINAL) != 0)
        //            {
        //              continue;
        //            }
        //            mv.visitVarInsn(ALOAD, 0);
        //            FieldAccessMethodVisitor.visitPutFieldCallback(
        //                mv,
        //                classInfo.name,
        //                field.name,
        //                true);
        //          }
        //        }
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(
            INVOKESTATIC,
            "org/jtool/runtime/InstrumentationPoints",
            "newObject",
            "(Ljava/lang/Object;)V");
        return;
      }

    }
    super.visitMethodInsn(opcode, owner, name, desc);
  }
  
  
  
  @Override
  public void visitTypeInsn(final int opcode, final String type)
  {
    if (opcode == NEW)
    {
      initVsNewCount--;
    }
    super.visitTypeInsn(opcode, type);
  }


  
}

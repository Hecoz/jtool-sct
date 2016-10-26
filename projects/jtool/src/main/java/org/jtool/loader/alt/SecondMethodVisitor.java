package org.jtool.loader.alt;

import org.objectweb.asm.MethodVisitor;

public class SecondMethodVisitor extends MethodVisitor
{
  private final MethodVisitor myMv;
  
  public SecondMethodVisitor(final int api, final MethodVisitor mv)
  {
    super(api, null);
    myMv = mv;
  }
  
  @Override
  public void visitCode()
  {
    // ignore, but from now on, visit myMv
    mv = myMv;
  }
  
  //  @Override
  //  public void visitVarInsn(final int opcode, final int var)
  //  {
  //    // if var is a parameter
  //    // e.g. 3 params:
  //    // [0 (this), 1, 2] (params), [3,4,] (other local vars)
  //    if (var >= 0 && var < numParameters)
  //    {
  //      // call original
  //      super.visitVarInsn(opcode, var);
  //    }
  //    else
  //    {
  //      // increase var index
  //      // e.g. 2 params, 7 locals:
  //      // [0 (this), 1, 2] (params), [3,4,5,6] (others from method1),
  //      // [7,8,9,10] (others from method2)
  //      final int numOthers = numLocals - numParameters;
  //      super.visitVarInsn(opcode, var + numOthers);
  //    }
  //  }
  
  //  @Override
  //  public void visitLocalVariable(final String name, final String desc,
  //      final String signature, final Label start, final Label end,
  //      final int index)
  //  {
  //        // visit 0 (this) and params, with updated start Label.
  //        // e.g. 3 params:
  //        // [0 (this), 1, 2] (params), [3,4] (other local vars)
  //        if (index < numParameters)
  //        {
  //          super.visitLocalVariable(name, desc, signature, firstLabel, end, index);
  //        }
  //        else
  //        {
  //          // visit other vars with increased index
  //          final int numOthers = numLocals - numParameters;
  //          super.visitLocalVariable(name, desc, signature, start, end, index
  //              + numOthers);
  //          
  //        }
  //  }

}

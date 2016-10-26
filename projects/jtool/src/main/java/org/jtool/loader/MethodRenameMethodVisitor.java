package org.jtool.loader;

import java.util.regex.Pattern;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodRenameMethodVisitor extends MethodVisitor implements Opcodes
{
  private final Pattern leaveInstrumentedWorldPattern;
  public static String getRenamedMethodName(final String owner, String name,
      final String desc)
  {
    if (!name.startsWith("<"))
    {
      name += "$instr";
    }
    // if (name.startsWith("<"))
    // {
    // name = "$" + name.substring(1, name.length() - 1);
    // }
    return name;
  }
  
  public MethodRenameMethodVisitor(final int api, final MethodVisitor mv,
      final Pattern leaveInstrumentedWorldPattern)
  {
    super(api, mv);
    this.leaveInstrumentedWorldPattern = leaveInstrumentedWorldPattern;
  }

  @Override
  public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc)
  {
    if (ClassManager.shouldDouble(owner, name, desc)
        && !(opcode == INVOKESPECIAL && owner.equals("java/lang/Object"))
        && !(owner.startsWith("org/jtool/")
            && !owner.startsWith("org/jtool/test") && !owner
              .startsWith("org/jtool/runtime/rpl/"))
        && !leaveInstrumentedWorldPattern.matcher(owner + "." + name).matches())
    {
      super.visitMethodInsn(opcode, owner,
          getRenamedMethodName(owner, name, desc), desc);
    }
    else
    {
      super.visitMethodInsn(opcode, owner, name, desc);
    }
  }
}

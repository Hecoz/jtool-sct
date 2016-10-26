package org.jtool.loader;

import java.util.regex.Pattern;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class FieldAccessClassVisitor extends ClassVisitor
{
  private final Pattern ignoreFieldsPattern;
  private final ClassLoader classLoader;
  
  public FieldAccessClassVisitor(final int api, final ClassVisitor cv,
      final Pattern ignoreFieldsPattern, final ClassLoader classLoader)
  {
    super(api, cv);
    this.ignoreFieldsPattern = ignoreFieldsPattern;
    this.classLoader = classLoader;
  }
  
  @Override
  public MethodVisitor visitMethod(final int access, final String name,
      final String desc, final String signature, final String[] exceptions)
  {
    MethodVisitor mv =
        super.visitMethod(access, name, desc, signature, exceptions);
    mv =
        new FieldAccessMethodVisitor(
            Opcodes.ASM4,
            mv,
            ignoreFieldsPattern,
            name.equals("<init>"),
            classLoader);
    return mv;
  }
}

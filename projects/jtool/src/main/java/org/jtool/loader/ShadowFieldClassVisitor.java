package org.jtool.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

public class ShadowFieldClassVisitor extends ClassVisitor implements Opcodes
{
  public ShadowFieldClassVisitor(final int api, final ClassVisitor cv)
  {
    super(api, cv);
  }
  
  @Override
  public FieldVisitor visitField(final int access, final String name,
      final String desc, final String signature, final Object value)
  {
    int newAccess = ACC_PUBLIC;
    if ((access & ACC_STATIC) != 0)
    {
      newAccess |= ACC_STATIC;
    }

    final FieldVisitor fv =
        super.visitField(
            newAccess,
            name + "$shadow",
            "Lorg/jtool/runtime/SyncObjectData;",
            null,
            null);
    return fv;
  }
  
}

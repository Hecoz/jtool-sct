package org.jtool.loader;

import static com.google.common.base.Preconditions.checkState;

import java.util.regex.Pattern;

import org.jtool.loader.ClassInfo.FieldInfo;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class FieldAccessMethodVisitor extends MethodVisitor implements Opcodes
{
  private final Pattern ignoreFieldsPattern;
  private final boolean constructor;
  private int initVsNewCount;
  private final ClassLoader classLoader;
  
  public FieldAccessMethodVisitor(final int api, final MethodVisitor mv,
      final Pattern ignoreFieldsPattern, final boolean constructor,
      final ClassLoader classLoader)
  {
    super(api, mv);
    this.ignoreFieldsPattern = ignoreFieldsPattern;
    this.constructor = constructor;
    initVsNewCount = 0;
    this.classLoader = classLoader;
  }
  
  @Override
  public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc)
  {
    if (!ignoreFieldsPattern.matcher(owner).matches()
        && (!constructor || initVsNewCount > 0))
    {
      if (opcode == GETFIELD
          || opcode == PUTFIELD
          || opcode == PUTSTATIC
          || opcode == GETSTATIC)
      {
        final boolean isStatic = ( opcode == PUTSTATIC
            || opcode == GETSTATIC);
        try
        {
          final ClassInfo ci =
              ClassManager.getAndStoreClassInfo(owner, classLoader);
          
          final FieldInfo fieldInfo =
              isStatic ? ci.getStaticField(name, desc) : ci
                  .getField(name, desc);
          //          System.out.println("Owner: " + owner);
          //          System.out.println("Name: " + name);
          //          System.out.println("desc: " + desc);
          // TODO: Figure out why we can't find
          // "org/eclipse/jdt/internal/junit4/runner/JUnit4TestClassReference"
          
          if ((fieldInfo.fn.access & ACC_FINAL) == 0)
          {
            addFieldInstrumentation(fieldInfo, opcode, owner, name, desc);
          }
        }
        catch (final ClassNotFoundException e)
        {
          throw new RuntimeException(e);
        }
      }
    }
    mv.visitFieldInsn(opcode, owner, name, desc);
  }

  private void addFieldInstrumentation(final FieldInfo fieldInfo,
      final int opcode, final String owner, final String name, final String desc)
  {
    if (opcode == GETFIELD)
    {
      // objref
      mv.visitInsn(DUP);
      // objref, objref
      mv.visitInsn(DUP);
      // objref, objref, objref
      mv.visitFieldInsn(
          GETFIELD,
          owner,
          name + "$shadow",
          "Lorg/jtool/runtime/SyncObjectData;");
      // objrefSyncData, objref, objref
      mv.visitInsn(DUP);
      // objrefSyncData, objrefSyncData, objref, objref
      final Label nonNull = new Label();
      mv.visitJumpInsn(IFNONNULL, nonNull);
      // NULL, objref, objref
      mv.visitInsn(POP);
      // objref, objref
      mv.visitTypeInsn(NEW, "org/jtool/runtime/SyncObjectData");
      // NEWobjrefSyncData, objref, objref
      mv.visitInsn(DUP);
      // NEWobjrefSyncData, NEWobjrefSyncData, objref, objref
      mv.visitInsn(ICONST_0);
      mv.visitMethodInsn(
          INVOKESPECIAL,
          "org/jtool/runtime/SyncObjectData",
          "<init>",
          "(I)V");
      // objrefSyncData, objref, objref
      mv.visitInsn(DUP2);
      // objrefSyncData, objref, objrefSyncData, objref, objref
      mv.visitFieldInsn(
          PUTFIELD,
          owner,
          name + "$shadow",
          "Lorg/jtool/runtime/SyncObjectData;");
      // objrefSyncData, objref, objref
      mv.visitLabel(nonNull);
      // objrefSyncData, objref, objref
      mv.visitLdcInsn(new Integer(fieldInfo.fieldIndex));
      // iFieldIndex, objrefSyncData, objref, objref
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "getField",
          "(Ljava/lang/Object;Lorg/jtool/runtime/SyncObjectData;I)V");
      // objref
      // (done)
    }
    else if (opcode == PUTFIELD)
    {
      final Type fieldType = Type.getType(desc);
      final int fieldSize = fieldType.getSize();
      if (fieldSize == 1)
      {
        // w1, objref
        mv.visitInsn(DUP2);
        // w1, objref, w1, objref
        mv.visitInsn(POP);
        // objref, w1, objref
      }
      else
      {
        checkState(fieldSize == 2);
        // w1, w2, objref
        mv.visitInsn(DUP2_X1);
        // w1, w2, objref, w1, w2
        mv.visitInsn(POP2);
        // objref, w1, w2
        mv.visitInsn(DUP_X2);
        // objref, w1, w2, objref
      }


      // objref, ...
      mv.visitInsn(DUP);
      // objref, objref, ...
      mv.visitFieldInsn(
          GETFIELD,
          owner,
          name + "$shadow",
          "Lorg/jtool/runtime/SyncObjectData;");
      // objrefSyncData, objref
      mv.visitInsn(DUP);
      // objrefSyncData, objrefSyncData, objref
      final Label nonNull = new Label();
      mv.visitJumpInsn(IFNONNULL, nonNull);
      // NULL, objref
      mv.visitInsn(POP);
      // objref
      mv.visitTypeInsn(NEW, "org/jtool/runtime/SyncObjectData");
      // NEWobjrefSyncData, objref
      mv.visitInsn(DUP);
      // NEWobjrefSyncData, NEWobjrefSyncData, objref
      mv.visitInsn(ICONST_0);
      mv.visitMethodInsn(
          INVOKESPECIAL,
          "org/jtool/runtime/SyncObjectData",
          "<init>",
          "(I)V");
      // objrefSyncData, objref
      mv.visitInsn(DUP2);
      // objrefSyncData, objref, objrefSyncData, objref
      mv.visitFieldInsn(
          PUTFIELD,
          owner,
          name + "$shadow",
          "Lorg/jtool/runtime/SyncObjectData;");
      // objrefSyncData, objref
      mv.visitLabel(nonNull);
      // objrefSyncData, objref, ...
      mv.visitLdcInsn(new Integer(fieldInfo.fieldIndex));
      // iFieldIndex, objrefSyncData, objref, ...
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "putField",
          "(Ljava/lang/Object;Lorg/jtool/runtime/SyncObjectData;I)V");
      // ...
      // (done)

    }
    else if (opcode == GETSTATIC || opcode == PUTSTATIC)
    {
      // ...
      mv.visitFieldInsn(
          GETSTATIC,
          owner,
          name + "$shadow",
          "Lorg/jtool/runtime/SyncObjectData;");
      // objrefSyncData, ...
      mv.visitInsn(DUP);
      // objrefSyncData, objrefSyncData, ...
      final Label nonNull = new Label();
      mv.visitJumpInsn(IFNONNULL, nonNull);
      // NULL, ...
      mv.visitInsn(POP);
      // ...
      mv.visitTypeInsn(NEW, "org/jtool/runtime/SyncObjectData");
      // NEWobjrefSyncData, ...
      mv.visitInsn(DUP);
      // NEWobjrefSyncData, NEWobjrefSyncData, ...
      mv.visitInsn(ICONST_0);
      mv.visitMethodInsn(
          INVOKESPECIAL,
          "org/jtool/runtime/SyncObjectData",
          "<init>",
          "(I)V");
      // objrefSyncData, ...
      mv.visitInsn(DUP);
      // objrefSyncData, objrefSyncData, ...
      mv.visitFieldInsn(
          PUTSTATIC,
          owner,
          name + "$shadow",
          "Lorg/jtool/runtime/SyncObjectData;");
      // objrefSyncData, ...
      mv.visitLabel(nonNull);
      // objrefSyncData, ...
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          (opcode == GETSTATIC ? "getStatic" : "putStatic"),
          "(Lorg/jtool/runtime/SyncObjectData;)V");
      // ...
    }
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

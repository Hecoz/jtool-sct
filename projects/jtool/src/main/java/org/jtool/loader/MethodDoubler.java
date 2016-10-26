package org.jtool.loader;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jtool.loader.alt.FirstMethodVisitor;
import org.jtool.loader.alt.SecondMethodVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodDoubler implements ClassFileTransformer, Opcodes
{
  private final Pattern dontInstrumentPattern = Pattern
      .compile("java/lang/.*(Thread|Stack|String|AssertionStatusDirectives).*"
          + "|java/lang/ref/.*"
          + "|java/lang/Class"
          + "|java/(n|)io/.*"
      //+ "|com/sun/.*"
      //+ "|sun/.*"
      //+ "|java/security/.*"
      );
  
  private final Pattern trickyClasses = Pattern
      .compile("WrapperGenerator.*|java/lang/"
          + "(Object"
          + "|ref/Finalizer[$]FinalizerThread"
          + "|ref/Reference[$]ReferenceHandler"
          //+ "|ClassLoader"
          + "|AssertionStatusDirectives)");
  
  public static Pattern noNativeWarning = Pattern
      .compile("java/lang/Throwable[.]fillInStackTrace[(].*"
          + "|.*(doubleToRawLongBits|longBitsToDouble).*"
          + "|java/lang/StrictMath[.].*");
  
  private final Pattern leaveInstrumentedWorldPattern = Pattern
      .compile("java/util/PropertyResourceBundle.getBundle|"
          + "java/util/ResourceBundle.getBundle|"
          + "java/util/Locale.getDefault");

  public MethodDoubler()
  {
  }

  @SuppressWarnings("unchecked")
  @Override
  public byte[] transform(final ClassLoader loader, final String className,
      final Class<?> classBeingRedefined,
      final ProtectionDomain protectionDomain, final byte[] classfileBuffer)
      throws IllegalClassFormatException
  {
    try
    {
      // Don't touch these classes. But if other classes refer to these, then there may be a problem.
      if (trickyClasses.matcher(className).matches())
      {
        return null;
      }

      // Ignore classes within our package.
      if(className.startsWith("org/jtool/"))
      {
        if(className.equals("org/jtool/runtime/ExecutionManager")
            || className.startsWith("org/jtool/test") // includes /tests/
            || className.startsWith("org/jtool/runtime/rpl/")
            || className.equals("org/jtool/runtime/InstrumentationPoints")
            )
        {

        }
        else
        {
          return null;
        }
      }
      

      final boolean dontInstrument =
          dontInstrumentPattern.matcher(className).matches();

      final ClassReader cr = new ClassReader(classfileBuffer);
      final ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS
              | ClassWriter.COMPUTE_FRAMES)
          {
            
            @Override
            protected String getCommonSuperClass(final String type1,
                final String type2)
            {
              try
              {
                final ClassInfo ci1 =
                    ClassManager.getAndStoreClassInfo(type1, loader);
                final ClassInfo ci2 =
                    ClassManager.getAndStoreClassInfo(type2, loader);
                final ClassInfo common = ci1.getCommonSuperClass(ci2);
                return common.name;
              }
              catch (final ClassNotFoundException e)
              {
                throw new RuntimeException(e);
              }
              // Alternatively, can use super implementation, but modified to
              // use the following classLoader:
              //              final ClassLoader classLoader =
              //                  loader == null ? getClass().getClassLoader() : loader;
            }
            
          };
      
      // special case: add "$instr" suffix for calls to ConcurrencyTestCase methods
      // and instrument the setThreadRuntimeState method.
      if (className.equals("org/jtool/runtime/ExecutionManager"))
      {
        cr.accept(new SelfRuntimeStateClassVisitor(
            ASM4,
            new SelfInstrumentationClassVisitor(ASM4, cw)), 0);
        final byte[] classFile = cw.toByteArray();
        return classFile;
      }

      final ClassNode cn = new ClassNode()
      {
        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String desc,
            final String signature, final String[] exceptions)
        {
          return new JSRInlinerAdapter(super.visitMethod(access, name, desc,
              signature, exceptions), access, name, desc, signature, exceptions);
        }
      };
      
      // read class into ClassNode cn
      {
        ClassVisitor classNodeVisitor = cn;

        // renames run() to run$orig()
        classNodeVisitor = new RunRenamerClassVisitor(ASM4, classNodeVisitor);
        
        if (className.equals("java/lang/ClassLoader"))
        {
          classNodeVisitor =
              new ClassLoaderClassVisitor(ASM4, classNodeVisitor);
        }

        cr.accept(classNodeVisitor, ClassReader.SKIP_FRAMES);
        
      }
      
      if (cn.invisibleAnnotations != null)
      {
        for (final AnnotationNode an : (List<AnnotationNode>) cn.invisibleAnnotations)
        {
          if (an.desc.equals("Lorg/jtool/loader/NoInstrument;"))
          {
            return null;
          }
        }
      }

      ClassVisitor cvNativeWrappers = cn;
      
      ClassVisitor cvInstrumented = cn;
      cvInstrumented =
          new MethodRenameClassVisitor(
              ASM4,
              cvInstrumented,
              leaveInstrumentedWorldPattern);
      cvInstrumented =
          wrapInstrumentationVisitors(
              loader,
              className,
              cvInstrumented,
              classfileBuffer);
      ClassVisitor cvNoninstrumented = cn;
      cvNoninstrumented =
          wrapNoninstrumentationVisitors(loader, className, cvNoninstrumented);
      
      ClassVisitor cv = null;

      if (dontInstrument)
      {
        cvNativeWrappers =
            wrapNoninstrumentationVisitors(loader, className, cvNativeWrappers);
        cv = cvNoninstrumented;
      }
      else
      {
        cvNativeWrappers =
            wrapInstrumentationVisitors(
                loader,
                className,
                cvNativeWrappers,
                classfileBuffer);
        cv = cvInstrumented;
      }
      
      final List<MethodNode> methodsToCopy = new ArrayList<>(cn.methods);
      final List<FieldNode> fields = new ArrayList<>(cn.fields);
      
      // add shadow fields
      if (!dontInstrument)
      {
        final ClassVisitor cnWithShadowFieldVisitor =
            new ShadowFieldClassVisitor(ASM4, cn);
        for (final FieldNode fn : fields)
        {
          if ((ACC_FINAL & fn.access) == 0)
          {
            fn.accept(cnWithShadowFieldVisitor);
          }
        }
      }
      
      // original methods
      for (final MethodNode mn : methodsToCopy)
      {
        if (mn.name.equals("$dummy$intr"))
        {
          // class already instrumented
          return null;
        }
        if (mn.name.equals("<init>"))
        {
          final String origDesc = mn.desc;
          mn.desc = mn.desc.replace(")",
              "Lorg/jtool/loader/ConstrInstrMarker;)");
          mn.accept(cv);
          mn.desc = origDesc;
        }
      }
      
      // instrumented (doubled) methods
      for (final MethodNode mn : methodsToCopy)
      {
        if (mn.name.equals("$dummy$intr"))
        {
          // already instrumented
          return null;
        }
        if (mn.name.equals("<clinit>"))
        {
          // skip static initialisers
          continue;
        }
        if ((mn.access & ACC_FINAL) != 0 && className.equals("java/lang/Object"))
        {
          continue;
        }
        if ((mn.access & ACC_NATIVE) != 0)
        {
          visitNativeDelagateMethod(className, cvNativeWrappers, mn);
          continue;
        }
        if (ClassManager.isSpecialObjectMethod(className, mn.name, mn.desc)
        // TODO: remove these? should be captured in dontInstrument?
            && (!className.startsWith("java/lang/String") && !className
                .startsWith("java/lang/Char")
                ))
        {
          if (dontInstrument)
          {
            continue;
          }
          
          //          System.out.println(className + "." + mn.name);
          int i = 0;
          for (;; ++i)
          {
            if (cn.methods.get(i) == mn)
            {
              cn.methods.remove(i);
              break;
            }
          }
          final MethodVisitor mv =
              cn.visitMethod(
                  mn.access,
                  mn.name,
                  mn.desc,
                  mn.signature,
                  (String[]) mn.exceptions.toArray(new String[mn.exceptions
                      .size()]));
          final FirstMethodVisitor fmv = new FirstMethodVisitor(ASM4, mv);
          
          mn.accept(fmv);
          
          MethodVisitor smv = new SecondMethodVisitor(ASM4, mv);
          smv = wrapMethodVisitorForInstrumentation(loader, className, smv);
          mn.accept(smv);
          
          final MethodNode newMn =
              (MethodNode) cn.methods.get(cn.methods.size() - 1);
          cn.methods.remove(cn.methods.size() - 1);
          cn.methods.add(i, newMn);

          continue;
        }
        if (!ClassManager.shouldDouble(className, mn.name, mn.desc))
        {
          continue;
        }
        if (mn.name.equals("<init>"))
        {
          continue;
        }

        final String origName = mn.name;
        if (!mn.name.equals("<init>"))
        {
          mn.name += "$instr";
        }
        
        if (className.equals("java/lang/Thread")
            && mn.name.equals("run$orig$instr"))
        {
          mn.accept(cvInstrumented);
        }
        else
        {
          mn.accept(cv);
        }


        mn.name = origName;
      }
      
      if (className.equals("java/lang/Thread"))
      {
        visitRunWrapper(cn);
        
        final FieldNode fieldInstrumented = new FieldNode(ACC_PUBLIC, "instrumented", "Z",
            null, null);
        final FieldNode fieldData =
            new FieldNode(
                ACC_PUBLIC,
                "threadData",
                "Lorg/jtool/runtime/ThreadData;",
                null,
                null);
        cn.fields.add(fieldInstrumented);
        cn.fields.add(fieldData);
        
      }
      
      // add dummy method to start so we don't instrument it twice
      visitMarkerMethod(cn);
      final Object markerMethod = cn.methods.remove(cn.methods.size() - 1);
      cn.methods.add(0, markerMethod);


      
      cn.accept(cw);
      final byte[] classFile = cw.toByteArray();
      //      final ClassReader cr2 = new ClassReader(classFile);
      //      final ClassWriter cw2 = new ClassWriter(cr2, 0);
      //      cr2.accept(new CheckClassAdapter(cw2), 0);
      //      CheckClassAdapter
      //          .verify(cr2, loader == null ? Thread.currentThread()
      //              .getContextClassLoader() : loader, false, new PrintWriter(
      //              System.out));

      //System.out.println("Verified " + className);

      //      try
      //      {
      //        final FileOutputStream fos =
      //            new FileOutputStream(new File("instr", className.replaceAll(
      //                "/",
      //                ".") + ".class"));
      //        fos.write(classFile);
      //        fos.close();
      //      }
      //      catch (final FileNotFoundException e)
      //      {
      //        e.printStackTrace();
      //      }
      //      catch (final IOException e)
      //      {
      //        e.printStackTrace();
      //      }
      
      return classFile;
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    return null;
  }

  
  private ClassVisitor wrapNoninstrumentationVisitors(final ClassLoader loader,
      final String className, ClassVisitor cv)
  {
    if ("java/lang/Thread".equals(className))
    {
      cv = new ThreadStart(Opcodes.ASM4, cv);
    }
    cv = new InitClassVisitor(ASM4, cv);
    cv = new ObjectInitClassVisitor(ASM4, cv);
    cv = new NewArrayClassVisitor(ASM4, cv);
    return cv;
  }

  private ClassVisitor wrapInstrumentationVisitors(final ClassLoader loader,
      final String className, ClassVisitor cv, final byte[] classBuffer)
  {
    if ("java/lang/Thread".equals(className))
    {
      cv = new ThreadStart(Opcodes.ASM4, cv);
    }
    final boolean ignoreSync = dontInstrumentPattern.matcher(className).matches();
    if (!ignoreSync)
    {
      cv = new MonitorClassVisitor(Opcodes.ASM4, cv);
      cv = new ReplaceCallClassVisitor(Opcodes.ASM4, cv, className, loader);
    }
    cv = new ReplaceSyncClassVisitor(Opcodes.ASM4, cv, className);
    
    try
    {
      ClassManager.getAndStoreClassInfo(className, loader, new ClassReader(
          classBuffer));
    }
    catch (final Exception e)
    {
      // ignore
    }
    cv = new InitClassVisitor(ASM4, cv);
    cv = new ObjectInitClassVisitor(ASM4, cv);
    cv = new NewArrayClassVisitor(ASM4, cv);
    cv =
        new FieldAccessClassVisitor(
            Opcodes.ASM4,
            cv,
            dontInstrumentPattern,
            loader);
    if (!dontInstrumentPattern.matcher(className).matches())
    {
      cv = new ArrayAccessClassVisitor(Opcodes.ASM4, cv);
    }
    cv = new ConstructorCallClassVisitor(ASM4, cv);
    cv = new ReflectionClassVisitor(ASM4, cv);
    cv = new CloneClassVisitor(ASM4, cv);


    return cv;
  }
  
  /**
   * Not suitable for instrumenting constructors.
   * 
   * @param loader
   * @param className
   * @param mv
   * @return
   */
  public MethodVisitor wrapMethodVisitorForInstrumentation(
      final ClassLoader loader, final String className, MethodVisitor mv)
  {
    final boolean ignoreSync = dontInstrumentPattern.matcher(className).matches();
    if (!ignoreSync)
    {
      mv = new MonitorMethodVisitor(Opcodes.ASM4, mv);
      mv = new ReplaceCallMethodVisitor(Opcodes.ASM4, mv, className, loader);
    }
    mv = new ObjectInitMethodVisitor(ASM4, mv);
    mv = new NewArrayMethodVisitor(ASM4, mv);
    mv =
        new FieldAccessMethodVisitor(
            Opcodes.ASM4,
            mv,
            dontInstrumentPattern,
            false,
            loader);
    if (!dontInstrumentPattern.matcher(className).matches())
    {
      mv = new ArrayAccessMethodVisitor(Opcodes.ASM4, mv);
    }
    mv = new ReflectionMethodVisitor(ASM4, mv);
    mv = new CloneMethodVisitor(ASM4, mv);
    return mv;
  }
  
  private void visitMarkerMethod(final ClassNode cn)
  {
    int access = ACC_PUBLIC;
    if ((cn.access & ACC_INTERFACE) != 0)
    {
      access += ACC_ABSTRACT;
    }

    final MethodVisitor mv = cn.visitMethod(access, "$dummy$intr", "()V",
        null, null);
    final GeneratorAdapter ga = new GeneratorAdapter(mv, access, "dummy",
        "()V");
    if ((cn.access & ACC_INTERFACE) == 0)
    {
      ga.visitCode();
      ga.visitInsn(RETURN);
    }
    ga.visitEnd();
  }

  @SuppressWarnings("unchecked")
  private void visitNativeDelagateMethod(final String className,
      final ClassVisitor cv, final MethodNode mn)
  {
    String[] temp = null;
    if(mn.exceptions != null)
    {
      temp = (String[]) mn.exceptions.toArray(new String[mn.exceptions.size()]);
    }
    final MethodVisitor mv = cv.visitMethod(mn.access & ~ACC_NATIVE,
        mn.name + "$instr", mn.desc,
        mn.signature, temp);
    final GeneratorAdapter ga = new GeneratorAdapter(mv, mn.access, mn.name,
        mn.desc);
    ga.visitCode();
    
    if (!MethodDoubler.noNativeWarning.matcher(
        className + "." + mn.name + mn.desc)
        .matches())
    {
      mv.visitLdcInsn(className);
      mv.visitLdcInsn(mn.name);
      mv.visitLdcInsn(mn.desc);
      mv.visitMethodInsn(
          INVOKESTATIC,
          "org/jtool/runtime/InstrumentationPoints",
          "calledNativeMethod",
          "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    }

    if ((mn.access & ACC_STATIC) == 0)
    {
      // not static? load obj
      ga.loadThis();
    }
    // load args
    ga.loadArgs();

    int opcode;
    if ((mn.access & ACC_STATIC) != 0)
    {
      opcode = INVOKESTATIC;
    }
    else 
    {
      opcode = INVOKESPECIAL;
    }
    ga.visitMethodInsn(opcode, className, mn.name, mn.desc);
    ga.returnValue();
    ga.endMethod();
  }
  
  private void visitRunWrapper(final ClassVisitor cv)
  {
    final MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "run", "()V", null,
        null);
    mv.visitCode();
    final Label l0 = new Label();
    final Label l1 = new Label();
    final Label l2 = new Label();
    mv.visitTryCatchBlock(l0, l1, l2, "java/lang/Throwable");
    final Label l3 = new Label();
    mv.visitLabel(l3);
    mv.visitLineNumber(126, l3);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitFieldInsn(GETFIELD, "java/lang/Thread", "instrumented", "Z");
    final Label l4 = new Label();
    mv.visitJumpInsn(IFNE, l4);
    final Label l5 = new Label();
    mv.visitLabel(l5);
    mv.visitLineNumber(128, l5);
    // mv.visitFieldInsn(
    // GETSTATIC,
    // "java/lang/System",
    // "out",
    // "Ljava/io/PrintStream;");
    // mv.visitLdcInsn("Running original");
    // mv.visitMethodInsn(
    // INVOKEVIRTUAL,
    // "java/io/PrintStream",
    // "println",
    // "(Ljava/lang/String;)V");
    final Label l6 = new Label();
    mv.visitLabel(l6);
    mv.visitLineNumber(129, l6);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "run$orig", "()V");
    final Label l7 = new Label();
    mv.visitLabel(l7);
    mv.visitLineNumber(130, l7);
    mv.visitInsn(RETURN);
    mv.visitLabel(l4);
    mv.visitLineNumber(132, l4);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitInsn(ACONST_NULL);
    mv.visitVarInsn(ASTORE, 1);
    final Label l8 = new Label();
    mv.visitLabel(l8);
    mv.visitLineNumber(133, l8);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(
        INVOKESTATIC,
        "org/jtool/runtime/InstrumentationPoints",
        "executedRun",
        "(Ljava/lang/Object;)V");
    mv.visitLabel(l0);
    mv.visitLineNumber(136, l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(
        INVOKEVIRTUAL,
        "java/lang/Thread",
        "run$orig$instr",
        "()V");
    mv.visitLabel(l1);
    mv.visitLineNumber(137, l1);
    final Label l9 = new Label();
    mv.visitJumpInsn(GOTO, l9);
    mv.visitLabel(l2);
    mv.visitLineNumber(138, l2);
    mv.visitFrame(Opcodes.F_FULL, 2, new Object[] {
        "java/lang/Thread",
        "java/lang/Throwable" }, 1, new Object[] { "java/lang/Throwable" });
    mv.visitVarInsn(ASTORE, 2);
    final Label l10 = new Label();
    mv.visitLabel(l10);
    mv.visitLineNumber(140, l10);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitVarInsn(ASTORE, 1);
    mv.visitLabel(l9);
    mv.visitLineNumber(142, l9);
    mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(
        INVOKESTATIC,
        "org/jtool/runtime/InstrumentationPoints",
        "exitRun",
        "(Ljava/lang/Object;Ljava/lang/Throwable;)V");
    final Label l11 = new Label();
    mv.visitLabel(l11);
    mv.visitLineNumber(143, l11);
    mv.visitInsn(RETURN);
    final Label l12 = new Label();
    mv.visitLabel(l12);
    mv.visitLocalVariable("this", "Ljava/lang/Thread;", null, l3, l12, 0);
    mv.visitLocalVariable("t", "Ljava/lang/Throwable;", null, l8, l12, 1);
    mv.visitLocalVariable("e", "Ljava/lang/Throwable;", null, l10, l9, 2);
    mv.visitMaxs(2, 3);
    mv.visitEnd();
  }

  
}

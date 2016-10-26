package org.jtool.jdkinstr;

import static com.google.common.base.Preconditions.checkState;

import java.io.FileOutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

import org.jtool.loader.MethodDoubler;

import com.google.common.io.ByteStreams;

public class JDKInstrumenter
{
  // -Xbootclasspath/p:rt_instr.jar

  // java -cp target/jtool-runtime-0.1.jar org.jtool.jdkinstr.JDKInstrumenter
  // /usr/lib/jvm/java-1.7.0-openjdk-amd64/jre/lib/rt.jar

  public static void main(final String[] args) throws Exception
  {
    //System.out.println(System.getProperty("java.class.path"));
    //System.out.println(System.getProperty("sun.boot.class.path"));
    String rtJar = null;
    final String bootClassPath = System.getProperty("sun.boot.class.path");
    final String pathSep = System.getProperty("path.separator");
    final String[] bootJars = bootClassPath.split(pathSep);
    for (final String jar : bootJars)
    {
      if (jar.endsWith("rt.jar"))
      {
        rtJar = jar;
        break;
      }
    }
    
    checkState(rtJar != null);

    final MethodDoubler md = new MethodDoubler();

    try (final JarFile inputJarFile = new JarFile(rtJar, false);
        final JarOutputStream outputJarFile = new JarOutputStream(
            new FileOutputStream("rt_instr.jar")))
    {
      final Enumeration<JarEntry> entries = inputJarFile.entries();
      while (entries.hasMoreElements())
      {
        JarEntry entry = entries.nextElement();
        // System.out.println(entry.getName());
        byte[] bytecodeClass = ByteStreams.toByteArray(inputJarFile
            .getInputStream(entry));
        if (entry.getName().endsWith(".class"))
        {
          final byte[] newBytecodeClass = md.transform(null, entry.getName()
              .replaceFirst("[.]class", ""), null, null, bytecodeClass);
          if (newBytecodeClass != null)
          {
            bytecodeClass = newBytecodeClass;
          }
          entry = new JarEntry(entry.getName());
        }
        outputJarFile.putNextEntry(entry);
        outputJarFile.write(bytecodeClass);
        outputJarFile.closeEntry();
      }
    }
  }
}

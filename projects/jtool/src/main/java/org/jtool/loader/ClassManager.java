package org.jtool.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassReader;


public class ClassManager
{
  private static final Map<ClassLoader, Map<String, ClassInfo>> classLoaderToClasses = new HashMap<>();
  
  private static ClassInfo getClassInfo(final String internalClassName,
      final ClassLoader cl)
  {
    ClassLoader cl_it = cl;
    while (true)
    {
      final Map<String, ClassInfo> classInfos = classLoaderToClasses.get(cl_it);
      if (classInfos != null)
      {
        final ClassInfo ci = classInfos.get(internalClassName);
        if (ci != null)
        {
          return ci;
        }
      }
      
      if (cl_it == null)
      {
        break;
      }
      cl_it = cl_it.getParent();
    }
    return null;
  }
  
  public static ClassInfo getAndStoreClassInfo(final String internalClassName,
      final ClassLoader cl, final ClassReader cr) throws ClassNotFoundException
  {
    ClassInfo ci = getClassInfo(internalClassName, cl);
    if (ci == null)
    {
      final Map<String, ClassInfo> classInfos = getOrAddClassesInfo(cl);
      ci = new ClassInfo(internalClassName, cr, cl);
      classInfos.put(internalClassName, ci);
    }
    return ci;
  }
  
  public static ClassInfo getAndStoreClassInfo(final String internalClassName,
      final ClassLoader cl) throws ClassNotFoundException
  {
    final ClassInfo ci = getClassInfo(internalClassName, cl);
    if (ci != null)
    {
      return ci;
    }

    // ClassInfo not loaded yet
    
    final ClassLoader clForLoading = cl == null ? ClassLoader.getSystemClassLoader()
        : cl;

    try (InputStream is = clForLoading.getResourceAsStream(internalClassName
        + ".class"))
    {
      final ClassReader cr = new ClassReader(is);
      return getAndStoreClassInfo(internalClassName, cl, cr);
    }
    catch (final IOException e)
    {
      System.err.println("WARNING: Could not read class " + internalClassName);
      //      e.printStackTrace();
      return null;
    }
  }
  
  private static Map<String, ClassInfo> getOrAddClassesInfo(final ClassLoader cl)
  {
    Map<String, ClassInfo> res = classLoaderToClasses.get(cl);
    if (res == null)
    {
      res = new HashMap<String, ClassInfo>();
      classLoaderToClasses.put(cl, res);
    }
    return res;
  }
  
  public static boolean compareNameDesc(final String name, final String desc,
      final String[][] compare)
  {
    for (final String[] m : compare)
    {
      if (name.equals(m[0]) && desc.equals(m[1]))
      {
        return true;
      }
    }
    return false;
  }
  
  public static boolean isSpecialObjectMethod(final String owner,
      final String name, final String desc)
  {
    return compareNameDesc(name, desc, new String[][] {
        new String[] { "hashCode", "()I" },
        new String[] { "equals", "(Ljava/lang/Object;)Z" },
        new String[] { "clone", "()Ljava/lang/Object;" },
        new String[] { "toString", "()Ljava/lang/String;" },
    //new String[] { "finalize", "()V" }, 
        });
  }
  
  public static boolean shouldDouble(final String owner, final String name,
      final String desc)
  {
    if (name.equals("<clinit>"))
    {
      return false;
    }
    if (compareNameDesc(name, desc, new String[][] {
        new String[] { "registerNatives", "()V" },
        new String[] { "getClass", "()Ljava/lang/Class;" },
        new String[] { "hashCode", "()I" },
        new String[] { "equals", "(Ljava/lang/Object;)Z" },
        new String[] { "clone", "()Ljava/lang/Object;" },
        new String[] { "toString", "()Ljava/lang/String;" },
        new String[] { "notify", "()V" },
        new String[] { "notifyAll", "()V" },
        new String[] { "wait", "(J)V" },
        new String[] { "wait", "(JI)V" },
        new String[] { "wait", "()V" },
        new String[] { "finalize", "()V" },
    }))
    {
      return false;
    }
    return true;
  }
}

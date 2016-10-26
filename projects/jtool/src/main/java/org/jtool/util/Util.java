package org.jtool.util;

import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

public class Util
{
  public static int getLoadOpcodeForType(final Type type)
  {
    switch (type.getSort())
    {
    case Type.BOOLEAN:
    case Type.CHAR:
    case Type.BYTE:
    case Type.SHORT:
    case Type.INT:
      return Opcodes.ILOAD;
    case Type.FLOAT:
      return Opcodes.FLOAD;
    case Type.LONG:
      return Opcodes.LLOAD;
    case Type.DOUBLE:
      return Opcodes.DLOAD;
    case Type.ARRAY:
    case Type.OBJECT:
      return Opcodes.ALOAD;
    default:
      //case Type.VOID:
      //case Type.METHOD:
      System.err.println("ERROR2");
      throw new RuntimeException();
    }
  }
  
  public static int getReturnOpcodeForType(final Type type)
  {
    switch (type.getSort())
    {
    case Type.BOOLEAN:
    case Type.CHAR:
    case Type.BYTE:
    case Type.SHORT:
    case Type.INT:
      return Opcodes.IRETURN;
    case Type.FLOAT:
      return Opcodes.FRETURN;
    case Type.LONG:
      return Opcodes.LRETURN;
    case Type.DOUBLE:
      return Opcodes.DRETURN;
    case Type.ARRAY:
    case Type.OBJECT:
      return Opcodes.ARETURN;
    case Type.VOID:
      return Opcodes.RETURN;
    default:
      //case Type.METHOD:
      System.err.println("ERROR1");
      throw new RuntimeException();
    }
  }
  
  public static Set<String> getSuperClasses(final String binaryClassName,
      final String binarySuperClassName)
  {
    final Set<String> res = new HashSet<>();
    res.add(binaryClassName);
    res.add(binarySuperClassName);
    try
    {
      Class<?> aClass = Class.forName(binarySuperClassName, false, null);
      aClass = aClass.getSuperclass();
      while (aClass != null)
      {
        res.add(aClass.getName());
        aClass = aClass.getSuperclass();
      }
    }
    catch (final ClassNotFoundException e)
    {
      System.err.println("WARNING: Could not find superclass of "
          + binarySuperClassName + ".");
    }
    return res;
    
  }
  
  public static void printStackTrace()
  {
    System.out.println("\nStack:");
    for (final StackTraceElement ste : Thread.currentThread().getStackTrace())
    {
      System.out.println(ste);
    }
  }

  public static void monitorEnter(final Object o)
  {
    
  }
  
  public static void monitorExit(final Object o)
  {
    
  }

}

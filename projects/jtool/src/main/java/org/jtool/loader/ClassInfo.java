package org.jtool.loader;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

public class ClassInfo
{
  public final String name;
  public final String supername;
  private Set<String> supernames = null;
  private final ClassNode classNode = new ClassNode();
  private final ClassLoader classLoader;
  private ClassInfo superClassInfo = null;
  
  /**
   * These are non-static fields of this class only.
   */
  private final Map<String, FieldInfo> fields;
  /**
   * These are static fields of this class only.
   */
  private final Map<String, FieldInfo> staticFields;
  public int nextFieldIndex = 1;
  
  private final List<ClassInfo> interfaces;

  public static class FieldInfo
  {
    public FieldNode fn;
    public final int fieldIndex;
    public FieldInfo(final FieldNode fn, final int fieldIndex)
    {
      this.fn = fn;
      this.fieldIndex = fieldIndex;
    }
  }

  @SuppressWarnings("unchecked")
  public ClassInfo(final String name, final ClassReader cr,
      final ClassLoader cl) throws ClassNotFoundException
  {
    this.name = name;
    this.supername = cr.getSuperName();
    cr.accept(classNode, ClassReader.SKIP_CODE);
    this.classLoader = cl;
    if (supername != null)
    {
      superClassInfo = ClassManager
          .getAndStoreClassInfo(supername, classLoader);
      nextFieldIndex = superClassInfo.nextFieldIndex;
    }
    
    fields = new HashMap<>();
    staticFields = new HashMap<>();
    for (final FieldNode field : (List<FieldNode>) getClassNode().fields)
    {
      final boolean isStatic = ((field.access & Opcodes.ACC_STATIC) != 0);
      final Map<String, FieldInfo> map = isStatic ? staticFields : fields;
      map.put(field.name + "." + field.desc, new FieldInfo(field, isStatic
          ? -1
          : nextFieldIndex));
      if (!isStatic)
      {
        ++nextFieldIndex;
      }
    }
    
    interfaces = new ArrayList<>();
    for (final String interfaceName : (List<String>) classNode.interfaces)
    {
      final ClassInfo interfaceInfo =
          ClassManager.getAndStoreClassInfo(interfaceName, classLoader);
      interfaces.add(interfaceInfo);
    }

  }
  
  public FieldInfo getField(final String name, final String desc)
  {
    return getFieldHelper(name + "." + desc);
  }
  
  private FieldInfo getFieldHelper(final String namedesc)
  {
    FieldInfo res = fields.get(namedesc);
    if (res == null && superClassInfo != null)
    {
      res = superClassInfo.getFieldHelper(namedesc);
    }
    return res;
  }
  
  public FieldInfo getStaticField(final String name, final String desc)
  {
    return getStaticFieldHelper(name + "." + desc);
  }
  
  private FieldInfo getStaticFieldHelper(final String namedesc)
  {
    // First, check in current class/interface
    FieldInfo res = staticFields.get(namedesc);
    if (res != null)
    {
      return res;
    }
    
    // Not found, so recursively find in our interfaces.
    for (final ClassInfo interfaceInfo : interfaces)
    {
      res = interfaceInfo.getStaticFieldHelper(namedesc);
      if (res != null)
      {
        return res;
      }
    }
    
    // Not found, so recursively find in our superclass.
    if (superClassInfo != null)
    {
      res = superClassInfo.getStaticFieldHelper(namedesc);
    }
    return res;
  }

  public Set<String> getSupernames() throws ClassNotFoundException
  {
    if (supernames == null)
    {
      // System.out.println(name + ".getSupernames(): supername is: " +
      // supername);
      // System.out.println("Calculating supernames of " + name);
      final Set<String> temp = new HashSet<>();
      temp.add(name);
      temp.add("java/lang/Object");
      if (supername != null && !supername.equals("java/lang/Object"))
      {
        checkNotNull(superClassInfo);
        temp.addAll(superClassInfo.getSupernames());
      }
      // System.out.println("Result is:");
      // for (final String s : supernames)
      // {
      // System.out.println(" " + s);
      // }
      supernames = temp;
    }
    return supernames;
  }
  
  public ClassNode getClassNode()
  {
    return classNode;
  }
  
  public ClassInfo getCommonSuperClass(final ClassInfo other)
      throws ClassNotFoundException
  {
    ClassInfo it = other;
    
    
    while (true)
    {
      if (getSupernames().contains(it.name))
      {
        return it;
      }
      
      it = it.getSuperClassInfo();
      checkNotNull(it);
    }
    
  }

  public ClassInfo getSuperClassInfo()
  {
    return superClassInfo;
  }

}

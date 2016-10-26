package org.jtool.runtime.rpl;

import static com.google.common.base.Preconditions.checkNotNull;

public class Static
{
  /**
   * Copies an array from the specified source array, beginning at the specified
   * position, to the specified position of the destination array. A subsequence
   * of array components are copied from the source array referenced by
   * <code>src</code> to the destination array referenced by <code>dest</code>.
   * The number of components copied is equal to the <code>length</code>
   * argument. The components at positions <code>srcPos</code> through
   * <code>srcPos+length-1</code> in the source array are copied into positions
   * <code>destPos</code> through <code>destPos+length-1</code>, respectively,
   * of the destination array.
   * <p>
   * If the <code>src</code> and <code>dest</code> arguments refer to the same
   * array object, then the copying is performed as if the components at
   * positions <code>srcPos</code> through <code>srcPos+length-1</code> were
   * first copied to a temporary array with <code>length</code> components and
   * then the contents of the temporary array were copied into positions
   * <code>destPos</code> through <code>destPos+length-1</code> of the
   * destination array.
   * <p>
   * If <code>dest</code> is <code>null</code>, then a
   * <code>NullPointerException</code> is thrown.
   * <p>
   * If <code>src</code> is <code>null</code>, then a
   * <code>NullPointerException</code> is thrown and the destination array is
   * not modified.
   * <p>
   * Otherwise, if any of the following is true, an
   * <code>ArrayStoreException</code> is thrown and the destination is not
   * modified:
   * <ul>
   * <li>The <code>src</code> argument refers to an object that is not an array.
   * <li>The <code>dest</code> argument refers to an object that is not an
   * array.
   * <li>The <code>src</code> argument and <code>dest</code> argument refer to
   * arrays whose component types are different primitive types.
   * <li>The <code>src</code> argument refers to an array with a primitive
   * component type and the <code>dest</code> argument refers to an array with a
   * reference component type.
   * <li>The <code>src</code> argument refers to an array with a reference
   * component type and the <code>dest</code> argument refers to an array with a
   * primitive component type.
   * </ul>
   * <p>
   * Otherwise, if any of the following is true, an
   * <code>IndexOutOfBoundsException</code> is thrown and the destination is not
   * modified:
   * <ul>
   * <li>The <code>srcPos</code> argument is negative.
   * <li>The <code>destPos</code> argument is negative.
   * <li>The <code>length</code> argument is negative.
   * <li><code>srcPos+length</code> is greater than <code>src.length</code>, the
   * length of the source array.
   * <li><code>destPos+length</code> is greater than <code>dest.length</code>,
   * the length of the destination array.
   * </ul>
   * <p>
   * Otherwise, if any actual component of the source array from position
   * <code>srcPos</code> through <code>srcPos+length-1</code> cannot be
   * converted to the component type of the destination array by assignment
   * conversion, an <code>ArrayStoreException</code> is thrown. In this case,
   * let <b><i>k</i></b> be the smallest nonnegative integer less than length
   * such that <code>src[srcPos+</code><i>k</i><code>]</code> cannot be
   * converted to the component type of the destination array; when the
   * exception is thrown, source array components from positions
   * <code>srcPos</code> through <code>srcPos+</code><i>k</i><code>-1</code>
   * will already have been copied to destination array positions
   * <code>destPos</code> through <code>destPos+</code><i>k</I><code>-1</code>
   * and no other positions of the destination array will have been modified.
   * (Because of the restrictions already itemized, this paragraph effectively
   * applies only to the situation where both arrays have component types that
   * are reference types.)
   * 
   * @param src
   *          the source array.
   * @param srcPos
   *          starting position in the source array.
   * @param dest
   *          the destination array.
   * @param destPos
   *          starting position in the destination data.
   * @param length
   *          the number of array elements to be copied.
   * @exception IndexOutOfBoundsException
   *              if copying would cause access of data outside array bounds.
   * @exception ArrayStoreException
   *              if an element in the <code>src</code> array could not be
   *              stored into the <code>dest</code> array because of a type
   *              mismatch.
   * @exception NullPointerException
   *              if either <code>src</code> or <code>dest</code> is
   *              <code>null</code>.
   */
  public static void arraycopy(final Object src, final int srcPos,
      final Object dest, final int destPos, final int length)
  {
    checkNotNull(src);
    checkNotNull(dest);
    
    if(srcPos < 0 || destPos < 0 || length < 0)
    {
      throw new ArrayStoreException();
    }
    
    if (src instanceof Object[] && dest instanceof Object[])
    {
      arrayCopy0((Object[]) src, srcPos, (Object[]) dest, destPos, length);
    }
    else if (src instanceof byte[] && dest instanceof byte[])
    {
      arrayCopy0((byte[]) src, srcPos, (byte[]) dest, destPos, length);
    }
    else if (src instanceof short[] && dest instanceof short[])
    {
      arrayCopy0((short[]) src, srcPos, (short[]) dest, destPos, length);
    }
    else if (src instanceof int[] && dest instanceof int[])
    {
      arrayCopy0((int[]) src, srcPos, (int[]) dest, destPos, length);
    }
    else if (src instanceof long[] && dest instanceof long[])
    {
      arrayCopy0((long[]) src, srcPos, (long[]) dest, destPos, length);
    }
    else if (src instanceof char[] && dest instanceof char[])
    {
      arrayCopy0((char[]) src, srcPos, (char[]) dest, destPos, length);
    }
    else if (src instanceof float[] && dest instanceof float[])
    {
      arrayCopy0((float[]) src, srcPos, (float[]) dest, destPos, length);
    }
    else if (src instanceof double[] && dest instanceof double[])
    {
      arrayCopy0((double[]) src, srcPos, (double[]) dest, destPos, length);
    }
    else if (src instanceof boolean[] && dest instanceof boolean[])
    {
      arrayCopy0((boolean[]) src, srcPos, (boolean[]) dest, destPos, length);
    }
    else
    {
      throw new ArrayStoreException();
    }
  }
  
  private static void arrayCopy0(final Object[] src, final int srcPos, final Object[] dest,
      final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }

    if (src == dest)
    {
      final Object[] temp = new Object[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }
  
  // byte
  private static void arrayCopy0(final byte[] src, final int srcPos,
      final byte[] dest, final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }
    
    if (src == dest)
    {
      final byte[] temp = new byte[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }
  
  // short
  private static void arrayCopy0(final short[] src, final int srcPos,
      final short[] dest, final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }
    
    if (src == dest)
    {
      final short[] temp = new short[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }
  
  //int
  private static void arrayCopy0(final int[] src, final int srcPos,
      final int[] dest, final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }
    
    if (src == dest)
    {
      final int[] temp = new int[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }
  
  //long
  private static void arrayCopy0(final long[] src, final int srcPos,
      final long[] dest, final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }
    
    if (src == dest)
    {
      final long[] temp = new long[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }
  
  //char
  private static void arrayCopy0(final char[] src, final int srcPos,
      final char[] dest, final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }
    
    if (src == dest)
    {
      final char[] temp = new char[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }
  
  //float
  private static void arrayCopy0(final float[] src, final int srcPos,
      final float[] dest, final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }
    
    if (src == dest)
    {
      final float[] temp = new float[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }
  
  //double
  private static void arrayCopy0(final double[] src, final int srcPos,
      final double[] dest, final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }
    
    if (src == dest)
    {
      final double[] temp = new double[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }
  
  //boolean
  private static void arrayCopy0(final boolean[] src, final int srcPos,
      final boolean[] dest, final int destPos, final int length)
  {
    if (srcPos + length > src.length || destPos + length > dest.length)
    {
      throw new ArrayStoreException();
    }
    
    if (src == dest)
    {
      final boolean[] temp = new boolean[length];
      // src -> temp
      for (int i = 0; i < length; i++)
      {
        temp[i] = src[srcPos + i];
      }
      // temp -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = temp[i];
      }
    }
    else
    {
      // src -> dest
      for (int i = 0; i < length; i++)
      {
        dest[destPos + i] = src[srcPos + i];
      }
    }
  }


}

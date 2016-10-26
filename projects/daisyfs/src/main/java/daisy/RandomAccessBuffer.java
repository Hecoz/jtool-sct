package daisy;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;

public class RandomAccessBuffer
{
  private static final int MAX_POS = 2000;
  private int cursor = 0;
  private final byte[] buffer = new byte[MAX_POS + 1];
  private int max_pos = 0;

  
  public RandomAccessBuffer(final File f, final String string)
  {
    
  }
  
  public void seek(final long n) throws IOException
  {
    if (n < 0)
    {
      throw new IOException();
    }
    cursor = (int) n;
  }
  
  public void writeByte(final byte b) throws IOException
  {
    if (cursor > MAX_POS)
    {
      throw new IOException();
    }
    if (cursor > max_pos)
    {
      max_pos = cursor + 1;
    }
    buffer[cursor] = b;
    cursor++;
  }
  
  public void write(final byte[] b, final int offset, final int length)
      throws IOException
  {
    for (int i = offset, end = offset + length; i < end; ++i)
    {
      writeByte(b[i]);
    }
  }
  
  public long length()
  {
    return max_pos + 1;
  }
  
  public void setLength(final long l) throws IOException
  {
    if (l < 0)
    {
      throw new IOException();
    }
    max_pos = (int) l;
  }
  
  public void read(final byte[] b, final int offset, final int length)
      throws IOException
  {
    for (int i = offset, end = offset + length; i < end; ++i)
    {
      b[i] = readByte();
    }
  }
  
  public byte readByte() throws IOException
  {
    if (cursor > max_pos)
    {
      throw new EOFException();
    }
    final byte res = buffer[cursor];
    ++cursor;
    return res;
  }
  
}

/* 
 * This file is part of the Daisy distribution.  This software is
 * distributed 'as is' without any guarantees whatsoever. It may be
 * used freely for research but may not be used in any commercial
 * products.  The contents of this distribution should not be posted
 * on the web or distributed without the consent of the authors.
 *
 * Authors: Cormac Flanagan, Stephen N. Freund, Shaz Qadeer 
 * Contact: Shaz Qadeer (qadeer@microsoft.com)
 */

package daisy;

import java.io.*;

public class Petal {

  public String petalDir = ".";
  private RandomAccessBuffer disk = null;
  
  private final Daisy daisy;
  
  public Petal(final Daisy daisy)
  {
    this.daisy = daisy;
  }

  public void init(final boolean keepOld)
  {
	    try {
      final File f = null;//new File(petalDir + File.separator + "PETAL");
//	        boolean b = f.exists();
//	        if (!keepOld && b) {
//	            f.delete();
//	            b = false;
//	        }
	        disk = new RandomAccessBuffer(f, "rw");
//	        if (!b) {
      daisy.init();
//	        }
	    } catch (final Exception e) {
	    	e.printStackTrace();
	        assert false : "can't make disk";
	    }
    }

  public synchronized void write(final long n, final byte b)
  {
	try {
	    disk.seek(n);
	    disk.writeByte(b);
	    //Log.log("petal", "write " + n + " " + b);
	} catch (final IOException e) {
		e.printStackTrace();
	}
    }

  public synchronized void write(final long loc, final byte b[], final int n)
  {
	try {
	    disk.seek(loc);
	    disk.write(b, 0, n);
	} catch (final IOException e) {
	    e.printStackTrace();
	}
    }


  public synchronized void read(final long loc, final byte b[], final int n)
  {
	try {
	    final int i;
	    if (loc + n >= disk.length()) {
		disk.setLength(loc + n);
	    }
	    disk.seek(n);
	    disk.read(b, 0, n);
	} catch (final IOException e) {
		e.printStackTrace();
	}
    }

  public synchronized byte read(final long n)
  {
	try {
	    if (n >= disk.length()) {
		return 0;
	    }
	    disk.seek(n);
	    final byte b = disk.readByte();
	    //Log.log("petal", "read " + n + " " + b);	
	    return b;
	} catch (final IOException e) {
	    e.printStackTrace();
	    return 0;
	}
    }

    public synchronized void writeLong(final long n, long num) {
	for (int i = 0; i < 8; i++) {
	    write(n + i, (byte)(num & 0xff));
	    num = num >> 8;
	}
    }

  public synchronized long readLong(final long n)
  {
	long num = 0;
	for (int i = 7; i >= 0; i--) {
	    final byte x = read (n + i);
	    num = (num << 8) + (x & 0xff);
	}
	return num;
    }

}

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

//@ thread_local
class DirectoryEntry {
    public static final int MAXNAMESIZE = 256;
    // 8 bytes for the inodenum
    // 8 bytes for the length of filename (yes, there is some redundancy here)
    // MAXNAMESIZE bytes for the contents of filename
    public static final int ENTRYSIZE = 8 + 8 + MAXNAMESIZE;
    public long inodenum;
    public byte[] filename;
    // On the disk, the contents of the directory entry are stored 
    // in the order: inodenum, filename length, filename contents
}

//@ thread_local
class Directory {
    static public final int DIRSIZE=256;
    public FileHandle file;
    public long size;
    public DirectoryEntry entries[] = new DirectoryEntry[DIRSIZE];
}

public class DaisyDir {
  
  private final Daisy daisy;
  private final DaisyLock dl;
  
  public DaisyDir(final Daisy daisy, final DaisyLock dl)
  {
    this.daisy = daisy;
    this.dl = dl;
  }

    // Private methods

    //@ helper
    static private boolean names_equal(final byte[] a, final byte[] b) {
        if (a.length != b.length)
        {
          return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i])
            {
              return false;
            }
        }
        return true;
    }

  private long readLong(final long inodenum, final int offset)
  {
        final byte b[] = new byte[8];
    final int x = daisy.read(inodenum, offset, 8, b);
        return Utility.bytesToLong(b, 0);
    }

  private int writeLong(final long inodenum, final int offset, final long n)
  {
        final byte b[] = new byte[8];
	Utility.longToBytes(n, b, 0);
    return daisy.write(inodenum, offset, 8, b);
    }

    /*@ performs 
        action "act1" () {
	  \result != Daisy.DAISY_ERR_OK && 
	  \old(DaisyLock.fileLocks)[dir.inodenum] == null &&
	  DaisyLock.fileLocks[dir.inodenum] == null
	}
	[]
	action "act2" (d.file, d.size, d.entries, DaisyLock.fileLocks[dir.inodenum]) {
	  \result == Daisy.DAISY_ERR_OK && 
          dirs[dir.inodenum] &&
	  (\forall int i; 0 <= i && i < d.size && d.entries[i].inodenum != -1 ==>
	                  dirContents[dir.inodenum][d.entries[i].filename] == d.entries[i].inodenum) &&
	  (\forall long f; dirContents[dir.inodenum][f] == -1 ||
 	                   (\exists int i; 0 <= i && i < d.size && d.entries[i].filename == f &&
			                                           d.entries[i].inodenum == 
						                   dirContents[dir.inodenum][f])) &&
	  d.entries != null &&
	  (\forall int i; 0 <= i && i < d.entries.length ==> d.entries[i] != null) &&
          0 <= d.size && d.size < d.entries.length &&
	  \type(DirectoryEntry) == \elemtype(\typeof(d.entries)) &&
	  \old(DaisyLock.fileLocks)[dir.inodenum] == null && DaisyLock.fileLocks[dir.inodenum] == \tid &&
	  d.file == dir 
      }
    */ 
    int openDirectory(final FileHandle dir, final Directory d) {
	final Attribute a = new Attribute();
	int res;
	// Currently, we do not check that dir is indeed a directory.  We assume 
	// that a client of the file system is well-behaved and does not attempt 
	// to create a file in another file.
    dl.lock_file(dir.inodenum);
        d.file = dir;
    res = daisy.get_attr(dir.inodenum, a);
	if (res != Daisy.DAISY_ERR_OK) {
      dl.unlock_file(d.file.inodenum);
	    return res;
	}
        d.size = a.size / DirectoryEntry.ENTRYSIZE;
		//if (d.size >= d.entries.length) d.size = d.entries.length - 1;
		//System.out.println("[DEBUG] attribute size = " + a.size);
//	System.out.println("Size of directory = " + d.size);
        for (int i = 0; i < d.size; i++) {
            d.entries[i] = new DirectoryEntry();
            d.entries[i].inodenum = 
          readLong(dir.inodenum, i * DirectoryEntry.ENTRYSIZE);
	    // We know that we only need a byte to store the size of filename
            final int namesize = 
          (int) readLong(dir.inodenum, i * DirectoryEntry.ENTRYSIZE + 8);
	    final byte[] b = new byte[namesize];
      read(dir, i * DirectoryEntry.ENTRYSIZE + 16, namesize, b);
            d.entries[i].filename = b;
        }
         return Daisy.DAISY_ERR_OK;        
    }

    /*@ performs 
        action "act1" (dirContents[d.file.inodenum][*], DaisyLock.fileLocks[d.file.inodenum]) {
	  \result == Daisy.DAISY_ERR_OK &&
	  (\forall int i; 0 <= i && i < d.size && d.entries[i].inodenum != -1 ==>
	                  dirContents[d.file.inodenum][d.entries[i].filename] == d.entries[i].inodenum) &&
	  DaisyLock.fileLocks[d.file.inodenum] == null
	}
    */
  int closeDirectory(final Directory d)
  {
        for (int i = 0; i < d.size; i++) {
      writeLong(
          d.file.inodenum,
				i * DirectoryEntry.ENTRYSIZE, 
				d.entries[i].inodenum);
      writeLong(
          d.file.inodenum,
			       i * DirectoryEntry.ENTRYSIZE + 8, 
			       d.entries[i].filename.length);
	    final byte[] b = new byte[DirectoryEntry.MAXNAMESIZE];
	    System.arraycopy(d.entries[i].filename, 0, b, 0, d.entries[i].filename.length);
      write(
          d.file,
			   i * DirectoryEntry.ENTRYSIZE + 16, 
			   DirectoryEntry.MAXNAMESIZE,
			   b);
        }
    dl.unlock_file(d.file.inodenum);
        return Daisy.DAISY_ERR_OK;
    }

    // Set of inodenums corresponding to directories encoded as a map from inodenums to boolean
    //@ ghost /*@ guarded_by[i] DaisyLock.fileLocks[i] == \tid */ public static long -> boolean dirs  

    /* Set of files in a directory: inodenum -> filename -> inodenum.
       dirContents[inodenum][filename] provides the inode number of the file named
       filename in the directory with inode number inodenum.  This is meaningful 
       only when dirs[inodenum] is true.  If dirContents[inodenum][filename] == -1 
       then filename does not exist in the directory with inode number inodenum.
    */
    //@ ghost /*@ guarded_by[i] DaisyLock.fileLocks[i] == \tid */ public static long -> long -> long dirContents 

    /*@ performs 
        action "act1" () {
  	  \result != Daisy.DAISY_ERR_OK &&
	  \old(DaisyLock.fileLocks)[dir.inodenum] == null &&
	  DaisyLock.fileLocks[dir.inodenum] == null
	}
	[]
        action "act2" (dirContents[dir.inodenum][filename], DaisyDisk.inodeUsed[fh.inodenum], fh.inodenum) {
	  (       \old(dirContents)[dir.inodenum][filename] == -1 &&      
	      dirContents[dir.inodenum][filename] == fh.inodenum &&
	      \old(DaisyLock.fileLocks)[dir.inodenum] == null && DaisyLock.fileLocks[dir.inodenum] == null &&
	     !\old(DaisyDisk.inodeUsed)[fh.inodenum] && DaisyDisk.inodeUsed[fh.inodenum] )
       };
       requires dir != fh
    */
  public int creat(/*@ non_null */final FileHandle dir,
			    /*@ non_null */ final byte[] filename, 
			    /*@ non_null */ final FileHandle fh) {
        final Directory d = new Directory();

    final int res = openDirectory(dir, d);

        if (res != Daisy.DAISY_ERR_OK) {
	    //@ set \witness = "act1"
	    return res;
        }

	int new_entry = (int)d.size;
        for (int i = 0; i < d.size; i++) {
            if (d.entries[i].inodenum != -1) {
		if (names_equal(filename, d.entries[i].filename)) {
          closeDirectory(d);
		    //@ set \witness = "act1"
		    return Daisy.DAISY_ERR_EXIST;
		}
	    } else {
		new_entry = i;
	    }
        }

        if (new_entry == Directory.DIRSIZE) {
      closeDirectory(d);
	    //@ set \witness = "act1"
            return Daisy.DAISY_ERR_NOSPC;
        }
    final long inodenum = daisy.creat();
        if (inodenum < 0) {
      closeDirectory(d);
	    //@ set \witness = "act1"
            return (int)inodenum;
        }

        d.entries[new_entry] = new DirectoryEntry();
        d.entries[new_entry].inodenum = inodenum;
        d.entries[new_entry].filename = filename;

        fh.inodenum = inodenum;

	if (new_entry == d.size) {
	    d.size++;        
	}

	//@ set \witness = "act2"
    return closeDirectory(d);
    }

  public int read_dir(final FileHandle dir,
			       final int cookie, 
			       final byte b[]) {
        final Directory d = new Directory();
    int res = openDirectory(dir, d);
        if (res != Daisy.DAISY_ERR_OK) {
	    return res;
        }

        for (int i = cookie; i < d.size; i++) {
            if (d.entries[i].inodenum != -1) {
                for (int j = 0; j < DirectoryEntry.MAXNAMESIZE; j++) {
                    b[j] = d.entries[i].filename[j];                
                }
        res = closeDirectory(d);
                if (res != Daisy.DAISY_ERR_OK) {
		    return res;
		} else {
		    return i + 1;
		}
            }
        }
    closeDirectory(d);
        return Daisy.DAISY_ERR_NOENT;
    }

  public int lookup(final FileHandle dir,
			     final byte[] filename, 
			     final FileHandle fh) {
        final Directory d = new Directory();
    final int res = openDirectory(dir, d);
        if (res != Daisy.DAISY_ERR_OK) {
                return res;
        }
        for (int i = 0; i < d.size; i++) {
            if (d.entries[i].inodenum != -1 && 
		names_equal(filename, d.entries[i].filename)) {
                fh.inodenum = d.entries[i].inodenum;
        return closeDirectory(d);
            }
        }
    closeDirectory(d);
        return Daisy.DAISY_ERR_NOENT;
    }

  public int unlink(final FileHandle dir,
			     final byte[] filename) {
        final Directory d = new Directory();
    final int res = openDirectory(dir, d);
        if (res != Daisy.DAISY_ERR_OK) {
                return res;
        }
        for (int i = 0; i < d.size; i++) {
            if (d.entries[i].inodenum != -1 && 
		names_equal(filename, d.entries[i].filename)) {
                final long t = d.entries[i].inodenum;
                d.entries[i].inodenum = -1;
        daisy.unlink(t);
        return closeDirectory(d);
            }
        }
    closeDirectory(d);
        return Daisy.DAISY_ERR_NOENT;
    }

  public int write(final FileHandle file,
			    final int offset, 
			    final int size, 
			    final byte b[]) {
    return daisy.write(file.inodenum, offset, size, b);
    }

  public int read(final FileHandle file,
			   final int offset, 
			   final int size, 
			   final byte b[]) {
    return daisy.read(file.inodenum, offset, size, b);
    }

  public int get_attr(final FileHandle file, final Attribute a)
  {
    return daisy.get_attr(file.inodenum, a);
    }

  public int set_attr(final FileHandle file, final Attribute a)
  {
    return daisy.set_attr(file.inodenum, a);
    }
}

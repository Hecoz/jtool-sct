package daisy;

import org.jtool.test.ConcurrencyTestCase;

public class DaisyTest implements ConcurrencyTestCase
{
  
  private static class Tester implements Runnable
  {
    private final int id;
    
    private final FileHandle root;
    private final FileHandle topdir;
    private final FileHandle topfile;
    private final DaisyDir daisyDir;
    
    public Tester(final int id, final FileHandle root, final FileHandle tdir,
        final FileHandle tfile, final DaisyDir daisyDir)
    {
      this.id = id;
      
      this.root = root;
      this.topdir = tdir;
      this.topfile = tfile;
      this.daisyDir = daisyDir;
    }
    
    public void run()
    {
      
      //      System.out.println("Writing the string 'test' into file");
      daisyDir.write(topfile, 2 * id, 2, new byte[] { 't', 'e' });
      
      //      System.out.println("Reading the string 'test' from file");
      daisyDir.read(topfile, id, 2, new byte[2]);
      
      //      final FileHandle file1 = new FileHandle();
      
      //      System.out.println("Creating new file in the directory");
      //      DaisyDir.creat(topdir, stringToBytes("file" + id), file1);
      //      
      //      //      System.out.println("Reading the string 'test' from file");
      ////      DaisyDir.read(file1, 0, 4, new byte[4]);
      //      
      //      //      System.out.println("Writing the string 'test' into file");
      //      DaisyDir.write(file1, 2, 4, stringToBytes("test"));
      //      
      //      //      System.out.println("Reading the string 'test' from file");
      //      DaisyDir.read(topfile, 0, 4, new byte[4]);
      //      
      //      //      System.out.println("Writing the string 'test' into file");
      ////      DaisyDir.write(topfile, 8 + 4 * id, 4, stringToBytes("test"));
      //      
      //      //      System.out.println("Deleting file from the top level directory");
      //      DaisyDir.unlink(topdir, stringToBytes("file" + id));
      
      /*
      System.out.println("Disk contents:");
      Daisy.dumpDisk();
      */
    }
  }
  
  private final int threadCount;
  
  public DaisyTest(final int threadCount)
  {
    this.threadCount = threadCount;
  }

  public static void main(final String[] args) throws Exception
  {
    new DaisyTest(2).execute();
  }
  
  static byte[] stringToBytes(final String s)
  {
    final byte[] b = new byte[s.length()];
    for (int i = 0; i < s.length(); i++)
    {
      b[i] = (byte) s.charAt(i);
    }
    return b;
    }

  public void execute() throws Exception
  {
    
    final FileHandle root = new FileHandle();
    final LockManager lm = new LockManager();
    final DaisyLock dl = new DaisyLock(lm);
    final DaisyDisk dd = new DaisyDisk();
    final Daisy daisy = new Daisy(dl, dd);
    final Petal petal = new Petal(daisy);
    dd.setPetal(petal);
    final DaisyDir daisyDir = new DaisyDir(daisy, dl);
    
    root.inodenum = 0;
    petal.init(false);
    
    //System.out.println("Disk contents:");
    //Daisy.dumpDisk();
    
    //        System.out
    //            .println("Creating top level directory in the root directory");
    final FileHandle topdir = new FileHandle();
    //        DaisyDir.creat(root, stringToBytes("dir"), topdir);
    
    //        System.out.println("Creating file in the top level directory");
    final FileHandle topfile = new FileHandle();
    daisyDir.creat(root, stringToBytes("file"), topfile);
    
    final int count = threadCount;
    final Thread[] threads = new Thread[count];
    
    for (int i = 0; i < count; i++)
    {
      threads[i] = new Thread(new Tester(i, root, topdir, topfile, daisyDir));
      threads[i].start();
    }
    
    for (int i = 0; i < count; i++)
    {
      threads[i].join();
    }
  }

}

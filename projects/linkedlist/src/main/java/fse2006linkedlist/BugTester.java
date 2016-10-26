package fse2006linkedlist;

//BugTester.java
//implements two threads that are building the same list
//and are conflicting each other next pointer in the latency between
//fetch and write back

import org.jtool.test.ConcurrencyTestCase;

public class BugTester implements ConcurrencyTestCase
{
  private final int threads;
  private final int listsize;
  
  public BugTester(final int threads, final int listsize)
  {
    this.threads = threads;
    this.listsize = listsize;
  }

  public static void main(final String[] args) throws Exception
  {
    new BugTester(2, 6).execute();
  }
  
  public void execute() throws Exception
  {
    final int builders = threads;
    final int maxsize = listsize;
    
    final int step = maxsize / builders;
    final Thread[] threads = new Thread[builders];
    
    try
    {
      final MyLinkedList mlst = new MyLinkedList(maxsize);
      MyListBuilder mlist = null;
      
      for (int i = 0; i < builders; i++)
      {
        mlist = new MyListBuilder(mlst, i * step, (i + 1) * step, true);
        threads[i] = new Thread(mlist);
      }
      
      for (int i = 0; i < builders; i++)
      {
        threads[i].start();
      }
      
      for (int i = 0; i < builders; i++)
      {
        threads[i].join();
      }
      
      mlist.print(); //prints results to output file
      
      mlist.empty(); //empties list
    }
    catch (final InterruptedException e)
    {
      throw new RuntimeException("interrupted exception");
    }
  }


}

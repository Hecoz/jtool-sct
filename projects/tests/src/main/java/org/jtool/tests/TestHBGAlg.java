package org.jtool.tests;

import org.jtool.loader.NoInstrument;
import org.jtool.runtime.ExecutionManager;
import org.jtool.test.ConcurrencyTestCase;
//import org.junit.Test;

//import static org.junit.Assert.*;

@NoInstrument
public class TestHBGAlg
{
//  @Test
//  public void testMemOps()
//  {
//      final ExecutionManager em1 = new ExecutionManager();
//      em1.doHBGAlg = true;
//      
//      em1.setTestCase(new ConcurrencyTestCase()
//      {
//        public void execute() throws Exception
//        {
//          new MemOps().execute();
//        }
//      });
//      
//    em1.execute(300);
//
//
//      final ExecutionManager em2 = new ExecutionManager();
//      em2.dpor = true;
//      
//      em2.setTestCase(new ConcurrencyTestCase()
//      {
//        public void execute() throws Exception
//        {
//          new MemOps().execute();
//        }
//      });
//      
//    em2.execute(300);
//    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }
//  
//  @Test
//  public void testMemOps3()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.doHBGAlg = true;
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOps3().execute();
//      }
//    });
//    
//    em1.execute(1000);
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    em2.dpor = false;
//    em2.sleepSets = false;
//    em2.hbgCaching = true;
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOps3().execute();
//      }
//    });
//    
//    em2.execute(10000);
//    System.out.println(em1.getNumTerminalHashes());
//    System.out.println(em2.getNumTerminalHashes());
//    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }
//  
//  @Test
//  public void testMemOpsSS()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    //    em1.doHBGAlg = true;
//    em1.dpor = true;
//    em1.dfs = true;
//    em1.sleepSets = true;
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOpsSS().execute();
//      }
//    });
//    
//    em1.execute(300);
//    //    final ExecutionManager em2 = new ExecutionManager();
//    //    em2.dpor = true;
//    //    
//    //    em2.setTestCase(new ConcurrencyTestCase()
//    //    {
//    //      public void execute() throws Exception
//    //      {
//    //        new MemOpsSS().execute();
//    //      }
//    //    });
//    //    
//    //    em2.execute(300);
//    //    
//    //    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }
//  
//  @Test
//  public void testMemOps2()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.doHBGAlg = true;
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOps2().execute();
//      }
//    });
//    
//    em1.execute(300);
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    em2.dpor = true;
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOps2().execute();
//      }
//    });
//    
//    em2.execute(300);
//    
//    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }
//  
//  @Test
//  public void testMemOpsIndep()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.doHBGAlg = true;
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOpsIndep().execute();
//      }
//    });
//    
//    em1.execute(300);
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    em2.hbgCaching = true;
//    em2.dfs = false;
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOpsIndep().execute();
//      }
//    });
//    
//    em2.execute(10000);
//    
//    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }
//  
//  @Test
//  public void testMemOpsProtected()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.doHBGAlg = true;
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOpsProtected().execute();
//      }
//    });
//    
//    em1.execute(300);
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    em2.hbgCaching = true;
//    em2.dfs = false;
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOpsProtected().execute();
//      }
//    });
//    
//    em2.execute(10000);
//    
//    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }
//  
//  @Test
//  public void testRdRdWr()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.doHBGAlg = true;
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new RdRdWr().execute();
//      }
//    });
//    
//    em1.execute(300);
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    em2.dpor = true;
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new RdRdWr().execute();
//      }
//    });
//    
//    em2.execute(10000);
//    
//    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }
//  
//  @Test
//  public void testRdWrWr()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.doHBGAlg = true;
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new RdWrWr().execute();
//      }
//    });
//    
//    em1.execute(300);
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    em2.dpor = true;
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new RdWrWr().execute();
//      }
//    });
//    
//    em2.execute(10000);
//    
//    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }
//  
//  @Test
//  public void testMemOpsAll()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.doHBGAlg = true;
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOpsAll().execute();
//      }
//    });
//    
//    em1.execute(300);
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    //    em2.hbgCaching = true;
//    em2.dpor = true;
//    em2.dfs = true;
//    //    em2.sleepSets = true;
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new MemOpsAll().execute();
//      }
//    });
//    
//    em2.execute(10000);
//    
//    System.out.println(em1.getNumTerminalHashes());
//    System.out.println(em2.getNumTerminalHashes());
//    assertTrue(em1.getTerminalHashes().equals(em2.getTerminalHashes()));
//  }

}

class MemOps extends FourThreadedTestCase
{
  public int s = 0;
  public int t = 0;
  public int u = 0;
  public int v = 0;
  
  @Override
  public void thread1()
  {
    if (s != 0)
    {
      t = 1;
    }
  }
  
  @Override
  public void thread2()
  {
    //    if (u > 0 && t > 0)
    //    {
    //      u++;
    //    }
    //    t++;
    s = 1;
  }

  @Override
  public void thread3()
  {
    if (s != 0)
    {
      v = 1;
    }
    //    u++;
  }
  
  @Override
  public void thread4()
  {
    //    s++;
    //    if (u != 0)
    //    {
    //      t++;
    //    }
  }
  
}

class MemOpsAll extends FourThreadedTestCase
{
  public int s = 0;
  public int t = 0;
  public int u = 0;
  public int v = 0;
  
  @Override
  public void thread1()
  {
    if (s != 0)
    {
      t = 1;
    }
  }
  
  @Override
  public void thread2()
  {
    final int x = u;
    final int y = t;
    
    //    if (u > 0 && t > 0)
    //    {
    //    u = 1;
    //    }
    //    t = 1;
    s = 1;
  }
  
  @Override
  public void thread3()
  {
    //    final int x = s;
    //    if (s != 0)
    //    {
    //    v = 1;
    //    }
    u = 1;
    t = 2;
  }
  
  @Override
  public void thread4()
  {
    //    s++;
    if (u != 0)
    {
      s = 1;
    }
    //    if (u != 0)
    //    {
    //      t++;
    //    }
  }
  
}

class RdRdWr extends FourThreadedTestCase
{
  public int s = 0;
  public int t = 0;
  public int u = 0;
  public int v = 0;
  
  @Override
  public void thread1()
  {
    //    if (s != 0)
    //    {
    //      t = 1;
    //    }
    final int x = s;
    //    t = 1;
  }
  
  @Override
  public void thread2()
  {
    final int x = s;
    //    final int x = u;
    //    t = 2;
  }
  
  @Override
  public void thread3()
  {
    s = 1;
    //    v = 1;
    //    u = 1;
  }
  
  @Override
  public void thread4()
  {

  }
  
}

class RdWrWr extends FourThreadedTestCase
{
  public int s = 0;
  public int t = 0;
  public int u = 0;
  public int v = 0;
  
  @Override
  public void thread1()
  {
    //    if (s != 0)
    //    {
    //      t = 1;
    //    }
    final int x = s;
    //    t = 1;
  }
  
  @Override
  public void thread2()
  {
    s = 1;
    //    final int x = u;
    //    t = 2;
  }
  
  @Override
  public void thread3()
  {
    s = 2;
    //    v = 1;
    //    u = 1;
  }
  
  @Override
  public void thread4()
  {
    
  }
  
}

class MemOpsSS extends FourThreadedTestCase
{
  public int s = 0;
  public int t = 0;
  public int u = 0;
  public int v = 0;
  
  public int x = 0;
  public int y = 0;
  public int z = 0;

  @Override
  public void thread1()
  {
    x = 1;
  }
  
  @Override
  public void thread2()
  {
    s = y;
    t = x;
  }
  
  @Override
  public void thread3()
  {
    u = z;
    v = x;
  }
  
  @Override
  public void thread4()
  {
    //    s++;
    //    if (u != 0)
    //    {
    //      t++;
    //    }
  }
  
  
  
}

class MemOps2 extends FourThreadedTestCase
{
  public volatile int s = 0;
  public volatile int t = 0;
  public volatile int u = 0;
  public volatile int v = 0;
  
  @Override
  public void thread1()
  {
    final int x = u + t;
    s = 1;
  }
  
  @Override
  public void thread2()
  {
    u = 1;
  }
  
  @Override
  public void thread3()
  {
    s = 1;
    final int x = u;
    t = 1;
  }
  
  @Override
  public void thread4()
  {

  }

}

class MemOps3 extends FourThreadedTestCase
{
  public volatile int s = 0;
  public volatile int t = 0;
  public volatile int u = 0;
  public volatile int v = 0;
  
  @Override
  public void thread1()
  {
    //    if (s != 0)
    //    {
    //      t = 1;
    //    }
    final int x = s;
    //    t = 1;
  }
  
  @Override
  public void thread2()
  {
    final int x = u;
    //    x += t;
    //    u = 2;
    //    t = 2;
    s = 2;
//    if (u > 0 && t > 0)
//    {
//      u++;
//    }
//    t++;
//    s = 1;
  }
  
  @Override
  public void thread3()
  {
    //    if (s != 0)
    //    {
    //      v = 1;
    //    }
    u = 2;
  }
  
  @Override
  public void thread4()
  {
    //    s++;
    //    if (u != 0)
    //    {
    //      t++;
    //    }
  }
  
}

class MemOpsIndep extends FourThreadedTestCase
{
  public volatile int x = 0;
  public volatile int y = 0;
  
  @Override
  public void thread1()
  {
    x = 1;
  }
  
  @Override
  public void thread2()
  {
    x = 2;
  }
  
  @Override
  public void thread3()
  {
    y = 1;
  }
  
  @Override
  public void thread4()
  {
    y = 2;
  }
  
}

class MemOpsProtected extends TwoThreadedTestCase
{
  public volatile int x = 0;
  public volatile int y = 0;
  
  @Override
  public void thread1()
  {
    x = 1;
    y = 1;
  }
  
  @Override
  public void thread2()
  {
    y = 2;
    x = 2;
  }
  
}

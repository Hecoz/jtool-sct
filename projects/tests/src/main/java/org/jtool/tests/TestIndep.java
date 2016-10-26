/**
 * 
 */
package org.jtool.tests;

//import static org.junit.Assert.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jtool.loader.NoInstrument;
import org.jtool.runtime.ExecutionManager;
import org.jtool.test.ConcurrencyTestCase;
//import org.junit.Test;

/**
 * @author pt1110
 *
 */
@NoInstrument
public class TestIndep
{
  
//  public static void main(final String[] args)
//  {
//    new TestIndep().testIndepWrites();
//  }
//
//  @Test
//  public void testIndepWrites()
//  {
//    final ExecutionManager em = new ExecutionManager();
//    
//    em.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new IndependentWrites().execute();
//      }
//    });
//    
//    em.execute();
//    em.outputGraph("testIndepWrites");
//    
//    //assertEquals(1, em.getNumExecutions());
//  }
//  
//  @Test
//  public void testDepWrites()
//  {
//    final ExecutionManager em = new ExecutionManager();
//    em.dpor = false;
//    em.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new DependentWrites().execute();
//      }
//    });
//    
//    em.execute();
//    em.outputGraph("testDepWrites");
//    
//    //assertEquals(3, em.getNumExecutions());
//    assertEquals(2, em.getNumTerminalHashes());
//  }
//  
//  @Test
//  public void testDPORExample()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.hbgCaching = false;
//    em1.dpor = false;
//    em1.doHBGAlg = true;
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new DPORExample().execute();
//      }
//    });
//    
//    em1.execute();
//    em1.outputGraph("testDPORExample1");
//    
//    //assertEquals(3, em1.numUniqueTerminalStates);
//    
//    //    final ExecutionManager em2 = new ExecutionManager();
//    //    em2.hbgCaching = false;
//    //    em2.dpor = false;
//    //    em2.setTestCase(new ConcurrencyTestCase()
//    //    {
//    //      public void execute() throws Exception
//    //      {
//    //        new DPORExample().execute();
//    //      }
//    //    });
//    //    
//    //    em2.execute();
//    //    em2.outputGraph("testDPORExample2");
//    
//    
//    //assertTrue(em1.getNumExecutions() < em2.getNumExecutions());
//  }
//  
//  @Test
//  public void testReads()
//  {
//    final ExecutionManager em = new ExecutionManager();
//    
//    em.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new Reads().execute();
//      }
//    });
//    
//    em.dpor = true;
//    em.dfs = true;
//    em.sleepSets = true;
//
//    em.execute();
//    em.outputGraph("testReads");
//    
//    assertEquals(1, em.getNumTerminalHashes());
//    assertEquals(1, em.getNumExecutions());
//  }
//  
//  @Test
//  public void testThreeReadsOneWrite()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new ThreeReadsOneWrite().execute();
//      }
//    });
//    
//    em1.execute();
//    //em1.outputGraph("testThreeReadsOneWrite1");
//    
//    
//    /*
//     *  W R1 R2
//     *  R1 W R2
//     *  R2 W R1
//     *  R1 R2 W
//     */
//    assertEquals(4, em1.getNumTerminalHashes());
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    em2.dpor = false;
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new ThreeReadsOneWrite().execute();
//      }
//    });
//    
//    em2.execute(em1.getNumExecutions() + 1);
//    //em2.outputGraph("testThreeReadsOneWrite2");
//    
//    assertTrue(em2.getNumExecutions() == em1.getNumExecutions() + 1);
//  }
//  
//  @Test
//  public void testLocks()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    //em1.doHBGAlg = true;
//    em1.dpor = true;
//    em1.dfs = true;
//    em1.lazyLocks = true;
//    //em1.sleepSets = true;
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new Locks().execute();
//      }
//    });
//    
//    em1.execute(30);
//    //    em1.outputGraph("testLocks1");
//    
//    //    assertEquals(2, em1.numUniqueTerminalStates);
//    //
//    //    
//    //    final ExecutionManager em2 = new ExecutionManager();
//    //    em2.hbgCaching = false;
//    //    
//    //    em2.setTestCase(new ConcurrencyTestCase()
//    //    {
//    //      public void execute() throws Exception
//    //      {
//    //        new Locks().execute();
//    //      }
//    //    });
//    //    
//    //    //    em2.execute(em1.getNumExecutions() + 1);
//    //    //    em2.outputGraph("testLocks2");
//    //    //    assertTrue(em1.getNumExecutions() + 1 == em2.getNumExecutions());
//    //    System.out.println("Num terminal hashes: " + em1.getNumTerminalHashes());
//    //    System.out.println("Num terminal hashes without locks: "
//    //        + em1.getNumTerminalHashesWithoutLocks());
//
//  }
//  
//  @Test
//  public void testLocks2()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        final LockRemovalSimple lrs = new LockRemovalSimple();
//        lrs.execute();
//      }
//    });
//    
//    em1.execute();
//    em1.outputGraph("testLocks2");
//    
//    assertEquals(6, em1.getNumTerminalHashes());
//  }
//  
//  @Test
//  public void testLockRemovalSimple()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.lazyLocks = true;
//    em1.dpor = true;
//    em1.dfs = true;
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        final LockRemovalSimple lrs = new LockRemovalSimple();
//        lrs.execute();
//      }
//    });
//    
//    em1.execute();
//    em1.outputGraph("testLockRemovalSimpleOn");
//    
//    //    assertEquals(1, em1.getNumExecutions());
//    //    
//    //    
//    //    final ExecutionManager em2 = new ExecutionManager();
//    //    
//    //    em2.setTestCase(new ConcurrencyTestCase()
//    //    {
//    //      public void execute() throws Exception
//    //      {
//    //        final LockRemovalSimple lrs = new LockRemovalSimple();
//    //        lrs.execute();
//    //      }
//    //    });
//    //    
//    //    em2.execute();
//    //    em2.outputGraph("testLockRemovalSimpleOff");
//    //    
//    //    assertTrue(em1.getNumExecutions() < em2.getNumExecutions());
//    //    assertEquals(6, em2.numUniqueTerminalStates);
//    //    
//    //    System.out.println("With lock removal:    " + em1.numUniqueTerminalStates);
//    //    System.out.println("Without lock removal: " + em2.numUniqueTerminalStates);
//    
//  }
//  
//  @Test
//  public void testLockRemovalSimple2()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.lazyLocks = true;
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new LockRemovalSimple2().execute();
//      }
//    });
//    
//    em1.execute();
//    em1.outputGraph("testLockRemovalSimple2On");
//    
//    assertEquals(2, em1.getNumTerminalHashes());
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new LockRemovalSimple2().execute();
//      }
//    });
//    
//    em2.execute();
//    em2.outputGraph("testLockRemovalSimple2Off");
//    
//    System.out.println("With lock removal:    " + em1.getNumTerminalHashes());
//    System.out.println("Without lock removal: " + em2.getNumTerminalHashes());
//
//    assertEquals(2, em2.getNumTerminalHashes());
//    
//    
//  }
//  
//  @Test
//  public void testLockRemovalSet()
//  {
//    final ExecutionManager em1 = new ExecutionManager();
//    em1.lazyLocks = true;
//    em1.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new LockRemovalSet().execute();
//      }
//    });
//    
//    em1.execute();
//    em1.outputGraph("testLockRemovalSetOn");
//    
//    
//    
//    final ExecutionManager em2 = new ExecutionManager();
//    
//    em2.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new LockRemovalSet().execute();
//      }
//    });
//    
//    em2.execute();
//    em2.outputGraph("testLockRemovalSetOff");
//    
//    System.out.println("With lock removal:    " + em1.getNumTerminalHashes());
//    System.out.println("Without lock removal: " + em2.getNumTerminalHashes());
//
//    assertTrue(em1.getNumTerminalHashes() < em2.getNumTerminalHashes());
//    
//
//  }

}


class LockRemovalSet extends TwoThreadedTestCase
{
  public Set<Integer> set = Collections.synchronizedSet(new HashSet<Integer>());
  public boolean t1;
  public boolean t2;
  
  @Override
  public void thread1()
  {
    set.add(1);
    t1 = set.contains(2);
  }
  
  @Override
  public void thread2()
  {
    t2 = set.contains(1);
    t2 = set.contains(4);
    set.add(2);
  }
  
}

class LockRemovalSimple2 extends TwoThreadedTestCase
{
  public int t1g = 0;
  public int t2g = 0;
  public int x = 0;
  public int y = 0;
  public Object mutex = new Object();
  
  @Override
  public void thread1()
  {
    int t1;
    synchronized (mutex)
    {
      x = 1;
      t1 = y;
      x = y;
      y = t1;
      t1g = t1;
    }
    
  }
  
  @Override
  public void thread2()
  {
    final int t2;
    synchronized (mutex)
    {
      x = 1;
      t2 = y;
      x = y;
      y = t2;
      t2g = t2;
    }
  }
  
}

class LockRemovalSimple extends TwoThreadedTestCase
{
  public int t1g = 0;
  public int t2g = 0;
  public int x = 0;
  public int y = 0;
  public Object mutex = new Object();
  
  @Override
  public void thread1()
  {
    int t1;
    synchronized (mutex)
    {
      t1 = x;
    }
    
    synchronized (mutex)
    {
      t1g = t1;
    }

  }
  
  @Override
  public void thread2()
  {
    final int t2;
    synchronized (mutex)
    {
      t2 = y;
    }
    
    synchronized (mutex)
    {
      t2g = t2;
    }
  }
  
}

class Locks extends FourThreadedTestCase
{
  public int x = 0;
  public int y = 0;
  public int t3 = 0;
  public int t4 = 0;
  public Object mutex;
  
  public Locks()
  {
    super();
    mutex = new Integer(0);
  }
  
  @Override
  public void thread1()
  {
    synchronized (mutex)
    {
      x = 2;
    }
  }
  
  @Override
  public void thread2()
  {
    synchronized (mutex)
    {
      x = 3;
    }
  }
  
  @Override
  public void thread3()
  {
    t3 = 1;
  }
  
  @Override
  public void thread4()
  {
    t4 = 1;
  }
  
}

class ThreeReadsOneWrite extends FourThreadedTestCase
{
  public int x = 0;
  public int t2 = 0;
  public int t3 = 0;
  public int t4 = 0;
  
  @Override
  public void thread1()
  {
    x = 2;
  }
  
  @Override
  public void thread2()
  {
    t2 = x;
  }

  @Override
  public void thread3()
  {
    t3 = x;
  }
  
  @Override
  public void thread4()
  {
  }

}

class Reads extends TwoThreadedTestCase
{
  public int x = 0;
  public int y = 0;
  public int z = 0;
  
  @Override
  public void thread1()
  {
    // read x
    y = x + 1;
  }
  
  @Override
  public void thread2()
  {
    // read x
    z = x * 2;
  }
  
}

class DPORExample extends TwoThreadedTestCase
{
  public int x = 0;
  public int y = 0;
  
  @Override
  public void thread1()
  {
    x = 1;
    x = 2;
  }
  
  @Override
  public void thread2()
  {
    y = 1;
    x = 3;
  }
  
}

class IndependentWrites extends TwoThreadedTestCase
{
  public int x = 0;
  public int y = 0;
  
  @Override
  public void thread1()
  {
    x = 1;
  }
  
  @Override
  public void thread2()
  {
    y = 1;
  }
  
}

class DependentWrites extends TwoThreadedTestCase
{
  public int x = 0;
  
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
  
}

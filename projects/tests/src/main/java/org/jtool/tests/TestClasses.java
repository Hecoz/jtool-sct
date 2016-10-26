package org.jtool.tests;

//import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Vector;

import org.jtool.loader.NoInstrument;
import org.jtool.runtime.ExecutionManager;
import org.jtool.test.ConcurrencyTestCase;
//import org.junit.Test;

@NoInstrument
public class TestClasses
{
//  @Test
//  public void testArrayList()
//  {
////    final ExecutionManager em = new ExecutionManager();
////    em.lazyLocks = true;
////    em.setTestCase(new ConcurrencyTestCase()
////    {
////      public void execute() throws Exception
////      {
////        new UsesArrayList().execute();
////      }
////    });
////    
////    em.execute();
////    
////    em.outputGraph("testArrayList");
//
//    //assertEquals(1, em.getNumExecutions());
//  }
//  
//  @Test
//  public void testArrayListNativeCopy()
//  {
////    final ExecutionManager em = new ExecutionManager();
////    
////    em.setTestCase(new ConcurrencyTestCase()
////    {
////      public void execute() throws Exception
////      {
////        new UseArrayCopy().execute();
////      }
////    });
////    
////    em.execute();
////    
////    assertEquals(4, em.getNumTerminalHashes());
//  }
}

class UseArrayCopy extends TwoThreadedTestCase
{
  public int[] array = new int[2];
  public int[] array2 = new int[2];
  
  public UseArrayCopy()
  {
    array[0] = 1;
    array[1] = 2;
    array2[0] = 0;
    array2[1] = 0;
  }
  
  @Override
  public void thread1()
  {
    System.arraycopy(array, 0, array2, 0, 2);
  }
  
  @Override
  public void thread2()
  {
    System.arraycopy(array, 0, array2, 0, 2);
  }
  
}

class UsesArrayList extends TwoThreadedTestCase
{
  public Vector<Integer> list = new Vector<Integer>();
  public int t2hc = 0;
  
  public UsesArrayList()
  {
    list.add(1);
    list.add(2);
  }

  @Override
  public void thread1()
  {
    final boolean c1 = list.contains(2);
    if (c1)
    {
      list.add(3);
    }

  }
  
  @Override
  public void thread2()
  {
    for (int i = 0; i < list.size(); i++)
    {
      final Integer element = list.get(i);
      final int hc = element.hashCode();
      t2hc = hc;
    }
  }
  
}

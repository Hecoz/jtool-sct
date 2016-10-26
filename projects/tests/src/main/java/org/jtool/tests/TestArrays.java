package org.jtool.tests;

//import static org.junit.Assert.assertEquals;

import org.jtool.loader.NoInstrument;
import org.jtool.runtime.ExecutionManager;
import org.jtool.test.ConcurrencyTestCase;
//import org.junit.Test;

@NoInstrument
public class TestArrays
{
//  @Test
//  public void testArrays1()
//  {
//    final ExecutionManager em = new ExecutionManager();
//    
//    em.setTestCase(new ConcurrencyTestCase()
//    {
//      public void execute() throws Exception
//      {
//        new TestArrays1().execute();
//      }
//    });
//    
//    em.dpor = true;
//    em.dfs = true;
//    em.sleepSets = true;
//
//    em.execute();
//    em.outputGraph("testArrays1");
//    
//    assertEquals(2, em.getNumTerminalHashes());
//  }
}

class TestArrays1 extends TwoThreadedTestCase
{
  public int[] array = new int[3];
  
  @Override
  public void thread1()
  {
    array[0] = 1;
    array[2] = 2;
  }
  
  @Override
  public void thread2()
  {
    array[1] = 1;
    array[2] = 3;
  }
  
}

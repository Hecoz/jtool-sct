package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestBarrier implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new FourThreadedTestCase()
    {
      public volatile int w = 0;
      public volatile int x = 0;
      public volatile int y = 0;
      public volatile int z = 0;
      
      public Barrier b = new Barrier(4);
      
      @Override
      public void thread1()
      {
        w = 1;
        b.DoBarrier(0);
        x = 1;
        b.DoBarrier(0);
        y = 1;
      }
      
      @Override
      public void thread2()
      {
        x = 1;
        
        w = 1;
        
        b.DoBarrier(1);
        y = 1;
        b.DoBarrier(1);
        z = 1;
      }

      @Override
      public void thread3()
      {
        y = 1;
        b.DoBarrier(2);
        z = 1;
        b.DoBarrier(2);
        w = 1;
      }

      @Override
      public void thread4()
      {
        z = 1;
        b.DoBarrier(3);
        w = 1;
        b.DoBarrier(3);
        x = 1;
      }
    }.execute();
  }
}

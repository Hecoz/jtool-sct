package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestLS4 implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new FourThreadedTestCase()
    {
      public volatile int x;
      public volatile int y;
      public volatile int z;
      public volatile Object m = new Object();
      public volatile Object n = new Object();
      
      @Override
      public void thread1()
      {
        Thread.currentThread();
        synchronized (m)
        {
          x = 1;
        }
      }
      
      @Override
      public void thread2()
      {
        synchronized (m)
        {
          z = 1;
        }
      }
      
      @Override
      public void thread3()
      {
        synchronized (n)
        {
          y = 1;
        }

        x = 3;

      }
      
      @Override
      public void thread4()
      {
        
      }
    }.execute();
  }
}

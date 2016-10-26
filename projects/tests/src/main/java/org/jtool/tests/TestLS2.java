package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestLS2 implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new FourThreadedTestCase()
    {
      public volatile int x;
      public volatile int y;
      public volatile int z;
      public volatile Integer m = new Integer(0);
      
      @Override
      public void thread1()
      {
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
        y = 1;
      }
      
      @Override
      public void thread3()
      {
        if (y == 1)
        {
          x = 2;
        }

      }
      
      @Override
      public void thread4()
      {
        
      }
    }.execute();
  }
}

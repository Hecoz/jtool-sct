package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestLS3 implements ConcurrencyTestCase
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
          // yield?
          x = 1;
        }
      }
      
      @Override
      public void thread2()
      {
        x = 2;
      }
      
      @Override
      public void thread3()
      {
        // yield?
        synchronized (m)
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

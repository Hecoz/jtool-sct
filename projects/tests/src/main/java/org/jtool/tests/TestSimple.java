package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestSimple implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new TwoThreadedTestCase()
    {
      public volatile int x = 0;
      public volatile int y = 0;
      public volatile int z = 0;
      
      public volatile int a = 0;
      public volatile int b = 0;
      public volatile int c = 0;
      
      
      public volatile Integer m = new Integer(1);
      
      @Override
      public void thread1()
      {
        x = 1;
        z = x;
        synchronized (m)
        {
          y = 2;
        }
        
        a++;
        b--;
      }
      
      @Override
      public void thread2()
      {
        x = 1;
        x = y;
        y = y + 1;
        synchronized (m)
        {
          b++;
          c--;
        }
      }

    }.execute();
  }
  
}

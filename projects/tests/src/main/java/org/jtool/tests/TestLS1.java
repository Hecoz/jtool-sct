package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestLS1 implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new TwoThreadedTestCase()
    {
      public volatile int x;
      public volatile int y;
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
          // unimportant
          y = 1;
        }
        x = 2;
      }
    }.execute();
  }
}

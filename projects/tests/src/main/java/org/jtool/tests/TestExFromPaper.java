package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestExFromPaper implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new FourThreadedTestCase()
    {
      public volatile int x = 0;
      public volatile int y = 0;
      public volatile Integer m = new Integer(1);
      
      public void f(int tid)
      {
        synchronized (m)
        {
          if(tid == 1)
          {
            x = 1;
          }
          else if(tid == 2)
          {
            y = 1;
          }
          else
          {
            if(x == 0)
            {
              y = 2;
            }
          }
        }
      }
      
      @Override
      public void thread1()
      {
        f(1);
      }
      
      @Override
      public void thread2()
      {
        f(2);
      }
      
      @Override
      public void thread3()
      {
        f(3);

      }
      
      @Override
      public void thread4()
      {
        f(4);
      }
    }.execute();
  }
}

package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestArrays2 implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new FourThreadedTestCase()
    {
      public volatile int x[] = new int[4];
      public volatile Integer m = new Integer(1);
      
      //public volatile int y;
      
      @Override
      public void thread1()
      {
        synchronized (m)
        {
          x[0] = 0;
          x[1] = 1;
        }
      }
      
      @Override
      public void thread2()
      {
        synchronized (m)
        {
          x[2] = 2;
          x[3] = 3;
        }
      }
      
      @Override
      public void thread3()
      {
      }
      
      @Override
      public void thread4()
      {
        
      }
    }.execute();
  }
}

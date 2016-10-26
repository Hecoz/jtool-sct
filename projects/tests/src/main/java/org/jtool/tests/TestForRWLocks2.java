package org.jtool.tests;

import org.jtool.runtime.InstrumentationPoints;
import org.jtool.test.ConcurrencyTestCase;

public class TestForRWLocks2 implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new FourThreadedTestCase()
    {
      public volatile int x;
      public volatile int t2;
      public volatile int t3;
      public volatile int t4;
      public volatile Object m = new Object();
      public volatile Object n = new Object();
      
      @Override
      public void thread1()
      {
        try
        {
          InstrumentationPoints.enterRWMonitor(m, true);
          x=2;
        }
        finally
        {
          InstrumentationPoints.exitRWMonitor(m, true);
        }
      }
      
      @Override
      public void thread2()
      {
        try
        {
          InstrumentationPoints.enterRWMonitor(m, false);
          t2 = x;
          if(x == 2)
          {
            try
            {
              InstrumentationPoints.enterRWMonitor(m, true);
              x=3;
            }
            finally
            {
              InstrumentationPoints.exitRWMonitor(m, true);
            }
          }
        }
        finally
        {
          InstrumentationPoints.exitRWMonitor(m, false);
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

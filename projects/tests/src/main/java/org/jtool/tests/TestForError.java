package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public class TestForError implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new FourThreadedTestCase()
    {
      public volatile int x;
      
      @Override
      public void thread1()
      {
        x = 1;
      }
      
      @Override
      public void thread2()
      {
        x = 2;
      }
      
      @Override
      public void thread3()
      {
        x = 3;
      }
      
      @Override
      public void thread4()
      {
        if (x == 1)
        {
          if (x == 2)
          {
            throw new IllegalStateException("x went from 1 to 2.");
          }
        }
      }
    }.execute();
  }
}

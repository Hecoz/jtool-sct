package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public abstract class TwoThreadedTestCase implements ConcurrencyTestCase
{
  public abstract void thread1();
  
  public abstract void thread2();

  public void execute() throws Exception
  {
    final Thread t1 = new Thread()
    {
      @Override
      public void run()
      {
        thread1();
      }
      
    };
    
    final Thread t2 = new Thread()
    {
      @Override
      public void run()
      {
        thread2();
      }

    };
    
    t1.start();
    t2.start();
    t1.join();
    t2.join();

  }

}

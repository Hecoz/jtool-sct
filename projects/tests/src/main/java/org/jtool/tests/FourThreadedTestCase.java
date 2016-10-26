package org.jtool.tests;

import org.jtool.test.ConcurrencyTestCase;

public abstract class FourThreadedTestCase implements ConcurrencyTestCase
{
  public abstract void thread1();
  
  public abstract void thread2();
  
  public abstract void thread3();
  
  public abstract void thread4();

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
    
    final Thread t3 = new Thread()
    {
      @Override
      public void run()
      {
        thread3();
      }
      
    };
    
    final Thread t4 = new Thread()
    {
      @Override
      public void run()
      {
        thread4();
      }
      
    };

    t1.start();
    t2.start();
    t3.start();
    t4.start();
    t1.join();
    t2.join();
    t3.join();
    t4.join();

  }

}

package fse2006losenotify;

import org.jtool.test.ConcurrencyTestCase;

public class TestLoseNotify implements ConcurrencyTestCase
{
  private final int iWait;
  private final int iNotify;
  private final int iterations;
  
  public TestLoseNotify(int iWait, int iNotify, int iterations)
  {
    this.iWait = iWait;
    this.iNotify = iNotify;
    this.iterations = iterations;
  }

  public void execute() throws Exception
  {
    Losenotify ln = new Losenotify();
    WaitThread[] waiters = new WaitThread[iWait];
    NotifyThread[] notifiers = new NotifyThread[iNotify];
    
    for (int i = 0; i < iWait; i++)
    {
      waiters[i] = new WaitThread(ln, iterations);
      waiters[i].start();
    }
    for (int i = 0; i < iNotify; i++)
    {
      notifiers[i] = new NotifyThread(ln, iterations);
      notifiers[i].start();
    }
    
    for (int i = 0; i < iWait; i++)
    {
      waiters[i].join();
    }
    for (int i = 0; i < iNotify; i++)
    {
      notifiers[i].join();
    }
    
    
  }
  
}

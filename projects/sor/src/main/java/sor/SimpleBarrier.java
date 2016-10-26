package sor;

import org.jtool.runtime.InstrumentationPoints;

import sor.EDU.oswego.cs.dl.util.concurrent.Barrier;

public class SimpleBarrier implements Barrier
{
  private final int numThreads;
  private int count = 0;
  private boolean flip = false;
  public SimpleBarrier(final int n)
  {
    numThreads = n;
    if(InstrumentationPoints.isSctExec())
    {
      InstrumentationPoints.newBarrier(this, n);
    }
  }
  
  public void barrier() throws InterruptedException
  {
    if(InstrumentationPoints.isSctExec())
    {
      InstrumentationPoints.barrierWait(this);
      return;
    }
    synchronized (this)
    {
      final boolean myFlip = flip;

      count++;
      if (count == numThreads)
      {
        count = 0;
        flip = !flip;
        this.notifyAll();
      }
      while (flip == myFlip)
      {
        try
        {
          this.wait();
        }
        catch (final InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      }
    }
  }

  public int parties()
  {
    throw new IllegalStateException();
  }

  public boolean broken()
  {
    throw new IllegalStateException();
  }
  
}

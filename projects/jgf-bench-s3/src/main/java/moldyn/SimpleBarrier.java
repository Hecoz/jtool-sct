package moldyn;

import org.jtool.runtime.InstrumentationPoints;

public class SimpleBarrier extends Barrier
{
  private int count = 0;
  private boolean flip = false;
  public SimpleBarrier(final int n)
  {
    super(n);
    if(InstrumentationPoints.isSctExec())
    {
      InstrumentationPoints.newBarrier(this, n);
    }
  }

  @Override
  public void DoBarrier(final int myid)
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
  
}

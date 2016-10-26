package org.jtool.runtime;

import static com.google.common.base.Preconditions.checkState;

import java.util.HashSet;
import java.util.Set;

import org.jtool.runtime.Op.OpType;
import org.jtool.runtime.rpl.TerminateThreadException;

public final class BarrierInfo
{
  public SyncObjectData sod;
  public final int numThreads;
  public Set<ThreadData> threads;
  public int count;
  public ThreadData childThreadData;
  public Executor exec;
  public Thread childThread = new Thread()
  {
    @Override
    public void run()
    {
      synchronized (childThreadData)
      {
        try
        {
          while (!childThreadData.active && !exec.terminateThreads)
          {
            childThreadData.wait();
          }
          if (exec.terminateThreads)
          {
            return;
          }
          // System.out.println("Barrier thread is going!");
        }
        catch (final InterruptedException e)
        {
          throw new RuntimeException(e);
        }
      }

      try
      {
        while (true)
        {
          for (final ThreadData otherThreadData : threads)
          {
            otherThreadData.enabled = true;
          }
          
          checkState(childThreadData.enabled);
          if (!childThreadData.enabled)
          {
            throw new TerminateThreadException();
          }
          
          childThreadData.enabled = false;
          count = 0;
          
          exec.schedule(childThreadData, sod, OpType.BARRIER_MID);
        }
      }
      catch (final TerminateThreadException e)
      {
        // ignore
      }
    }
  };
  
  public BarrierInfo(final SyncObjectData sod, final int numThreads,
      final Executor exec)
  {
    this.sod = sod;
    this.numThreads = numThreads;
    this.threads = new HashSet<>();
    this.count = 0;
    this.exec = exec;
  }


}

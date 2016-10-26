package org.jtool.tests;

import org.jtool.runtime.InstrumentationPoints;

public class Barrier
{
  public Barrier(int numThreads)
  {
    if(InstrumentationPoints.isSctExec())
    {
      InstrumentationPoints.newBarrier(this, numThreads);
    }
  }
  
  public void DoBarrier(int tid)
  {
    if(InstrumentationPoints.isSctExec())
    {
      InstrumentationPoints.barrierWait(this);
    }
  }
  
}

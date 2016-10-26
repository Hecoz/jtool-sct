package fse2006util;

import org.jtool.runtime.InstrumentationPoints;

// Author: Matt Dwyer
public final class StandardBarrier {
  private final long participants;
  private long numBlocked, numReleased;
  private boolean allBlocked, allReleased;

  public StandardBarrier(long initial) {
    if(InstrumentationPoints.isSctExec())
    {
      InstrumentationPoints.newBarrier(this, (int) initial);
    }
    participants = initial; 
    numReleased = numBlocked = 0; 
  }

  private synchronized void blockAll() {
    ++numBlocked;
    allBlocked = false;
    while ( numBlocked < participants && !allBlocked )
      try { wait(); } catch (InterruptedException ex) {};
    allBlocked = true; 
    numBlocked = 0;
    notifyAll();
  }

  private synchronized void releaseAll() {
    ++numReleased;
    allReleased = false;
    while ( numReleased < participants && !allReleased )
      try { wait(); } catch (InterruptedException ex) {};
    allReleased = true; 
    numReleased = 0;
    notifyAll();
  }

  public synchronized void await() {
    if(InstrumentationPoints.isSctExec())
    {
      InstrumentationPoints.barrierWait(this);
      return;
    }
    blockAll();
    releaseAll();
  }
}

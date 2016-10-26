package org.jtool.runtime;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class QueueData
{
  
  //
  public Set<ThreadData> enterQueue = new HashSet<>();
  public ThreadData ownerThread;
  public int lastAcquireClock;
  public int lastAcquireThreadId;
  public int recursiveEntered;
  
  // set of threads in wait queue. Mapped to signal ids
  public Deque<ThreadData> waitQueue = new ArrayDeque<>(1);
  public Map<ThreadData, Integer> notifiedSet = new HashMap<>(1);
  
  public QueueData()
  {
    reset();
  }

  public void reset()
  {
    enterQueue.clear();
    ownerThread = null;
    lastAcquireClock = 0;
    lastAcquireThreadId = -1;
    recursiveEntered = 0;
    waitQueue.clear();
    notifiedSet.clear();
  }

}

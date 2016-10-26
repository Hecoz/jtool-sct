package org.jtool.strategy;

import java.util.ArrayList;
import java.util.List;

import org.jtool.runtime.Op;
import org.jtool.runtime.Op.OpType;
import org.jtool.runtime.SchedulingStrategy;
import org.jtool.runtime.SyncObjectData;
import org.jtool.runtime.ThreadData;

public class DFSStrategy implements SchedulingStrategy
{
  private final TidListStack stack;
  private boolean doDpor;
  private boolean lazyDPOR;
  private boolean sleepSets;
  private HBGCache hbgCache;
  private boolean scheduleBounding;
  private int schedulingBound;
  private int numPreemptions;

  public DFSStrategy()
  {
    stack = new TidListStack();
  }

  public DFSStrategy setDpor(boolean dpor)
  {
    this.doDpor = dpor;
    return this;
  }

  public DFSStrategy setSleepSets(boolean sleepSets)
  {
    this.sleepSets = sleepSets;
    return this;
  }

  public DFSStrategy setHbgCaching(boolean hbgCaching, boolean useLazyCaching)
  {
    hbgCache = hbgCaching ? new HBGCache(useLazyCaching) : null;
    return this;
  }

  public DFSStrategy setLazyDpor(boolean lazyDpor)
  {
    this.lazyDPOR = lazyDpor;
    return this;
  }

  public DFSStrategy setScheduleBounding(boolean scheduleBounding)
  {
    this.scheduleBounding = scheduleBounding;
    return this;
  }

  public DFSStrategy setPreemptionBound(int preemptionBound)
  {
    this.schedulingBound = preemptionBound;
    return this;
  }

  @Override
  public int schedule(
      final ThreadData currThreadData,
      final SyncObjectData syncObject,
      final OpType opType,
      final List<ThreadData> threadList,
      final Op prevOp,
      final SyncObjectData prevOpSyncObjectData,
      final ExecutionHasher executionHasher)
  {
    updateMutexInfo(currThreadData, prevOp, prevOpSyncObjectData);

    if (hbgCache != null)
    {
      hbgCache.addExecutionHash(executionHasher);
    }

    final boolean added = stack.push(
        threadList,
        currThreadData.threadId,
        lazyDPOR);

    // if we pushed a new "state"...
    if (added)
    {
      if (sleepSets)
      {
        SleepSetsUtil.updateSleepSets(stack, currThreadData, threadList, prevOp);
      }

      if (hbgCache != null)
      {
        hbgCache.prune(stack, threadList, executionHasher);
      }

      if (doDpor)
      {
        DPORUtil.handleDpor(threadList, lazyDPOR, stack, prevOp);
      }
      else
      {
        // for naive DFS, every thread is added to the backtrack set
        stack.getTopOfStack().addAllToBacktrack();
      }

      if(scheduleBounding && numPreemptions == schedulingBound)
      {
        final ArrayList<TidEntry> top = stack.getTopOfStack().getInternalList();
        if(!top.isEmpty() && top.get(0).id == currThreadData.threadId)
        {
          for(int i=1; i < top.size(); ++i)
          {
            top.get(i).sleep = true;
          }
        }
      }
    }

    final TidEntry nextThreadEntry = stack.getNextThread(!added, lazyDPOR);

    // Special case for LazyDPOR:
    if(nextThreadEntry != null && !nextThreadEntry.enabled)
    {
      assert lazyDPOR;

      // We have backtracked to 

      ThreadData threadData = threadList.get(nextThreadEntry.id);
      ArrayList<Integer> raceList = new ArrayList<>();
      DPORUtil.getRaces(threadData, raceList, lazyDPOR);
      for(Integer i : raceList)
      {
        DPORUtil.unpruneDporThread(threadData, i, stack);
      }
    }

    if (nextThreadEntry != null && !nextThreadEntry.selected)
    {
      nextThreadEntry.selected = true;
      nextThreadEntry.done = true;
      nextThreadEntry.backtrack = true;
    }

    if (nextThreadEntry != null)
    {
      if (doDpor)
      {
        DPORUtil.updateVectorClocks(
            threadList.get(nextThreadEntry.id),
            lazyDPOR,
            stack.getCurrentClock() + 1);
      }

      if(!nextThreadEntry.enabled)
      {
        return THREAD_ID_LAZY_DPOR_BLOCKED;
      }

      if(scheduleBounding)
      {
        TidEntry prevEntry = stack.getTopOfStack().getEntry(currThreadData.threadId);
        if(prevEntry != null && prevEntry.enabled && prevEntry.id != nextThreadEntry.id)
        {
          ++numPreemptions;
        }
      }

      return nextThreadEntry.id;
    }

    return getNegativeReturnCode();
  }

  private void updateMutexInfo(
      final ThreadData currThreadData,
      final Op prevOp,
      final SyncObjectData prevOpSyncObjectData)
  {
    if (prevOp.getOpType().equals(OpType.ENTER_MONITOR))
    {
      prevOpSyncObjectData.getQueueData().lastAcquireClock = stack.getCurrentClock() + 1;
      prevOpSyncObjectData.getQueueData().lastAcquireThreadId = currThreadData.threadId;
    }
  }



  @Override
  public void prepareForNextExecution() throws NoMoreExecutionsException
  {
    stack.prepareForNextExecution(doDpor);
    numPreemptions = 0;
  }

  private int getNegativeReturnCode()
  {
    if (stack.topEmpty())
    {
      return SchedulingStrategy.THREAD_ID_DEADLOCK;
    }

    assert !stack.topAllDone();
    assert stack.topContainsSleep();
    return SchedulingStrategy.THREAD_ID_SLEEP_SET_BLOCKED;
  }

}

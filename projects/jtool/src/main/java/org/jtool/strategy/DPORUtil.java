package org.jtool.strategy;

import java.util.ArrayList;
import java.util.List;

import org.jtool.runtime.Op;
import org.jtool.runtime.SyncObjectData;
import org.jtool.runtime.ThreadData;
import org.jtool.runtime.Op.OpType;

public final class DPORUtil
{
  
  public static void handleDpor(
      final List<ThreadData> threadList,
      final boolean lazyLocks,
      final TidListStack stack,
      final Op prevOp)
  {
    // For the alreadyRaceChecked optimisation:
    // Here we clear the alreadyRaceChecked operation
    // if the prev op conflicted or replaced it.
    for (final ThreadData threadData : threadList)
    {
      if (threadData.alreadyRaceChecked != null
          && (prevOp.conflicts(threadData.alreadyRaceChecked) || prevOp.getThread()
              .equals(threadData.alreadyRaceChecked.getThread())))
      {
        threadData.alreadyRaceChecked = null;
      }
    }

    for (final ThreadData threadData : threadList)
    {
      if (threadData.running)
      {
        final List<Integer> races = new ArrayList<>();
        DPORUtil.getRaces(threadData, races, lazyLocks);
        // TODO: re-add this: numRaces += races.size();
        for (final Integer raceClock : races)
        {
          DPORUtil.unpruneDporThread(threadData, raceClock, stack);
        }
      }
    }
  }

  public static void updateVectorClocks(
      final ThreadData threadData,
      final boolean lazyLocks,
      final int clockOfNextOp)
  {
    final SyncObjectData threadVC = threadData.threadVectorClock;
    final Op op = threadData.getCurrOp();
    final SyncObjectData oData = threadData.currOpSyncObjectData;
    final boolean write = op.getOpType().isWrite();
    
    threadVC.setWriteClock(threadData.threadId, clockOfNextOp);
    
    // optimisation
    if (op.getOpType().isExclusiveMutexOp() && lazyLocks)
    {
      return;
    }
    
    threadVC.joinWriteWithWrite(threadData.currOpSyncObjectData);
    
    if (write)
    {
      // W' = T' = (T+nodeIndex) U W U R
      
      threadVC.joinWriteWithRead(oData);
      oData.setWriteToWrite(threadVC);
      
      // store which clock (position) is actually the write
      oData.setWritePos(threadData.threadId);
      
      // clear previous reads
      oData.resetReadClocks();
    }
    else
    {
      // T' = (T+nodeIndex) U W
      // R' = R U T'
      
      oData.joinReadWithWrite(threadVC);
      // update read mask
      oData.addToReadMask(threadData.threadId);
    }
    
  }

  public static void getRaces(
      final ThreadData threadData,
      final List<Integer> raceClocks,
      final boolean lazyLocks)
  {
    final SyncObjectData threadVC = threadData.threadVectorClock;
    final SyncObjectData oData = threadData.currOpSyncObjectData;
    final Op op = threadData.getCurrOp();
    final boolean write = op.getOpType().isWrite();
    
    // ignore thread and barrier ops -- these cannot be reversed.
    if (op.getOpType().isThreadOp() || op.getOpType().isBarrierOp())
    {
      return;
    }
    
    // ignore locks during lazy locks
    if (lazyLocks && op.getOpType().isExclusiveMutexOp())
    {
      return;
    }
    
    // Optimisation:
    // We ignore ops that have already been race checked.
    // "alreadyRaceChecked" is set below and cleared elsewhere.
    if (threadData.alreadyRaceChecked != null)
    {
      assert op.equals(threadData.alreadyRaceChecked);
      return;
    }
    
    threadData.alreadyRaceChecked = op;
    
    // reverse lock-lock
    if (op.getOpType().equals(OpType.ENTER_MONITOR))
    {
      assert !lazyLocks;
      final int lastAcquireThreadId = oData.getQueueData().lastAcquireThreadId;
      if (lastAcquireThreadId != -1)
      {
        final int lastAcquireClock = oData.getQueueData().lastAcquireClock;
        
        final int[] tw = threadVC.getWriteClocks(lastAcquireThreadId);
        
        if (lastAcquireClock > tw[lastAcquireThreadId])
        {
          // concurrent, so return the clock as the only race
          raceClocks.add(lastAcquireClock);
          return;
        }
      }
    }
    
    final int lastWriteThreadId = oData.getLastWriteThreadId();
    // race only with previous write
    if (lastWriteThreadId >= 0 && (!write || oData.isReadEmpty()))
    {
      final int[] ow = oData.getWriteClocks(0);
      final int owLength = oData.getWriteClocksLength();
      final int[] tw = threadVC.getWriteClocks(owLength);
      
      // is last write concurrent with this thread?
      if (ow[lastWriteThreadId] > tw[lastWriteThreadId])
      {
        int clock = ow[lastWriteThreadId];
        if (clock != threadData.wokenClock)
        {
          clock = moveClockToEmptyLockset(
              clock,
              tw[lastWriteThreadId],
              threadData,
              lazyLocks);
          if (clock > 0)
          {
            raceClocks.add(clock);
            return;
          }
        }
      }
    }
    // race with one or more previous reads
    else if (write && !oData.isReadEmpty())
    {
      // Previous reads exist.
      // Check if concurrent.
      final int size = oData.getReadsMaskSize();
      final int[] reads = oData.getReads();
      final byte[] readsMask = oData.getReadsMask();
      final int[] threadClocks = threadVC.getWriteClocks(size - 1);
      for (int i = 0; i < size; ++i)
      {
        // concurrent
        if (reads[i] > threadClocks[i])
        {
          if (readsMask[i] != 0)
          {
            int clock = reads[i];
            // at present, no reads can wake up a thread
            // checkState(clock != threadData.wokenClock);
            // checkState(clock != 0);
            // checkState(clock <= nodeIndex);
            // if (clock != threadData.wokenClock)
            // {
            clock = moveClockToEmptyLockset(
                clock,
                threadClocks[i],
                threadData,
                lazyLocks);
            if (clock > 0)
            {
              // TODO: Readd this? ++numRacesOnReads;
              raceClocks.add(clock);
            }
            // }
          }
        }
      }
    }
  }
  
  /**
   * Returns a smaller clock of where to unprune such that the lockset of the op
   * at the smaller clock /\ with thread is empty.
   *
   * @param currThread
   * @param clock
   */
  public static int moveClockToEmptyLockset(
      final int clock,
      final int lastKnownClockOfOtherThread,
      final ThreadData currThread,
      final boolean lazyLocks)
  {
    assert !lazyLocks;
    return clock;
    // if (!lazyLocks)
    // {
    // return clock;
    // }
    //
    // {
    // final LockSet locksetAtClock = locksets.get(clock - 1);
    // if (locksetAtClock.isEmpty())
    // {
    // return clock;
    // }
    // }
    //
    // final int[] threadVectorClock =
    // currThread.threadVectorClock.getWriteClocks(threadList.size() - 1);
    //
    // final Set<SyncObjectData> threadLockset = new
    // HashSet<>(currThread.lockset);
    // int c = nodeIndex;
    // while (true)
    // {
    // // collect all lock acquires that happen-before curr thread
    // // for (; c >= clock; --c)
    // // for (int i = clock; i <= c; i++)
    // if (c >= clock)
    // {
    // final SortedMap<Integer, LockAcquire> view = lockAcquires.subMap(
    // clock,
    // c + 1);// exclusive
    // for (final Entry<Integer, LockAcquire> i : view.entrySet())
    // {
    // // ls = locksets.get(c - 1);
    // if (i.getKey() <=
    // // currThread.threadVectorClock
    // // .getWriteClockAssumeAlloc(i.getValue().threadId))
    // threadVectorClock[i.getValue().threadId])
    // {
    // threadLockset.add(i.getValue().monitor);
    // }
    // }
    // }
    // c = clock;
    //
    // final LockSet locksetAtClock = locksets.get(clock - 1);
    // final LockSet intersection = locksetAtClock.intersection(threadLockset);
    //
    // if (intersection.isEmpty())
    // {
    // return clock;
    // }
    // // push clock back
    // for (final Entry<SyncObjectData, Integer> lock_clock :
    // intersection.entrySet())
    // {
    // final int newClock = lock_clock.getValue();
    // if (newClock < clock)
    // {
    // clock = newClock;
    // }
    // }
    // // check that clock does not happen-before currThread
    // if (clock <= lastKnownClockOfOtherThread)
    // {
    // // no race
    // return 0;
    // }
    // if (locksets.get(clock - 1).isEmpty())
    // {
    // return clock;
    // }
    // }
  }
  
  public static void unpruneDporThread(
      final ThreadData thread,
      final int clock,
      final TidListStack stack)
  {
    final TidList list = stack.getListAt(clock - 1);
    final TidEntry threadEntry = list.getEntry(thread.threadId);
    
    if(threadEntry != null && !threadEntry.sleep)
    {
      threadEntry.backtrack = true;
    }
    else
    {
      final ArrayList<TidEntry> internalList = list.getInternalList();
      for (final TidEntry entry : internalList)
      {
        entry.backtrack = true;
      }
    }
  }

}

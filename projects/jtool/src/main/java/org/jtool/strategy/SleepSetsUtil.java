package org.jtool.strategy;

import java.util.ArrayList;
import java.util.List;

import org.jtool.runtime.Op;
import org.jtool.runtime.ThreadData;

public final class SleepSetsUtil
{
  public static void updateSleepSets(
      final TidListStack stack,
      final ThreadData currThreadData,
      final List<ThreadData> threadList,
      final Op prevOp)
  {
    final boolean[] oldSleep = new boolean[threadList.size()];
    final boolean[] newSleep = new boolean[threadList.size()];
    
    final ArrayList<TidEntry> previousState = stack.getSecondOnStack()
        .getInternalList();
    final ArrayList<TidEntry> currentState = stack.getTopOfStack()
        .getInternalList();
    
    // Copy previous sleep set forward, removing ops that are dependent with
    // prevOp.

    for (final TidEntry entry : previousState)
    {
      if (entry.sleep)
      {
        oldSleep[entry.id] = true;
      }
    }
    
    for(final ThreadData threadData : threadList)
    {
      if (oldSleep[threadData.threadId]
          && !threadData.getCurrOp().conflicts(prevOp))
      {
        newSleep[threadData.threadId] = true;
      }
    }
    
    for (final TidEntry entry : currentState)
    {
      if (newSleep[entry.id])
      {
        entry.sleep = true;
      }
    }
    
    // Add current thread to previous state's sleep set.
    stack.getSecondOnStack().setSleep(currThreadData.threadId, true);
  }
}

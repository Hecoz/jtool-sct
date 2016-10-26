package org.jtool.strategy;

import java.util.ArrayList;
import java.util.List;

import org.jtool.runtime.ThreadData;

public final class TidListStack
{
  private final ArrayList<TidList> stack = new ArrayList<>();
  private int topOfStack;
  
  public TidListStack()
  {
    topOfStack = 0;
    final ArrayList<TidEntry> list = new ArrayList<>();
    final TidEntry entry = new TidEntry(0, true);
    entry.backtrack = true;
    list.add(entry);
    final TidList tidList = new TidList(list);
    stack.add(tidList);
  }
  
  public boolean topOfStackMatches(final ArrayList<TidEntry> list)
  {
    final ArrayList<TidEntry> top = getTopOfStack().getInternalList();
    for (int i = 0, size = list.size(); i < size; ++i)
    {
      final TidEntry l = top.get(i);
      final TidEntry r = list.get(i);
      if (l.id != r.id || l.enabled != r.enabled)
      {
        return false;
      }
    }
    return true;
  }

  public boolean push(
      final List<ThreadData> threadList,
      final int prevThreadId,
      final boolean lazyDPOR)
  {
    final ArrayList<TidEntry> list = new ArrayList<>();
    int i = prevThreadId;
    final int threadCount = threadList.size();
    for (int count = 0; count < threadCount; ++count)
    {
      final ThreadData threadData = threadList.get(i);
      // special case for lazy DPOR - add disabled lock operations
      if (lazyDPOR
          && !threadData.enabled
          && threadData.currOpType.isExclusiveMutexOp())
      {
        list.add(new TidEntry(threadData.threadId, false));

      }
      // normal case
      else if (threadData.enabled)
      {
        list.add(new TidEntry(threadData.threadId, true));
      }
      i++;
      if (i >= threadCount)
      {
        i = 0;
      }
    }

    assert topOfStack <= stack.size() - 1;

    final int oldTopOfStack = topOfStack;
    final boolean added = stack.size() - 1 == topOfStack;

    if (added)
    {
      assert stack.size() - 1 == topOfStack;
      stack.add(new TidList(list));
      topOfStack++;
    }
    else
    {
      // check
      topOfStack++;
      assert topOfStackMatches(list);
    }
    assert topOfStack <= stack.size() - 1;
    assert oldTopOfStack + 1 == topOfStack;
    return added;
  }
  
  public TidList getTopOfStack()
  {
    return stack.get(topOfStack);
  }
  
  public TidList getSecondOnStack()
  {
    return stack.get(topOfStack - 1);
  }

  public void pop()
  {
    assert topOfStack == stack.size() - 1;

    stack.remove(topOfStack);
    topOfStack--;
    
    assert topOfStack == stack.size() - 1;
  }
  
  public TidEntry getNextThread(
      final boolean backtracking,
      final boolean lazyDPOR)
  {
    final TidList top = getTopOfStack();
    
    if(topOfStack < stack.size() - 1)
    {
      return top.getSelectedTid();
    }
    
    return top.getFirstTidNotDoneOrSlept(backtracking);
  }

  public void prepareForNextExecution(final boolean onlyExploreBacktrack)
      throws NoMoreExecutionsException
  {
    while (true)
    {
      if (topOfStack == -1)
      {
        throw new NoMoreExecutionsException();
      }
      
      if (!getTopOfStack().allDoneOrSlept(onlyExploreBacktrack))
      {
        break;
      }

      pop(); // decreases topOfStack
    }
    final TidList list = getTopOfStack();
    list.clearSelected();
    if (stack.size() - 1 == 0)
    {
      final TidEntry entry = list.getInternalList().get(0);
      entry.done = true;
      entry.selected = true;
    }
    topOfStack = 0;
  }
  
  public TidList getListAt(final int clock)
  {
    return stack.get(clock);
  }
  
  public int getCurrentClock()
  {
    return topOfStack;
  }
  
  public boolean topEmpty()
  {
    return getTopOfStack().isEmpty();
  }
  
  public boolean topAllDone()
  {
    return getTopOfStack().allDone();
  }
  
  public boolean topContainsSleep()
  {
    return getTopOfStack().containsSleep();
  }
  
}

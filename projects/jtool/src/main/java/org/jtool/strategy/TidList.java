package org.jtool.strategy;

import java.util.ArrayList;
import java.util.Arrays;

import com.google.common.collect.Lists;


public class TidList
{
  private final ArrayList<TidEntry> list;
  
  public TidList(final TidEntry[] list, final int firstTidSeen)
  {
    this.list = Lists.newArrayList(list);
  }
  
  public TidList(final ArrayList<TidEntry> list)
  {
    this.list = list;
  }

  public void add(final TidEntry entry)
  {
    list.add(entry);
  }
  
  public ArrayList<TidEntry> getInternalList()
  {
    return list;
  }

  public TidEntry getEntry(final int tid)
  {
    for (final TidEntry entry : list)
    {
      if (entry.id == tid)
      {
        return entry;
      }
    }
    return null;
  }
  
  public TidEntry getSelectedTid()
  {
    for (final TidEntry entry : list)
    {
      if (entry.selected)
      {
        return entry;
      }
    }
    return null;
  }
  
  public TidEntry getFirstTidNotDoneOrSlept(
      final boolean mustBeInBacktrack)
  {
    for (final TidEntry entry : list)
    {
      if (!entry.done
          && entry.enabled
          && !entry.sleep
          && (!mustBeInBacktrack || entry.backtrack))
      {
        return entry;
      }
    }
    return null;
  }
  
  public boolean allDoneOrSlept(final boolean orNotInBacktrack)
  {
    for (final TidEntry entry : list)
    {
      if (!(entry.done || entry.sleep || (!entry.backtrack && orNotInBacktrack)))
      {
        return false;
      }
    }
    return true;
  }

  @Override
  public String toString()
  {
    return "[" + Arrays.toString(list.toArray()) + "]";
  }
  
  public void clearSelected()
  {
    for (final TidEntry entry : list)
    {
      if (entry.selected)
      {
        entry.selected = false;
        break;
      }
    }
    assert noneSelected();
  }
  
  public boolean noneSelected()
  {
    for (final TidEntry entry : list)
    {
      if (entry.selected)
      {
        return false;
      }
    }
    return true;
  }
  
  public void setSleep(final int tid, final boolean value)
  {
    getEntry(tid).sleep = value;
  }
  
  public boolean isEmpty()
  {
    return list.isEmpty();
  }
  
  public boolean allDone()
  {
    for (final TidEntry entry : list)
    {
      if (!entry.done)
      {
        return false;
      }
    }
    return true;
  }
  
  public boolean containsSleep()
  {
    for (final TidEntry entry : list)
    {
      if (entry.sleep)
      {
        return true;
      }
    }
    return false;
  }
  
  public void addAllToBacktrack()
  {
    for (final TidEntry entry : list)
    {
      entry.backtrack = true;
    }
  }
}

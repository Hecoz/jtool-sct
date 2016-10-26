package org.jtool.runtime;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.Iterables;

public final class WaitQueue
{
  public final Map<ThreadData, Set<Integer>> q = new HashMap<>();

  public void add(final ThreadData threadData)
  {
    checkState(!q.containsKey(threadData));
    q.put(threadData, new TreeSet<Integer>());
  }
  
  public int getFirstNotifyId(final ThreadData threadData)
  {
    final Set<Integer> notifyIds = q.get(threadData);
    checkState(!notifyIds.isEmpty());
    final Integer id = Iterables.getFirst(notifyIds, null);
    checkNotNull(id);
    return id;
  }
  
  public void remove(final ThreadData threadData)
  {
    q.remove(threadData);
  }
  
  public void removeNotifyId(final int id)
  {
    for (final Entry<ThreadData, Set<Integer>> entry : q.entrySet())
    {
      entry.getValue().remove(id);
      if (entry.getValue().isEmpty())
      {
        entry.getKey().enabled = false;
      }
    }
  }
  
  public void notify(final int notifyId)
  {
    for (final Entry<ThreadData, Set<Integer>> entry : q.entrySet())
    {
      entry.getValue().add(notifyId);
      entry.getKey().enabled = true;
    }
  }
  
  public int notifyAll(int notifyId)
  {
    for (final Entry<ThreadData, Set<Integer>> entry : q.entrySet())
    {
      entry.getValue().add(notifyId++);
      entry.getKey().enabled = true;
    }
    
    return notifyId;
  }

}

package org.jtool.runtime;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public final class LockSet
{
  // [lock id -> most recent acquire]
  private final HashMap<SyncObjectData, Integer> locks;
  
  public LockSet(final int initCapacity)
  {
    locks = new HashMap<>(initCapacity);
  }

  public LockSet(final Set<SyncObjectData> lockset)
  {
    this(lockset.size());
    for (final SyncObjectData s : lockset)
    {
      locks.put(s, s.getQueueData().lastAcquireClock);
    }
  }
  
  public LockSet intersection(final LockSet lockset)
  {
    return intersection(lockset.locks.keySet());
  }

  public LockSet intersection(final Set<SyncObjectData> lockset)
  {
    final LockSet res = new LockSet(lockset.size());
    
    for (final Entry<SyncObjectData, Integer> l_ac : locks.entrySet())
    {
      if (lockset.contains(l_ac.getKey()))
      {
        res.locks.put(l_ac.getKey(), l_ac.getValue());
      }
    }
    return res;
  }
  
  public boolean isIntersectionEmpty(final LockSet other)
  {
    return isIntersectionEmpty(other.locks.keySet());
  }
  
  public boolean isIntersectionEmpty(final Set<SyncObjectData> other)
  {
    return Sets.intersection(other, locks.keySet()).isEmpty();
  }

  public boolean isEmpty()
  {
    return locks.isEmpty();
  }
  
  public Entry<SyncObjectData, Integer> getFirst()
  {
    return Iterables.getFirst(locks.entrySet(), null);
  }
  
  public Set<Entry<SyncObjectData, Integer>> entrySet()
  {
    return locks.entrySet();
  }
  
  public Set<SyncObjectData> getLocks()
  {
    return locks.keySet();
  }


}

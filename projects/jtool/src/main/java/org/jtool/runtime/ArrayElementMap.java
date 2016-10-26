package org.jtool.runtime;

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.Array;
import java.util.IdentityHashMap;
import java.util.Map;

import org.jtool.runtime.Op.OpType;

public class ArrayElementMap
{
  private final Map<Object, SyncObjectData[]> arraySyncObjects =
      new IdentityHashMap<>();
  
  private int cacheLoc = 0;
  private static final int CACHE_SIZE = 4;
  private final Object[] cacheObj = new Object[CACHE_SIZE];
  private final SyncObjectData[][] cacheSync = new SyncObjectData[CACHE_SIZE][];

  private final SyncObjectData[] findInCache(final Object obj)
  {
    for (int i = 0; i < CACHE_SIZE; ++i)
    {
      int index = cacheLoc - i;
      if (index < 0)
      {
        index = CACHE_SIZE - 1;
      }
      final Object objC = cacheObj[index];
      if (objC == null)
      {
        return null;
      }
      if (obj == objC)
      {
        return cacheSync[index];
      }
    }
    return null;
  }
  
  private final void putInCache(final Object obj, final SyncObjectData[] sync)
  {
    cacheLoc = (cacheLoc + 1) % CACHE_SIZE;
    cacheObj[cacheLoc] = obj;
    cacheSync[cacheLoc] = sync;
  }

  public final SyncObjectData getSyncObjectData(final Object array,
      final int index, final OpType opType, final int executionId)
  {
    final SyncObjectData[] shadowArray = getSyncObjectDataArray(array, opType);
    SyncObjectData res = shadowArray[index];
    if(res == null)
    {
      res = new SyncObjectData(executionId);
      checkState(!opType.isValidInitialOp());
      res.addr = System.identityHashCode(array);
      res.offset = index;
      shadowArray[index] = res;
    }
    else if (res.executionId != executionId)
    {
      // global
      res.reset(executionId);
      checkState(res.addr == System.identityHashCode(array));
      checkState(res.offset == index);
    }
    return res;
  }
  
  public final SyncObjectData[] getSyncObjectDataArray(final Object array,
      final OpType opType)
  {
    SyncObjectData[] shadowArray = null;
    shadowArray = findInCache(array);
    
    if (shadowArray == null)
    {
      // cache miss
      shadowArray = arraySyncObjects.get(array);
      if (shadowArray == null)
      {
        shadowArray = new SyncObjectData[Array.getLength(array)];
        arraySyncObjects.put(array, shadowArray);
      }
      // put in cache
      putInCache(array, shadowArray);
    }
    return shadowArray;
  }
}

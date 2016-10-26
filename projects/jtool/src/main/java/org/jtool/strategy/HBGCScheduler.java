package org.jtool.strategy;

import java.util.List;

import org.jtool.runtime.Op.OpType;
import org.jtool.runtime.Op;
import org.jtool.runtime.SchedulingStrategy;
import org.jtool.runtime.SyncObjectData;
import org.jtool.runtime.ThreadData;

public class HBGCScheduler implements SchedulingStrategy
{
  public boolean lazyInHBGC = false;
  
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
    return 0;
  }

  @Override
  public void prepareForNextExecution()
  {

  }
  
}

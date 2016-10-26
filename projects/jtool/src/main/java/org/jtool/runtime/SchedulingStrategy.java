package org.jtool.runtime;

import java.util.List;

import org.jtool.runtime.Op.OpType;
import org.jtool.strategy.ExecutionHasher;
import org.jtool.strategy.NoMoreExecutionsException;

public interface SchedulingStrategy
{
  // Should never occur
  public static final int THREAD_ID_ALL_DONE = -10;
  public static final int THREAD_ID_DEADLOCK = -11;
  public static final int THREAD_ID_SLEEP_SET_BLOCKED = -12;
  public static final int THREAD_ID_LAZY_DPOR_BLOCKED = -12;

  public int schedule(
      ThreadData currThreadData,
      SyncObjectData syncObject,
      OpType opType,
      List<ThreadData> threadList,
      Op prevOp,
      SyncObjectData prevOpSyncObjectData,
      ExecutionHasher executionHasher);
  
  public void prepareForNextExecution() throws NoMoreExecutionsException;

}

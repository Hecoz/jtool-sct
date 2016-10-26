package org.jtool.runtime;

import java.util.HashSet;
import java.util.Set;

import org.jtool.runtime.Op.OpType;

public class ThreadData {
  public int executionId;
  public final Thread thread;
  public Op creationOp;
  public boolean running = false;
  public int opCounter = 0;
  public boolean active = false;
  public boolean enabled = true;
  public boolean daemon = false;
  public OpType currOpType = null;
  public SyncObjectData currOpSyncObjectData = null;
  public int threadId;
  
  // this stores writes the the thread object. e.g. on creation
  public SyncObjectData threadSyncObjectData;
  // this stores the vector clock of what this thread "knows" about
  public SyncObjectData threadVectorClock;
  
  public Set<ThreadData> joinQueue = new HashSet<>();
  
  public Set<SyncObjectData> lockset = new HashSet<>();
  public int wokenClock;
  
  public Op alreadyRaceChecked = null;

  public ThreadData(final int executionId, final Thread thread,
      final Op creationOp, final int threadId,
      final SyncObjectData threadSyncObjectData,
      final SyncObjectData threadVectorClock)
  {
    this.executionId = executionId;
    this.thread = thread;
    this.creationOp = creationOp;
    this.threadId = threadId;
    this.threadSyncObjectData = threadSyncObjectData;
    this.threadVectorClock = threadVectorClock;
  }

  public final boolean enabled()
  {
    return enabled && running;
  }
  
  public final Op getCurrOp()
  {
    
    final int numWrites = currOpSyncObjectData.numWrites
        + (currOpType.isWrite() ? 1 : 0);
    return Op.getOp(
        creationOp,
        opCounter + 1,
        currOpSyncObjectData.id,
        currOpSyncObjectData.addr,
        currOpSyncObjectData.offset,
        currOpType,
        numWrites);
  }



}

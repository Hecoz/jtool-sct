package org.jtool.runtime;

public class LockAcquire
{
  public int threadId;
  public SyncObjectData monitor;
  
  public LockAcquire(final int threadId, final SyncObjectData monitor)
  {
    this.threadId = threadId;
    this.monitor = monitor;
  }

}

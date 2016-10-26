package org.jtool.runtime;

import static com.google.common.base.Preconditions.checkState;

import java.util.Objects;

import com.google.common.hash.Funnel;
import com.google.common.hash.PrimitiveSink;

public final class Op
{
  private final Op thread;
  
  /**
   * Starts at 1.
   */
  private final int perThreadOpIndex;
  
  /**
   * Op where this object was "created". Used to uniquely refer to the object.
   */
  private final Op syncObject;
  
  /**
   * If we didn't see this object get created, then syncObject will be null and
   * this field is set to the address of the object.
   */
  private final int syncObjectAddr;
  
  /**
   * It is expensive and troublesome to give each field its own "create" Op
   * (i.e. the Op stored in field syncObject), especially since these can't
   * really be scheduled at. Instead, we will lazily set "syncObject" to the
   * object to which the field belongs and set this field to a unique number
   * that represents the field.
   * 
   * @see org.jtool.loader.ClassInfo
   */
  private final int syncObjectOffset;
  
  private final OpType opType;
  
  private final int numWrites;
  
  public static final Funnel<Op> hashFunnel = new Funnel<Op>()
  {
    private static final long serialVersionUID = 1L;
    @Override
    public final void funnel(final Op from, final PrimitiveSink into)
    {
      if (from.thread != null)
      {
        Op.hashFunnel.funnel(from.thread, into);
      }
      into.putInt(from.perThreadOpIndex);
      if (from.syncObject != null)
      {
        Op.hashFunnel.funnel(from.syncObject, into);
      }
      into.putInt(from.syncObjectAddr);
      into.putInt(from.syncObjectOffset);
      into.putInt(from.numWrites);
    }
  };
  
  private Op(final Op thread, final int perThreadOpIndex, final Op syncObject,
      final int syncObjectAddr, final int syncObjectOffset, final OpType opType,
      final int numWrites)
  {
    this.thread = thread;
    this.perThreadOpIndex = perThreadOpIndex;
    this.syncObject = syncObject;
    this.opType = opType;
    this.numWrites = numWrites;
    this.syncObjectAddr = syncObjectAddr;
    this.syncObjectOffset = syncObjectOffset;
    checkState(thread == null || this != thread);
    checkState(syncObject == null || this != syncObject);
  }

  /**
   * @param thread
   * @param perThreadOpIndex
   *          Starts at 1.
   * @param syncObject
   * @param opType
   * @param numWrites
   * @return
   */
  public static Op getOp(final Op thread, final int perThreadOpIndex,
      final Op syncObject, final int syncObjectAddr, final int syncObjectOffset,
      final OpType opType,
      final int numWrites)
  {
    return new Op(
        thread,
        perThreadOpIndex,
        syncObject,
        syncObjectAddr,
        syncObjectOffset,
        opType,
        numWrites);
  }
  
  public final Op getThreadNotThis()
  {
    return thread == this ? null : thread;
  }
  
  public final Op getThread()
  {
    return thread;
  }
  
  /**
   * Starts at 1.
   */
  public final int getPerThreadOpIndex()
  {
    return perThreadOpIndex;
  }
  
  public final Op getSyncObject()
  {
    return syncObject;
  }

  public enum OpType
  {
    READ, WRITE, FIELD_INIT, ARRAY_INIT, THREAD_CREATE,
    THREAD_JOIN, THREAD_START, THREAD_EXIT, OBJ_INIT,
    ENTER_MONITOR, EXIT_MONITOR, NOTIFY, NOTIFY_ALL,
    PREWAIT, WAIT, YIELD,
    BARRIER_INIT, BARRIER_PRE, BARRIER_MID, BARRIER_POST,
    ENTER_R_MONITOR, EXIT_R_MONITOR,
    ENTER_W_MONITOR, EXIT_W_MONITOR;

    public final boolean isWrite()
    {
      switch (this)
      {
      case READ:
      case BARRIER_PRE:
      case BARRIER_POST:
      case ENTER_R_MONITOR:
      case EXIT_R_MONITOR:
        return false;
      default:
        break;
      }
      return true;
    }
    
    public final boolean mayBlock()
    {
      switch (this)
      {
      case THREAD_JOIN:
      case THREAD_EXIT:
      case ENTER_MONITOR:
      case WAIT:
      case BARRIER_MID:
      case BARRIER_POST:
        return true;
      default:
        break;
      }
      return false;
    }

    public final boolean isValidInitialOp()
    {
      switch (this)
      {
      case FIELD_INIT:
      case THREAD_START:
      case THREAD_CREATE:
      case OBJ_INIT:
      case ARRAY_INIT:
      case BARRIER_INIT:
        return true;
      default:
        break;
      }
      return false;
    }

    public final boolean isThreadOp()
    {
      switch (this)
      {
      case THREAD_CREATE:
      case THREAD_EXIT:
      case THREAD_JOIN:
      case THREAD_START:
        return true;
      default:
        break;
      }
      return false;
    }
    
    public final boolean isBarrierOp()
    {
      switch (this)
      {
      case BARRIER_PRE:
      case BARRIER_MID:
      case BARRIER_POST:
        return true;
      default:
        break;
      }
      return false;
    }

    public final boolean isExclusiveMutexOp()
    {
      switch (this)
      {
      case ENTER_MONITOR:
      case EXIT_MONITOR:
        return true;
      default:
        break;
      }
      return false;
    }
    
    public final boolean isMemOp()
    {
      switch (this)
      {
      case READ:
      case WRITE:
        return true;
      default:
        break;
      }
      return false;
    }
  }


  @Override
  public final int hashCode()
  {
    return Objects.hash(
        thread,
        perThreadOpIndex,
        syncObject,
        syncObjectAddr,
        syncObjectOffset,
        numWrites);
  }
  
  // TODO: This needs to be FIXED!
  // It is possible to have:
  // firstOp's thread refer to a
  // different Op instance,
  // which is actually equal to firstOp.
  @Override
  public final boolean equals(final Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    if (this == obj)
    {
      return true;
    }
    if (!(obj instanceof Op))
    {
      return false;
    }
    final Op other = (Op) obj;
    return Objects.equals(thread, other.thread)
        && perThreadOpIndex == other.perThreadOpIndex
        && Objects.equals(syncObject, other.syncObject)
        && syncObjectAddr == other.syncObjectAddr
        && syncObjectOffset == other.syncObjectOffset
        && numWrites == other.numWrites;
  }
  
  
  /**
   * Checks whether the sync object accessed by "this" Op and "other" Op are the
   * same. Takes into account the "addr" and "offset" fields -- if this and
   * other disagree on these fields, then false is returned.
   * 
   * @param other
   * @return
   */
  public final boolean conflicts(final Op other)
  {
    assert other != null;
    
    if (!opType.isWrite() && !other.opType.isWrite())
    {
      return false;
    }

    if (syncObject == null && other.syncObject == null)
    {
      if (syncObjectAddr == 0 || other.syncObjectAddr == 0)
      {
        // at least one op is an independent object creation op
        return false;
      }
      // these are accesses to global(s)

      if (syncObjectAddr == other.syncObjectAddr)
      {
        assert syncObjectOffset == 0;
        assert other.syncObjectOffset == 0;
        return true;
      }
      else
      {
        return false;
      }
    }
    
    return Objects.equals(syncObject, other.syncObject)
        && syncObjectAddr == other.syncObjectAddr
        && syncObjectOffset == other.syncObjectOffset;
  }

  @Override
  public final String toString()
  {
    try
    {
      return "(t"
          + InstrumentationPoints.runtime.currentExecutor
              .getThreadData(thread == null ? this : thread).threadId
          + ",pti="
          + perThreadOpIndex
          + ","
          + opType
          + ",syncOb="
          + syncObject
          + ",syncObAddr="
          + syncObjectAddr
          + ",syncObOffset="
          + syncObjectOffset
          + ",numWrites="
          + numWrites
          + ")";
    }
    catch (final Exception e)
    {
      return "(error)";
    }
  }

  public final OpType getOpType()
  {
    return opType;
  }

  public final boolean createsThread()
  {
    return opType.equals(OpType.THREAD_CREATE);
  }

  public final int getNumWrites()
  {
    return numWrites;
  }
  
  public final Op withNumWrites(final int newNumWrites)
  {
    return withSyncObjectAndNumWrites(syncObject, newNumWrites);
  }

  public final Op withSyncObjectAndNumWrites(final Op syncObject,
      final int numWrites)
  {
    //    if (this.syncObject.equals(syncObject) && this.numWrites == numWrites)
    //    {
    //      return this;
    //    }
    //    
    //    return Op.getOp(
    //        this.thread,
    //        this.perThreadOpIndex,
    //        syncObject,
    //        this.opType,
    //        numWrites);
    throw new RuntimeException("Not yet implemented.");
  }
  
  public final Op asLazyOp()
  {
    if (this.opType.isExclusiveMutexOp())
    {
    return Op.getOp(
        this.thread,
        this.perThreadOpIndex,
        this.syncObject,
        this.syncObjectAddr,
        this.syncObjectOffset,
        this.opType,
        0);
    }
    return this;
  }
}

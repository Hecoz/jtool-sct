package org.jtool.runtime;

import java.util.Arrays;

import com.carrotsearch.hppc.ByteArrayList;
import com.carrotsearch.hppc.IntArrayList;

public final class SyncObjectData
{
  /**
   * We identify this sync object via the first Op performed on it.
   */
  public Op id;
  
  public int addr;
  
  public int offset;

  /**
   * This allows us to detect whether we are now on a later execution, so we can
   * reset this SyncObjectData. This only happens for fields at the moment.
   */
  public int executionId;
  
  /**
   * Each Op in the execution tree stores the number of writes that an object
   * has. This efficiently stores the HB relation for HBG caching. Thus, we keep
   * track of this number.
   */
  public int numWrites;
  
  // Write vector clock info.
  
  /**
   * See {@code lastWriteClocks}.
   **/
  private int lastWriteThreadId;
  
  /**
   * Vector clock of the most recent write. Only one position (thread)
   * corresponds to the actual write, which is identified by
   * {@code lastWriteThreadId}.
   */
  private IntArrayList lastWriteClocks;
  
  
  // Read vector clock info.
  
  /**
   * Vector clock of all reads since the last write. Several positions (threads)
   * may correspond to actual reads of this sync object -- these are identified
   * by the {@code isRead} mask.
   */
  private IntArrayList readClocks;
  private ByteArrayList isRead;
  
  
  /**
   * In Java, any object can be treated as a mutex and/or be waited on and
   * notified. This stores the related information. It is redundant when this
   * SyncObjectData is storing info about a field.
   */
  private QueueData queueData;
  
  private static final int initialCapacity = 4;

  public SyncObjectData(final int executionId)
  {
    this.executionId = executionId;
  }

  
  /**
   * Does not reset id, addr, offset.
   * 
   * @param executionId
   */
  public void reset(final int executionId)
  {
    //    if (this.executionId == 0)
    //    {
    //      this.executionId = executionId;
    //      return;
    //    }
    this.executionId = executionId;
    numWrites = 0;
    resetWriteClocks();
    resetReadClocks();
    lastWriteThreadId = -1;
    if (queueData != null)
    {
      queueData.reset();
    }
  }
  
  public void resetReadClocks()
  {
    if (readClocks != null)
    {
      readClocks.resize(0);
      isRead.resize(0);
    }
  }
  
  public void resetWriteClocks()
  {
    if (lastWriteClocks != null)
    {
      lastWriteClocks.resize(0);
    }
  }
  
  public boolean isReadEmpty()
  {
    if (readClocks == null || readClocks.isEmpty())
    {
      return true;
    }
    for (int i = 0; i < readClocks.elementsCount; ++i)
    {
      if (readClocks.buffer[i] > 0)
      {
        return false;
      }
    }
    
    return true;
  }

  public QueueData getQueueData()
  {
    if (queueData == null)
    {
      queueData = new QueueData();
    }
    return queueData;
  }
  
  private static void ensureSize(final IntArrayList list, final int threadId)
  {
    final int maxIndex = list.size() - 1; 
    if (maxIndex < threadId)
    {
      list.resize(threadId + 1);
    }
  }
  
  private static void ensureSize(final ByteArrayList list, final int threadId)
  {
    final int maxIndex = list.size() - 1;
    if (maxIndex < threadId)
    {
      list.resize(threadId + 1);
    }
  }

  private void ensureWriteClocksAllocated()
  {
    if (lastWriteClocks == null)
    {
      lastWriteClocks = new IntArrayList(initialCapacity);
    }
  }
  
  private void ensureReadClocksAllocated()
  {
    if (readClocks == null)
    {
      readClocks = new IntArrayList(initialCapacity);
      isRead = new ByteArrayList(initialCapacity);
    }
  }

  public void incrementWriteClock(final int threadId)
  {
    ensureWriteClocksAllocated();
    ensureSize(lastWriteClocks, threadId);
    lastWriteClocks.buffer[threadId]++;
    lastWriteThreadId = threadId;
  }
  
  public int prevConcurrentWrite(final int threadId,
      final SyncObjectData threadVectorClock)
  {
    if (lastWriteThreadId == -1)
    {
      return 0;
    }
    final int lastWriteEventClock = getWriteClock(lastWriteThreadId);
    if (lastWriteEventClock > threadVectorClock
        .getWriteClock(lastWriteThreadId))
    {
      return lastWriteEventClock;
    }
    return 0;
  }
  
  public int[] getWriteClocks(final int atLeastThreadId)
  {
    ensureWriteClocksAllocated();
    ensureSize(lastWriteClocks, atLeastThreadId);
    return lastWriteClocks.buffer;
  }
  
  public int getWriteClocksLength()
  {
    return lastWriteClocks.elementsCount;
  }

  public final int getWriteClock(final int threadId)
  {
    ensureWriteClocksAllocated();
    //ensureSize(lastWriteClocks, threadId);
    if (threadId > lastWriteClocks.size() - 1)
    {
      return 0;
    }
    return lastWriteClocks.buffer[threadId];
  }
  
  public final int getWriteClockAssumeAlloc(final int threadId)
  {
    if (threadId > lastWriteClocks.size() - 1)
    {
      return 0;
    }
    return lastWriteClocks.buffer[threadId];
  }

  public void setWriteClock(final int threadId, final int clock)
  {
    ensureWriteClocksAllocated();
    ensureSize(lastWriteClocks, threadId);
    lastWriteClocks.buffer[threadId] = clock;
  }
  
  public void setWritePos(final int threadId)
  {
    lastWriteThreadId = threadId;
  }

  public void joinWriteWithWrite(final SyncObjectData other)
  {
    if (other.lastWriteClocks == null || other.lastWriteClocks.isEmpty())
    {
      return;
    }
    ensureWriteClocksAllocated();
    join(lastWriteClocks, other.lastWriteClocks);
  }
  
  public void joinWriteWithRead(final SyncObjectData other)
  {
    if (other.readClocks == null || other.readClocks.isEmpty())
    {
      return;
    }
    ensureWriteClocksAllocated();
    join(lastWriteClocks, other.readClocks);
  }
  
  
  /**
   * This updates the read clocks to be at least as large as the given write
   * clocks of "other". If a clock increases, then the isReads mask is cleared
   * for that clock, as that clock no longer represents an operation that read
   * from this sync object.
   * 
   * @param other
   */
  public void joinReadWithWrite(final SyncObjectData other)
  {
    if (other.lastWriteClocks == null || other.lastWriteClocks.isEmpty())
    {
      return;
    }
    ensureReadClocksAllocated();
    //join(readClocks, other.lastWriteClocks);
    final int otherSize = other.lastWriteClocks.size();
    ensureSize(readClocks, otherSize - 1);
    ensureSize(isRead, otherSize - 1);
    for (int i = 0; i < otherSize; ++i)
    {
      if (readClocks.buffer[i] < other.lastWriteClocks.buffer[i])
      {
        readClocks.buffer[i] = other.lastWriteClocks.buffer[i];
        isRead.buffer[i] = 0;
      }
    }
  }

  private static void join(final IntArrayList mutate, final IntArrayList other)
  {
    final int otherSize = other.size();
    ensureSize(mutate, otherSize - 1);
    for (int i = 0; i < otherSize; ++i)
    {
      mutate.buffer[i] =
          mutate.buffer[i] > other.buffer[i]
              ? mutate.buffer[i]
              : other.buffer[i];
    }
  }

  public void setWriteToWrite(final SyncObjectData other)
  {
    if (other.lastWriteClocks == null || other.lastWriteClocks.isEmpty())
    {
      resetWriteClocks();
      return;
    }
    ensureWriteClocksAllocated();
    copy(lastWriteClocks, other.lastWriteClocks);
  }
  
  private static void copy(final IntArrayList mutate, final IntArrayList other)
  {
    mutate.buffer = Arrays.copyOf(other.buffer, other.buffer.length);
    mutate.elementsCount = other.elementsCount;
  }

  public void addToReadMask(final int threadId)
  {
    ensureReadClocksAllocated();
    ensureSize(isRead, threadId);
    isRead.buffer[threadId] = 1;
  }
  
  public void clearReadMaskPos(final int threadId)
  {
    ensureReadClocksAllocated();
    ensureSize(isRead, threadId);
    isRead.buffer[threadId] = 0;
  }

  public int[] getReads()
  {
    return readClocks.buffer;
  }
  
  public byte[] getReadsMask()
  {
    return isRead.buffer;
  }
  
  public int getReadsMaskSize()
  {
    return isRead.elementsCount;
  }

  public int getLastWriteThreadId()
  {
    return lastWriteThreadId;
  }


}

package org.jtool.runtime;

import java.util.IdentityHashMap;
import java.util.Map;

import org.jtool.runtime.Op.OpType;

public class RWMutexInfo
{
  public static class ThreadInfo
  {
    public int recursiveEntered = 0;
  }

  public static class SingleEntry
  {
    public final ThreadData threadData;
    public final ThreadInfo threadInfo;

    public SingleEntry(final ThreadData threadData, final ThreadInfo threadInfo)
    {
      this.threadData = threadData;
      this.threadInfo = threadInfo;
    }
  }

  private final Map<ThreadData, ThreadInfo> readersQueue = new IdentityHashMap<>(
      2);
  private final Map<ThreadData, ThreadInfo> writersQueue = new IdentityHashMap<>(
      2);

  private final Map<ThreadData, ThreadInfo> readers = new IdentityHashMap<>(2);
  private SingleEntry writer = null;
  
  public void enterReader(final Executor exec, final ThreadData currThreadData,
      final SyncObjectData sod)
  {
    ThreadInfo threadInfo = readers.get(currThreadData);
    if (threadInfo != null)
    {
      // We are already a reader. Increase recursiveEntered.
      threadInfo.recursiveEntered++;
      return;
    }
    threadInfo = new ThreadInfo();
    readersQueue.put(currThreadData, threadInfo);

    // should we become disabled?
    if (writer != null && writer.threadData != currThreadData)
    {
      currThreadData.enabled = false;
    }

    exec.schedule(currThreadData, sod, OpType.ENTER_R_MONITOR);

    // We are enabled and thus can become a reader.
    // We must disable other writers in the queue (unless it is us, although
    // I don't think this is possible).

    ThreadInfo temp;
    
    temp = readersQueue.remove(currThreadData);
    assert temp == threadInfo;
    temp = readers.put(currThreadData, threadInfo);
    assert temp == null;

    for (final ThreadData writer : writersQueue.keySet())
    {
      assert writer.enabled;
      assert writer != currThreadData;
      writer.enabled = false;
    }

  }
  
  public void exitReader(final Executor exec, final ThreadData currThreadData,
      final SyncObjectData sod)
  {
    final ThreadInfo threadInfo = readers.get(currThreadData);
    assert threadInfo != null;
    if (threadInfo.recursiveEntered > 0)
    {
      threadInfo.recursiveEntered--;
      return;
    }

    exec.schedule(currThreadData, sod, OpType.EXIT_R_MONITOR);

    ThreadInfo temp;

    // Remove us from set.
    temp = readers.remove(currThreadData);
    assert temp == threadInfo;

    // Wake up each queued writer thread unless there exists a different reader
    // thread in the readers set or we are still a writer.
    
    if (writer == null)
    {
      for (final ThreadData writer : writersQueue.keySet())
      {
        assert !writer.enabled;
        boolean wakeWriter = true;
        for (final ThreadData reader : readers.keySet())
        {
          if (reader != writer)
          {
            wakeWriter = false;
            break;
          }
        }
        if (wakeWriter)
        {
          writer.enabled = true;
        }
      }
    }
    else
    {
      assert writer.threadData == currThreadData;
    }

  }

  public void enterWriter(final Executor exec, final ThreadData currThreadData,
      final SyncObjectData sod)
  {
    if (writer != null && writer.threadData == currThreadData)
    {
      // We are already a writer. Increase recursiveEntered.
      writer.threadInfo.recursiveEntered++;
      return;
    }
    final ThreadInfo threadInfo = new ThreadInfo();
    writersQueue.put(currThreadData, threadInfo);

    // should we become disabled?
    if (writer != null)
    {
      // writer thread already entered
      currThreadData.enabled = false;
    }
    else
    {
      for (final ThreadData reader : readers.keySet())
      {
        if (reader != currThreadData)
        {
          // reader thread already entered (different to us)
          currThreadData.enabled = false;
          break;
        }
      }
    }


    exec.schedule(currThreadData, sod, OpType.ENTER_W_MONITOR);

    // We are enabled and thus can become a writer.
    assert writer == null;
    writersQueue.remove(currThreadData);
    writer = new SingleEntry(currThreadData, threadInfo);

    // We must disable other readers and writers in the queue.

    for (final ThreadData writer : writersQueue.keySet())
    {
      assert writer.enabled;
      assert writer != currThreadData;
      writer.enabled = false;
    }
    for (final ThreadData reader : readersQueue.keySet())
    {
      assert reader.enabled;
      if (reader != currThreadData)
      {
        reader.enabled = false;
      }
    }

  }

  public void exitWriter(final Executor exec, final ThreadData currThreadData,
      final SyncObjectData sod)
  {
    assert writer != null && writer.threadData == currThreadData;
    final ThreadInfo threadInfo = writer.threadInfo;
    if (threadInfo.recursiveEntered > 0)
    {
      threadInfo.recursiveEntered--;
      return;
    }

    exec.schedule(currThreadData, sod, OpType.EXIT_W_MONITOR);

    assert writer.threadData == currThreadData;
    // Remove us.
    writer = null;

    // Wake up queued writers and readers.

    for (final ThreadData queuedWriter : writersQueue.keySet())
    {
      assert !queuedWriter.enabled;
      // Only wake the writer if we aren't still a reader.
      // There can be no other readers.
      if (readers.isEmpty())
      {
        queuedWriter.enabled = true;
      }
      else
      {
        assert readers.size() == 1 && readers.get(currThreadData) != null;
      }
    }
    for (final ThreadData reader : readersQueue.keySet())
    {
      assert !reader.enabled;
      reader.enabled = true;
    }

  }

}

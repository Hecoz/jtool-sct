package org.jtool.runtime;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.lang.Thread.State;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jtool.runtime.Op.OpType;
import org.jtool.runtime.rpl.TerminateThreadException;
import org.jtool.strategy.ExecutionHasher;
import org.jtool.util.Util;

public final class Executor
{
  // TODO: replayExecution.
  // TODO: Add numSyncObjectsAccessed to getNotifyObjectData
  // TODO: Yield

  public final List<ThreadData> threadList = new ArrayList<>();
  private final Object newThreadSync = new Object();
  
  private final Map<Object, SyncObjectData> syncObjects =
      new IdentityHashMap<>();
  private final Map<Object, SyncObjectData> notifyObjects =
      new IdentityHashMap<>();
  
  private final Map<SyncObjectData, RWMutexInfo> rwMutexMap = new IdentityHashMap<>();

  private final Map<Object, BarrierInfo> barrierMap =
      new IdentityHashMap<>();

  private final ArrayElementMap arraySyncObjects;
  
  private final ThreadData mainThreadData;
  
  private final int executionId;
  
  public boolean terminateThreads = false;
  public boolean deadlockOccurred = false;
  public boolean mutexDeadlockOccurred = false;
  public Throwable errorOccurred = null;
  public boolean sleepSetBlocked = false;
  
  public int numSyncObjectsAccessed = 0;

  public ExecutionHasher executionHasher = new ExecutionHasher();
  
  private final SchedulingStrategy strategy;

  public Executor(
      final int executionId,
      final Thread mainThread,
      final SchedulingStrategy strategy)
  {
    this.executionId = executionId;
    this.arraySyncObjects = new ArrayElementMap();

    this.mainThreadData =
        new ThreadData(executionId, mainThread, Op.getOp(
            null,
            0,
            null,
            0,
            0,
            OpType.THREAD_CREATE,
            0), 0, new SyncObjectData(executionId), new SyncObjectData(
            executionId));
    
    setNextOpForThread(
        mainThreadData,
        mainThreadData.threadSyncObjectData,
        OpType.THREAD_START);
    
    this.strategy = strategy;

    threadList.add(mainThreadData);
    ExecutionManager.setThreadData(mainThread, mainThreadData);
    // TODO: Use a params object here, instead of this?
    // As this.[param] may be updated after construction.
    mainThreadData.running = true;
  }
  
  public final SyncObjectData getSyncObjectDataUnsafe(final Object o)
  {
    checkNotNull(o);
    final SyncObjectData res = syncObjects.get(o);
    return res;
  }
  
  public final SyncObjectData getSyncObjectData(final Object o,
      final OpType opType)
  {
    checkNotNull(o);
    SyncObjectData res = syncObjects.get(o);
    if (res == null)
    {
      ++numSyncObjectsAccessed;
      res = new SyncObjectData(executionId);
      if (!opType.isValidInitialOp())
      {
        res.addr = System.identityHashCode(o);
      }
      syncObjects.put(o, res);
    }
    return res;
  }
  
  public final SyncObjectData getNotifyObjectData(final Object o,
      final OpType opType)
  {
    checkNotNull(o);
    SyncObjectData res = notifyObjects.get(o);
    if (res == null)
    {
      res = new SyncObjectData(executionId);
      if (!opType.isValidInitialOp())
      {
        res.addr = System.identityHashCode(o);
      }
      notifyObjects.put(o, res);
    }
    return res;
  }

  public final ThreadData getThreadData(final Op op)
  {
    for (final ThreadData td : threadList)
    {
      if (td.creationOp.equals(op))
      {
        return td;
      }
    }
    throw new ArrayIndexOutOfBoundsException();
  }

  public final boolean isDeadlock()
  {
    for (final ThreadData td : threadList)
    {
      if (td.enabled && td.running)
      {
        return false;
      }
    }
    return true;
  }
  
  public final void schedule(final ThreadData currThreadData,
      final SyncObjectData syncObject, final OpType opType)
  {
    if (terminateThreads)
    {
      ExecutionManager.throwException(new TerminateThreadException());
    }

    final Op prevOp = currThreadData.getCurrOp();
    final SyncObjectData prevOpSyncObjectData =
        currThreadData.currOpSyncObjectData;
    
    executionHasher.addOp(prevOp);

    // finished previous op
    currThreadData.opCounter++;
    checkState(prevOp.getPerThreadOpIndex() == currThreadData.opCounter);
    

    // update num writes
    if (prevOp.getOpType().isWrite())
    {
      prevOpSyncObjectData.numWrites++;
    }
    checkState(prevOp.getNumWrites() == prevOpSyncObjectData.numWrites);

    // If this is first op for the sync object, set
    // its id as the first op
    if (prevOpSyncObjectData.id == null && prevOpSyncObjectData.addr == 0)
    {
      checkState(prevOp.getOpType().isValidInitialOp());
      prevOpSyncObjectData.id = prevOp;
    }

    // set new op
    setNextOpForThread(currThreadData, syncObject, opType);
    
    final int nextTid = strategy.schedule(
        currThreadData,
        syncObject,
        opType,
        threadList,
        prevOp,
        prevOpSyncObjectData,
        executionHasher);

    if (nextTid < 0)
    {
      if (nextTid == SchedulingStrategy.THREAD_ID_DEADLOCK)
      {
        handleDeadlock();
        return;
      }
      assert nextTid == SchedulingStrategy.THREAD_ID_SLEEP_SET_BLOCKED;
      sleepSetBlocked = true;
      terminateAllThreads();
      ExecutionManager.throwException(new TerminateThreadException());
      return;
    }
    
    final ThreadData nextThreadData = threadList.get(nextTid);

    currThreadData.wokenClock = 0;

    // release the chosen thread
    releaseThreadAndBlock(currThreadData, nextThreadData, true);
  }

  // TODO: May want to use this when sleep set blocked instead of throwing.
  private void runAnyThread(final ThreadData currThreadData,
      final boolean mainThreadBlocksOnTermination)
  {
    for (final ThreadData td : threadList)
    {
      if (td.enabled())
      {
        releaseThreadAndBlock(currThreadData, td, mainThreadBlocksOnTermination);
        return;
      }
    }
    // deadlock
    handleDeadlock();
  }
  
  private final void releaseThreadAndBlock(final ThreadData currThreadData,
      final ThreadData nextThreadData,
      final boolean mainThreadBlocksOnTermination)
  {
    if (nextThreadData != currThreadData)
    {
      currThreadData.active = false;

      synchronized (nextThreadData)
      {
        nextThreadData.active = true;
        nextThreadData.notifyAll();
      }
      
      // when a thread has terminated, we don't wait here
      // unless we are the main thread, which waits until deadlock
      if (currThreadData.running
          || (currThreadData == mainThreadData && mainThreadBlocksOnTermination))
      {
        synchronized (currThreadData)
        {
          try
          {
            while (!currThreadData.active && !terminateThreads)
            {
              currThreadData.wait();
            }
          }
          catch (final InterruptedException e)
          {
            throw new RuntimeException(e);
          }
          if (terminateThreads)
          {
            ExecutionManager.throwException(new TerminateThreadException());
          }
        }
      }
    }
  }



  private final void handleDeadlock()
  {
    checkState(isDeadlock());
    checkState(!terminateThreads);
    

    if (errorOccurred == null)
    {
      for (final ThreadData threadData : threadList)
      {
        if (threadData.running && !threadData.daemon)
        {
          deadlockOccurred = true;
          if (threadData.currOpType.equals(OpType.ENTER_MONITOR))
          {
            mutexDeadlockOccurred = true;
            break;
          }
        }
      }
    }
    

    terminateAllThreads();

    ExecutionManager.throwException(new TerminateThreadException());
  }
  
  
  
  
  
  private final void setNextOpForThread(final ThreadData threadData,
      final SyncObjectData syncObject, final OpType opType)
  {
    checkNotNull(syncObject);
    checkNotNull(opType);
    threadData.currOpSyncObjectData = syncObject;
    threadData.currOpType = opType;
  }

  public final void preStartThread(final Thread thread)
  {
    try
    {
      final Field field = Thread.class.getField("instrumented");
      field.setBoolean(thread, true);
    }
    catch (NoSuchFieldException | SecurityException | IllegalArgumentException
        | IllegalAccessException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }

  public final void executedRun() throws InterruptedException
  {
    ThreadData currThreadData = null;
    final Thread currThread = Thread.currentThread();
    synchronized (newThreadSync)
    {
      while (true)
      {
        currThreadData = getThreadData(currThread, true);
        if (currThreadData != null)
        {
          break;
        }
        newThreadSync.wait();
      }
      
    }

    // next op for this thread is set by parent thread

    synchronized (currThreadData)
    {
      currThreadData.running = true;
      currThreadData.notifyAll();
      try
      {
        while (!currThreadData.active && !terminateThreads)
        {
          currThreadData.wait();
        }
        if (terminateThreads)
        {
          // ExecutionManager.throwException(new TerminateThreadException());
          return;
        }

        // System.out.println("Child thread is going!");

      }
      catch (final InterruptedException e)
      {
        throw new RuntimeException(e);
      }
    }
  }
  
  public final void failedStartThread(final Thread thread)
  {
    Field field;
    try
    {
      // TODO: use this instrumented field.
      field = Thread.class.getField("instrumented");
      field.setBoolean(thread, true);
    }
    catch (NoSuchFieldException | SecurityException | IllegalArgumentException
        | IllegalAccessException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  public final ThreadData getThreadData(final Thread thread)
  {
    return getThreadData(thread, false);
  }
  
  public final ThreadData getThreadData(final Thread thread,
      final boolean mayBeNull)
  {
    final ThreadData threadData = ExecutionManager.getThreadData(thread);
    if (threadData != null)
    {
      if (threadData.executionId != executionId)
      {
        throw new RuntimeException("ExecId Thread mismatch.");
      }
    }
    else if (!mayBeNull)
    {
      throw new RuntimeException("ThreadData is null.");
    }
    return threadData;
  }

  public final void successStartThread(final Thread childThread)
      throws InterruptedException
  {
    final Thread parentThread = Thread.currentThread();
    final ThreadData parentThreadData = getThreadData(parentThread);
    final SyncObjectData childThreadSyncData = new SyncObjectData(executionId);
    
    schedule(parentThreadData, childThreadSyncData, OpType.THREAD_CREATE);
    
    final ThreadData childThreadData = new ThreadData(
        executionId,
        childThread,
        parentThreadData.getCurrOp(),
        threadList.size(),
        childThreadSyncData,
        new SyncObjectData(executionId));

    synchronized (newThreadSync)
    {
      ExecutionManager.setThreadData(childThread, childThreadData);
      threadList.add(childThreadData);
      newThreadSync.notifyAll();
    }

    setNextOpForThread(
        childThreadData,
        childThreadSyncData,
        OpType.THREAD_START);
    
    // The child thread sets running to true.
    // This is redundant -- we could just initially set it to true.
    // This allows us to wait here until the child has finished accessing
    // "threadToThreadData", otherwise the access races with 
    // the next call to schedule from this thread.

    synchronized (childThreadData)
    {
      while (childThreadData.running == false && !terminateThreads)
      {
        childThreadData.wait();
      }
      if (terminateThreads)
      {
        ExecutionManager.throwException(new TerminateThreadException());
      }
    }
  }
  
  private final void terminateAllThreads()
  {
    terminateThreads = true;

    for (final ThreadData threadData : threadList)
    {
      if (threadData.running || threadData == mainThreadData)
      {
        synchronized (threadData)
        {
          threadData.notifyAll();
        }
      }
    }
  }

  public final void threadException(final Thread currThread, final Throwable ex)
  {
    checkState(!terminateThreads);
    if (errorOccurred == null)
    {
      errorOccurred = ex;
    }
  }
  
  public final void terminateThread(final Thread currThread)
  {
    final ThreadData threadData = getThreadData(currThread);
    final SyncObjectData threadSyncData = threadData.threadSyncObjectData;
    
    // thread exit op - must complete. i.e. must get to another schedule.
    schedule(threadData, threadSyncData, OpType.THREAD_EXIT);

    threadData.running = false;
    threadData.enabled = false;
    
    // "notify" others
    for (final ThreadData waiter : threadData.joinQueue)
    {
      waiter.enabled = true;
    }
    
    // any op -- never happens -- it just lets others get scheduled.
    schedule(threadData, threadSyncData, OpType.THREAD_EXIT);
  }
  
  public final void onExitThread(final Thread currThread, final Throwable ex)
  {
    if (terminateThreads)
    {
      return;
    }
    if (ex != null && !terminateThreads)
    {
      threadException(currThread, ex);
    }
    try
    {
      terminateThread(currThread);
    }
    catch (final Throwable e)
    {
      if (e.getClass() != TerminateThreadException.class)
      {
        e.printStackTrace();
        System.exit(-1);
      }
      if (!terminateThreads)
      {
        System.err.println("Terminate threads was false!");
        System.exit(-1);
      }
      // ignore TerminateThreadException
    }
  }

  public final void exitRun(final Throwable ex)
  {
    final Thread currThread = Thread.currentThread();
    final StackTraceElement[] stack = currThread.getStackTrace();
    
    //    printStackTrace();

    checkState(stack.length >= 4);
    if (stack.length == 4)
    {
      onExitThread(currThread, ex);
    }
    else
    {
      ExecutionManager.throwException(ex);
    }
  }
  
  public final void onEnterMonitor(final Object o)
  {
    if (o == null)
    {
      throw new NullPointerException("ENTERMONITOR on null object.");
    }

    final ThreadData threadData = getThreadData(Thread.currentThread());
    
    final SyncObjectData oData = getSyncObjectData(o, OpType.ENTER_MONITOR);
    final QueueData oQueueData = oData.getQueueData();
    
    if (oQueueData.ownerThread == null)
    {
      // no thread holds the monitor
      oQueueData.enterQueue.add(threadData);
    }
    else if (oQueueData.ownerThread == threadData)
    {
      // curr thread holds it
      oQueueData.recursiveEntered++;
      return;
    }
    else
    {
      // another thread owns it
      oQueueData.enterQueue.add(threadData);
      threadData.enabled = false;
    }
    
    schedule(threadData, oData, OpType.ENTER_MONITOR);
    
    checkState(threadData.enabled);
    checkState(oQueueData.enterQueue.contains(threadData)
        && oQueueData.ownerThread == null);

    enterMonitor(o, threadData, oQueueData);
  }

  private final void enterMonitor(final Object o, final ThreadData threadData,
      final QueueData oQueueData)
  {
    checkState(oQueueData.ownerThread == null);
    checkState(oQueueData.recursiveEntered == 0);

    oQueueData.ownerThread = threadData;
    //oQueueData.lastAcquireClock = nodeIndex + 1;
    // Util.monitorEnter(o);
    oQueueData.enterQueue.remove(threadData);
    for (final ThreadData td : oQueueData.enterQueue)
    {
      td.enabled = false;
    }
    // also, disable anybody on wait queue
    // that is enabled.
    //    for (final ThreadData td : oQueueData.waitQueue.q.keySet())
    //    {
    //      td.enabled = false;
    //    }
  }
  
  public final void onExitMonitor(final Object o)
  {
    if (terminateThreads)
    {
      return;
    }
    if (o == null)
    {
      throw new NullPointerException("EXITMONITOR on null object.");
    }

    final ThreadData threadData = getThreadData(Thread.currentThread());
    
    final SyncObjectData oData = getSyncObjectData(o, OpType.ENTER_MONITOR);
    final QueueData oQueueData = oData.getQueueData();
    
    checkState(oQueueData.ownerThread == threadData);
    
    if (oQueueData.recursiveEntered > 0)
    {
      oQueueData.recursiveEntered--;
      return;
    }

    schedule(threadData, oData, OpType.EXIT_MONITOR);
    
    exitMonitor(o, oQueueData);
  }

  private final void exitMonitor(final Object o, final QueueData oQueueData)
  {
    checkState(oQueueData.ownerThread != null);
    checkState(oQueueData.recursiveEntered == 0);

    oQueueData.ownerThread = null;
    // Util.monitorExit(o);
    for (final ThreadData td : oQueueData.enterQueue)
    {
      td.enabled = true;
    }
    // also, wake up anybody on the wait queue that
    // has been signalled.
    //    for (final Map.Entry<ThreadData, Set<Integer>> entry : oQueueData.waitQueue.q
    //        .entrySet())
    //    {
    //      if (!entry.getValue().isEmpty())
    //      {
    //        entry.getKey().enabled = true;
    //      }
    //    }
  }
  
  public final void calledNotify(final Object o)
  {
    final SyncObjectData oData = getSyncObjectData(o, OpType.NOTIFY);
    final QueueData oQueueData = oData.getQueueData();
    
    final SyncObjectData notifyData = getNotifyObjectData(o, OpType.NOTIFY);
    final QueueData notifyQueueData = notifyData.getQueueData();

    final ThreadData threadData = getThreadData(Thread.currentThread());
    
    //    System.out.println("...........Thread "
    //        + threadData.threadId
    //        + " called NOTIFY.");
    //    printStackTrace();
    //    System.out.println("...........\n\n");


    // we must own the monitor
    checkState(oQueueData.ownerThread == threadData);
    
    schedule(threadData, notifyData, OpType.NOTIFY);
    
    if (!notifyQueueData.waitQueue.isEmpty())
    {
      final ThreadData threadNotified = notifyQueueData.waitQueue.remove();
      // Used to be mapped to "nodeIndex+1".
      // Removed as I forget what this was for.
      notifyQueueData.notifiedSet.put(threadNotified, 1);
      if (!threadNotified.enabled)
      {
        threadNotified.enabled = true;
      }
    }
  }
  
  public final void calledNotifyAll(final Object o)
  {
    final SyncObjectData oData = getSyncObjectData(o, OpType.NOTIFY_ALL);
    final QueueData oQueueData = oData.getQueueData();
    
    final SyncObjectData notifyData = getNotifyObjectData(o, OpType.NOTIFY_ALL);
    final QueueData notifyQueueData = notifyData.getQueueData();

    final ThreadData threadData = getThreadData(Thread.currentThread());
    
    //    System.out.println("...........Thread "
    //        + threadData.threadId
    //        + " called NOTIFYALL.");
    //    printStackTrace();
    //    System.out.println("...........\n\n");

    // we must own the monitor
    checkState(oQueueData.ownerThread == threadData);
    
    schedule(threadData, notifyData, OpType.NOTIFY_ALL);
    
    for (final ThreadData threadNotified : notifyQueueData.waitQueue)
    {
      // Used to be mapped to "nodeIndex+1".
      // Removed as I forget what this was for.
      notifyQueueData.notifiedSet.put(threadNotified, 1);
      if (!threadNotified.enabled)
      {
        threadNotified.enabled = true;
      }
    }
    notifyQueueData.waitQueue.clear();

  }
  
  public final void calledWait(final Object o)
  {
    
    final SyncObjectData oData = getSyncObjectData(o, OpType.PREWAIT);
    final QueueData oQueueData = oData.getQueueData();

    final SyncObjectData notifyData = getNotifyObjectData(o, OpType.PREWAIT);
    final QueueData notifyQueueData = notifyData.getQueueData();

    final ThreadData threadData = getThreadData(Thread.currentThread());
    
    //    System.out.println("...........Thread "
    //        + threadData.threadId
    //        + " called WAIT.");
    //    printStackTrace();
    //    System.out.println("...........\n\n");

    // we must own the monitor
    checkState(oQueueData.ownerThread == threadData);
    
    // In order to definitely exit monitor, we need to 0 recursive entered
    // , but we must restore it after.
    final int origRecursiveEntered = oQueueData.recursiveEntered;
    oQueueData.recursiveEntered = 0;

    schedule(threadData, notifyData, OpType.PREWAIT);

    // add to wait queue
    notifyQueueData.waitQueue.add(threadData);

    // release monitor (which enables some threads)
    onExitMonitor(o);
    
    // wait -- may or may not block in our model
    if (!notifyQueueData.notifiedSet.containsKey(threadData))
    {
      threadData.enabled = false;
    }
    schedule(threadData, notifyData, OpType.WAIT);
    
    // TODO Remember what this was for.
    final Integer clockNotified =
        checkNotNull(notifyQueueData.notifiedSet.remove(threadData));
    
    // if (dporIgnoreIrreversibleReleaseRaces)
    // {
    // threadData.wokenClock = clockNotified;
    // }

    // acquire monitor
    onEnterMonitor(o);
    oQueueData.recursiveEntered = origRecursiveEntered;

  }
  
  public final void calledTimedWait(final Object o)
  {
    final SyncObjectData oData = getSyncObjectData(o, OpType.PREWAIT);
    final QueueData oQueueData = oData.getQueueData();
    
    final SyncObjectData notifyData = getNotifyObjectData(o, OpType.PREWAIT);
    final QueueData notifyQueueData = notifyData.getQueueData();
    
    final ThreadData threadData = getThreadData(Thread.currentThread());
    
    // we must own the monitor
    checkState(oQueueData.ownerThread == threadData);
    
    // In order to definitely exit monitor, we need to 0 recursive entered
    // , but we must restore it after.
    final int origRecursiveEntered = oQueueData.recursiveEntered;
    oQueueData.recursiveEntered = 0;
    
    //    schedule(threadData, notifyData, OpType.PREWAIT);
    
    // add to wait queue
    //    notifyQueueData.waitQueue.add(threadData);
    
    // release monitor (which enables some threads)
    onExitMonitor(o);
    
    // wait -- may or may not block in our model
    //    if (!notifyQueueData.notifiedSet.containsKey(threadData))
    //    {
    //      threadData.enabled = false;
    //    }


    //    schedule(threadData, notifyData, OpType.WAIT);
    
    schedule(threadData, notifyData, OpType.YIELD);
    
    final Integer clockNotified =
        checkNotNull(notifyQueueData.notifiedSet.remove(threadData));
    
    //    if (dporIgnoreIrreversibleReleaseRaces)
    //    {
    //      threadData.wokenClock = clockNotified;
    //    }
    
    // acquire monitor
    onEnterMonitor(o);
    oQueueData.recursiveEntered = origRecursiveEntered;
    
  }

  public final void calledWait(final Object o, final long l)
  {
    if (l == 0)
    {
      calledWait(o);
      return;
    }
    calledTimedWait(o);
  }
  
  public final void calledWait(final Object o, final long l, final int i)
  {
    if (l == 0 && i == 0)
    {
      calledWait(o);
      return;
    }
    calledTimedWait(o);
  }

  public final void calledJoin(final Thread otherThread)
  {
    if (otherThread.getState() == State.NEW)
    {
      return;
    }
    final ThreadData currThreadData = getThreadData(Thread.currentThread());
    final ThreadData otherThreadData = getThreadData(otherThread);
    final SyncObjectData otherThreadSyncData =
        otherThreadData.threadSyncObjectData;
    
    if (otherThreadData.running)
    {
      currThreadData.enabled = false;
      otherThreadData.joinQueue.add(currThreadData);
    }
    
    schedule(currThreadData, otherThreadSyncData, OpType.THREAD_JOIN);
    
    otherThreadData.joinQueue.remove(currThreadData);
  }

  public final void calledTimedJoin(final Thread otherThread, final long l,
      final int i)
  {
    if (l == 0 && i == 0)
    {
      calledJoin(otherThread);
      return;
    }
    if (otherThread.getState() == State.NEW)
    {
      return;
    }
    System.out.println("calledTimedJoin.");
    Util.printStackTrace();
    System.exit(1);
  }

  public final void calledYield()
  {
    final Thread thread = Thread.currentThread();
    final SyncObjectData oData = getSyncObjectData(thread, OpType.YIELD);
    schedule(getThreadData(thread), oData, OpType.YIELD);
  }
  
  public final void fieldOperationGlobal(final boolean write,
      final SyncObjectData sod)
  {
    if (sod.executionId != executionId)
    {
      ++numSyncObjectsAccessed;
      // does not reset id, addr, offset
      sod.reset(executionId);
      checkState(sod.id == null);
      checkState(sod.offset == 0);
      if (sod.addr == 0)
      {
        sod.addr = System.identityHashCode(sod);
      }
    }

    schedule(getThreadData(Thread.currentThread()), sod, write
        ? OpType.WRITE
        : OpType.READ);
  }
  
  public final void fieldOperation(final boolean write, final Object object,
      final SyncObjectData sod, final int fieldIndex)
  {
    if (sod.executionId != executionId)
    {
      ++numSyncObjectsAccessed;
      // does not reset id, addr, offset
      sod.reset(executionId);
      
      // must check if we have id for object
      final SyncObjectData objectSod = getSyncObjectDataUnsafe(object);
      if (objectSod != null && objectSod.id != null)
      {
        // Object has id. Set offset of field sod.
        checkState(sod.id == null || sod.id.equals(objectSod.id));
        checkState(sod.addr == 0);
        checkState(sod.offset == 0 || sod.offset == fieldIndex);
        sod.id = objectSod.id;
        sod.offset = fieldIndex;
      }
      else
      {
        // Object has no id. This is a global field.
        checkState(sod.id == null);
        checkState(sod.offset == 0);
        if (sod.addr == 0)
        {
          sod.addr = System.identityHashCode(sod);
        }
      }

    }
    schedule(getThreadData(Thread.currentThread()), sod, write
        ? OpType.WRITE
        : OpType.READ);

  }
  
  public final void arrayOperation(final boolean write, final Object array,
      final int index)
  {
    final SyncObjectData sod =
        arraySyncObjects.getSyncObjectData(
            array,
            index,
            OpType.READ,
            executionId);
    //    System.out.println("Array access index: " + index);
    schedule(getThreadData(Thread.currentThread()), sod, write
        ? OpType.WRITE
        : OpType.READ);
  }

  public final SyncObjectData newObject(final Object obj)
  {
    checkNotNull(obj);
    final SyncObjectData sod = getSyncObjectData(obj, OpType.OBJ_INIT);
    final SyncObjectData notifySod = getNotifyObjectData(obj, OpType.OBJ_INIT);
    
    checkState(sod.executionId == executionId);
    checkState(notifySod.executionId == executionId);
    
    final ThreadData threadData = getThreadData(Thread.currentThread());

    if (sod.id == null)
    {
      schedule(threadData, sod, OpType.OBJ_INIT);
    }
    
    if (notifySod.id == null)
    {
      schedule(threadData, notifySod, OpType.OBJ_INIT);
    }
    return sod;
  }

  public final void newArray(final Object array)
  {
    checkNotNull(array);
    final SyncObjectData arraySod = newObject(array);
    final SyncObjectData[] soda =
        arraySyncObjects.getSyncObjectDataArray(array, OpType.ARRAY_INIT);
    initArrayElems(arraySod, soda);
  }
  
  private final void initArrayElems(final SyncObjectData arraySod,
      final SyncObjectData[] soda)
  {
    checkState(arraySod.id != null);
    checkState(arraySod.addr == 0);

    for (int i = 0; i < soda.length; ++i)
    {
      checkState(soda[i] == null);
      soda[i] = new SyncObjectData(executionId);
      soda[i].id = arraySod.id;
      soda[i].offset = i;
    }
  }

  public final void newArrayMulti(final Object array)
  {
    // since we call this recursively,
    // array may be null
    if (array == null)
    {
      return;
    }
    final SyncObjectData arraySod = newObject(array);

    final SyncObjectData[] soda =
        arraySyncObjects.getSyncObjectDataArray(array, OpType.ARRAY_INIT);
    
    try
    {
      for (int i = 0; i < soda.length; ++i)
      {
        final Object innerArray = Array.get(array, i);
        // may throw if not array
        newArrayMulti(innerArray);
      }
    }
    catch (final IllegalArgumentException e)
    {
      // ignore
    }

    initArrayElems(arraySod, soda);

  }

  public final void ensureThreadsTerminated()
  {
    boolean allTerminated = false;
    while (!allTerminated)
    {
      allTerminated = true;
      for (final ThreadData threadData : threadList)
      {
        if (!threadData.thread.getState().equals(Thread.State.TERMINATED))
        {
          allTerminated = false;
          try
          {
            Thread.sleep(10);
          }
          catch (final InterruptedException e)
          {
            throw new RuntimeException(e);
          }
          break;
        }
      }
    }
  }

  public void cloneCalled(final Object object)
  {
    checkState(object != null);
    boolean isArray = true;
    try
    {
      Array.get(object, 0);
    }
    catch (final ArrayIndexOutOfBoundsException e)
    {
      // ignore
    }
    catch (final IllegalArgumentException e)
    {
      // not array
      isArray = false;
    }
    
    if (isArray)
    {
      newArrayMulti(object);
    }
    else
    {
      newObject(object);
    }
  }

  public void newBarrier(final Object barrier, final int numThreads)
  {
    checkNotNull(barrier);

    final SyncObjectData sod = new SyncObjectData(executionId);
    final BarrierInfo info = new BarrierInfo(sod, numThreads, this);
    
    schedule(getThreadData(Thread.currentThread()), sod, OpType.BARRIER_INIT);
    
    final ThreadData childThreadData =
        new ThreadData(
            executionId,
            info.childThread,
            getSyncObjectDataUnsafe(barrier).id,
            threadList.size(),
            new SyncObjectData(executionId),
            new SyncObjectData(executionId));

    info.childThreadData = childThreadData;
    ExecutionManager.setThreadData(info.childThread, childThreadData);

    childThreadData.enabled = false;
    childThreadData.running = true;
    childThreadData.daemon = true;
    setNextOpForThread(childThreadData, sod, OpType.BARRIER_MID);
    
    checkState(barrierMap.put(barrier, info) == null);

    info.childThread.start();
    threadList.add(childThreadData);
  }

  public void barrierWait(final Object barrier)
  {
    checkNotNull(barrier);
    final BarrierInfo info = barrierMap.get(barrier);
    checkNotNull(info);
    
    final ThreadData threadData = getThreadData(Thread.currentThread());
    
    schedule(threadData, info.sod, OpType.BARRIER_PRE);

    info.count++;
    if(info.threads.size() < info.numThreads)
    {
      info.threads.add(threadData);
    }
    else
    {
      checkState(info.threads.contains(threadData));
    }
    
    threadData.enabled = false;

    if (info.count == info.numThreads)
    {
      // all other threads blocked at barrier post
      info.childThreadData.enabled = true;
    }
    schedule(threadData, info.sod, OpType.BARRIER_POST);

  }

  // TODO: FIX or remove
  // public void preemptFirstTime()
  // {
  // if (executionManager.executionCounter == 0)
  // {
  // preemptFirstTime = true;
  // }
  // }

  public final RWMutexInfo getRWMutexInfo(final SyncObjectData sod)
  {
    RWMutexInfo res = rwMutexMap.get(sod);
    if (res == null)
    {
      res = new RWMutexInfo();
      rwMutexMap.put(sod, res);
    }
    return res;
  }

  public final void onEnterRWMonitor(final Object o, final boolean writer)
  {
    if (o == null)
    {
      throw new NullPointerException("EnterRWMonitor on null object.");
    }
    final SyncObjectData oData = getSyncObjectData(o,
        writer ? OpType.ENTER_W_MONITOR : OpType.ENTER_R_MONITOR);
    onEnterRWMonitorSod(oData, writer);
  }

  public final void onEnterRWMonitorSod(final SyncObjectData oData,
      final boolean writer)
  {
    final ThreadData threadData = getThreadData(Thread.currentThread());
    final RWMutexInfo rwInfo = getRWMutexInfo(oData);
    if (writer)
    {
      rwInfo.enterWriter(this, threadData, oData);
    }
    else
    {
      rwInfo.enterReader(this, threadData, oData);
    }
  }

  public final void onExitRWMonitor(final Object o, final boolean writer)
  {
    if (o == null)
    {
      throw new NullPointerException("ExitRWMonitor on null object.");
    }
    final SyncObjectData oData = getSyncObjectData(o,
        writer ? OpType.EXIT_W_MONITOR : OpType.EXIT_R_MONITOR);
    onExitRWMonitorSod(oData, writer);
  }
  
  public final void onExitRWMonitorSod(final SyncObjectData oData,
      final boolean writer)
  {
    final ThreadData threadData = getThreadData(Thread.currentThread());
    final RWMutexInfo rwInfo = getRWMutexInfo(oData);
    if (writer)
    {
      rwInfo.exitWriter(this, threadData, oData);
    }
    else
    {
      rwInfo.exitReader(this, threadData, oData);
    }
  }
}

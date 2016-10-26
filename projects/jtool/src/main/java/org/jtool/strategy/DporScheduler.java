package org.jtool.strategy;

import java.util.List;

import org.jtool.runtime.Op;
import org.jtool.runtime.Op.OpType;
import org.jtool.runtime.SchedulingStrategy;
import org.jtool.runtime.SyncObjectData;
import org.jtool.runtime.ThreadData;


public class DporScheduler implements SchedulingStrategy
{
  
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
    // TODO Auto-generated method stub
    return 0;
  }
  
  @Override
  public void prepareForNextExecution()
  {
    // TODO Auto-generated method stub
  }
  
  // public final List<LockSet> locksets;
  //
  // // map from "clock/index" to "lock acquire"
  // public final TreeMap<Integer, LockAcquire> lockAcquires;
  // public final List<int[]> vectorClocks;
  //
  // public boolean sleepSetBlocked = false;
  // public boolean hbgCaching;
  //
  // public MonitorGraph monitorGraph = new MonitorGraph();
  //
  // private final boolean dporIgnoreThreadEvents = true;
  // private final boolean dporAcquireOptimisation = true;
  // private final boolean dporIgnoreIrreversibleReleaseRaces = false;
  // public final boolean lazyLocks;
  //
  // @Override
  // public ThreadData schedule(
  // final ThreadData currThreadData,
  // final SyncObjectData syncObject,
  // final OpType opType)
  // {
  //
  // if (prevOp.getOpType().equals(OpType.ENTER_MONITOR))
  // {
  // prevOpSyncObjectData.getQueueData().lastAcquireClock = nodeIndex;
  // prevOpSyncObjectData.getQueueData().lastAcquireThreadId =
  // currThreadData.threadId;
  // }
  //
  // updateLocksets(final prevOp, prevOpSyncObjectData, currThreadData);
  //
  // // Ignore mutex ops -- we check these using the monitor graph
  // if (opType.mayBlock()
  // && !opType.isExclusiveMutexOp()
  // && !currThreadData.lockset.isEmpty())
  // {
  // executionManager.sawPotentialMutexDeadlock = true;
  // }
  //
  // if (opType.equals(OpType.ENTER_MONITOR))
  // {
  // // Add to monitor graph
  // for (final SyncObjectData monitor : currThreadData.lockset)
  // {
  // monitorGraph.addEdge(monitor, currThreadData.threadId, syncObject);
  // }
  // }
  //
  // // optimisation: don't check next ops that have already been checked for
  // final races
  // // in previous steps. If prev op is dependent with op in this "cache",
  // // then op is removed (nulled).
  // for (final ThreadData threadData : threadList)
  // {
  // if (threadData.alreadyRaceChecked != null
  // && (prevOp.conflicts(threadData.alreadyRaceChecked, false) || prevOp
  // .getThread().equals(threadData.alreadyRaceChecked.getThread())))
  // {
  // threadData.alreadyRaceChecked = null;
  // }
  // }
  //
  // // original DPOR
  // if (!replaying && executionManager.dpor)
  // {
  // for (final ThreadData threadData : threadList)
  // {
  // if (threadData.running)
  // {
  // final List<Integer> races = new ArrayList<>();
  // getRaces(threadData, races);
  // numRaces += races.size();
  // for (final Integer raceClock : races)
  // {
  // unpruneDporThread2(threadData, raceClock);
  // }
  // }
  // }
  // }
  //
  // updateVectorClocks2(nextThreadData);
  //
  // if (opType.equals(OpType.YIELD))
  // {
  // currThreadData.enabled = true;
  // }
  //
  // return null;
  // }
  //
  // private void updateRaces(
  // final ThreadData currThreadData,
  // final OpEntry opEntry)
  // {
  // final List<Op> races = new ArrayList<Op>();
  //
  // final boolean write = currThreadData.currOpType.isWrite(lazyLocks);
  // final SyncObjectData oData = currThreadData.currOpSyncObjectData;
  //
  // // if (!write || oData.isReadEmpty())
  // {
  // // Is the potential op concurrent with the previous write?
  // final int clockOfPrevConcurrentWrite = oData.prevConcurrentWrite(
  // currThreadData.threadId,
  // currThreadData.threadVectorClock);
  //
  // if (clockOfPrevConcurrentWrite != 0)
  // {
  // final Op a = nodeList.get(clockOfPrevConcurrentWrite - 1).getOpTo(
  // nodeList.get(clockOfPrevConcurrentWrite));
  // races.add(a);
  // }
  //
  // opEntry.racesIfRead.addAll(races);
  // if (oData.isReadEmpty())
  // {
  // opEntry.racesIfWrite.addAll(races);
  // }
  // }
  // races.clear();
  // // op is a write and readset is not empty
  // // if (write && !oData.isReadEmpty())
  // if (!oData.isReadEmpty())
  // {
  // // Previous reads exist.
  // // Check if concurrent.
  // final int size = oData.getReadsMaskSize();
  // final int[] reads = oData.getReads();
  // final byte[] readsMask = oData.getReadsMask();
  // final int[] threadClocks =
  // currThreadData.threadVectorClock.getWriteClocks(size - 1);
  //
  // for (int i = 0; i < size; ++i)
  // {
  // if (readsMask[i] != 0)
  // {
  // // concurrent
  // if (reads[i] > threadClocks[i])
  // {
  // final Op a = nodeList.get(reads[i] - 1).getOpTo(
  // nodeList.get(reads[i]));
  // checkState(a.getSyncObject().equals(oData.id));
  // races.add(a);
  // }
  // }
  // }
  //
  // opEntry.racesIfWrite.addAll(races);
  // }
  //
  // opEntry.racesWith = write ? opEntry.racesIfWrite : opEntry.racesIfRead;
  // }
  //
  // private void updateSleepSets(final Op prevOp)
  // {
  // if (replayExecution)
  // {
  // return;
  // }
  // checkState(executionManager.sleepSets);
  // final Node parentNode = nodeList.get(nodeIndex - 1);
  // final Node childNode = currNode;
  //
  // // sanity check
  // checkState(parentNode.getChild(prevOp) == currNode);
  //
  // for (final Op sleepOp : parentNode.getSleepSet())
  // {
  // // pass forward sleepOp, if not dependent with prevOp
  //
  // checkState(!sleepOp.getThread().equals(prevOp.getThread()));
  //
  // if (!sleepOp.conflicts(prevOp, false))
  // {
  // // not dependent, so add to sleep set of child
  // childNode.addToSleep(sleepOp);
  // }
  // }
  // // add executed op to sleep set of parent
  // parentNode.addToSleep(prevOp);
  // }
  //
  // private final void updateLocksets(
  // final Op prevOp,
  // final SyncObjectData prevOpSyncObjectData,
  // final ThreadData currThreadData)
  // {
  // locksets.add(new LockSet(currThreadData.lockset));
  // if (locksets.size() != nodeIndex)
  // {
  // System.err.println("Lockset list size: " + locksets.size());
  // System.err.println("Node index : " + nodeIndex);
  // throw new IllegalStateException("Lockset list size != nodeIndex");
  // }
  //
  // if (prevOp.getOpType().equals(OpType.ENTER_MONITOR))
  // {
  // currThreadData.lockset.add(prevOpSyncObjectData);
  // lockAcquires.put(nodeIndex - 1, new LockAcquire(
  // currThreadData.threadId,
  // prevOpSyncObjectData));
  // }
  // else if (prevOp.getOpType().equals(OpType.EXIT_MONITOR))
  // {
  // currThreadData.lockset.remove(prevOpSyncObjectData);
  // }
  // }
  //
  // private final void updateVectorClocks(final ThreadData currThreadData)
  // {
  // final boolean write = currThreadData.currOpType.isWrite(lazyLocks);
  // final int threadId = currThreadData.threadId;
  // final SyncObjectData threadVC = currThreadData.threadVectorClock;
  //
  // threadVC.setWriteClock(threadId, nodeIndex + 1);
  // // if (lazyLocks && currThreadData.currOpType.isExclusiveMutexOp())
  // // {
  // // // don't update vector clocks based on mutex operations.
  // // return;
  // // }
  // threadVC.joinWriteWithWrite(currThreadData.currOpSyncObjectData);
  //
  // if (write)
  // {
  // // W' = T' = (T+nodeIndex) U W U R
  //
  // threadVC.joinWriteWithRead(currThreadData.currOpSyncObjectData);
  // currThreadData.currOpSyncObjectData.setWriteToWrite(threadVC);
  //
  // // store which clock (position) is actually the write
  // currThreadData.currOpSyncObjectData.setWritePos(threadId);
  //
  // // clear previous reads
  // currThreadData.currOpSyncObjectData.resetReadClocks();
  //
  // }
  // else
  // {
  // // T' = (T+nodeIndex) U W
  // // R' = R U T'
  //
  // currThreadData.currOpSyncObjectData.joinReadWithWrite(threadVC);
  //
  // // update read mask
  // currThreadData.currOpSyncObjectData.addToReadMask(threadId);
  //
  // }
  // }
  //
  // private final void unpruneDporThread(final ThreadData thread, int clock)
  // {
  // final int clockAfterLocksetsOpt = unpruneDporThreadBeforeLock(thread,
  // clock);
  //
  // if (assumeDataRaceFreedom
  // && thread.currOpType.isMemOp()
  // && clockAfterLocksetsOpt == clock)
  // {
  // // mem op that did not get "moved back" due to locksets,
  // // so we ignore it.
  // return;
  // }
  // if (locksetsOptimisation)
  // {
  // clock = clockAfterLocksetsOpt;
  // }
  //
  // final Node parent = nodeList.get(clock - 1);
  //
  // Node child = null;
  // for (final OutEdge edge : parent.getChildren())
  // {
  // if (edge.op.getThread().equals(thread.creationOp)
  // && (!executionManager.sleepSets ||
  // !parent.sleepContainsThread(edge.op.getThread())))
  // {
  // child = edge.node;
  // break;
  // }
  // }
  //
  // if (child != null)
  // {
  // if (child.dporPruned)
  // {
  // child.dporPruned = false;
  // executionManager.addUnexploredNode(child);
  // }
  // }
  // else
  // {
  // for (final OutEdge c : parent.getChildren())
  // {
  // if (c.node.dporPruned)
  // {
  // if (!executionManager.sleepSets
  // || !parent.sleepContainsThread(c.op.getThread()))
  // {
  // c.node.dporPruned = false;
  // executionManager.addUnexploredNode(c.node);
  // }
  // }
  // }
  // }
  // }
  //
  // /**
  // * Returns a smaller clock of where to unprune such that the lockset of the
  // op
  // * at the smaller clock /\ with thread is empty.
  // *
  // * @param thread
  // * @param clock
  // */
  // private final int unpruneDporThreadBeforeLock(
  // final ThreadData thread,
  // final int clock)
  // {
  // // while (true)
  // // {
  // // final LockSet locksetAtClock = locksets.get(clock - 1);
  // // final Set<SyncObjectData> threadLocksetAfterNextOp =
  // // new HashSet<>(thread.lockset);
  // // if (thread.currOpType.equals(OpType.ENTER_MONITOR))
  // // {
  // // threadLocksetAfterNextOp.add(thread.currOpSyncObjectData);
  // // }
  // // final LockSet intersection =
  // // locksetAtClock.intersection(threadLocksetAfterNextOp);
  // //
  // // if (intersection.isEmpty())
  // // {
  // // return clock;
  // // }
  // //
  // // for (final Entry<SyncObjectData, Integer> lock_clock :
  // // intersection.entrySet())
  // // {
  // // final int newClock = lock_clock.getValue();
  // // if (newClock < clock)
  // // {
  // // clock = newClock;
  // // }
  // // }
  // // }
  // throw new IllegalArgumentException();
  // }
  //
  // /**
  // * Returns a smaller clock of where to unprune such that the lockset of the
  // op
  // * at the smaller clock /\ with thread is empty.
  // *
  // * @param currThread
  // * @param clock
  // */
  // private final int moveClockToEmptyLockset(
  // int clock,
  // final int lastKnownClockOfOtherThread,
  // final ThreadData currThread)
  // {
  // if (!lazyLocks)
  // {
  // return clock;
  // }
  //
  // {
  // final LockSet locksetAtClock = locksets.get(clock - 1);
  // if (locksetAtClock.isEmpty())
  // {
  // return clock;
  // }
  // }
  //
  // final int[] threadVectorClock =
  // currThread.threadVectorClock.getWriteClocks(threadList.size() - 1);
  //
  // final Set<SyncObjectData> threadLockset = new
  // HashSet<>(currThread.lockset);
  // int c = nodeIndex;
  // while (true)
  // {
  // // collect all lock acquires that happen-before curr thread
  // // for (; c >= clock; --c)
  // // for (int i = clock; i <= c; i++)
  // if (c >= clock)
  // {
  // final SortedMap<Integer, LockAcquire> view = lockAcquires.subMap(
  // clock,
  // c + 1);// exclusive
  // for (final Entry<Integer, LockAcquire> i : view.entrySet())
  // {
  // // ls = locksets.get(c - 1);
  // if (i.getKey() <=
  // // currThread.threadVectorClock
  // // .getWriteClockAssumeAlloc(i.getValue().threadId))
  // threadVectorClock[i.getValue().threadId])
  // {
  // threadLockset.add(i.getValue().monitor);
  // }
  // }
  // }
  // c = clock;
  //
  // final LockSet locksetAtClock = locksets.get(clock - 1);
  // final LockSet intersection = locksetAtClock.intersection(threadLockset);
  //
  // if (intersection.isEmpty())
  // {
  // return clock;
  // }
  // // push clock back
  // for (final Entry<SyncObjectData, Integer> lock_clock :
  // intersection.entrySet())
  // {
  // final int newClock = lock_clock.getValue();
  // if (newClock < clock)
  // {
  // clock = newClock;
  // }
  // }
  // // check that clock does not happen-before currThread
  // if (clock <= lastKnownClockOfOtherThread)
  // {
  // // no race
  // return 0;
  // }
  // if (locksets.get(clock - 1).isEmpty())
  // {
  // return clock;
  // }
  // }
  // }
  //
  // private void getRaces(
  // final ThreadData threadData,
  // final List<Integer> raceClocks)
  // {
  // final SyncObjectData threadVC = threadData.threadVectorClock;
  // final SyncObjectData oData = threadData.currOpSyncObjectData;
  // final Op op = threadData.getCurrOp(lazyLocks);
  // final boolean write = op.getOpType().isWrite(lazyLocks);
  //
  // // ignore thread and barrier ops -- these cannot be reversed.
  // if (op.getOpType().isThreadOp() || op.getOpType().isBarrierOp())
  // {
  // return;
  // }
  //
  // // optimisation: ignore locks during lazy locks
  // if (op.getOpType().isExclusiveMutexOp() && lazyLocks)
  // {
  // return;
  // }
  //
  // if (threadData.alreadyRaceChecked != null)
  // {
  // checkState(op.equals(threadData.alreadyRaceChecked));
  // return;
  // }
  //
  // threadData.alreadyRaceChecked = op;
  //
  // // reverse lock-lock
  // if (op.getOpType().isWrite(lazyLocks)
  // && op.getOpType().equals(OpType.ENTER_MONITOR))
  // {
  // final int lastAcquireThreadId = oData.getQueueData().lastAcquireThreadId;
  // if (lastAcquireThreadId != -1)
  // {
  // final int lastAcquireClock = oData.getQueueData().lastAcquireClock;
  //
  // final int[] tw = threadVC.getWriteClocks(lastAcquireThreadId);
  //
  // if (lastAcquireClock > tw[lastAcquireThreadId])
  // {
  // // concurrent, so return just the clock as a race
  // raceClocks.add(lastAcquireClock);
  // return;
  // }
  // }
  // }
  //
  // final int lastWriteThreadId = oData.getLastWriteThreadId();
  // // race only with previous write
  // if (lastWriteThreadId >= 0 && (!write || oData.isReadEmpty()))
  // {
  // final int[] ow = oData.getWriteClocks(0);
  // final int owLength = oData.getWriteClocksLength();
  // final int[] tw = threadVC.getWriteClocks(owLength);
  //
  // // is last write concurrent with this thread?
  // if (ow[lastWriteThreadId] > tw[lastWriteThreadId])
  // {
  // int clock = ow[lastWriteThreadId];
  // if (clock != threadData.wokenClock)
  // {
  // clock = moveClockToEmptyLockset(
  // clock,
  // tw[lastWriteThreadId],
  // threadData);
  // if (clock > 0)
  // {
  // raceClocks.add(clock);
  // return;
  // }
  // }
  // }
  // }
  // // race with one or more previous reads
  // else if (write && !oData.isReadEmpty())
  // {
  // // Previous reads exist.
  // // Check if concurrent.
  // final int size = oData.getReadsMaskSize();
  // final int[] reads = oData.getReads();
  // final byte[] readsMask = oData.getReadsMask();
  // final int[] threadClocks = threadVC.getWriteClocks(size - 1);
  // for (int i = 0; i < size; ++i)
  // {
  // // concurrent
  // if (reads[i] > threadClocks[i])
  // {
  // if (readsMask[i] != 0)
  // {
  // int clock = reads[i];
  // // at present, no reads can wake up a thread
  // // checkState(clock != threadData.wokenClock);
  // // checkState(clock != 0);
  // // checkState(clock <= nodeIndex);
  // // if (clock != threadData.wokenClock)
  // // {
  // clock = moveClockToEmptyLockset(clock, threadClocks[i], threadData);
  // if (clock > 0)
  // {
  // ++numRacesOnReads;
  // raceClocks.add(clock);
  // }
  // // }
  // }
  // }
  // }
  // }
  // }
  //
  // private void updateVectorClocks2(final ThreadData threadData)
  // {
  // final SyncObjectData threadVC = threadData.threadVectorClock;
  // final Op op = threadData.getCurrOp(lazyLocks);
  // final SyncObjectData oData = threadData.currOpSyncObjectData;
  // final boolean write = op.getOpType().isWrite(lazyLocks);
  //
  // threadVC.setWriteClock(threadData.threadId, nodeIndex + 1);
  //
  // // optimisation
  // if (op.getOpType().isExclusiveMutexOp() && lazyLocks)
  // {
  // return;
  // }
  //
  // threadVC.joinWriteWithWrite(threadData.currOpSyncObjectData);
  //
  // if (write)
  // {
  // // W' = T' = (T+nodeIndex) U W U R
  //
  // threadVC.joinWriteWithRead(oData);
  // oData.setWriteToWrite(threadVC);
  //
  // // store which clock (position) is actually the write
  // oData.setWritePos(threadData.threadId);
  //
  // // clear previous reads
  // oData.resetReadClocks();
  // }
  // else
  // {
  // // T' = (T+nodeIndex) U W
  // // R' = R U T'
  //
  // oData.joinReadWithWrite(threadVC);
  // // update read mask
  // oData.addToReadMask(threadData.threadId);
  // }
  //
  // }
  //
  // private List<Integer> updateVectorClocks(
  // final Op prevOp,
  // final SyncObjectData oData,
  // final ThreadData currThreadData)
  // {
  // final List<Integer> raceClocks = new ArrayList<>();
  // final List<Integer> raceClocksReplacement = new ArrayList<>();
  // final SyncObjectData threadVC = currThreadData.threadVectorClock;
  // threadVC.setWriteClock(currThreadData.threadId, nodeIndex);
  //
  // if (!lazyLocks && prevOp.getOpType().equals(OpType.ENTER_MONITOR))
  // {
  // final int lastAcquireThreadId = oData.getQueueData().lastAcquireThreadId;
  // if (lastAcquireThreadId != -1)
  // {
  // final int lastAcquireClock = oData.getQueueData().lastAcquireClock;
  //
  // final int[] tw = threadVC.getWriteClocks(lastAcquireThreadId);
  //
  // // is prior enter_monitor concurrent with this enter_monitor?
  // // is any clock in lastAcquireVectorClock greater than clock in tw?
  // // for (int i = 0; i < lastAcquireVectorClock.length; ++i)
  // // {
  // if (lastAcquireClock > tw[lastAcquireThreadId])
  // {
  // // concurrent, so add the clock as a race
  // raceClocksReplacement.add(lastAcquireClock);
  //
  // }
  // // }
  // }
  // }
  //
  // final boolean write = prevOp.getOpType().isWrite(lazyLocks);
  //
  // final int lastWriteThreadId = oData.getLastWriteThreadId();
  // if (lastWriteThreadId >= 0 && (!write || oData.isReadEmpty()))
  // {
  // final int[] ow = oData.getWriteClocks(0);
  // final int owLength = oData.getWriteClocksLength();
  // final int[] tw = threadVC.getWriteClocks(owLength);
  //
  // if (lastWriteThreadId >= 0
  // && ow[lastWriteThreadId] > tw[lastWriteThreadId])
  // {
  // int clock = ow[lastWriteThreadId];
  // if (clock != currThreadData.wokenClock)
  // {
  // clock = moveClockToEmptyLockset(
  // clock,
  // tw[lastWriteThreadId],
  // currThreadData);
  // if (clock > 0)
  // {
  // raceClocks.add(clock);
  // }
  // }
  // }
  //
  // for (int i = 0; i < owLength; ++i)
  // {
  // if (ow[i] > tw[i])
  // {
  // tw[i] = ow[i];
  // }
  // }
  // }
  // else if (write && !oData.isReadEmpty())
  // {
  // // Previous reads exist.
  // // Check if concurrent.
  // final int size = oData.getReadsMaskSize();
  // final int[] reads = oData.getReads();
  // final byte[] readsMask = oData.getReadsMask();
  // final int[] threadClocks = threadVC.getWriteClocks(size - 1);
  // for (int i = 0; i < size; ++i)
  // {
  // // concurrent
  // if (reads[i] > threadClocks[i])
  // {
  // if (readsMask[i] != 0)
  // {
  // int clock = reads[i];
  // // at present, no reads can wake up a thread
  // checkState(clock != currThreadData.wokenClock);
  // if (clock != currThreadData.wokenClock)
  // {
  // clock = moveClockToEmptyLockset(
  // clock,
  // threadClocks[i],
  // currThreadData);
  // if (clock > 0)
  // {
  // raceClocks.add(clock);
  // }
  // }
  // }
  // threadClocks[i] = reads[i];
  // }
  // }
  // }
  //
  // if (write)
  // {
  // oData.setWriteToWrite(threadVC);
  // oData.resetReadClocks();
  // oData.setWritePos(currThreadData.threadId);
  // }
  // else
  // {
  // oData.joinReadWithWrite(threadVC);
  // oData.addToReadMask(currThreadData.threadId);
  // }
  //
  // if (prevOp.getOpType().isThreadOp())
  // {
  // raceClocks.clear();
  // }
  //
  // if (raceClocksReplacement.isEmpty())
  // {
  // return raceClocks;
  // }
  // else
  // {
  // return raceClocksReplacement;
  // }
  // }
  //
  // private final void unpruneDpor()
  // {
  // // for (final ThreadData thread : threadList)
  // // {
  // // if (!thread.running)
  // // {
  // // continue;
  // // }
  // //
  // // // if (executionManager.sleepSets
  // // // && currNode.sleepContains(thread.creationOp))
  // // // {
  // // // continue;
  // // // }
  // //
  // // final SyncObjectData oData = thread.currOpSyncObjectData;
  // //
  // // if (dporIgnoreThreadEvents && thread.currOpType.isThreadOp())
  // // {
  // // continue;
  // // }
  // //
  // // if (lazyLocks && thread.currOpType.isExclusiveMutexOp())
  // // {
  // // continue;
  // // }
  // // if (dporAcquireOptimisation
  // // && thread.currOpType.equals(OpType.ENTER_MONITOR))
  // // {
  // // final int lastAcquireClock = oData.getQueueData().lastAcquireClock;
  // // if (lastAcquireClock != -1)
  // // {
  // // final int lastWriteThreadId = oData.getLastWriteThreadId();
  // // final int lastWriteClock = oData.getWriteClock(lastWriteThreadId);
  // //
  // // if (lastWriteClock > thread.threadVectorClock
  // // .getWriteClock(lastWriteThreadId))
  // // {
  // // unpruneDporThread(thread, lastAcquireClock);
  // // }
  // // }
  // // continue;
  // // }
  // //
  // // final boolean write = thread.currOpType.isWrite();
  // //
  // // if (!write || oData.isReadEmpty())
  // // {
  // // // Is the potential op concurrent with the previous write?
  // // final int clockOfPrevConcurrentWrite =
  // // oData
  // // .prevConcurrentWrite(thread.threadId, thread.threadVectorClock);
  // //
  // // if (clockOfPrevConcurrentWrite != 0)
  // // {
  // // unpruneDporThread(thread, clockOfPrevConcurrentWrite);
  // // }
  // // }
  // // else
  // // // op is a write and readset is not empty
  // // //if (write && !oData.isReadEmpty())
  // // {
  // // // Previous reads exist.
  // // // Check if concurrent.
  // // final int size = oData.getReadsMaskSize();
  // // final int[] reads = oData.getReads();
  // // final byte[] readsMask = oData.getReadsMask();
  // // final int[] threadClocks =
  // // thread.threadVectorClock.getWriteClocks(size - 1);
  // //
  // //
  // // for (int i = 0; i < size; ++i)
  // // {
  // // if (readsMask[i] == 0)
  // // {
  // // continue;
  // // }
  // // // concurrent
  // // if (reads[i] > threadClocks[i])
  // // {
  // // unpruneDporThread(thread, reads[i]);
  // // }
  // // }
  // //
  // // }
  // // }
  // throw new RuntimeException();
  // }
  //
  // private final void unpruneSourceDpor(
  // final List<Integer> races,
  // final Op prevOp,
  // final SyncObjectData prevOpSyncObjectData,
  // final ThreadData currThreadData)
  // {
  // for (final Integer clock : races)
  // {
  // unpruneSourceDporThread(currThreadData, clock, prevOp);
  // // unpruneDporThread2(currThreadData, clock);
  // }
  // // final SyncObjectData oData = currThreadData.currOpSyncObjectData;
  // //
  // // if (currThreadData.currOpType.isThreadOp())
  // // {
  // // return;
  // // }
  // //
  // // if (lazyLocks && currThreadData.currOpType.isExclusiveMutexOp())
  // // {
  // // return;
  // // }
  // // if (currThreadData.currOpType.equals(OpType.ENTER_MONITOR))
  // // {
  // // final int lastAcquireClock = oData.getQueueData().lastAcquireClock;
  // // if (lastAcquireClock != -1)
  // // {
  // // final int lastWriteThreadId = oData.getLastWriteThreadId();
  // // final int lastWriteClock = oData.getWriteClock(lastWriteThreadId);
  // //
  // // if (lastWriteClock > currThreadData.threadVectorClock
  // // .getWriteClock(lastWriteThreadId))
  // // {
  // // unpruneSourceDporThread(currThreadData, lastAcquireClock);
  // // }
  // // }
  // // return;
  // // }
  // //
  // // final boolean write = currThreadData.currOpType.isWrite();
  // //
  // // if (!write || oData.isReadEmpty())
  // // {
  // // // Is the potential op concurrent with the previous write?
  // // final int clockOfPrevConcurrentWrite =
  // // oData.prevConcurrentWrite(
  // // currThreadData.threadId,
  // // currThreadData.threadVectorClock);
  // //
  // // if (clockOfPrevConcurrentWrite != 0)
  // // {
  // // unpruneSourceDporThread(currThreadData, clockOfPrevConcurrentWrite);
  // // }
  // // }
  // // else
  // // // op is a write and readset is not empty
  // // //if (write && !oData.isReadEmpty())
  // // {
  // // // Previous reads exist.
  // // // Check if concurrent.
  // // final int size = oData.getReadsMaskSize();
  // // final int[] reads = oData.getReads();
  // // final byte[] readsMask = oData.getReadsMask();
  // // final int[] threadClocks =
  // // currThreadData.threadVectorClock.getWriteClocks(size - 1);
  // //
  // //
  // // for (int i = 0; i < size; ++i)
  // // {
  // // if (readsMask[i] == 0)
  // // {
  // // continue;
  // // }
  // // // concurrent
  // // if (reads[i] > threadClocks[i])
  // // {
  // // unpruneSourceDporThread(currThreadData, reads[i]);
  // // }
  // // }
  // //
  // // }
  // }
  //
  // private static class FirstStep
  // {
  // public int clock;
  // public int threadId;
  // public Node childNode;
  // public Op op;
  // public boolean found = false;
  // public boolean ha = false;
  //
  // public FirstStep(
  // final int clock,
  // final int threadId,
  // final Node childNode,
  // final Op op)
  // {
  // this.clock = clock;
  // this.threadId = threadId;
  // this.childNode = childNode;
  // this.op = op;
  // }
  // }
  //
  // private final void
  // unpruneDporThread2(final ThreadData thread, final int clock)
  // {
  // final Node parent = nodeList.get(clock - 1);
  //
  // OutEdge childEdge = null;
  // for (final OutEdge edge : parent.getChildren())
  // {
  // if (edge.op.getThread().equals(thread.creationOp)
  // && (!parent.sleepContainsThread(edge.op.getThread())))
  // {
  // childEdge = edge;
  // break;
  // }
  // }
  //
  // if (childEdge != null)
  // {
  // if (childEdge.node.dporPruned)
  // {
  // childEdge.node.dporPruned = false;
  // executionManager.addUnexploredNode(childEdge.node);
  // }
  // }
  // else
  // {
  // for (final OutEdge c : parent.getChildren())
  // {
  // if (c.node.dporPruned && !parent.sleepContainsThread(c.op.getThread()))
  // {
  // c.node.dporPruned = false;
  // executionManager.addUnexploredNode(c.node);
  // }
  // }
  // }
  // }
  //
  // private boolean alreadyUnpruned(
  // final FirstStep firstStep,
  // final Node parentOfRacey)
  // {
  // checkState(firstStep.found);
  //
  // return !firstStep.ha
  // && (!firstStep.childNode.dporPruned || firstStep.childNode.visited ||
  // parentOfRacey.sleepContainsThread(firstStep.op.getThread()));
  // }
  //
  // private void unpruneSourceDporThread(
  // final ThreadData currThreadData,
  // final int clock,
  // final Op prevOp)
  // {
  // final Node parentOfRacey = nodeList.get(clock - 1);
  // final Node childOfRacey = nodeList.get(clock);
  // final Op raceyOp = parentOfRacey.getOpTo(childOfRacey);
  //
  // // first step ops
  // final Map<Op, FirstStep> firstStepOpToInfo = new HashMap<>();
  // for (final OutEdge edge : parentOfRacey.getChildren())
  // {
  // firstStepOpToInfo.put(
  // edge.op,
  // new FirstStep(
  // 0,
  // getThreadData(edge.op.getThread()).threadId,
  // edge.node,
  // edge.op));
  // // add prev op's thread if enabled.
  // if (edge.op.getThread().equals(currThreadData.creationOp))
  // {
  // firstStepOpToInfo.get(edge.op).found = true;
  // if (alreadyUnpruned(firstStepOpToInfo.get(edge.op), parentOfRacey))
  // {
  // return;
  // }
  // }
  // }
  //
  // firstStepOpToInfo.get(raceyOp).ha = true;
  // firstStepOpToInfo.get(raceyOp).found = true;
  //
  // // from racey op to last op (including prev op)
  // for (int i = clock; i <= nodeIndex; i++)
  // {
  // final Node parent = nodeList.get(i - 1);
  // final Node child = nodeList.get(i);
  // final Op op = parent.getOpTo(child);
  // for (final FirstStep firstStep : firstStepOpToInfo.values())
  // {
  // if (!firstStep.found)
  // {
  // if (firstStep.op.equals(op))
  // {
  // firstStep.found = true;
  // if (alreadyUnpruned(firstStep, parentOfRacey))
  // {
  // return;
  // }
  // }
  // else
  // {
  // // dependent
  // if (op.getSyncObject().equals(firstStep.op.getSyncObject())
  // && (op.getOpType().isWrite(false) || firstStep.op.getOpType()
  // .isWrite(false)))
  // {
  // firstStep.ha = true;
  // }
  // }
  // }
  //
  // }
  // }
  //
  // FirstStep firstStepToUnprune = null;
  // for (final FirstStep firstStep : firstStepOpToInfo.values())
  // {
  // if (firstStep.found
  // && !firstStep.ha
  // && firstStep.op.getThread().equals(prevOp.getThread()))
  // {
  // firstStepToUnprune = firstStep;
  // break;
  // }
  // }
  //
  // if (firstStepToUnprune == null)
  // {
  // for (final FirstStep firstStep : firstStepOpToInfo.values())
  // {
  // if (firstStep.found && !firstStep.ha)
  // {
  // firstStepToUnprune = firstStep;
  // break;
  // }
  // }
  // }
  //
  // if (firstStepToUnprune == null)
  // {
  // System.out.println("WARNING: Could not unprune.");
  // System.out.println("raceyOp: " + raceyOp);
  // System.out.println("prevOp: " + prevOp);
  // for (final FirstStep firstStep : firstStepOpToInfo.values())
  // {
  // System.out.println("--");
  // System.out.println(firstStep.op);
  // System.out.println(firstStep.found + ", " + firstStep.ha);
  // }
  // return;
  // }
  //
  // final Node unpruneNode = firstStepToUnprune.childNode;
  // checkState(unpruneNode.dporPruned);
  // unpruneNode.dporPruned = false;
  // checkState(unpruneNode.getAParent() == parentOfRacey);
  // checkState(!parentOfRacey.sleepContainsThread(firstStepToUnprune.op.getThread()));
  // executionManager.addUnexploredNode(unpruneNode);
  // return;
  //
  // // throw new ArrayIndexOutOfBoundsException();
  //
  // // final List<FirstStep> firstStepsToConsider = new ArrayList<>();
  // // final Map<Op, FirstStep> threadToFirstStep = new HashMap<>();
  // //
  // // for (final OutEdge edge : parentOfRacey.getChildren())
  // // {
  // // threadToFirstStep.put(edge.op.getThread(), new FirstStep(
  // // 0,
  // // getThreadData(edge.op.getThread()).threadId,
  // // edge.node, edge.op));
  // // }
  // //
  // // final int raceyOpThreadId = getThreadData(raceyOp.getThread()).threadId;
  // //
  // // int numFirstStepsFound = 0;
  // //
  // // // from after racey op to last op (including prev op)
  // // foreachop:
  // // for (int i = clock + 1; i <= nodeIndex; i++)
  // // {
  // // if (numFirstStepsFound >= threadToFirstStep.size())
  // // {
  // // break;
  // // }
  // // final Node parent = nodeList.get(i - 1);
  // // final Node child = nodeList.get(i);
  // // final Op op = parent.getOpTo(child);
  // //
  // // final FirstStep firstStep = threadToFirstStep.get(op.getThread());
  // //
  // // if (firstStep != null && firstStep.clock == 0)
  // // {
  // // firstStep.clock = i;
  // // numFirstStepsFound++;
  // // // found a first step
  // // final int[] firstStepVC = vectorClocks.get(firstStep.clock-1);
  // //
  // // // must not happen-after racey event, unless it is the last op
  // // if (i < nodeIndex
  // // && firstStepVC.length - 1 >= raceyOpThreadId
  // // && firstStepVC[raceyOpThreadId] >= clock)
  // // {
  // // // happens after e
  // // firstStep.clock = -1;
  // // continue foreachop;
  // // }
  // //
  // // // does it happen after others?
  // // for (final FirstStep otherFirstStep : firstStepsToConsider)
  // // {
  // // checkState(otherFirstStep.clock > 0);
  // // if (firstStepVC.length - 1 >= otherFirstStep.threadId
  // // && firstStepVC[otherFirstStep.threadId] >= otherFirstStep.clock)
  // // {
  // // // happens after other
  // // firstStep.clock = -1;
  // // continue foreachop;
  // // }
  // //
  // // }
  // //
  // // if (!firstStep.childNode.dporPruned
  // // || firstStep.childNode.visited
  // // || parentOfRacey.sleepContains(firstStep.op.getThread()))
  // // {
  // // // done - already unpruned a suitable thread
  // // return;
  // // }
  // // firstStepsToConsider.add(firstStep);
  // // }
  // // }
  // //
  // // final Node unpruneNode = firstStepsToConsider.get(0).childNode;
  // // checkState(unpruneNode.dporPruned);
  // // unpruneNode.dporPruned = false;
  // // checkState(unpruneNode.getAParent() == parentOfRacey);
  // // checkState(!parentOfRacey.sleepContains(firstStepsToConsider.get(0).op
  // // .getThread()));
  // // executionManager.addUnexploredNode(unpruneNode);
  // }

}

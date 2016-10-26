package org.jtool.strategy;

import java.util.ArrayList;
import java.util.List;

import org.jtool.runtime.Op;
import org.jtool.runtime.ThreadData;

import com.carrotsearch.hppc.LongOpenHashSet;

public final class HBGCache
{
  private final LongOpenHashSet cache = new LongOpenHashSet();
  private final boolean lazyLocks;
  
  public HBGCache(final boolean lazyLocks)
  {
    super();
    this.lazyLocks = lazyLocks;
  }

  public void addExecutionHash(final ExecutionHasher executionHasher)
  {
    cache.add(lazyLocks
        ? executionHasher.getLazyHash()
        : executionHasher.getNormalHash());
  }

  public void prune(
      final TidListStack stack,
      final List<ThreadData> threadList,
      final ExecutionHasher executionHasher)
  {
    final long currentHash = lazyLocks
        ? executionHasher.getLazyHash()
        : executionHasher.getNormalHash();
    
    final ArrayList<TidEntry> top = stack.getTopOfStack().getInternalList();
    for(final TidEntry entry : top)
    {
      if (entry.sleep)
      {
        continue;
      }
      final ThreadData threadData = threadList.get(entry.id);
      Op op = threadData.getCurrOp();
      if (lazyLocks)
      {
        op = op.asLazyOp();
      }
      final long opHash = ExecutionHasher.hashOf(op);
      final long newHash = currentHash ^ opHash;
      if (cache.contains(newHash))
      {
        entry.sleep = true;
      }
    }
  }
  
}

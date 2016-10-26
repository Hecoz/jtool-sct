package org.jtool.strategy;

import org.jtool.runtime.Op;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public final class ExecutionHasher
{
  public static final HashFunction hashFunction = Hashing.murmur3_128(0);

  private long normalHash = 0;
  private long lazyHash = 0;
  
  public long getNormalHash()
  {
    return normalHash;
  }
  
  public long getLazyHash()
  {
    return lazyHash;
  }
  
  public static long hashOf(final Op op)
  {
    return ExecutionHasher.hashFunction.hashObject(op, Op.hashFunnel).asLong();
  }

  public void addOp(final Op op)
  {
    final Op lazyOp = op.getOpType().isExclusiveMutexOp()
        ? op.asLazyOp()
        : op;

    final long hashOfPrevOpNormal = ExecutionHasher.hashOf(op);
    final long hashOfPrevOpLazy = (lazyOp != op)
        ? ExecutionHasher.hashOf(lazyOp)
        : hashOfPrevOpNormal;

    lazyHash = lazyHash ^ hashOfPrevOpLazy;
    normalHash = normalHash ^ hashOfPrevOpNormal;
  }

}

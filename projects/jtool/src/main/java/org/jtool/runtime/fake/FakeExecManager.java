package org.jtool.runtime.fake;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jtool.runtime.Node;
import org.jtool.runtime.Op;

import com.google.common.collect.Iterables;

public class FakeExecManager
{
  

  public static void go(final Node node, final Op inOp, final int bcountOfInOp,
      final List<Op> threadOrder)
  {
    final Set<FakeExecutor> executors = new HashSet<>();
    executors.add(new FakeExecutor(inOp, node, threadOrder));

    FakeExecutor exec = null;
    while (!executors.isEmpty())
    {
      exec = checkNotNull(Iterables.getFirst(executors, null));
      executors.remove(exec);
      
      exec.updateOutNumBEvents(executors);

    }

  }
}

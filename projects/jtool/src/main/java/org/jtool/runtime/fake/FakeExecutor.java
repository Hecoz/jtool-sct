package org.jtool.runtime.fake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jtool.runtime.Node;
import org.jtool.runtime.Node.OutEdge;
import org.jtool.runtime.Op;

public class FakeExecutor
{
  public Op inOp;
  public Node node;
  public List<Op> threadOrder;
  
  public FakeExecutor(final Op inOp, final Node node, final List<Op> threadOrder)
  {
    this.inOp = inOp;
    this.node = node;
    this.threadOrder = threadOrder;
  }

  public void updateOutNumBEvents(final Set<FakeExecutor> executors)
  {
    // [op,      null,        op,       ...]
    //  t1's op, t2 disabled, t3's op,  etc.
    final Op[] opList = new Op[threadOrder.size()];
    
    // enabled thread -> op
    final Map<Op, Op> enabledThreads = new HashMap<>(node.numChildren());
    
    for (final OutEdge edge : node.getChildren())
    {
      enabledThreads.put(edge.op.getThread(), edge.op);
    }
    
    // populate opList
    for(int i=0; i < threadOrder.size(); i++)
    {
      final Op thread = threadOrder.get(i);
      final Op op = enabledThreads.get(thread);
      // will be null if thread is disabled
      opList[i] = op;
    }
    
    final int bcountOfInOp = node.getBoundCountSafe(inOp);
    
    // make index point to previous thread
    int index = 0;
    while (!threadOrder.get(index).equals(inOp.getThread()))
    {
      index++;
    }
    
    int delays = 0;
    for (int zzz = 0; zzz < opList.length; zzz++)
    {
      // zzz is not used in body - we use index

      final Op nextOp = opList[index];
      if (nextOp != null)
      {
        // nextOp is enabled, so set/update its bound count
        
        final int newBoundCount = bcountOfInOp + delays;
        final Node child = node.getChild(nextOp);
        int boundCount = child.getBoundCount(nextOp);
        if (boundCount == -1)
        {
          boundCount = Integer.MAX_VALUE;
        }
        if (newBoundCount < boundCount)
        {
          child.updateBoundCount(nextOp, newBoundCount);
          if (child.numChildren() > 0)
          {
            List<Op> threadOrderOfChild = threadOrder;
            if (nextOp.createsThread())
            {
              threadOrderOfChild = new ArrayList<>(threadOrder);
              threadOrderOfChild.add(nextOp);
            }
            executors.add(new FakeExecutor(nextOp, child, threadOrderOfChild));
          }
        }
        delays++;
      }
      index = (index + 1) % opList.length;
    }
  }

}

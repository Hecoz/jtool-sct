package org.jtool.runtime;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Node implements Comparable<Node>
{
  private final ArrayList<Node> incoming = new ArrayList<>(1);
  private final ArrayList<OutEdge> outgoing = new ArrayList<>(1);
  
  private final ArrayList<Op> sleepOps = new ArrayList<>(1);

  public long hash = 0;
  public int nodeId = 0;
  public boolean visited = false;
  public boolean dporPruned = true;
  
  
  public static class OutEdge
  {
    public Op op;
    public Node node;
    public int boundCount;
    
    public OutEdge(final Op op, final Node node, final int boundCount)
    {
      this.op = op;
      this.node = node;
      this.boundCount = boundCount;
    }
  }

  public static class Edge
  {
    public Op op;
    public int boundCount;
    public Node node;
    
    public Edge(final Op op, final int boundCount, final Node node)
    {
      this.op = op;
      this.boundCount = boundCount;
      this.node = node;
    }

  }

  public Node(final int nodeId)
  {
    this.nodeId = nodeId;
  }
  
  public final List<Edge> getEdges()
  {
    final List<Edge> res = new ArrayList<>();
    for (final Node parent : incoming)
    {
      for (final OutEdge edge : parent.outgoing)
      {
        if (edge.node == this)
        {
          res.add(new Edge(edge.op, edge.boundCount, parent));
        }
      }
    }
    return res;
  }
  
  public final Set<Node> getChildSet()
  {
    final Set<Node> res = new HashSet<>();
    
    for (final OutEdge i : outgoing)
    {
      res.add(i.node);
    }
    
    return res;
  }

  public final int getMinBoundCount()
  {
    //    int minBc = Integer.MAX_VALUE;
    //    for (final Edge i : incoming)
    //    {
    //      minBc = Math.min(i.boundCount, minBc);
    //    }
    //    if (minBc == Integer.MAX_VALUE)
    //    {
    //      minBc = -1;
    //    }
    //    checkState(minBc != -1);
    //    return minBc;
    return 0;
  }

  public final int numChildren()
  {
    return outgoing.size();
  }
  
  public final void addChild(final Op op, final Node child)
  {
    for (final OutEdge i : outgoing)
    {
      checkState(!i.op.equals(op));
    }
    
    outgoing.add(new OutEdge(op, child, -1));

    child.addParent(op, this);
  }
  
  private void addParent(final Op op, final Node parent)
  {
    for (final Node i : incoming)
    {
      checkState(!i.equals(parent));
    }
    incoming.add(parent);
  }

  public final Node getChild(final Op op)
  {
    return checkNotNull(getChildOrNull(op));
  }
  
  public final OutEdge getOnlyChildAndOp()
  {
    checkState(outgoing.size() == 1);
    return outgoing.get(0);
  }

  public final Node getChildOrNull(final Op op)
  {
    for (final OutEdge i : outgoing)
    {
      if (i.op.equals(op))
      {
        return i.node;
      }
    }
    return null;
  }
  
  public final int getOutBoundCount(final Op outOp)
  {
    for (final OutEdge out : outgoing)
    {
      if (out.op.equals(outOp))
      {
        return out.boundCount;
      }
    }
    return -1;
  }

  public final int getBoundCount(final Op inOp)
  {
    for (final Edge inEdge : getEdges())
    {
      if (inEdge.op.equals(inOp))
      {
        return inEdge.boundCount;
      }
    }
    return -1;
  }

  public final int getBoundCountSafe(final Op inOp)
  {
    final int res = getBoundCount(inOp);
    checkState(res != -1);
    return -1;
  }
  
  public final void setBoundCount(final Op outOp, final int bc)
  {
    for (final OutEdge i : outgoing)
    {
      if (i.op.equals(outOp))
      {
        checkState(i.boundCount == -1 || bc <= i.boundCount);
        i.boundCount = bc;
        return;
      }
    }
    throw new IndexOutOfBoundsException();
  }

  public final void updateBoundCount(final Op inOp, final int bc)
  {
    for (final Edge inEdge : getEdges())
    {
      if (inEdge.op.equals(inOp))
      {
        inEdge.node.setBoundCount(inEdge.op, bc);
        return;
      }
    }
    throw new IllegalArgumentException("Cannot find incoming op!");
  }

  public final boolean hasOp(final Op op)
  {
    return getChildOrNull(op) != null;
  }

  public final Op getOpTo(final Node child)
  {
    return checkNotNull(getOpToOrNull(child));
  }
  
  public final Op getOpToOrNull(final Node child)
  {
    for (final OutEdge i : outgoing)
    {
      if (i.node.equals(child))
      {
        return i.op;
      }
    }
    return null;
  }
  
  public final int getNumParents()
  {
    return incoming.size();
  }

  public final Node getAParent()
  {
    if (incoming.size() == 0)
    {
      return null;
    }
    return incoming.get(0);
  }
  
  public final Edge getAParentAndOp()
  {
    if (incoming.size() == 0)
    {
      return null;
    }
    
    final Node parentNode = incoming.get(0);
    final Op op = parentNode.getOpTo(this);
    final int bc = parentNode.getOutBoundCount(op);
    
    final Edge edge = new Edge(op, bc, parentNode);
    return edge;
  }

  //  @Override
  //  public final String toString()
  //  {
  //    // String parentOp = "null";
  //    // if (parent != null)
  //    // {
  //    // parentOp = parent.getOpTo(this).toString();
  //    // }
  //    // return "(" + parentOp + "->" + nodeId + ":" + numChildren() + ")";
  //    return "Node(" + nodeId + ")";
  //  }
  
  public final int getNumChildren()
  {
    return outgoing.size();
  }

  public final List<OutEdge> getChildren()
  {
    return outgoing;
  }

  @Override
  public final int compareTo(final Node o)
  {
    return nodeId - o.nodeId;
  }
  
  public void nullVisitedChildrenChildren()
  {
    for (final OutEdge i : outgoing)
    {
      if (i.node.visited)
      {
        for (final OutEdge j : i.node.outgoing)
        {
          j.node.outgoing.clear();
        }
      }
    }
  }
  
  public void nullOutgoing()
  {
    outgoing.clear();
  }

  public void addToSleep(final Op op)
  {
    for (final Op i : sleepOps)
    {
      if (i.getThread().equals(op.getThread()))
      {
        throw new IllegalArgumentException();
      }
    }
    
    sleepOps.add(op);
  }
  
  //  public void removeFromSleep(final Op op)
  //  {
  //    for (int i=0, size = sleepOps.size(); i < size; ++i)
  //    {
  //      if (sleepOps.get(i).equals(op))
  //      {
  //        sleepOps.remove(i);
  //        return;
  //      }
  //    }
  //    
  //    throw new IllegalArgumentException();
  //  }


  public boolean sleepContainsThread(final Op thread)
  {
    for (final Op i : sleepOps)
    {
      if (i.getThread().equals(thread))
      {
        return true;
      }
    }
    return false;
  }
  
  public List<Op> getSleepSet()
  {
    return sleepOps;
  }
  
  public interface TestEdge
  {
    public boolean test(Node node, OutEdge edge);
  }

  public OutEdge getNextInOrder(final Op prevOp,
      final List<ThreadData> threadList, final TestEdge predicate)
  {
    int prevIndex = 0;
    while (!threadList.get(prevIndex).creationOp.equals(prevOp.getThread()))
    {
      ++prevIndex;
    }

    for (int i = 0, size = threadList.size(); i < size; ++i)
    {
      final ThreadData nextThread = threadList.get((prevIndex + i) % size);
      for (final OutEdge edge : outgoing)
      {
        if (edge.op.getThread().equals(nextThread.creationOp))
        {
          if (predicate.test(this, edge))
          {
            return edge;
          }
        }
      }
    }
    
    return null;
  }

  @Override
  public String toString()
  {
    if (!incoming.isEmpty())
    {
      final Op op = incoming.get(0).getOpTo(this);
      return "Incoming op is: " + op;
    }
    return "NodeId: " + nodeId;
  }

  public Set<Op> getOps()
  {
    final HashSet<Op> ops = new HashSet<>();
    for (final Edge e : getEdges())
    {
      ops.add(e.op);
    }
    return ops;
  }
  
  public Op getOp(final Op thread)
  {
    for (final OutEdge i : outgoing)
    {
      if (i.op.getThread().equals(thread))
      {
        return i.op;
      }
    }
    return null;
  }
}

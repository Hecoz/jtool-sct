package org.jtool.runtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

class MonitorNodeEdge
{
  public final int threadIndex;
  public final SyncObjectData child;
  
  public MonitorNodeEdge(final int threadIndex, final SyncObjectData child)
  {
    this.threadIndex = threadIndex;
    this.child = child;
  }
  
  @Override
  public int hashCode()
  {
    return Objects.hash(threadIndex, child.hashCode());
  }
  
  @Override
  public boolean equals(final Object obj)
  {
    if (obj == null)
    {
      return false;
    }
    if (!(obj instanceof MonitorNodeEdge))
    {
      return false;
    }
    
    final MonitorNodeEdge other = (MonitorNodeEdge) obj;
    
    return threadIndex == other.threadIndex
        && Objects.equals(child, other.child);
  }
}

class MonitorNode
{
  public final Set<MonitorNodeEdge> children = new HashSet<>(4);
  
  public void addEdge(final int threadIndex, final SyncObjectData child)
  {
    children.add(new MonitorNodeEdge(threadIndex, child));
  }
}

public class MonitorGraph
{
  private final Map<SyncObjectData, MonitorNode> nodes = new HashMap<>();
  
  private MonitorNode getNode(final SyncObjectData sod)
  {
    MonitorNode res = nodes.get(sod);
    if (res == null)
    {
      res = new MonitorNode();
      nodes.put(sod, res);
    }
    return res;
  }
  
  public void addEdge(final SyncObjectData from, final int threadIndex,
      final SyncObjectData to)
  {
    final MonitorNode fromNode = getNode(from);
    // ensure to node is added
    getNode(to);

    fromNode.addEdge(threadIndex, to);
  }
  
  public boolean findCycle()
  {
    final Set<SyncObjectData> seenGlobal = new HashSet<>();

    for (final Entry<SyncObjectData, MonitorNode> sod_node : nodes.entrySet())
    {
      final SyncObjectData sod = sod_node.getKey();
      final MonitorNode node = sod_node.getValue();
      final Set<SyncObjectData> seenPath = new HashSet<>();
      final Set<Integer> seenPathTids = new HashSet<>();
      //      if (!seenGlobal.contains(sod))
      //      {
        final boolean res =
            findCycle(seenGlobal, seenPath, seenPathTids, null, sod, node);
        if (res)
        {
          return true;
        }
      //      }
    }
    return false;
  }
  
  private boolean findCycle(final Set<SyncObjectData> seenGlobal,
      final Set<SyncObjectData> seenPath, final Set<Integer> seenPathTids,
      final Integer viaTid,
      final SyncObjectData sod,
      final MonitorNode node)
  {
    seenGlobal.add(sod);
    if (seenPath.contains(sod))
    {
      return true;
    }
    if (viaTid != null && seenPathTids.contains(viaTid))
    {
      return false;
    }
    
    final Set<SyncObjectData> nextSeenPath = new HashSet<>(seenPath);
    nextSeenPath.add(sod);
    final Set<Integer> nextSeenPathTids = new HashSet<>(seenPathTids);
    nextSeenPathTids.add(viaTid);
    
    for (final MonitorNodeEdge edge : node.children)
    {
      final boolean res =
          findCycle(
              seenGlobal,
              nextSeenPath,
              nextSeenPathTids,
              edge.threadIndex,
              edge.child,
              getNode(edge.child));
      if (res)
      {
        return true;
      }
    }
    return false;
  }

}

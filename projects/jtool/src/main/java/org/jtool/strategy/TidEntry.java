package org.jtool.strategy;

public class TidEntry
{
  public final int id;
  // This field is currently redundant -- it is always true.
  public boolean enabled = false;
  public boolean done = false;
  public boolean sleep = false;
  public boolean selected = false;
  public boolean backtrack = false;
  public boolean avoid = false;

  public TidEntry(
      final int id,
      final boolean enabled)
  {
    this.id = id;
    this.enabled = enabled;
  }
  
  @Override
  public String toString()
  {
    return (enabled ? "" : "-") + id + (done ? "*" : "");
  }
  
}

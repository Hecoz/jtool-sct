package fse2006apps;

import java.util.Vector;
import java.util.Enumeration;
import fse2006replicatedworkers.ResultItemComputation;

public class AQResults implements ResultItemComputation
{
  private final float value;
  private final Main main;

   // Empty work object used to bind result computation to abstraction
  //public AQResults() { value = (float)0.0; }

  public AQResults(final Main main, final float f)
  {
    this.main = main;
    value = f;
  }

   public boolean doResults()
   {
    main.totalsum += value;
     return false;
   }
}

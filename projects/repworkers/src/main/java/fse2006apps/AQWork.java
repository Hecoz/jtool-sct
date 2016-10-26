package fse2006apps;

import java.lang.*;
import java.lang.Math;
import java.util.Vector;
import java.util.Enumeration;
import fse2006replicatedworkers.WorkItemComputation;

public class AQWork implements WorkItemComputation
{
  private final float start;
  private final float end;
  private final float epsilon;
  private final Main main;

   // Empty work object used to bind work computation to abstraction
  //public AQWork() {}

  public AQWork(final Main main, final float s, final float e, final float eps)
   {
    this.main = main;
     start = s;
     end = e;
     epsilon = eps;
   }

   @Override
  public String toString()
   {
     return "AQWork.interval[" + start + ", " + end + ", " + epsilon + "]"; 
   }

   private float f(final float x)
   {
     return x*x - x;
   }

   public boolean doWork(final Vector newWork, final Vector theResults)
   {
     float  x1,x2,x3;

     //System.out.println(this.toString());

     x1 = (float)((end + start)/2.0);
     x2 = (float)((start + x1)/2.0);
     x3 = (float)((end + x1)/2.0);
      
     if ( Math.abs(f(x2)/2.0 + f(x3)/2.0 - f(x1))/10.0 >= epsilon ) {
       // Create new half-intervals
      newWork.addElement(new AQWork(main, start, x1, epsilon));
      newWork.addElement(new AQWork(main, x1, end, epsilon));

     } else {
      theResults
          .addElement(new AQResults(main, (x1 - start) * (f(x2) + f(x3))));
     }
     return false;
   } // end public boolean doWork(Vector newWork, Vector theResults)
}

package fse2006apps;

import fse2006replicatedworkers.ReplicatedWorkers;
import fse2006replicatedworkers.Configuration;
import java.util.Vector;
import java.lang.*;

/**
 * This example was originally created by Dr. Dwyer but
 * was re-written by Todd Wallentine to make it easier to
 * model check in a reasonable amount of time and see how
 * bad the performance scales as the command line arguments
 * change (to modify the number of workers and number of
 * work items).
 * 
 * @author Matt Dwyer matt AT cse unl edu
 * @author Todd Wallentinetcw AT cis ksu edu
 * @version $Revision: 1.1 $ - $Date: 2005/10/10 20:00:18 $
 */
public class Main {
    /**
     * The default number of workers to use.
     */
    public static int DEFAULT_NUM_WORKERS = 2;  // CHANGE from 3

    /**
     * The default number of work items.
     */
    public static int DEFAULT_NUM_ITEMS = 2;
    
    /**
     * The default minimum for the work item.
     */
    public static final float DEFAULT_MIN = (float) 0.0;
    
    /**
     * The default maxmimum for the work item.
     */
    public static final float DEFAULT_MAX = (float)10.0;
    
    /**
     * The default epsilon for the work item.
     */
    public static final float DEFAULT_EPSILON = (float)0.05;   // CHANGE from 0.25
  
  public int totalsum;

    /**
     * Start up an instance of the replicated workers example using
     * the command line arguments or the defaults.
     */
  public void main()
  {
        ReplicatedWorkers theInstance;
        Configuration theConfig;
        FIFOCollection workPool;
        FIFOCollection resultPool;
        Vector inputs;

        theConfig = new Configuration(Configuration.EXCLUSIVE, Configuration.SYNCHRONOUS,
                Configuration.SOMEVALUES);
        workPool = new FIFOCollection();
        resultPool = new FIFOCollection();

        final int numWorkers = DEFAULT_NUM_WORKERS;
        final int numItems = DEFAULT_NUM_ITEMS;
        final float min = DEFAULT_MIN;
        final float max = DEFAULT_MAX;
        final float epsilon = DEFAULT_EPSILON;
    //        if ((args != null) && (args.length == 5)) {
    //            numWorkers = Integer.parseInt(args[0]);
    //            numItems = Integer.parseInt(args[1]);
    //            min = Float.parseFloat(args[2]);
    //            max = Float.parseFloat(args[3]);
    //            epsilon = Float.parseFloat(args[4]);
    //        }

        theInstance = new ReplicatedWorkers(theConfig, workPool, resultPool, numWorkers, numItems);
        inputs = new Vector(1);
    inputs.addElement(new AQWork(this, min, max, epsilon));
        theInstance.putWork(inputs);
    totalsum = 0;
        theInstance.execute();
	//System.out.println("result is "+AQResults.totalsum);
        //For EPSILON = .25
        //if ((AQResults.totalsum < 278.1) || (AQResults.totalsum > 278.15)) {
        //For EPSILON = .05
    if ((totalsum < 282.0) || (totalsum > 282.1))
    {
//	   throw new RuntimeException("bug");
	}
        theInstance.destroy();
    }
}

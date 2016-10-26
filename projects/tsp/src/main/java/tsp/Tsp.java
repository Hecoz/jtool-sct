package tsp;
/*
 * Copyright (C) 2000 by ETHZ/INF/CS
 * All rights reserved
 * 
 * @version $Id$
 * @author Florian Schneider
 */

import java.util.*;
import java.io.*;

public class Tsp {
  public final static boolean debug = false;
  public final static int MAX_TOUR_SIZE = 8;
  public final static int MAX_NUM_TOURS = 32;
  public final static int BIGINT = 2000000;
  public final static int END_TOUR = -1;
  public final static int ALL_DONE = -1;
  public int nWorkers = 4;
  static int TspSize = MAX_TOUR_SIZE;
  final static int StartNode = 0;
  final static int NodesFromEnd = 2;
  
  final InputStream input;
  
  Tsp(final InputStream input)
  {
    this.input = input;
  }
  
  public void execute()
  {
    int i;
    final String fname = "testdata";
    final SharedData sh = new SharedData();
    sh.TourStackTop = -1;
    sh.MinTourLen = BIGINT;
    
    TspSize = read_tsp(input, sh);
    
    //final long start = new Date().getTime();
    
    /* init arrays */
    for (i = 0; i < MAX_NUM_TOURS; i++)
    {
      sh.Tours[i] = new TourElement();
      sh.PrioQ[i] = new PrioQElement();
    }
    
    /* Initialize first tour */
    sh.Tours[0].prefix[0] = StartNode;
    sh.Tours[0].conn = 1;
    sh.Tours[0].last = 0;
    sh.Tours[0].prefix_weight = 0;
    TspSolver.calc_bound(0, sh); /* Sets lower_bound */
    
    /* Initialize the priority queue structures */
    sh.PrioQ[1].index = 0;
    sh.PrioQ[1].priority = sh.Tours[0].lower_bound;
    sh.PrioQLast = 1;
    
    /* Put all unused tours in the free tour stack */
    for (i = MAX_NUM_TOURS - 1; i > 0; i--)
    {
      sh.TourStack[++sh.TourStackTop] = i;
    }
    
    /* create worker threads */
    final Thread[] t = new Thread[nWorkers];
    for (i = 0; i < nWorkers; i++)
    {
      t[i] = new TspSolver(sh);
      
    }
    for (i = 0; i < nWorkers; i++)
    {
      t[i].start();
    }
    
    /* wait for completion */
    try
    {
      for (i = 0; i < nWorkers; i++)
      {
        t[i].join();
        // if (debug) System.out.println("joined thread "+i);
      }
    }
    catch (final InterruptedException e)
    {
    }
    
    //final long end = new Date().getTime();
    
    //System.out.println("tsp-" + nWorkers + "\t" + ((int) end - (int) start));
    System.out.println("Minimum tour length: " + sh.MinTourLen);
    System.out.print("Minimum tour:");
    TspSolver.MakeTourString(TspSize, sh.MinTour);
  }
  
  static int read_tsp(final InputStream input, final SharedData sh)
  {
    String line;
    StringTokenizer tok;
    int i, j;
    
    try
    {
      final BufferedReader in =
          new BufferedReader(new InputStreamReader(input));
      //BufferedReader in = new BufferedReader(new FileReader(fname));
      TspSize = Integer.parseInt(in.readLine());
      for (i = 0; i < TspSize; i++)
      {
        line = in.readLine();
        tok = new StringTokenizer(line, " ");
        j = 0;
        while (tok.hasMoreElements())
        {
          sh.weights[i][j++] = Integer.parseInt((String) tok.nextElement());
        }
      }
    }
    catch (final Exception e)
    {
      e.printStackTrace();
      System.exit(-1);
    }
    return (TspSize);
  }
}

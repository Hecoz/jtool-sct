/**************************************************************************
*                                                                         *
*         Java Grande Forum Benchmark Suite - Thread Version 1.0          *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         * 
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/

package raytracer;

import org.jtool.test.ConcurrencyTestCase;

public class JGFRayTracerBenchSizeA implements ConcurrencyTestCase
{
  private final int nthreads;
  
  public JGFRayTracerBenchSizeA(final int nthreads)
  {
    this.nthreads = nthreads;
  }

  public void execute() throws Exception
  {
    final JGFRayTracerBench rtb = new JGFRayTracerBench(nthreads);
    rtb.JGFrun(0);
  }
}



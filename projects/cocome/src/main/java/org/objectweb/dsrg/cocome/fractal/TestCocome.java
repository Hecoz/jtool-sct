package org.objectweb.dsrg.cocome.fractal;

import org.jtool.test.ConcurrencyTestCase;

public class TestCocome implements ConcurrencyTestCase
{
  public final int numCashDesks;

  public TestCocome(final int numCashDesks)
  {
    this.numCashDesks = numCashDesks;
  }

  public void execute() throws Exception
  {
    new Simulator(numCashDesks).main(null);
  }
  
}

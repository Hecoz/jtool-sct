package org.objectweb.dsrg.bpc.demo;

import org.jtool.test.ConcurrencyTestCase;

public class TestCreDemo implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    new Simulator().main();    
  }
}

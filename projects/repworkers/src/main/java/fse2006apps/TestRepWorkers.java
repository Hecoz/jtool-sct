package fse2006apps;

import org.jtool.test.ConcurrencyTestCase;

public class TestRepWorkers implements ConcurrencyTestCase
{
  private final int numworkers;
  private final int numitems;
  
  public TestRepWorkers(int numworkers, int numitems)
  {
    this.numworkers = numworkers;
    this.numitems = numitems;
  }
  
  public void execute() throws Exception
  {
    Main.DEFAULT_NUM_WORKERS = numworkers;
    Main.DEFAULT_NUM_ITEMS = numitems;
    new Main().main();
  }
  
}

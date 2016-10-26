package fse2006wronglock;

import org.jtool.test.ConcurrencyTestCase;

public class TestWrongLock implements ConcurrencyTestCase
{
  private final int iNum1;
  private final int iNum2;
  
  public TestWrongLock(int iNum1, int iNum2)
  {
    this.iNum1 = iNum1;
    this.iNum2 = iNum2;
  }

  public void execute() throws Exception
  {
    Main.iNum1 = iNum1;
    Main.iNum2 = iNum2;
    
    Main t = new Main();
    t.run();
  }
}

package fse2006DiningPhil;

import org.jtool.test.ConcurrencyTestCase;

public class TestDiningPhil implements ConcurrencyTestCase
{
  private final int num;
  
  public TestDiningPhil(int num)
  {
    this.num = num;
  }

  public void execute() throws Exception
  {
    DiningPhilosophers.main(num);
  }
  
}

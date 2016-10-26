package fse2006raxextended;

import org.jtool.test.ConcurrencyTestCase;

public class TestRaxExtended implements ConcurrencyTestCase
{
  private final int gc;
  private final int wc;
  private final int iterations;
  
  

  public TestRaxExtended(int gc, int wc, int iterations)
  {
    // 10, 3, 3
    this.gc = gc;
    this.wc = wc;
    this.iterations = iterations;
  }



  public void execute() throws Exception
  {
    RAXextended.main(gc, wc, iterations);
  }
}

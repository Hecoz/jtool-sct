package lufact;

import org.jtool.test.ConcurrencyTestCase;

public class TestLufact implements ConcurrencyTestCase {

  private final int nthreads;
  
  public TestLufact(int nthreads)
  {
    this.nthreads = nthreads;
  }
  
  public void execute() throws Exception {
    new JGFLUFactBench(nthreads).JGFrun(0);
  }

}

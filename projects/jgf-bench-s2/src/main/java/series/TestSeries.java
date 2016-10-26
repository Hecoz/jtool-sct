package series;

import org.jtool.test.ConcurrencyTestCase;

public class TestSeries implements ConcurrencyTestCase {

  private final int nthreads;
  
  public TestSeries(int nthreads) {
    this.nthreads = nthreads;
  }

  public void execute() throws Exception {
    new JGFSeriesBench(nthreads).JGFrun(0);
  }

}

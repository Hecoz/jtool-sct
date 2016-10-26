package moldyn;

import org.jtool.test.ConcurrencyTestCase;

public class TestMoldyn implements ConcurrencyTestCase
{
  private final int nthreads;
  
  public TestMoldyn(int nthreads)
  {
    this.nthreads = nthreads;
  }

  public void execute() throws Exception
  {
    new JGFMolDynBench(nthreads).JGFrun(0);
  }
  
}

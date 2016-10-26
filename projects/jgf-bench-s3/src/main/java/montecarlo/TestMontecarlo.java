package montecarlo;

import org.jtool.test.ConcurrencyTestCase;

public class TestMontecarlo implements ConcurrencyTestCase
{
  private final int nthreads;
  private final int size;

  public TestMontecarlo(int nthreads, int size)
  {
    this.nthreads = nthreads;
    this.size = size;
  }

  public void execute() throws Exception
  {
    new JGFMonteCarloBench(nthreads).JGFrun(size);
  }
  
  public static void main(String[] args) throws Exception
  {
    new TestMontecarlo(2, 0).execute();
  }
  
}

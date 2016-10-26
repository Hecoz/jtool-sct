package philo;

import org.jtool.test.ConcurrencyTestCase;


public class TestPhilo implements ConcurrencyTestCase
{
  public int numThreads;
  
  public TestPhilo()
  {
    this(2);
  }
  
  public TestPhilo(final int numThreads)
  {
    this.numThreads = numThreads;
  }

  public void execute() throws Exception
  {
    Philo.NUM_PHIL = numThreads;
    final Table tab = new Table();
    final Philo[] p = new Philo[Philo.NUM_PHIL];
    for (int i = 0; i < Philo.NUM_PHIL; ++i)
    {
      p[i] = new Philo(i, tab);
      p[i].start();
    }
    for (int i = 0; i < Philo.NUM_PHIL; ++i)
    {
      p[i].join();
    }
  }
}

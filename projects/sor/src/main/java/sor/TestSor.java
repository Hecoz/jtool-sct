package sor;

import org.jtool.test.ConcurrencyTestCase;

public class TestSor implements ConcurrencyTestCase
{
  
  public int numThreads;
  
  public TestSor()
  {
    this(2);
  }
  
  public TestSor(final int numThreads)
  {
    this.numThreads = numThreads;
  }

  public void execute() throws Exception
  {
    final Sor sor = new Sor();
    sor.go(numThreads);
  }
}

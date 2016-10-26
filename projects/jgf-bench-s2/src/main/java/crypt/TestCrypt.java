package crypt;

import org.jtool.test.ConcurrencyTestCase;

public class TestCrypt implements ConcurrencyTestCase
{
  public int numThreads;
  
  public TestCrypt()
  {
    this(2);
  }
  
  public TestCrypt(final int numThreads)
  {
    this.numThreads = numThreads;
  }

  public void execute() throws Exception
  {
    final IDEATest test = new IDEATest();
    test.array_rows = 100;
    JGFCryptBench.nthreads = numThreads;
    test.buildTestData();
    test.Do();
  }
}

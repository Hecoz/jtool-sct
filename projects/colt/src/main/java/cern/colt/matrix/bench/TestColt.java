package cern.colt.matrix.bench;

import org.jtool.test.ConcurrencyTestCase;

public class TestColt implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    BenchmarkMatrix.main(new String[] {
        //10 2 0.99 false true 50 100 150 200

        "dgemm",
        "dense",
        "2",
        "2",
        "0.99",
        "false",
        "true",
        "5",
        "10",
        "15",
        "20" });
  }
  
  public static void main(String[] args) throws Exception
  {
    new TestColt().execute();
  }
  
}

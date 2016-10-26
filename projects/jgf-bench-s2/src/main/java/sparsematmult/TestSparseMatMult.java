package sparsematmult;

import org.jtool.test.ConcurrencyTestCase;

public class TestSparseMatMult implements ConcurrencyTestCase {
  
  private final int nthreads;
  
  public TestSparseMatMult(int nthreads) {
    this.nthreads = nthreads;
  }

  public void execute() throws Exception {
    new JGFSparseMatmultBench(nthreads).JGFrun(0);
  }

}

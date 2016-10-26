package fse2006readerswriters;

import org.jtool.test.ConcurrencyTestCase;

public class TestReadersWriters implements ConcurrencyTestCase {
  
  private final int readers;
  private final int writers;
  private final int boundp;
  
  public TestReadersWriters(int readers, int writers, int boundp) {
    this.readers = readers;
    this.writers = writers;
    this.boundp = boundp;
  }

  public void execute() throws Exception {
    // 5, 5, 100
    RWVSNDriver.main(readers, writers, boundp);
  }

}

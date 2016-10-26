package fse2006piper;

import org.jtool.test.ConcurrencyTestCase;

public class TestPiper implements ConcurrencyTestCase
{
  private final int numSeatsOnPlane;
  private final int numThreads;
  private final int numSeats;
  
  public TestPiper(int numSeatsOnPlane, int numThreads, int numSeats)
  {
    this.numSeatsOnPlane = numSeatsOnPlane;
    this.numThreads = numThreads;
    this.numSeats = numSeats;
  }

  public void execute() throws Exception
  {
    IBM_Airlines.main(new String[] {
        "" + numSeatsOnPlane,
        "" + numThreads,
        "" + numSeats });
  }
  
}

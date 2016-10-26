package fse2006airline;

import org.jtool.test.ConcurrencyTestCase;

public class TestAirline implements ConcurrencyTestCase
{
  private final int numberThreads;
  private final int cushion;
  
  /**
   * I think cushion should be smaller than numberThreads.
   * 
   * @param numberThreads
   * @param cushion
   */
  public TestAirline(int numberThreads, int cushion)
  {
    this.numberThreads = numberThreads;
    this.cushion = cushion;
  }



  public void execute() throws Exception
  {
    Bug.Num_Of_Seats_Sold = 0;
    new Bug("test",numberThreads,cushion).go();
  }
}

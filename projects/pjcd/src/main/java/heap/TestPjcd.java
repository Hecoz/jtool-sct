package heap;

import org.jtool.test.ConcurrencyTestCase;

public class TestPjcd implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    Main.main(new String[] {
        "unused",
        "MAX_FRAMES",
        "3",
        "DETECTOR_PERIOD",
        "2",
        "DETECTOR_PRIORITY",
        "9" });
  }
  
}

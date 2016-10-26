package elevator;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.jtool.test.ConcurrencyTestCase;


public class TestElevator implements ConcurrencyTestCase
{
  public int numThreads;
  final byte[] input;
  
  public TestElevator()
  {
    this(2);
  }

  public TestElevator(final int numThreads)
  {
    this.numThreads = numThreads;
    final String sinput = "4 " + numThreads + "\n" + "1 1 3\n" + "3 4 2\n" +
    //"11 8 2\n" + "12 6 4\n" +
        "0\n";
    
    input = sinput.getBytes(StandardCharsets.UTF_8);
  }

  public void execute() throws Exception
  {
    Lift.count = 0;
    final Elevator elevator = new Elevator(new ByteArrayInputStream(input));
    elevator.begin();
    elevator.waitForLiftsToFinishOperation();
  }
  
}

package tsp;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import org.jtool.test.ConcurrencyTestCase;

public class TestTsp implements ConcurrencyTestCase
{
  public int numThreads;
  private final byte[] input;
  
  public TestTsp()
  {
    this(2);
  }

  public TestTsp(final int numThreads)
  {
    this.numThreads = numThreads;
    final String sinput =
        "4\n" + "0 1 3 1\n" + "1 0 1 2\n" + "3 1 0 1\n" + "1 2 1 0\n";

    //    final String sinput =
    //        "6\n"
    //            + "0 9 2 6 1 4\n"
    //            + "8 0 2 1 8 3 \n"
    //            + "2 4 0 1 6 2 \n"
    //            + "8 5 1 0 2 2 \n"
    //            + "7 5 2 3 0 4 \n"
    //            + "9 4 1 4 8 0 \n";
    //    final String sinput =
    //        "10\n"
    //            + "5  9 14 19  2  4 19 17 12 17\n"
    //            + "7  7  8  8  2 14  1  0  2  7\n"
    //            + "2 10 17  4 11 11  0  0 11 16\n"
    //            + "0  4 14 16 17  6  6 19 11 13\n"
    //            + "11  0 16  3  1 11 14 13 14  4\n"
    //            + "6 12  9 12  1 16  9 13 13  5\n"
    //            + "3 17 19 10  9 18 15  4 18  8\n"
    //            + "6 15 10 15  7 15 18 15 19 13\n"
    //            + "5  7 16  1 13 15  4 14 19 17\n"
    //            + "0 11  5  9 11  4 13  8  8 13\n";

    input = sinput.getBytes(StandardCharsets.UTF_8);
  }
  
  public void execute() throws Exception
  {
    final Tsp tsp = new Tsp(new ByteArrayInputStream(input));
    tsp.nWorkers = numThreads;
    tsp.execute();
  }
}

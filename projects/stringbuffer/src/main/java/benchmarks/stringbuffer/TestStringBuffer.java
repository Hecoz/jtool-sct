package benchmarks.stringbuffer;

import org.jtool.test.ConcurrencyTestCase;

public class TestStringBuffer implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    StringBuffer al1 = new benchmarks.stringbuffer.StringBuffer("Hello");
    StringBuffer al2 = new benchmarks.stringbuffer.StringBuffer("World");
    StringBufferTest t1 = new StringBufferTest(al1, al2, 0);
    StringBufferTest t2 = new StringBufferTest(al2, al1, 1);
    t1.start();
    t2.start();
    t1.join();
    t2.join();
  }
}

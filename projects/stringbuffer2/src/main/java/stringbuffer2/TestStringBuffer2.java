package stringbuffer2;

import org.jtool.test.ConcurrencyTestCase;

public class TestStringBuffer2 implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    // Wrote this test for jtool based on:
    // C. Flanagan and S. Qadeer. 
    // A type and effect system for atomicity.
    // Proceedings of the ACM SIGPLAN Conference on Programming Language
    // Design and Implementation, 2003
    
    StringBuffer buff = new stringbuffer2.StringBuffer("abc");
    
    StringBufferTest t1 = new StringBufferTest(buff);
    t1.start();
    
    new StringBuffer().append(buff);
    
    t1.join();
  }
}

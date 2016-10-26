package fse2006deos;

import org.jtool.test.ConcurrencyTestCase;

public class TestDEOS implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    DEOS.abstraction = false;
    Assertion.num_entries = 0;
    DEOS.main(new String[]{"abstraction"});
  }
  
}

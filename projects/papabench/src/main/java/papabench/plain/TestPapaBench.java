package papabench.plain;

import org.jtool.test.ConcurrencyTestCase;

public class TestPapaBench implements ConcurrencyTestCase
{
  public void execute() throws Exception
  {
    PapaBenchApplication.main(null);
  }
  
}

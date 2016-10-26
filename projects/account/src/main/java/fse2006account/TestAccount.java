package fse2006account;

import org.jtool.test.ConcurrencyTestCase;

public class TestAccount implements ConcurrencyTestCase
{
  private final int num;
  
  public TestAccount(int num)
  {
    this.num = num;
  }

  public void execute() throws Exception
  {
    ManageAccount.num = this.num;
    Main.main(null);
  }
}

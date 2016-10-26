package fse2006accountsubtype;

import org.jtool.test.ConcurrencyTestCase;

public class TestAccountSubType implements ConcurrencyTestCase
{
  private final int numBusinessAccounts;
  private final int numPersonalAccounts;
  
  public TestAccountSubType(int numBusinessAccounts, int numPersonalAccounts)
  {
    this.numBusinessAccounts = numBusinessAccounts;
    this.numPersonalAccounts = numPersonalAccounts;
  }

  public void execute() throws Exception
  {
    // Create accouns with initial balance of 100
    Bank bank = new Bank(numBusinessAccounts, numPersonalAccounts, 100);
    bank.work();
//    bank.printAllAccounts();
    
    // Check to see that all of the balances are stable
    for (int i = 0; i < numBusinessAccounts + numPersonalAccounts; i++)
    {
      if (bank.getAccount(i).getBalance() != 300)
      {
        throw new RuntimeException("bug found");
      }
    }
  }
  
}

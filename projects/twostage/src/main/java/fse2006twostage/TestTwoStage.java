package fse2006twostage;

import org.jtool.test.ConcurrencyTestCase;

public class TestTwoStage implements ConcurrencyTestCase
{
  private final int iTthreads;
  private final int iRthreads;
  
  public TestTwoStage(int iTthreads, int iRthreads)
  {
    this.iTthreads = iTthreads;
    this.iRthreads = iRthreads;
  }

  public void execute() throws Exception
  {
    Main.iTthreads = iTthreads;
    Main.iRthreads = iRthreads;
    Main t=new Main();
    t.run();
  }
}

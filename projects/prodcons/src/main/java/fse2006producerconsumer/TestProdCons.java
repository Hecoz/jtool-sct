package fse2006producerconsumer;

import org.jtool.test.ConcurrencyTestCase;

public class TestProdCons implements ConcurrencyTestCase
{
  public final int prods;
  public final int cons;
  public final int count;
  
  public TestProdCons(int prods, int cons, int count)
  {
    this.prods = prods;
    this.cons = cons;
    this.count = count;
  }

  public void execute() throws Exception
  {
    new ProducerConsumer(prods,cons,count).go();
  }
  
}

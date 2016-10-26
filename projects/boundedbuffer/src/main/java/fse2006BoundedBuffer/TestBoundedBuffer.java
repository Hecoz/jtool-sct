package fse2006BoundedBuffer;

import org.jtool.test.ConcurrencyTestCase;

public class TestBoundedBuffer implements ConcurrencyTestCase
{
  
  int size = 1; /* parameter */
  
  /**
   * In this version of the test case, prods and cons must be equal. This way, a
   * deadlock error will sometimes occur, depending on the schedule.
   */
  int prods = 4; /* parameter */
  int cons = 4; /* parameter */
  int putcount = 2; /* parameter */
  
  public TestBoundedBuffer()
  {
  }
  
  public TestBoundedBuffer(int size, int num, int putcount)
  {
    this.size = size;
    this.prods = num;
    this.cons = num;
    this.putcount = putcount;
  }

  public void execute() throws Exception
  {
    Buffer buf = new BufferImpl(size);
    Producer[] producers = new Producer[prods];
    Consumer[] consumers = new Consumer[cons];
    
    for (int i=0; i<prods; i++) {
      producers[i] = new Producer(buf, putcount);
      producers[i].start();
    }
    for (int i=0; i<cons; i++) {
      consumers[i] = new Consumer(buf, putcount);
      consumers[i].start();
    }
    
    for (int i=0; i<prods; i++) {
      producers[i].join();
    }
    for (int i=0; i<cons; i++) {
      consumers[i].join();
    }
    
    
  }
  
}

package fse2006nestedmonitor;

/* from http://www.doc.ic.ac.uk/~jnm/book/ */
/* Concurrency: State Models & Java Programs - Jeff Magee & Jeff Kramer */
/* has a deadlock */



/*********************SEMABUFFER*****************************/

class SemaBuffer implements Buffer {
  // protected Object[] buf;
  // protected int in = 0;
  // protected int out= 0;
  // protected int count= 0;
  protected int size;

  Semaphore full; //counts number of items
  Semaphore empty;//counts number of spaces

  SemaBuffer(int size) {
    this.size = size; 
    // buf = new Object[size];
    full = new Semaphore(0);
    empty= new Semaphore(size);
  }

  synchronized public void put(Object o)
              throws InterruptedException {
    empty.down();
    // buf[in] = o;
    // ++count;
    // in=(in+1) % size;
    full.up();
  }

  synchronized public Object get()
               throws InterruptedException{
    full.down();
    // Object o =buf[out];
    // buf[out]=null;
    // --count;
    // out=(out+1) % size;
    empty.up();
    return (null); //(o);
  }
}


public class NestedMonitor  {
  static int SIZE = 5; /* parameter */
  static Buffer buf;

    public static void main(String [] args) {
        buf = new SemaBuffer(SIZE);

	new Producer(buf, SIZE).start();
    	new Consumer(buf, SIZE).start();
    }
}

/*******************PRODUCER************************/
class Producer extends Thread {

    final Buffer buf;
    final int num;
    
    public Producer(Buffer buf, int num) {
		this.buf = buf;
		this.num = num;
	}

	public void run() {
      try {
        // int tmp = 0;
        for(int i=0; i < num; ++i) {
            buf.put(i); // (new Integer(tmp));
            //System.out.println(this + " produced " + tmp);
            //tmp=tmp+1;
        }
      } catch (InterruptedException e){}
    }
}

/********************CONSUMER*******************************/
class Consumer extends Thread {

    final Buffer buf;
    final int num;

    public Consumer(Buffer buf, int num) {
		this.buf = buf;
		this.num = num;
	}

	public void run() {
      try {
    	  for(int i=0; i < num; ++i) {
            buf.get(); // int tmp = ((Integer)buf.get()).intValue();
            //System.out.println(this+" consumed "+tmp);
        }
      } catch(InterruptedException e ){}
    }
}

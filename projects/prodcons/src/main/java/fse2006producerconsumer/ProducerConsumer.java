package fse2006producerconsumer;

/**
 * This is an implementation of the ProducerConsumer algorithm.
 *
 * @author Todd Wallentine tcw AT cis ksu edu
 * @version $Revision: 1.2 $ - $Date: 2005/04/15 19:32:49 $
 */
public class ProducerConsumer {
    public int PRODS;
    public int CONS;
    public int COUNT;
    public int total = 0;
    
    public ProducerConsumer()
    {
      this(1,1,4);
    }

    public ProducerConsumer(int pRODS, int cONS, int cOUNT)
    {
      PRODS = pRODS;
      CONS = cONS;
      COUNT = cOUNT;
    }

    public void go() throws Exception {
//        if (args != null && args.length == 3) {
//	  PRODS = Integer.parseInt(args[0]);
//	  CONS = Integer.parseInt(args[1]);
//	  COUNT = Integer.parseInt(args[2]);
//	}

        Consumer[] cons = new Consumer[CONS];
        Buffer b = new Buffer(COUNT);
        Producer p[] = new Producer[PRODS];

        for (int i = 0; i < PRODS; i++)
          p[i] = new Producer(b);

        for (int i = 0; i < CONS; i++)
          cons[i] = new Consumer(b, this);
        
        for (int i = 0; i < PRODS; i++)
          p[i].join();
        
        for (int i = 0; i < CONS; i++)
          cons[i].join();

        /*
        if (total != COUNT*PRODS)
          throw new RuntimeException("bug found - total is "+total+" and should be "+COUNT*PRODS);
        */
    }
}

class HaltException extends Exception {
}

interface BufferInterface {
    public void put(Object x);

    public Object get();

    public void halt();
}


class Buffer implements BufferInterface {
    public final int SIZE;
    protected Object[] array;
    protected int putPtr = 0;
    protected int getPtr = 0;
    protected int usedSlots = 0;
    protected boolean halted = false;

    public Buffer(int b) {
        SIZE = b;
        array = new Object[b];
    }

    public synchronized void put(Object x) {
		while (usedSlots == SIZE) {
			try {
				//System.out.println("producer wait");
				wait();
			} catch (InterruptedException ex) {
			}
		}

		//System.out.println("put at " + putPtr);
		array[putPtr] = x;
		putPtr = (putPtr + 1) % SIZE;

		if (usedSlots == 0) {
			notifyAll();
		}
        	
        usedSlots++;
    }

    public synchronized Object get() {
        while ((usedSlots == 0) & !halted) {
            try {
                //System.out.println("consumer wait");
                wait();
            } catch (InterruptedException ex) {
            }
        }

        if (usedSlots == 0) {
            //System.out.println("consumer gets halt exception");

            //HaltException he = new HaltException();
            //throw (he);
            return null;
        }

        Object x = array[getPtr];
        //System.out.println("get at " + getPtr);
        array[getPtr] = null;
        getPtr = (getPtr + 1) % SIZE;

        if (usedSlots == SIZE) {
            notifyAll();
        }

        usedSlots--;

        return x;
    }

    public synchronized void halt() {
        //System.out.println("producer sets halt flag");
        halted = true;
        notifyAll();
    }
}


class Attribute {
    public int attr;

    public Attribute() {
        attr = 0;
    }

    public Attribute(int attr) {
        this.attr = attr;
    }
}


class AttrData extends Attribute {
    public int data;

    public AttrData(int attr, int data) {
        this.attr = attr;
        this.data = data;
    }
}


class Producer extends Thread {
    private final Buffer buffer;

    public Producer(Buffer b) {
        buffer = b;
        this.start();
    }

    public void run() {
        for (int i = 0; i < buffer.SIZE-1; i++) {
            AttrData ad = new AttrData(i, 1);
            buffer.put(ad);

            //yield();
        }
        buffer.halt();
    }
}


class Consumer extends Thread {
    private final Buffer buffer;
    private final ProducerConsumer prodcons;

    public Consumer(Buffer b, ProducerConsumer prodcons) {
        buffer = b;
        this.prodcons = prodcons;
        this.start();
    }

    public void run() {
        int count = 0;
        
        AttrData[] received = new AttrData[10];

        while (count < received.length) {
            received[count] = (AttrData) buffer.get();
            if(received[count] == null) {
                break;    
            } else {
		inc(received[count].data);
	    }
            count++;
        }

        //System.out.println("Consumer ends");
    }

    public synchronized void inc(int x) {
      prodcons.total = prodcons.total + x;
    }
}

package fse2006BoundedBuffer;

/* from http://www.doc.ic.ac.uk/~jnm/book/ */
/* Concurrency: State Models & Java Programs - Jeff Magee & Jeff Kramer */
/* has a deadlock */

/*******************PRODUCER************************/

class Producer extends Thread {
    Buffer buf;
    int putcount;

    Producer(Buffer b, int mc) {
      buf = b;
      putcount = mc;
    }

    public void run() {
      try {
        while(putcount > 0) {
//            System.out.println("Producer trying to put - "+this);
            buf.put(new Integer(putcount));
            putcount--;
        }
      } catch (InterruptedException e){}
    }
}

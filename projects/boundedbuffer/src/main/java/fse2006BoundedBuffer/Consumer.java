package fse2006BoundedBuffer;


/* from http://www.doc.ic.ac.uk/~jnm/book/ */
/* Concurrency: State Models & Java Programs - Jeff Magee & Jeff Kramer */
/* has a deadlock */

/********************CONSUMER*******************************/

class Consumer extends Thread {

    Buffer buf;
    int getcount;
  
  Consumer(Buffer b, int getcount)
  {
    buf = b;
    this.getcount = getcount;
  }

    public void run() {
      try {
        while(getcount > 0) {
//            System.out.println("Consumer trying to get - "+this);
            int tmp = ((Integer)buf.get()).intValue();
            getcount--;
        }
      } catch(InterruptedException e ){}
    }
}

/**************************************************************************
*                                                                         *
*         Java Grande Forum Benchmark Suite - Thread Version 1.0          *
*                                                                         *
*                            produced by                                  *
*                                                                         *
*                  Java Grande Benchmarking Project                       *
*                                                                         *
*                                at                                       *
*                                                                         *
*                Edinburgh Parallel Computing Centre                      *
*                                                                         * 
*                email: epcc-javagrande@epcc.ed.ac.uk                     *
*                                                                         *
*                                                                         *
*      This version copyright (c) The University of Edinburgh, 2001.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/


import jgfutil.*; 

public class JGFSyncBench implements JGFSection1{

 public static int nthreads;
    private static final int INITSIZE = 1;
 private static final int MAXSIZE = 100000000;
 private static final double TARGETTIME = 10.0;

  public JGFSyncBench(final int nthreads) {
        this.nthreads=nthreads-1;
  }

 public void JGFrun(){

    int i,size;
    double time; 
    final int counter = 0;
    final int shared_cont = 0;
   
    size=INITSIZE;
    time = 0.0;

    final Runnable thobjects[] = new Runnable [nthreads+1];
    final Thread th[] = new Thread [nthreads+1];  
    final CounterClass contm = new CounterClass(shared_cont);
    final CounterClass conto = new CounterClass(shared_cont);


//    JGFInstrumentor.addTimer("Section1:Sync:Method", "synchronisations");

//    while (time < TARGETTIME && size < MAXSIZE){
//    JGFInstrumentor.resetTimer("Section1:Sync:Method");
//    JGFInstrumentor.startTimer("Section1:Sync:Method");

    for(i=1;i<nthreads+1;i++) {
        thobjects[i] = new SyncMethodRunner(i,contm,size);
        th[i] = new Thread(thobjects[i]);
        th[i].start();
    }

    thobjects[0] = new SyncMethodRunner(0,contm,size);
    thobjects[0].run();


    for(i=1;i<nthreads+1;i++) {
     try {
         th[i].join();
     }
     catch (final InterruptedException e) {}
    }

//      JGFInstrumentor.stopTimer("Section1:Sync:Method");
//      time = JGFInstrumentor.readTimer("Section1:Sync:Method");
//      JGFInstrumentor.addOpsToTimer("Section1:Sync:Method",(double) size);
//      size *=2;
//    }

//    JGFInstrumentor.printperfTimer("Section1:Sync:Method");

   
	// size=INITSIZE;
	// time = 0.0;
	//
	//
	// JGFInstrumentor.addTimer("Section1:Sync:Object", "synchronisations");
	//
	// while (time < TARGETTIME && size < MAXSIZE){
	// JGFInstrumentor.resetTimer("Section1:Sync:Object");
	// JGFInstrumentor.startTimer("Section1:Sync:Object");
	//
	// for(i=1;i<nthreads+1;i++) {
	// thobjects[i] = new SyncObjectRunner(i,conto,size);
	// th[i] = new Thread(thobjects[i]);
	// th[i].start();
	// }
	//
	// thobjects[0] = new SyncObjectRunner(i,conto,size);
	// thobjects[0].run();
	//
	//
	// for(i=1;i<nthreads+1;i++) {
	// try {
	// th[i].join();
	// }
	// catch (InterruptedException e) {}
	// }
	//
	// JGFInstrumentor.stopTimer("Section1:Sync:Object");
	// time = JGFInstrumentor.readTimer("Section1:Sync:Object");
	// JGFInstrumentor.addOpsToTimer("Section1:Sync:Object",(double) size);
	// size *=2;
	// }
	//
	// JGFInstrumentor.printperfTimer("Section1:Sync:Object");


 }

    public static void main(final String[] argv) {

      if(argv.length != 0 ) {
        nthreads = Integer.parseInt(argv[0]);
      } else {
        System.out.println("The no of threads has not been specified, defaulting to 1");
        System.out.println("  ");
        nthreads = 1;
      }
	
      JGFInstrumentor.printHeader(1,0,nthreads);
      final JGFSyncBench sb = new JGFSyncBench(nthreads);
      sb.JGFrun();
    }
}

class CounterClass {
    int shared_cont;

  public CounterClass(final int shared_cont) {
    this.shared_cont=shared_cont;
  }
    
  public synchronized void update() { 
    shared_cont++; 
  }    

}

class SyncMethodRunner implements Runnable {

   
    int id,size;
    CounterClass cont;

    public SyncMethodRunner(final int id, final CounterClass cont ,final int size) {
	this.id = id;
        this.cont=cont;
        this.size=size;
    }

 
    public void run() {

	for(int i=0;i<size;i++) {
	    cont.update(); 
        }

	// prevent dead code elimination 
        if(cont.shared_cont <= 0) {
	    System.out.println("Benchmark exited with unrealistic counter value " + cont.shared_cont);
	}

    }

}

class SyncObjectRunner implements Runnable {


    int id,size;
    CounterClass cont;

    public SyncObjectRunner(final int id, final CounterClass cont, final int size) {
        this.id = id;
        this.cont=cont;
        this.size=size;
    }


    public void run() {


        for(int i=0;i<size;i++) {
           synchronized(cont) {
            cont.shared_cont++;
           }
        }

	// prevent dead code elimination 
	if(cont.shared_cont <= 0) {
	    System.out.println("Benchmark exited with unrealistic counter value " + cont.shared_cont);
	}

    }

}



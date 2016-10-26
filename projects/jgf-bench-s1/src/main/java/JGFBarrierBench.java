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

public class JGFBarrierBench implements JGFSection1{


  public  static  int nthreads;
  private static final int INITSIZE = 1;
  private static final int MAXSIZE = 10000000;
  private static final double TARGETTIME = 10.0;
    private final boolean tournament;

    public JGFBarrierBench(final int nthreads, final boolean tournament) {
        this.nthreads=nthreads;
	this.tournament = tournament;
  }

    public void JGFrun() {

    int i,size;
    double time; 

    time = 0.0;
    size=INITSIZE;
    final int shared_cont = 0;

    final Runnable thobjects[] = new Runnable [nthreads];
    final Thread th[] = new Thread [nthreads];   
    final Counter cont = new Counter(shared_cont);
    final Barrier br= new TournamentBarrier(nthreads);

	if (!tournament) {
	    JGFInstrumentor.addTimer("Section1:Barrier:Simple", "barriers");

	    // while (time < TARGETTIME && size < MAXSIZE){
	    JGFInstrumentor.resetTimer("Section1:Barrier:Simple");
	    JGFInstrumentor.startTimer("Section1:Barrier:Simple");

	    for (i = 0; i < nthreads; i++) {
		thobjects[i] = new SimpleBarrierThread(i, cont, size);
		th[i] = new Thread(thobjects[i]);
		th[i].start();
	    }

	    for (i = 0; i < nthreads; i++) {
		try {
		    th[i].join();
		} catch (final InterruptedException e) {
		}
	    }

	    JGFInstrumentor.stopTimer("Section1:Barrier:Simple");
	    time = JGFInstrumentor.readTimer("Section1:Barrier:Simple");
	    JGFInstrumentor.addOpsToTimer("Section1:Barrier:Simple", size);
	    size *= 2;
	    // }

	    JGFInstrumentor.printperfTimer("Section1:Barrier:Simple");
	} else {
	    JGFInstrumentor.addTimer("Section1:Barrier:Tournament", "barriers");

	    time = 0.0;
	    size = INITSIZE;

	// while (time < TARGETTIME && size < MAXSIZE){
	    JGFInstrumentor.resetTimer("Section1:Barrier:Tournament");
	    JGFInstrumentor.startTimer("Section1:Barrier:Tournament");

	    for (i = 0; i < nthreads; i++) {
		thobjects[i] = new TournamentBarrierThread(i, br, size);
		th[i] = new Thread(thobjects[i]);
		th[i].start();
	    }

	    for (i = 0; i < nthreads; i++) {
		try {
		    th[i].join();
		} catch (final InterruptedException e) {
		}
	    }

	    JGFInstrumentor.stopTimer("Section1:Barrier:Tournament");
	    time = JGFInstrumentor.readTimer("Section1:Barrier:Tournament");
	    JGFInstrumentor.addOpsToTimer("Section1:Barrier:Tournament", size);
	    size *= 2;
	// }

	    JGFInstrumentor.printperfTimer("Section1:Barrier:Tournament");

	}


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

	final JGFBarrierBench tb = new JGFBarrierBench(nthreads, true);
    tb.JGFrun();

  }

}


class Counter {
    int shared_cont;

    public Counter(final int shared_cont) {
       this.shared_cont=shared_cont;
    }
}

class SimpleBarrierThread implements Runnable {

   
    int id,size;
    Counter cont;
     
    public SimpleBarrierThread(final int id,final Counter cont,final int size) {
	this.id = id;
        this.cont=cont;
        this.size=size; 
    }

 
    public void run() {

	for(int i=0;i<size;i++) {
          synchronized (cont) {
            cont.shared_cont++;

          try {
            if(cont.shared_cont != JGFBarrierBench.nthreads) {
                cont.wait();

            } else {
                cont.shared_cont = 0;
                cont.notifyAll();
            }

          }
          catch (final InterruptedException e) {}
          }

        }
    }

}


class TournamentBarrierThread implements Runnable {


    int id,size;
    Barrier br;

    public TournamentBarrierThread(final int id,final Barrier br,final int size) {
        this.id = id;
        this.br=br;
        this.size=size;
    }


    public void run() {
        for(int i=0;i<size;i++) {
	    br.DoBarrier(id);
	}
    }

}




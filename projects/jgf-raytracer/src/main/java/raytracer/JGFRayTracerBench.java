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


package raytracer; 

import jgfutil.*; 

public class JGFRayTracerBench extends RayTracer {

  public static int nthreads;
  public static long checksum1=0;
  public static int staticnumobjects;
 
  public JGFRayTracerBench(final int nthreads) {
	  super();
      this.nthreads=nthreads;
  }


  public void JGFsetsize(final int size){
    this.size = size;
  }

  public void JGFinitialise(){


    // set image size 
    width = height = super.datasizes[size]; 

    System.out.println("Initialization complete"); 
  }

  public void JGFapplication(){ 


      final Runnable thobjects[] = new Runnable [nthreads];
      final Thread th[] = new Thread [nthreads];
      final Barrier br= new SimpleBarrier(nthreads);

      //Start Threads

        for(int i=1;i<nthreads;i++) {

            thobjects[i] = new RayTracerRunner(i,width,height,br);
            th[i] = new Thread(thobjects[i]);
            th[i].start();
        }

        thobjects[0] = new RayTracerRunner(0,width,height,br);
        thobjects[0].run();
 

        for(int i=1;i<nthreads;i++) {
            try {
                th[i].join();
            }
      catch (final InterruptedException e)
      {
        throw new RuntimeException(e);
      }
        }

  } 

  /*
  public void JGFvalidate(){
    long refval[] = {2676692,29827635};
    long dev = checksum1 - refval[size]; 
    if (dev != 0 ){
      System.out.println("Validation failed"); 
      System.out.println("Pixel checksum = " + checksum1);
      System.out.println("Reference value = " + refval[size]); 
    }
  }
  */

  public void JGFtidyup(){    
    scene = null;  
    lights = null;  
    prim = null;  
    tRay = null;  
    inter = null;  

    System.gc(); 
  }


  public void JGFrun(final int size){

    JGFsetsize(size); 

    JGFinitialise(); 
    JGFapplication(); 
    
    //JGFvalidate(); 
    //JGFtidyup(); 
  }


}

class RayTracerRunner extends RayTracer implements Runnable {

    int id,height,width;
    Barrier br;

    public RayTracerRunner(final int id,final int width,final int height,final Barrier br) {
        this.id = id;
        this.width=width;
        this.height=height;
        this.br=br;

// create the objects to be rendered
        super.scene = createScene();

		System.out.println("Scene created ("+id+")");

// get lights, objects etc. from scene.
        setScene(super.scene);

        numobjects = super.scene.getObjects();
    //JGFRayTracerBench.staticnumobjects = numobjects;
    }


    public void run() {

        // Set interval to be rendered to the whole picture
        // (overkill, but will be useful to retain this for parallel versions)

        final Interval interval = new Interval(0,width,height,0,height,1,id);

// synchronise threads and start timer

        //br.DoBarrier(id);

		System.out.println("Rendering started ("+id+")");

        render(interval);

		System.out.println("Rendering finished ("+id+")");


        //br.DoBarrier(id);

    }
}
 

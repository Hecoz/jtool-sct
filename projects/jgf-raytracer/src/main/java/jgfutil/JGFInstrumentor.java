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
*      This version copyright (c) The University of Edinburgh, 1999.      *
*                         All rights reserved.                            *
*                                                                         *
**************************************************************************/


package jgfutil;

import java.util.*;

public class JGFInstrumentor{

  private static final Hashtable data;

  static {
    data = new Hashtable(); 
  }

  public static synchronized void storeData(String name, Object obj){
    data.put(name,obj); 
  }

  public static synchronized void retrieveData(String name, Object obj){
    obj = data.get(name); 
  }

  public static synchronized void printHeader(int section, int size,int nthreads) {

    String header, base; 
 
    header = "";
    base = "Java Grande Forum Thread Benchmark Suite - Version 1.0 - Section "; 
  
    switch (section) {
    case 1: 
      header = base + "1";
      break;
    case 2:
      switch (size) {
      case 0:
	header = base + "2 - Size A";
	break;
      case 1:
	header = base + "2 - Size B";
	break;
      case 2:
	header = base + "2 - Size C";
	break;
      }
      break; 
    case 3:    
      switch (size) {
      case 0:
	header = base + "3 - Size A";
	break;
      case 1:
	header = base + "3 - Size B";
	break;
      }
      break; 
    }

    System.out.println(header); 

    if (nthreads == 1) {
      System.out.println("Executing on " + nthreads + " thread");
    }
    else {
      System.out.println("Executing on " + nthreads + " threads");
    }

    System.out.println("");

  } 

}

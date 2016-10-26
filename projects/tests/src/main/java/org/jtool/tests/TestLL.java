package org.jtool.tests;

import org.jtool.runtime.InstrumentationPoints;
import org.jtool.test.ConcurrencyTestCase;

public class TestLL implements ConcurrencyTestCase {
  public void execute() throws Exception {
    System.out.println("\n\nNew execution:");
    new FourThreadedTestCase() {

      public volatile int x = 0;
      public volatile int y = 0;
      public volatile int z = 0;
      
      public volatile int dummy = 0;
      
      
      public volatile Integer m = new Integer(1);

      @Override
      public void thread1() {
        synchronized (m) {
          System.out.println("t1: lock m");
          x = 1;
          System.out.println("t1: x = 1");
          //InstrumentationPoints.preemptFirstTime();
          if (x == 2) {
            System.out.println("t1: if x == 2");
            z = 1;
            System.out.println("t1: z = 1");
          }
          else
          {
            System.out.println("t1: if x == 2");
          }
          //x = 2;
          System.out.println("t1: unlock m");
        }
      }

      @Override
      public void thread2() {
        if(x == 1)
        {
//          int l = y;
//          System.out.println("t2: l = y");
          y = 2;
          x = 2;
          System.out.println("t2: y = 2");
//          if(l == 0)
//          {
//            x = 2;
//            System.out.println("t2: x = 2");
//          }
        }
      }

      @Override
      public void thread3() {
        //InstrumentationPoints.preemptFirstTime();
        synchronized (m) {
          System.out.println("t3: lock m");
          dummy++;
          dummy++;
        }
        System.out.println("t3: unlock m");
        y = 1;
        System.out.println("t3: y = 1");
      }

      @Override
      public void thread4() {

      }

    }.execute();
  }
}
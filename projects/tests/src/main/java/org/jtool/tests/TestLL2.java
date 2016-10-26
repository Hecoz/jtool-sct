package org.jtool.tests;

import org.jtool.runtime.InstrumentationPoints;
import org.jtool.test.ConcurrencyTestCase;

public class TestLL2 implements ConcurrencyTestCase {
  public void execute() throws Exception {
    System.out.println("\n\nNew execution:");
    new FourThreadedTestCase() {

      public volatile int x = 0;
      
      public volatile int dummy = 0;
      
      public volatile int dummy3 = 0;
      
      
      public volatile Integer m = new Integer(1);
      public volatile Integer t = new Integer(2);

      @Override
      public void thread1() {
        InstrumentationPoints.preemptFirstTime();
        synchronized (m) {
          System.out.println("t1: lock m");
          x = 1;
          System.out.println("t1: x = 1");
        }
        System.out.println("t1: unlock m");
      }

      @Override
      public void thread2() {
        synchronized (t) {
          System.out.println("t2: lock t");
          dummy++;
          InstrumentationPoints.preemptFirstTime();
          synchronized (m) {
            System.out.println("t2: lock m");
            dummy++;
          }
          System.out.println("t2: unlock m");
          dummy++;
        }
        System.out.println("t2: unlock t");
      }

      @Override
      public void thread3() {
        InstrumentationPoints.preemptFirstTime();
        synchronized (t) {
          System.out.println("t3: lock t");
          dummy3++;
        }
        System.out.println("t3: unlock t");
        x = 2;
        System.out.println("t3: x = 2");
      }

      @Override
      public void thread4() {

      }

    }.execute();
  }
}
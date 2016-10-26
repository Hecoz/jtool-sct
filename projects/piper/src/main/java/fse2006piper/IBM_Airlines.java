package fse2006piper;

import java.io.*;

public class IBM_Airlines{
    static int NUM_OF_SEATS = 1;        // number of seats on the plane
    private static int _numberOfThreads = 32; // num of threads from input

    public static void main(String[] args) throws InterruptedException {
        int numSeats = 2;
        if (args != null && args.length == 3) {
          NUM_OF_SEATS = Integer.parseInt(args[0]); 
          _numberOfThreads = Integer.parseInt(args[1]); 
          numSeats = Integer.parseInt(args[2]); 
        } 

        IBM_Airlines airline = new  IBM_Airlines();

        Piper p = new Piper("file",_numberOfThreads * NUM_OF_SEATS, numSeats);
        
        Thread[] cons = new Thread[_numberOfThreads];
        Thread[] prods = new Thread[_numberOfThreads];
        
        for( int i = 1; i <= _numberOfThreads; i++){                       // Create all Producer threads
              cons[i-1] = new Thread(new Consumer(p,_numberOfThreads));
              prods[i-1] = new Thread(new Producer(p,"passenger"+i));
              cons[i-1].start();
              prods[i-1].start();
        }
        
        for( int i = 1; i <= _numberOfThreads; i++){                       // Create all Producer threads
          cons[i-1].join();
          prods[i-1].join();
        }
        //System.out.println("End of main");
    }

    static class Producer implements Runnable {

        private final Piper _piper;
        private final String _name;

        public Producer( Piper p, String n) {

            _piper = p;
            _name = n;
        }

        public void run() {

            try {
                for ( int i = 0; i < NUM_OF_SEATS; i++)                    // fill the piper with passengers
                    _piper.fillPlane(_name);

            } catch ( InterruptedException i)  { System.err.println(i); }
            //System.out.println("Producer "+_name+" complete");
        }
    }

    static class Consumer implements Runnable {

        private final Piper _Piper;
        private final int _numberOfThreads;

        public Consumer ( Piper p, int numberOfThreads) {

            _numberOfThreads = numberOfThreads;
            _Piper = p;
        }

        public void run() {

            try {
                for ( int i = 0; i < NUM_OF_SEATS ; i++)    // empty the plane
                    _Piper.emptyPlane();

            } catch ( InterruptedException i) { System.err.println(i); }
            //System.out.println("Consumer complete");
        }
    }

}

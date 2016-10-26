package fse2006piper;

import java.io.*;
import java.lang.*;

public class  Piper {

    static int NUM_OF_SEATS = 2;  // so capacity = number_of_seats -1
    private int _first, _last;
    private final String[] _passengers;
    private final String _fileName;
    static int _fillCount;
    static int  _emptyCount;

    public Piper(String fileName, int fillCount, int numSeats) {

        _first = 0;
        _last = 0;
        NUM_OF_SEATS = numSeats;
        _passengers = new String[NUM_OF_SEATS];
        _fileName = fileName;
        _fillCount = _emptyCount = fillCount;
        setFile();
    }

    public void setFile() {
    }

    public synchronized void fillPlane( String name) throws InterruptedException {

        if (( _last + 1) % NUM_OF_SEATS == _first)  // BUG - should be while, not if !!!
        this.wait();

         _passengers[_last] = name;                // load passenger to plane

        synchronized(this){   if(--_fillCount == 0) closeFile();   }  //close the file, no more passengers

        _last = (_last + 1) % NUM_OF_SEATS;
        this.notifyAll();
    }

    public synchronized String emptyPlane() throws InterruptedException {

        while ( _first == _last)
            this.wait();

        String  name = _passengers[_first];                // get passenger off the plane
        synchronized(this){this._emptyCount--; }


        _first = ( _first + 1) % NUM_OF_SEATS;
        this.notifyAll();

        return name;
    }

    public void closeFile() {
    }
}

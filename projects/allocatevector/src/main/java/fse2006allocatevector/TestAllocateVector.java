package fse2006allocatevector;

import java.io.FileOutputStream;

import org.jtool.test.ConcurrencyTestCase;

public class TestAllocateVector implements ConcurrencyTestCase
{
  private int vectorSize = 5;
  private int blockSize = 5;
  private int numberOfThreads = 2;
  private long seed = 0;
  
  
  public TestAllocateVector()
  {
  }
  
  public TestAllocateVector(int vectorSize, int blockSize, int numberOfThreads,
      long seed)
  {
    this.vectorSize = vectorSize;
    this.blockSize = blockSize;
    this.numberOfThreads = numberOfThreads;
    this.seed = seed;
  }
  
  public void execute() throws Exception
  {
    AllocationVector vector = null;
    TestThread[] testThreads = new TestThread[numberOfThreads];
    
    // Setting threads run configuration according to concurrency parameter.
    vector = new AllocationVector(vectorSize, seed);
    
    // Creating threads, starting their run and waiting till they finish.
    for (int i = 0; i < numberOfThreads; i++)
    {
      testThreads[i] = new TestThread(vector, new int[blockSize]);
    }
    for (int i = 0; i < numberOfThreads; i++)
    {
      testThreads[i].start();
    }
    try
    {
      for (int i = 0; i < numberOfThreads; i++)
      {
        testThreads[i].join();
      }
    }
    catch (InterruptedException e)
    {
      throw new RuntimeException(e);
    }
    
    // Checking correctness of threads run results and printing the according
    // tuple to output file.
    /*
         if (thread1Result[0] == -2) {
             throw new RuntimeException("bug found");
           //out.write("<Test, Thread1 tried to allocate block which is allocated, weak-reality (Two stage access)>\n".getBytes());
         } else if (thread1Result[0] == -3){
             throw new RuntimeException("bug found");
          // out.write("<Test, Thread1 tried to free block which is free, weak-reality (Two stage access)>\n".getBytes());
         } else if (thread2Result[0] == -2) {
             throw new RuntimeException("bug found");
         //  out.write("<Test, Thread2 tried to allocate block which is allocated, weak-reality (Two stage access)>\n".getBytes());
         } else if (thread2Result[0] == -3){
             throw new RuntimeException("bug found");
           //out.write("<Test, Thread2 tried to free block which is free, weak-reality (Two stage access)>\n".getBytes());
         } else {
            // out.write("<Test, correct-run, none>\n".getBytes());
         }
    */
  }
  
  public static void main(String[] args) throws Exception
  {
    new TestAllocateVector().execute();
  }
  
}

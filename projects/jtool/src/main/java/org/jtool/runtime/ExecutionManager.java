package org.jtool.runtime;

import java.util.Random;

import org.jtool.strategy.NoMoreExecutionsException;
import org.jtool.test.ConcurrencyTestCase;

import com.carrotsearch.hppc.LongOpenHashSet;

public final class ExecutionManager
{
  
  // TODO: instrument hashCode and System.getHashCode to return consistent hash codes? 
  // TODO: handle interrupts (inter-thread communication)
  
  // TODO: Re-visit Executor.ensureThreadsTerminated. This busy wait is ugly.
  // TODO: Remove most uses of Guava preconditions.

  // TODO: Reduce use of stdout by benchmarks.
  
  // TODO: Repeat executions.

  public static final int INIT_SCHED_LENGTH = 32768 << 1;
  public static final int INIT_NODE_LIST_LENGTH = 32768 << 3;
  public static final int NUM_TERM_STATES_ESTIMATE = 100000;

  public Executor currentExecutor;
  public Thread mainThread;
  public ThreadGroup threadGroup;
  
  // TODO: Use this.
  private Random random;
  private final Random randomNonDet;

  private int nextExecutionId;
  protected int executionCounter;
  
  public final ConcurrencyTestCase testCase;
  
  private SchedulingStrategy strategy;
  
  private final LongOpenHashSet normalTerminalHashes = new LongOpenHashSet(
      NUM_TERM_STATES_ESTIMATE);
  private final LongOpenHashSet lazyTerminalHashes = new LongOpenHashSet(
      NUM_TERM_STATES_ESTIMATE);

  /**
   * @param testCase
   */
  public ExecutionManager(final ConcurrencyTestCase testCase)
  {
    InstrumentationPoints.runtime = this;
    randomNonDet = new Random();
    while (nextExecutionId == 0)
    {
      nextExecutionId = randomNonDet.nextInt();
    }
    
    this.testCase = testCase;
  }
  
  public void setSchedulingStrategy(final SchedulingStrategy strategy)
  {
    this.strategy = strategy;
  }

  public static void setThreadInRuntimeState(final boolean runtimeCode)
  {
    throw new RuntimeException("Failed to instrument setThreadInRuntimeState");
  }
  
  public static ThreadData getThreadData(final Thread thread)
  {
    throw new RuntimeException("Failed to instrument getThreadData");
  }
  
  public static void setThreadData(final Thread thread, final ThreadData threadData)
  {
    throw new RuntimeException("Failed to instrument setThreadData");
  }
  
  public static void throwException(final Throwable t)
  {
    throw new RuntimeException("Failed to instrument throwException");
  }

  public int getNumExecutions()
  {
    return executionCounter;
  }
  
  public int getNumNormalTerminalHashes()
  {
    return normalTerminalHashes.size();
  }
  
  public int getNumLazyTerminalHashes()
  {
    return lazyTerminalHashes.size();
  }

  private void instrumentedExecute() throws Exception
  {
    // This call will be modified via instrumentation
    // to call execute$instr()
    testCase.execute();
  }

  public boolean doExecution() throws NoMoreExecutionsException
  {
    prepareNextExecutor();
    
    mainThread.start();
    try
    {
      mainThread.join();
    }
    catch (final InterruptedException e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    
    currentExecutor.ensureThreadsTerminated();
    
    
    if (!currentExecutor.sleepSetBlocked)
    {
      normalTerminalHashes.add(currentExecutor.executionHasher.getNormalHash());
      lazyTerminalHashes.add(currentExecutor.executionHasher.getLazyHash());
    }

    // if (!currentExecutor.hbgIgnoreCurrent &&
    // !currentExecutor.sleepSetBlocked)
    // {
    // if (writer != null)
    // {
    // writer.write("Normal Hash: " + currentExecutor.normalHash + "\n");
    // writer.write("Lazy   Hash: " + currentExecutor.lazyHash + "\n");
    // }
    // }
    // if (currentExecutor.deadlockOccurred)
    // {
    // if (writer != null)
    // {
    // writer.write("DEADLOCK ERROR!\n");
    // }
    // }
    //
    // if (writer != null)
    // {
    // writer.write("----FINISHED EXECUTION " + (executionCounter) + "\n");
    // }
    //
    // if (replayNode != null)
    // {
    // // skip updating of counters
    // return true;
    // }

    executionCounter++;

    
    return true;
  }
  
  
  // public void execute() throws IOException
  // {
  // execute(-1);
  // }

  // public void execute(final long numTimesToExecute) throws IOException
  // {
  // execute(numTimesToExecute, true, null);
  // }
  
  // public void execute(long numTimesToExecute, final boolean doWarmup,
  // final BufferedWriter writer) throws IOException
  // {
  // if (doWarmup)
  // {
  // try
  // {
  // testCase.execute();
  // testCase.execute();
  // }
  // catch (final Exception e)
  // {
  // System.err.println("Exception in warm up runs.");
  // e.printStackTrace();
  // System.exit(1);
  // }
  // // System.out.println("Finished warm up runs.====\n\n");
  // }
  //
  //
  // while (true)
  // {
  // final boolean more = doExecution(writer);
  //
  // if (numTimesToExecute > 0)
  // {
  // numTimesToExecute--;
  // }
  //
  // if (!more || numTimesToExecute == 0)
  // {
  // break;
  // }
  // }
  // }
  
  
  public final boolean prepareNextExecutor() throws NoMoreExecutionsException
  {
    strategy.prepareForNextExecution();

    threadGroup = new ThreadGroup("");
    mainThread = new Thread(threadGroup, "")
    {
      @Override
      public void run()
      {
        try
        {
          Throwable t = null;
          InstrumentationPoints.setThreadInRuntimeState(false);
          try
          {
            instrumentedExecute();
          }
          catch (final Throwable ex)
          {
            t = ex;
          }
          InstrumentationPoints.setThreadInRuntimeState(true);
          currentExecutor.onExitThread(mainThread, t);
        }
        catch (final Exception | Error e)
        {
          e.printStackTrace();
          System.exit(1);
        }
      }
      
    };

    currentExecutor = new Executor(nextExecutionId, mainThread, strategy);
    nextExecutionId++;
    if (nextExecutionId == 0)
    {
      nextExecutionId++;
    }
    return true;
  }
  
}

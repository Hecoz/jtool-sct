package runbenchmarks;

import heap.TestPjcd;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lufact.TestLufact;
import moldyn.TestMoldyn;
import montecarlo.TestMontecarlo;

import org.jtool.runtime.ExecutionManager;
import org.jtool.strategy.DFSStrategy;
import org.jtool.strategy.NoMoreExecutionsException;
import org.jtool.test.ConcurrencyTestCase;
import org.jtool.tests.TestArrays2;
import org.jtool.tests.TestBarrier;
import org.jtool.tests.TestExFromPaper;
import org.jtool.tests.TestForError;
import org.jtool.tests.TestLS1;
import org.jtool.tests.TestLS2;
import org.jtool.tests.TestLS3;
import org.jtool.tests.TestLS4;
import org.jtool.tests.TestSimple;
import org.objectweb.dsrg.bpc.demo.TestCreDemo;
import org.objectweb.dsrg.cocome.fractal.TestCocome;

import papabench.plain.TestPapaBench;
import philo.TestPhilo;
import raytracer.JGFRayTracerBenchSizeA;
import rhinotest1.org.mozilla.test.TestRhinoBug1;
import rhinotest2.org.mozilla.test.TestRhinoBug2;
import rhinotest3.org.mozilla.test.TestRhinoBug3;
import series.TestSeries;
import sor.TestSor;
import sparsematmult.TestSparseMatMult;
import stringbuffer2.TestStringBuffer2;
import tsp.TestTsp;
import benchmarks.stringbuffer.TestStringBuffer;

import com.couchbase.client.tests.util.TestStringUtils;

import crypt.TestCrypt;
import daisy.DaisyTest;
import elevator.TestElevator;
import fse2006AlarmClock1.AlarmClock;
import fse2006BoundedBuffer.TestBoundedBuffer;
import fse2006DiningPhil.TestDiningPhil;
import fse2006account.TestAccount;
import fse2006accountsubtype.TestAccountSubType;
import fse2006airline.TestAirline;
import fse2006allocatevector.TestAllocateVector;
import fse2006apps.TestRepWorkers;
import fse2006clean.TestClean;
import fse2006linkedlist.BugTester;
import fse2006losenotify.TestLoseNotify;
import fse2006nestedmonitor.TestNestedMonitor;
import fse2006piper.TestPiper;
import fse2006producerconsumer.TestProdCons;
import fse2006raxextended.TestRaxExtended;
import fse2006raxextendedenvfirst.TestRaxExtendedEnvFirst;
import fse2006readerswriters.TestReadersWriters;
import fse2006reorder.TestReorder;
import fse2006sleepingbarber.SleepingBarber;
import fse2006twostage.TestTwoStage;
import fse2006wronglock.TestWrongLock;

public class MainHarness
{
  public static class Test
  {
    public String name;
    public ConcurrencyTestCase test;
    public String mem;
    public boolean warmup;
    
    public Test(
        final String name,
        final ConcurrencyTestCase test,
        final String mem)
    {
      this.name = name;
      this.test = test;
      this.mem = mem;
      this.warmup = false;
    }
    
    public Test(final String name, final ConcurrencyTestCase test)
    {
      this(name, test, "6gb");
    }
    
    public Test doWarmup()
    {
      warmup = true;
      return this;
    }
    
    public Test setMem(String mem)
    {
      this.mem = mem;
      return this;
    }
  }
  
  public static List<Test> getAllTests()
  {
    // Force certain classes to be loaded to prevent a weird issue.
    LinkedHashMap a = new LinkedHashMap<>();
    a.keySet().iterator();
    a.entrySet().iterator();
    
    final List<Test> tests = new ArrayList<Test>();
    tests.add(new Test("testarrays2", new TestArrays2()));
    tests.add(new Test("testbarrier", new TestBarrier()));
    tests.add(new Test("testerror", new TestForError()));
    tests.add(new Test("testsimple", new TestSimple()));
    tests.add(new Test("testls1", new TestLS1()));
    tests.add(new Test("testls2", new TestLS2()));
    tests.add(new Test("testls3", new TestLS3()));
    tests.add(new Test("testls4", new TestLS4()));
    tests.add(new Test("testfrompaper", new TestExFromPaper()));
    
    tests.add(new Test("alarmclock-3t", new AlarmClock()));
    tests.add(new Test("account-2", new TestAccount(2)));
    tests.add(new Test("account-5", new TestAccount(5)));
    tests.add(new Test("accountsubtype-3t-2-1", new TestAccountSubType(2, 1)));
    tests.add(new Test("airline-3-1", new TestAirline(3, 1)));
    tests.add(new Test("allocatevector-2t", new TestAllocateVector()));
    tests.add(new Test("boundedbuffer-2t", new TestBoundedBuffer(1, 1, 1)));
    tests.add(new Test("boundedbuffer-4t", new TestBoundedBuffer(1, 2, 1)));
    tests.add(new Test("cache4j-2t-1", new net.sf.cache4j.test.Tester(2, 1)));
    tests.add(new Test("cache4j-2t-2", new net.sf.cache4j.test.Tester(2, 2)));
    tests.add(new Test("clean-1-1-2", new TestClean(1, 1, 2)));
    tests.add(new Test("clean-3-3-1", new TestClean(3, 3, 1)));
    tests.add(new Test("credemo-2t", new TestCreDemo()));
    tests.add(new Test("crypt-2t", new TestCrypt(2)));
    tests.add(new Test("crypt-4t", new TestCrypt(4)));
    tests.add(new Test("deadlock1", new fse2006deadlock1.Deadlock()));
    tests.add(new Test("deadlock2", new fse2006deadlock2.Deadlock()));
    tests.add(new Test("diningphil-3", new TestDiningPhil(3)));
    tests.add(new Test("diningphil-5", new TestDiningPhil(5)));
    tests.add(new Test("elevator-2t", new TestElevator(2)));
    tests.add(new Test("linkedlist-2t-6", new BugTester(2, 6)));
    tests.add(new Test("linkedlist-4t-20", new BugTester(4, 20)));
    tests.add(new Test("losenotify-1-1-1", new TestLoseNotify(1, 1, 1)));
    tests.add(new Test("losenotify-2-2-2", new TestLoseNotify(2, 2, 2)));
    tests.add(new Test("nestedmonitor-2", new TestNestedMonitor(2)));
    tests.add(new Test("nestedmonitor-5", new TestNestedMonitor(5)));
    tests.add(new Test("philo-2t", new TestPhilo(2)));
    tests.add(new Test("philo-4t", new TestPhilo(4)));
    tests.add(new Test("piper-1-2-2", new TestPiper(1, 2, 2)));
    tests.add(new Test("piper-6-3-2", new TestPiper(6, 3, 2)));
    tests.add(new Test("prodcons-1-1-4", new TestProdCons(1, 1, 4)));
    tests.add(new Test("prodcons-2-2-4", new TestProdCons(2, 2, 4)));
    tests.add(new Test("raxextended-1-3-3", new TestRaxExtended(1, 1, 1)));
    tests.add(new Test("raxextended-2-3-3", new TestRaxExtended(2, 3, 3)));
    tests.add(new Test(
        "raxextendedenvfirst-1-3-3",
        new TestRaxExtendedEnvFirst(1, 3, 3)));
    tests.add(new Test(
        "raxextendedenvfirst-2-3-3",
        new TestRaxExtendedEnvFirst(2, 3, 3)));
    tests.add(new Test("readerswriters-1-1-1", new TestReadersWriters(1, 1, 1)));
    tests.add(new Test("readerswriters-2-1-2", new TestReadersWriters(2, 1, 2)));
    tests.add(new Test("reorder-1-1", new TestReorder(1, 1)));
    tests.add(new Test("reorder-2-2", new TestReorder(2, 2)));
    tests.add(new Test("repworkers-2t-2", new TestRepWorkers(2, 2)));
    tests.add(new Test("repworkers-8t-8", new TestRepWorkers(8, 8)));
    tests.add(new Test("sleepbarber-3-3-1", new SleepingBarber(3, 3, 1)));
    tests.add(new Test("sleepbarber-6-6-2", new SleepingBarber(6, 6, 2)));
    tests.add(new Test("stringbuffer", new TestStringBuffer()));
    tests.add(new Test("stringbuffer2", new TestStringBuffer2()));
    tests.add(new Test("stringutilsbug-2t", new TestStringUtils()));
    tests.add(new Test("tsp-2t", new TestTsp(2)));
    tests.add(new Test("diasyfs-2t", new DaisyTest(2)));
    tests.add(new Test("tsp-4t", new TestTsp(4)));
    tests.add(new Test("rhino-bug1", new TestRhinoBug1()).doWarmup());
    tests.add(new Test("rhino-bug2", new TestRhinoBug2()).doWarmup());
    tests.add(new Test("rhino-arrays-2", new TestRhinoBug3(2)).doWarmup());
    tests.add(new Test("rhino-arrays-4", new TestRhinoBug3(4)).doWarmup());
    tests.add(new Test("sor-2t", new TestSor(2)));
    tests.add(new Test("sor-4t", new TestSor(4)));
    tests.add(new Test("twostage-1-1", new TestTwoStage(1, 1)));
    tests.add(new Test("twostage-3-3", new TestTwoStage(3, 3)));
    tests.add(new Test("wronglock-1-1", new TestWrongLock(1, 1)));
    tests.add(new Test("wronglock-3-3", new TestWrongLock(3, 3)));
    tests.add(new Test("cocome-2t", new TestCocome(2)));
    tests.add(new Test("papabench", new TestPapaBench()));
    tests.add(new Test("jgf-raytracer-4t", new JGFRayTracerBenchSizeA(4)));
    tests.add(new Test("pjcd", new TestPjcd()));
    tests.add(new Test("lufact-2t", new TestLufact(2)));
    tests.add(new Test("series-2t", new TestSeries(2)));
    tests.add(new Test("sparsematmult-2t", new TestSparseMatMult(2)));
    tests.add(new Test("moldyn-2t", new TestMoldyn(2)));
    tests.add(new Test("motecarlo-2t-3", new TestMontecarlo(2, 0)));
    tests.add(new Test("motecarlo-4t-3", new TestMontecarlo(4, 0)));
    
    // NOT YET WORKING:
    // no threads:
    // tests.add(new Test("deos", new TestDEOS()));
    // tests.add(new Test("raja", new TestRaja()).doWarmup());
    // implementation issues:
    // tests.add(new Test("colt-2t", new TestColt()));
    // other:
    // tests.add(new Test("avrora", new TestAvrora()));
    return tests;
  }
  
  public static void runTests(final List<Test> tests) throws IOException
  {
    int limit = 300;
    for (Test setupTest : tests)
    {
      final ExecutionManager em = new ExecutionManager(setupTest.test);
      
      em.setSchedulingStrategy(new DFSStrategy().setPreemptionBound(1).setScheduleBounding(true));
      System.out.println("\n\nStart " + setupTest.name);
      try
      {
        boolean bugFound = false;
        while (true)
        {
          em.doExecution();
          if (em.getNumExecutions() < 1000 && em.getNumExecutions() % 100 == 0 || em.getNumExecutions() % 1000 == 0)
          {
            System.out.println("  " + em.getNumExecutions());
          }
          if(!bugFound && em.currentExecutor.errorOccurred != null)
          {
            System.out.println("  " + em.getNumExecutions() + ": " + em.currentExecutor.errorOccurred);
            bugFound = true;
          }
        }
      }
      catch (NoMoreExecutionsException e)
      {
        System.out.println("Completed.");
      }
      
      System.out.println("Num executions: " + em.getNumExecutions());
      System.out.println("Num normal terminal hashes: "
          + em.getNumNormalTerminalHashes());
      System.out.println("Num lazy terminal hashes  : "
          + em.getNumLazyTerminalHashes());
    }
  }
  
  public static void main(final String[] args) throws IOException,
      InterruptedException
  {
    final List<Test> tests = getAllTests();
    
    runTests(tests);
    
  }
  
}

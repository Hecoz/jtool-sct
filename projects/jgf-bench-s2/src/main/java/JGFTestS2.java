import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lufact.JGFLUFactBench;

import org.jtool.test.ConcurrencyTestCase;

import crypt.JGFCryptBench;
import series.JGFSeriesBench;
import sor.JGFSORBench;
import sparsematmult.JGFSparseMatmultBench;


public class JGFTestS2
{
  //  private final List<ConcurrencyTestCase> tc = new ArrayList<ConcurrencyTestCase>();
  //  private final Iterator<ConcurrencyTestCase> it;
  //  
  //  public JGFTestS2()
  //  {
  //    tc.add(new ConcurrencyTestCase()
  //    {
  //      public void execute() throws Exception
  //      {
  //        final JGFSeriesBench se = new JGFSeriesBench(2);
  //        se.JGFrun(1);
  //      }
  //    });
  //    
  //    tc.add(new ConcurrencyTestCase()
  //    {
  //      public void execute() throws Exception
  //      {
  //        final JGFLUFactBench lub = new JGFLUFactBench(2);
  //        lub.JGFrun(1);
  //      }
  //    });
  //    
  //    
  //    tc.add(new ConcurrencyTestCase()
  //    {
  //      public void execute() throws Exception
  //      {
  //        final JGFCryptBench cb = new JGFCryptBench(2);
  //        cb.JGFrun(1);
  //      }
  //    });
  //    
  //    tc.add(new ConcurrencyTestCase()
  //    {
  //      public void execute() throws Exception
  //      {
  //        final JGFSORBench jb = new JGFSORBench(2);
  //        jb.JGFrun(1);
  //      }
  //    });
  //    
  //    tc.add(new ConcurrencyTestCase()
  //    {
  //      public void execute() throws Exception
  //      {
  //        final JGFSparseMatmultBench smm = new JGFSparseMatmultBench(2);
  //        smm.JGFrun(1);
  //      }
  //    });
  //
  //    it = tc.iterator();
  //  }
  //  
  //  public ConcurrencyTestCase getNext()
  //  {
  //    if (!it.hasNext())
  //    {
  //      return null;
  //    }
  //    return it.next();
  //  }

}

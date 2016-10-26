package rhinotest3.org.mozilla.test;

import org.jtool.test.ConcurrencyTestCase;

import rhinotest3.org.mozilla.javascript.Context;
import rhinotest3.org.mozilla.javascript.ContextFactory;
import rhinotest3.org.mozilla.javascript.Scriptable;

public class TestRhinoBug3 implements ConcurrencyTestCase
{
  private final String s;
  private final String s1;
  private final String s2;
  private final String s3;
  
  private static class OtherThread extends Thread
  {
    private final Scriptable scope;
    private final String s2;
    
    public OtherThread(final Scriptable scope, final String s2)
    {
      this.scope = scope;
      this.s2 = s2;
    }
    
    @Override
    public void run()
    {
      final Context cx = ContextFactory.getGlobal().enterContext();
      cx.setOptimizationLevel(-1);
      try
      {
        cx.evaluateString(scope, s2, "thread2", 1, null);
      }
      finally
      {
        Context.exit();
      }
    }
    
  }
  
  public TestRhinoBug3(final int numElems)
  {
    if(numElems < 1)
    {
      throw new IllegalArgumentException();
    }
    
    // s1 and s2 execute in parallel.

    // s:  "var a = [0,...,0];"
    // s1: "a[0] = 1; ... a[numElems-1] = 1;"
    // s2: "a[numElems] = 2; ... a[2*numElems-1] = 2;"
    // s3: "a;" -- this gives us the final value of a

    {
      final StringBuilder sb = new StringBuilder();
      sb.append("var a = [");
      sb.append("0");
      for (int i = 1; i < numElems; ++i)
      {
        sb.append(",0");
      }
      sb.append("];");
      
      s = sb.toString();
    }

    {
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < numElems; ++i)
      {
        sb.append("a[" + i + "] = 1;");
      }
      s1 = sb.toString();
    }
    
    {
      final StringBuilder sb = new StringBuilder();
      for (int i = 0; i < numElems; ++i)
      {
        sb.append("a[" + (i + numElems) + "] = 2;");
      }
      s2 = sb.toString();
    }
    
    s3 = "a;";
  }
  
  public void execute() throws Exception
  {
    
    final Context cx = ContextFactory.getGlobal().enterContext();
    cx.setOptimizationLevel(-1);
    try
    {
      final Scriptable scope = cx.initStandardObjects();
      
      cx.evaluateString(scope, s, "init", 1, null);
      
      final OtherThread t2 = new OtherThread(scope, s2);
      t2.start();
      cx.evaluateString(scope, s1, "thread1", 1, null);
      
      t2.join();
      
      final Object result = cx.evaluateString(scope, s3, "check", 1, null);
      
      //final String res = Context.toString(result);
      //System.out.println(res);
      
    }
    finally
    {
      Context.exit();
    }
  }
  
  public static void main(final String[] args) throws Exception
  {
    new TestRhinoBug3(1).execute();
  }
  
}

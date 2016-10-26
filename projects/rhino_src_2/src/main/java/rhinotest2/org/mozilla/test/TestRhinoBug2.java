package rhinotest2.org.mozilla.test;

import org.jtool.test.ConcurrencyTestCase;

import rhinotest2.org.mozilla.javascript.Context;
import rhinotest2.org.mozilla.javascript.ContextFactory;
import rhinotest2.org.mozilla.javascript.Scriptable;

public class TestRhinoBug2 implements ConcurrencyTestCase
{
  private final String s;
  private final String s1;
  private final String s2;
  private final String s3;
  
  private static class OtherThread extends Thread
  {
    private final Scriptable scope;
    private final String s2;
    
    public OtherThread(Scriptable scope, String s2)
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
  
  public TestRhinoBug2()
  {
    
    s =
     // "var b = 0;"
      "var res1=\"\";\n"
    + "var res2=\"\";\n"
    + "var f = function f(a, [b]) {\n"
    + " if(a == 1) res1 = a + \";\" + b; \n"
    + " else res2 = a + \";\" + b; \n"
    + "};\n";

    
    s1 = "f(1,[3]);";
    s2 = "f(2,[4]);";
    
    s3 = "var res = res1 + \":\" + res2; res;";
  }
  
  public void execute() throws Exception
  {
    
    final Context cx = ContextFactory.getGlobal().enterContext();
    cx.setOptimizationLevel(-1);
    try
    {System.out.println();
      final Scriptable scope = cx.initStandardObjects();
      
      cx.evaluateString(scope, s, "init", 1, null);
      
      OtherThread t2 = new OtherThread(scope, s2);
      t2.start();
      cx.evaluateString(scope, s1, "thread1", 1, null);
      
      t2.join();
      
      final Object result = cx.evaluateString(scope, s3, "check", 1, null);
      
      String res = Context.toString(result);
      System.out.println(res);
      if(!res.equals("1;3:2;4"))
      {
        System.out.println("BUG!");
      }
      
    }
    finally
    {
      Context.exit();
    }
  }
  
  public static void main(final String[] args) throws Exception
  {
    new TestRhinoBug2().execute();
  }
  
}

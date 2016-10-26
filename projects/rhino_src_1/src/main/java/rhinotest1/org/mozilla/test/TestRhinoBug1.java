package rhinotest1.org.mozilla.test;

import org.jtool.test.ConcurrencyTestCase;

import rhinotest1.org.mozilla.javascript.Context;
import rhinotest1.org.mozilla.javascript.Scriptable;

public class TestRhinoBug1 implements ConcurrencyTestCase
{
  private final String s;
  private final String s1;
  private final String s2;
  
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
      final Context cx = Context.enter();
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
  
  public TestRhinoBug1()
  {
    s =
        "var x = {POSITIVE_INFINITY : 2, NEGATIVE_INFINITY: 2};\n"
            
            + "var f1 = function() {\n"
            + " return x.NEGATIVE_INFINITY; \n"
            + "};\n"
            
            + "var f2 = function() {\n"
            + "  for (var i = 0; i < 16; i++) \n"
            + "  { \n"
            + "    x[\"prop\" + i] = 1; \n"
            + "  }\n"
            + "};";
    
    
    s1 = "f1();";
    s2 = "f2();";
  }
  
  public void execute() throws Exception
  {
    final Context cx = Context.enter();
    cx.setOptimizationLevel(-1);
    try
    {
      final Scriptable scope = cx.initStandardObjects();
      
      cx.evaluateString(scope, s, "init", 1, null);
      
      OtherThread t2 = new OtherThread(scope, s2);
      t2.start();
      
      final Object result = cx.evaluateString(scope, s1, "thread1", 1, null);
      
      t2.join();
      
      double res = Context.toNumber(result);
      if(res != 2.0)
      {
    	  //throw new RuntimeException("Unexpected result!");
    	  System.out.println("ERROR!");
      }
      else
      {
        System.out.println("SUCCESS!");
      }
      
    }
    finally
    {
      Context.exit();
    }
  }
  
  public static void main(final String[] args) throws Exception
  {
    new TestRhinoBug1().execute();
  }
  
}

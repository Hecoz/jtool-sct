package org.jtool.loader;

import java.lang.instrument.Instrumentation;

import org.jtool.runtime.InstrumentationPoints;

public class Premain
{
  
  public static void premain(final String agentArgument,
      final Instrumentation inst)
  {
    if (MethodDoubler.class.getClassLoader() != null
        || !inst.isRetransformClassesSupported())
    {
      System.err.println("Uh oh.");
      System.exit(1);
    }

    InstrumentationPoints.runtime = null;
    inst.addTransformer(new MethodDoubler(), false);
    
  }
}

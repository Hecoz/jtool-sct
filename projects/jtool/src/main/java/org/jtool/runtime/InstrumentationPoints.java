package org.jtool.runtime;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;


public final class InstrumentationPoints
{
  
  public static ExecutionManager runtime;
  
  public static boolean isSctExec()
  {
    return runtime != null && runtime.currentExecutor != null;
  }

  public static void setThreadInRuntimeState(final boolean runtimeCode)
  {
    //    try
    //    {
    //      final Field field = Thread.class.getField("instrumented");
    //      field.set(Thread.currentThread(), !runtimeCode);
    //    }
    //    catch (final Exception e)
    //    {
    //      throw new RuntimeException(e);
    //    }
    ExecutionManager.setThreadInRuntimeState(runtimeCode);
  }
  
  public static void calledNativeMethod(final String className,
      final String methodName, final String desc)
  {
    try
    {
      setThreadInRuntimeState(true);
      synchronized (InstrumentationPoints.class)
      {
        System.err.println("---");
        System.err.println("Native method: "
            + className
            + "."
            + methodName
            + desc);
        System.err.println("Stack:");
        for (final StackTraceElement ste : Thread.currentThread()
            .getStackTrace())
        {
          System.err.println(" " + ste);
        }

      }
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }

  public static void enterMonitor(final Object o)
  {
    try
    {
      setThreadInRuntimeState(true);
      //    System.out.println("Entered monitor for: " + o.getClass().getName() + "@"
      //        + System.identityHashCode(o));
      runtime.currentExecutor.onEnterMonitor(o);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }

  
  public static void exitMonitor(final Object object)
  {
    try
    {
      setThreadInRuntimeState(true);
      //    System.out.println("Exited monitor for: " + object.getClass().getName()
      //        + "@" + System.identityHashCode(object));
      runtime.currentExecutor.onExitMonitor(object);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void enterRWMonitor(final Object o, final boolean writer)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.onEnterRWMonitor(o, writer);
    } catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    } finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void exitRWMonitor(final Object o, final boolean writer)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.onExitRWMonitor(o, writer);
    } catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    } finally
    {
      setThreadInRuntimeState(false);
    }
  }

  public static void preStartThread(final Thread thread)
  {
    try
    {
      setThreadInRuntimeState(true);
      // System.out.println("preStartThread");
      runtime.currentExecutor.preStartThread(thread);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }

  public static void executedRun(final Object object)
  {
    if (object instanceof Runnable)
    {
      try
      {
        setThreadInRuntimeState(true);
        runtime.currentExecutor.executedRun();
      }
      catch (final Exception | Error e)
      {
        e.printStackTrace();
        System.exit(1);
      }
      finally
      {
        setThreadInRuntimeState(false);
      }
    }
  }
  
  public static void exitRun(final Object object, final Throwable ex)
  {
    if (object instanceof Runnable)
    {
      try
      {
        setThreadInRuntimeState(true);
        // System.out.println("exitRun");
        runtime.currentExecutor.exitRun(ex);
      }
      finally
      {
        setThreadInRuntimeState(false);
      }
    }
  }

  public static void successStartThread(final Thread thread)
  {
    try
    {
      setThreadInRuntimeState(true);
      // System.out.println("successStartThread");
      runtime.currentExecutor.successStartThread(thread);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }

  public static void failedStartThread(final Thread thread)
  {
    try
    {
      setThreadInRuntimeState(true);
      System.err.println("failedStartThread");
      runtime.currentExecutor.failedStartThread(thread);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledNotify(final Object o)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledNotify(o);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledNotifyAll(final Object o)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledNotifyAll(o);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledWait(final Object o)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledWait(o);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledWait(final Object o, final long l)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledWait(o, l);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledWait(final Object o, final long l, final int i)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledWait(o, l, i);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledJoin(final Thread thread)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledJoin(thread);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledJoin(final Thread thread, final long l)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledTimedJoin(thread, l, 0);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledJoin(final Thread thread, final long l, final int i)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledTimedJoin(thread, l, i);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledYield()
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.calledYield();
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void calledSleep(final long millis)
  {
    calledYield();
  }

  public static void putField(final Object object, final SyncObjectData sod, final int fieldIndex)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.fieldOperation(true, object, sod, fieldIndex);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void getField(final Object object, final SyncObjectData sod,
      final int fieldIndex)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.fieldOperation(false, object, sod, fieldIndex);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void getStatic(final SyncObjectData sod)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.fieldOperationGlobal(false, sod);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void putStatic(final SyncObjectData sod)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.fieldOperationGlobal(true, sod);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }

  public static void arrayLoad(final Object array, final int index)
  {
    if (array == null)
    {
      return;
    }
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.arrayOperation(false, array, index);
      
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void arrayStore(final Object array, final int index)
  {
    if (array == null)
    {
      return;
    }
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.arrayOperation(true, array, index);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void newObject(final Object obj)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.newObject(obj);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }

  public static void newArray(final Object array)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.newArray(array);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void newMultiArray(final Object array)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.newArrayMulti(array);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void cloneCalled(final Object newObject)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.cloneCalled(newObject);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  
  public static void newBarrier(final Object obj, final int numThreads)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.newBarrier(obj, numThreads);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }
  
  public static void barrierWait(final Object obj)
  {
    try
    {
      setThreadInRuntimeState(true);
      runtime.currentExecutor.barrierWait(obj);
    }
    catch (final Exception | Error e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }


  public static Method[] filterMethods(final Method[] methods)
  {
    Method[] res = null;
    try
    {
      setThreadInRuntimeState(true);
      final ArrayList<Method> methodList = new ArrayList<Method>();
      final String suffix = "$instr";
      final int suffixLength = suffix.length();
      final HashSet<String> doubledMethods = new HashSet<>();
      for (final Method m : methods)
      {
        final String name = m.getName();
        if (name.endsWith(suffix))
        {
          doubledMethods.add(name.substring(0, name.length() - suffixLength));
        }
      }
      for (final Method m : methods)
      {
        if (m.getName().startsWith("$dummy$"))
        {
          continue;
        }
        if (!doubledMethods.contains(m.getName()))
        {
          methodList.add(m);
          //          System.out.println("Added method: " + m.getName());
        }
      }

      res = methodList.toArray(new Method[methodList.size()]);
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
    return res;
  }
  
  public static String translateMethodName(String name)
  {
    try
    {
      setThreadInRuntimeState(true);
      final String suffix = "$instr";
      if (name.endsWith(suffix))
      {
        //        System.out.println("Translated method name from " + name);
        if (name.equals("run$orig$instr"))
        {
          name = "run";
        }
        else
        {
          name = name.substring(0, name.length() - suffix.length());
        }
        //        System.out.println("...to " + name);
      }
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
    return name;
  }
  
  public static void preemptFirstTime()
  {
    try
    {
      setThreadInRuntimeState(true);
      // runtime.currentExecutor.preemptFirstTime();
      throw new IllegalStateException();
    }
    catch (final Throwable e)
    {
      e.printStackTrace();
      System.exit(1);
    }
    finally
    {
      setThreadInRuntimeState(false);
    }
  }

}

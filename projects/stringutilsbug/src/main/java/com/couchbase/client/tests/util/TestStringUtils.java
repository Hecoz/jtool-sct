package com.couchbase.client.tests.util;

import org.jtool.test.ConcurrencyTestCase;


public class TestStringUtils implements ConcurrencyTestCase
{
  private static class StringUtilsThread extends Thread
  {
    private final StringUtils stringUtils;
    private final String input;
    
    public StringUtilsThread(final StringUtils stringUtils, final String input)
    {
      this.stringUtils = stringUtils;
      this.input = input;
    }

    @Override
    public void run()
    {
      final boolean res = stringUtils.isJsonObject(input);
      if (res != true)
      {
        throw new IllegalStateException();
      }
    }
    
  }

  public static void main(final String[] args) throws Exception
  {
    final int size = 500;
    final StringUtils stringUtils = new StringUtils();
    final StringUtilsThread threads[] = new StringUtilsThread[size];
    for (int i = 0; i < size; i++)
    {
      threads[i] = new StringUtilsThread(stringUtils, "1234");
    }
    
    for (int i = 0; i < size; i++)
    {
      threads[i].start();
    }
    
    for (int i = 0; i < size; i++)
    {
      threads[i].join();
    }
  }

  public void execute() throws Exception
  {
    final StringUtils stringUtils = new StringUtils();
    final String input = "1";
    final StringUtilsThread t1 = new StringUtilsThread(stringUtils, input);
    final StringUtilsThread t2 = new StringUtilsThread(stringUtils, input);
    
    t1.start();
    t2.start();
    
    t1.join();
    t2.join();
  }

}

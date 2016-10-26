package com.couchbase.client.tests.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility methods on string objects.
 */
public final class StringUtils
{
  
  /**
   * A pattern to match on a signed integer value.
   */
  private final Pattern decimalPattern = Pattern.compile("^-?\\d+$");
  
  /**
   * The matcher for the decimal pattern regex.
   */
  private final Matcher decimalMatcher = decimalPattern.matcher("");
  
  /**
   * Maximum supported key length.
   */
  private final int MAX_KEY_LENGTH = 250;
  
  /**
   * Exception thrown if the input key is too long.
   */
  private final IllegalArgumentException KEY_TOO_LONG_EXCEPTION = null;
  //=
  //    new IllegalArgumentException("Key is too long (maxlen = "
  //        + MAX_KEY_LENGTH
  //        + ")");
  
  /**
   * Exception thrown if the input key is empty.
   */
  private final IllegalArgumentException KEY_EMPTY_EXCEPTION = null;
  
  //=
  //    new IllegalArgumentException("Key must contain at least one character.");
  
  
  public StringUtils()
  {
    // Preset the stack traces for the exceptions.
    //    KEY_TOO_LONG_EXCEPTION.setStackTrace(new StackTraceElement[0]);
    //    KEY_EMPTY_EXCEPTION.setStackTrace(new StackTraceElement[0]);
  }
  
  /**
   * Check if a given string is a JSON object.
   * 
   * @param s
   *          the input string.
   * @return true if it is a JSON object, false otherwise.
   */
  public boolean isJsonObject(final String s)
  {
    if (s == null || s.isEmpty())
    {
      return false;
    }
    
    if (s.startsWith("{")
        || s.startsWith("[")
        || "true".equals(s)
        || "false".equals(s)
        || "null".equals(s)
        || decimalMatcher.reset(s).matches())
    {
      return true;
    }
    
    return false;
  }

}

package com.company.automation.automationframework.utils;

import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.TransformerException;

/**
 * Utility class for Exception related helper methods
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class ExceptionUtils
{

  /**
   * Return error message for a given exception as well as the cause of the throwable if one is provided
   * @param e the exception
   * @return the error message for the exception if one exists concatenated with the cause of the exception if one exists
   * If no exception message is available, the exception name is printed 
   */
  public static String getErrorMsg(Exception e)
  {
    Set<String> errorMsg = new HashSet<String>();
    if (e != null)
    {
      if (e.getMessage() != null)
      {
        errorMsg.add(e.getMessage());
      }
      else
      {
        errorMsg.add("Exception thrown: " + e.toString());
      }

      if (e.getCause() != null && e.getCause().getMessage() != null)
      {
        errorMsg.add(e.getCause().getMessage());
      }
      // This is specifically for the exception thrown inside the XMLDataProvider during parsing
      if (e instanceof TransformerException && ((TransformerException) e).getException() != null)
      {
        Throwable innerExp = ((TransformerException) e).getException();
        if (innerExp.getMessage() != null)
          errorMsg.add(innerExp.getMessage());

        if (innerExp.getCause() != null)
          errorMsg.add(innerExp.getCause().getMessage());
      }
    }
    return errorMsg.toString();
  }

}


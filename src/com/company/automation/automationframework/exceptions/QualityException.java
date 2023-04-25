package com.company.automation.automationframework.exceptions;


/**
 * Generic Exception class
 * 
 * If we want all automation exceptions to perform a common operation, that code will go here
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class QualityException extends Exception
{
  /**
   * 
   */
  private static final long serialVersionUID = 5896087858259400775L;

  public QualityException(Exception e)
  {
    super(e);
  }

  public QualityException(String message)
  {
    super(message);
  }

  public QualityException(String message, Exception e)
  {
    super(message, e);
  }

}


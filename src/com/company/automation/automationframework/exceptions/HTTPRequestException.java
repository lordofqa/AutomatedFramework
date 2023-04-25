package com.company.automation.automationframework.exceptions;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class HTTPRequestException extends QualityException
{
  private static final long serialVersionUID = 1L;

  public HTTPRequestException(Exception e)
  {
    super(e);
  }

  public HTTPRequestException(String message)
  {
    super(message);
  }

}


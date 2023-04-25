package com.company.automation.automationframework.exceptions;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class TestRailException extends HTTPRequestException
{

  private static final long serialVersionUID = 1234565L;

  public TestRailException(Exception e)
  {
    super(e);
  }
  
  public TestRailException(String errorMsg)
  {
    super(errorMsg);
  }

}

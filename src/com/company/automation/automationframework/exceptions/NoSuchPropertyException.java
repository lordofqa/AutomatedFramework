package com.company.automation.automationframework.exceptions;

/**
 * Exception thrown when property does not exist in any of the config files
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class NoSuchPropertyException extends QualityException
{

  /**
   * 
   */
  private static final long serialVersionUID = 647484980672970792L;

  public NoSuchPropertyException(String propertyName)
  {
    super("Property can't be found in config files - " + propertyName);
  }

}

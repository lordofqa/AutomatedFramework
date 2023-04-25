package com.company.automation.automationframework.exceptions;

/**
 * Exception used to communicate a situation where a page validation failed,
 * usually indicating that there's a product exception on the screen
 */
public class PageValidationException extends QualityException {

  /**
   * 
   */
  private static final long serialVersionUID = 8067156114370376338L;

  public PageValidationException(String string) {
    super(string);
  }

}


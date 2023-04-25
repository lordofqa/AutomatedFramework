package com.company.automation.automationframework.enums;


import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public enum BrowserEnum
{
  CHROME("Chrome", "chrome"), 
  FIREFOX("FireFox", "firefox"), 
  SAFARI("Safari", "safari"), 
  IE8("Internet Explorer 8", "internet explorer"), 
  IE9("Internet Explorer 9", "internet explorer"), 
  HTML("", "htmlunit");

  private String testRailConfigName;
  private String seleniumGridConfigName; 
 
  private static final Map<String, BrowserEnum> lookup = new HashMap<String, BrowserEnum>();
  
  static {
    for(BrowserEnum browser : BrowserEnum.values()) {
      lookup.put(browser.getTestRailConfigName(), browser);
    }
  }

  BrowserEnum(String testRailConfigName, String seleniumGridConfigName)
  {

    this.testRailConfigName = testRailConfigName;
    this.seleniumGridConfigName = seleniumGridConfigName;

  }

  /**
   * @return the testRailConfigName
   */
  public String getTestRailConfigName()
  {
    return testRailConfigName;
  }

  /**
   * @param testRailConfigName the testRailConfigName to set
   */
  public void setTestRailConfigName(String testRailConfigName)
  {
    this.testRailConfigName = testRailConfigName;
  }

  /**
   * @return the seleniumGridConfigName
   */
  public String getSeleniumGridConfigName()
  {
    return seleniumGridConfigName;
  }

  /**
   * @param seleniumGridConfigName the seleniumGridConfigName to set
   */
  public void setSeleniumGridConfigName(String seleniumGridConfigName)
  {
    this.seleniumGridConfigName = seleniumGridConfigName;
  }
  
  /**
   * return browser name in Selenium Grid configuration for a browser name in TestRail configuration
   * @param testRailConfigName
   * @return
   */
  public static String getSeleniumGridValue(String testRailConfigName)
  {
    if (lookup.containsKey(testRailConfigName))
    {
      return lookup.get(testRailConfigName).getSeleniumGridConfigName();
    } 
    
    throw new InvalidParameterException("Invalid config: " + testRailConfigName);
  }
  
  /**
   * Return browser for a given browser name
   * @param browser
   * @return
   */
  public static BrowserEnum getBrowser(String browser)
  {
    if (lookup.containsKey(browser))
    {
      return lookup.get(browser);
    } 
    
    throw new InvalidParameterException("Invalid config: " + browser);
    
  }

}


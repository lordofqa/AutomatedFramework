package com.company.automation.automationframework.pageengine;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;

import com.company.automation.automationframework.exceptions.PageValidationException;

/**
 *
 * A generic Page class which all pages will extend from.  It covers very basic elements
 * such as page initiation and validation.
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 * 
 */

public abstract class Page
{
  protected WebDriver driver;
  private String validationTitle;
  private String windowHandle;

  /**
   * @param driver WebDriver used to access page elements
   * @throws Exception If page does not validate
   */
  public Page(WebDriver driver) throws PageValidationException
  {
    this.driver = driver;
    this.windowHandle = driver.getWindowHandle();
    this.initiateElements();
  }

  /**
   * @return the handle to the window containing this page.
   */
  public String getWindowHandle()
  {
    return this.windowHandle;
  }

  /**
   * @throws Exception If a page fails validation (Java exception, etc)
   */
  protected void initiateElements() throws PageValidationException
  {
    CompanyPageFactory.initElements(driver, this);
    validatePage();
  }

  /**
   * @param url Sends a page to the new URL, and reinitates elements
   * @throws Exception
   */
  public void get(String url) throws Exception
  {
    this.driver.get(url);
    this.initiateElements();
  }

  /**
   * Validates the page, first through a validationTitle that's part of the definition of
   * the page object, and secondly through identifying whether an Exception is displayed on the
   * page or not
   * 
   * @throws PageValidationException
   */
  public void validatePage() throws PageValidationException
  {
	String pageTitle = driver.getTitle();
    if (validationTitle != null)
    {
      if (!validationTitle.equals(pageTitle))
      {
        throw new PageValidationException("PageObject failed title check: " + validationTitle
            + " does not equal " + pageTitle);
      }
    }

    if (pageTitle != null && pageTitle.equalsIgnoreCase("-------Need to be defined!!!!!!!!!!!!--------"))
    {
      // do nothing
    }
    else
    {
      // Scan for exceptions on the screen
      String[] patterns = new String[] {
          ".*<p>Exception: ([^:<]+)(: ([^<]+)|<).*",
          ".*Exception: ([^:<]+)(: ([^<]+)|<)?.*",
          ".*(java.lang.NullPointerException)().*"
      };
      Pattern p;
      Matcher m;
      String pageSource;

      // Firefox sometimes returns an exception
      try
      {
        pageSource = driver.getPageSource();
      }
      catch (WebDriverException e)
      {
        return;
      }

      for (String pattern : patterns)
      {
        p = Pattern.compile(pattern, Pattern.DOTALL);
        m = p.matcher(pageSource);

        if (m.matches())
        {
          throw new PageValidationException("Product Exception: " + m.group(1) + " " + m.group(2));
        }
      }
    }
  }

  /**
   * Can be used to reinitialize this page to a new driver context
   * in cases where you don't want to have to create a new page but simply want to
   * reuse the existing page object.  
   * In other words it allows you to have mutable pages as opposed to immutable pages.
   * @param driver
   * @throws Exception
   */
  public void reinitiateElements(WebDriver driver) throws Exception
  {
    this.driver = driver;
    this.initiateElements();
  }

  public WebDriver getDriver()
  {
    return driver;
  }

  public void setDriver(WebDriver driver)
  {
    this.driver = driver;
  }
}

package com.company.automation.automationframework.screenshots;

import java.io.File;

import org.openqa.selenium.Alert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogType;

import com.company.automation.automationframework.utils.WebDriverUtils;


/**
 * * Takes a general snapshot of the entire state of a screen, and accounts for things outside of just the
 * pixels of a screen shot.
 *
 *  @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *  
 */
public class ScreenState
{

  private File screenShot;
  private Alert alert;
  private WebDriver driver;
  private LogEntries consoleLogs;
  
  public ScreenState(WebDriver driver) {
    this.driver = driver;
    this.screenShot = WebDriverUtils.getScreenShot(driver);
    try {
      this.alert = driver.switchTo().alert();
    } catch (Exception e) {
      // that's ok
    }
    try {
    	this.consoleLogs = driver.manage().logs().get(LogType.BROWSER);
    } catch (WebDriverException e) {
    	// If logging not supported, don't worry about it
    } catch (NullPointerException npe) {
    	// If driver has flaked out for some reason, ignore
    }
  }
  
  public File getScreenShot() {
    return this.screenShot;
  }
  
  public Alert getAlert() {
    return this.alert;
  }
  
  public LogEntries getConsoleLog() {
	return this.consoleLogs;
  }
  
  /**
   * Returns the text of a current alert, while accepting it and resetting
   * the new screenstate with the new alert or lack thereof.
   * 
   * @return String including the alert message
   */
  public String popAlert() {
	
	  // take note of the current alert
	String alertText;
	if (alert == null) {
		return null;
	}
	alertText = alert.getText();
	
	// accept it
	alert.accept();
	
	// Update the screen state
	this.screenShot = WebDriverUtils.getScreenShot(driver);
	try {
		this.alert = null;
		this.alert = driver.switchTo().alert();
	} catch (Exception e) {
		// that's ok
	}
	
	return alertText;
  }
  
  public WebDriver getDriver() {
    return this.driver;
  }
}

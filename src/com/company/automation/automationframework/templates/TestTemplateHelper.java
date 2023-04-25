package com.company.automation.automationframework.templates;

import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.company.automation.automationframework.screenshots.ScreenState;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.automationframework.testrail.TestRunUtils;
import com.company.automation.automationframework.testrail.TestRunnerContext;
import com.company.automation.automationframework.utils.BrowserLibraryUtils;
import com.company.automation.automationframework.utils.ExceptionUtils;
import com.company.automation.automationframework.utils.StringUtils;

/**
 * This class contains common methods which help fulfill basic test initial steps, used by generic TestTemplate class
 *
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
class TestTemplateHelper
{
  
  /**
   * Reset TestLogger (verbose, stepDelay)
   * @param verbose
   * @param stepDelay
   */
  static void resetTestlogger(boolean verbose, int stepDelay)
  {
    // set verbose
    if (verbose) {
      TestLog.setVerbose("I_KNOW_THIS_SHOULD_ONLY_BE_SET_BY_CONFIGURATION_OPTIONS");
    }
    // set stepDelay
    if (stepDelay  < 0) 
    {
      stepDelay = 0;
    }
    
    TestLog.delaySteps(stepDelay);
  }
  
  /**
   * Initialize TestLog for a test method, including set scenario
   * 
   * @param context
   * @param className
   * @param method
   * @param objArray
   * @param verbose
   * @param stepDelay
   */
  static void initializeMethodLogger(ITestContext context, String className, Method method,
      Object[] objArray, boolean verbose, int stepDelay)
  {
    String uli = TestRunUtils.generateUniqueLogId(className, method.getName(), objArray);
    TestLog.initialize(uli);
    TestRunnerContext.setUniqueLogId(uli);

    // Get scenario from TestNg Parameter
    // If cannot find scenario from TestNG parameter,
    // then try to find a hashmap which contains a scenario indicator
    String scenario = TestRunUtils.getScenarioFromContext(context);
    if (StringUtils.isEmpty(scenario))
    {
      scenario = TestRunUtils.retriveScenarioId(objArray, method.getName());
    }

    TestLog.scenarioId(scenario);

    resetTestlogger(verbose, stepDelay);
  }
  
  /**
   * Release/Clean up TestLog for a test method
   * @param objArray
   * @param result
   * @param method
   * @param verbose
   * @param stepDelay
   * @param driver
   */
  static void finalizeMethodLogger(Object[] objArray, ITestResult result,
      Method method, boolean verbose, int stepDelay, WebDriver driver)
  {
    String ID = TestRunUtils.generateUniqueLogId(result.getTestClass().getName(), method.getName(), objArray);
    // Before closing logger, check if this test was failed and if so, capture
    // failure screenshot
    if (!result.isSuccess() && driver != null)
    {
      TestLog.screenshot(new ScreenState(driver), "Failed Screenshot");
    }

    // close the test log for this test
    TestLog.done(ID);

    TestLog.removeLogEntries(ID, Thread.currentThread().getId());

    resetTestlogger(verbose, stepDelay); // reset for after class)

    // Clear context
    TestRunnerContext.setUniqueLogId(null);
  }
  
  /**
   * Step up a webdriver instance based on given parameters
   * 
   * @param browser
   * @param version
   * @param platform
   * @param remote
   * @return
   */
  static WebDriver setupDriver(String browser, String version, String platform, String remote)
  {
    WebDriver driver = null;
    
    try
    {
    driver = BrowserLibraryUtils.loadBrowser(browser, version, platform, remote);
    }
    catch (Exception e)
    {
      throw new WebDriverException("Exception thrown when setting up web Driver[" + ExceptionUtils.getErrorMsg(e) + "]", e);
    }
    if (driver == null) {
      throw new WebDriverException("Web Driver not setup properly"); 
    }
    
    TestRunnerContext.setBrowser(browser);
    
    return driver;
  }
}

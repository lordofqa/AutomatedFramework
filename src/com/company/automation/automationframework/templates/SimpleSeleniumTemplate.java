package com.company.automation.automationframework.templates;

import java.lang.reflect.Method;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.company.automation.automationframework.screenshots.ScreenState;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.automationframework.testrail.TestRunnerContext;
import com.company.automation.automationframework.utils.ExceptionUtils;
import com.company.automation.automationframework.utils.FrameworkUtils;

/**
 * This is a generic Selenium test template. It includes various default setup
 * routines that allow for quick and easy creation of a test class.
 * <P>
 * Extending this class will automatically:
 * <UL>
 * <LI>Open a new browser window if UI testing parameter is specified</LI>
 * <LI>Once your test has completed, it will close your browser window</LI>
 * </UL>
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 */
public class SimpleSeleniumTemplate
{

 public final static String DEFAULT_BROWSER                             = "chrome";
 public final static String DEFAULT_VERSION                             = "";
 public final static String DEFAULT_PLATFORM                            = "ANY";
 public final static String DEFAULT_REMOTE                              = "false";
 public final static String DEFAULT_SERVER                              = "";
 public final static String DEFAULT_ADMIN_CONSOLE                       = "";
 public final static String DEFAULT_REST_API_SERVER                     = "http://*.*.*.*:8080";
 public final static String DEFAULT_CMS_API_URL                         = "";
 public final static String DEFAULT_IGNORE_LOG_ENTRY_FOR_PASSED_TESTS   = "false";
 public final static String DEFAULT_IS_IT_UI_TEST_NEEDED_BROWSER_LAUNCH = "false";
  
  /**
   * This is the main driver, which provides an interface to the web browser
   */
  protected WebDriver driver;

  /**
   * A string which contains the original window handle, in case you ever need
   * to make your way back to the first window again.
   */
  protected String originalWindowHandle;

  /**
   * The test class level how long each test step will be paused
   */
  protected int stepDelay;

  /**
   * The test class level flag if print out log information on the console
   */
  protected boolean verbose;

  /**
   * Setup logger parameters at class level
   * @param verbose: if print out log in console
   * @param stepDelay: how long pause test step
   */
  @BeforeClass
  @Parameters({ "verbose", "stepDelay" })
  protected void setupClassLogger(@Optional("false") String verbose, @Optional("0") int stepDelay)
  {
    this.stepDelay = stepDelay;
    this.verbose = verbose.equalsIgnoreCase("true") ? true : false;
    
    TestTemplateHelper.resetTestlogger(this.verbose, this.stepDelay); 

  }

  /**
   * Get browser
   * 
   * @param browser
   *          String representing the type of browser you want to open
   * @param version
   *          String representing the version of the browser
   * @param platform
   *          String representing the OS
   * @param remote
   *          String value representing boolean for whether test is to run
   *          remotely
   * @throws Exception
   *           If creation of the browser driver fails
   */
  @BeforeClass(dependsOnMethods = "setupClassLogger")
  @Parameters({ "browser", 
	  			"version", 
	  			"platform", 
	  			"remote", 
	  			"server",
	  			"adminConsoleUrl",
	  			"restApiUrl",
	  			"cmsApiUrl",
	  			"ignorePassed",
	  			"uiTest"
	  			})
  public void setupDriver(	@Optional(DEFAULT_BROWSER) String browser,
		  					@Optional(DEFAULT_VERSION) String version, 
		  					@Optional(DEFAULT_PLATFORM) String platform,
		  					@Optional(DEFAULT_REMOTE) String remote,
		  					@Optional(DEFAULT_SERVER) String server,
		  					@Optional(DEFAULT_ADMIN_CONSOLE) String adminConsole,
		  					@Optional(DEFAULT_REST_API_SERVER) String restApiUrl,
		  					@Optional(DEFAULT_CMS_API_URL) String cmsApiUrl,
		  					@Optional(DEFAULT_IGNORE_LOG_ENTRY_FOR_PASSED_TESTS) String ignorePassed,
		  					@Optional(DEFAULT_IS_IT_UI_TEST_NEEDED_BROWSER_LAUNCH) String uiTest
) throws Exception
  {
    
    if(uiTest.compareTo("true")==0) 
    {
     driver = TestTemplateHelper.setupDriver(browser, version, platform, remote);
     originalWindowHandle = driver.getWindowHandle();
     FrameworkUtils.initiateBrowserHandling(originalWindowHandle);
    }

    TestRunnerContext.setBrowser(browser);
    TestRunnerContext.setRemote(remote);
    TestRunnerContext.setServer(server);
    TestRunnerContext.setAdminConsole(adminConsole);
    TestRunnerContext.setRestAPIURL(restApiUrl);
    TestRunnerContext.setCmsAPIURL(cmsApiUrl);
    TestRunnerContext.setIgnorePassed(ignorePassed);
    
  }

  /**
   * Set up TestReport parameters.
   * Allow to override the final test status: passed, skipped, failed to different status.
   * For instance, override passed status to completed
   */
  @BeforeClass
  public void setTestReportParameter()
  {
   //// TBD
  }

  /**
   * Initialize the static logger with a unique identifier for the test and thread
   * its running on
   * 
   * @param objArray
   */
  @BeforeMethod
  protected void setupLogger(ITestContext context, Method method, Object[] objArray)
  {
    TestTemplateHelper.initializeMethodLogger(context, this.getClass().getName(), method, objArray,
        this.verbose, this.stepDelay);
  }

  /**
   * Destroy the static logger for the test and thread its running on
   * @param objArray
   * @param result
   * @param method
   */
  @AfterMethod(alwaysRun = true)
  protected void closeLogger(Object[] objArray, ITestResult result,
      Method method)
  {
    TestTemplateHelper.finalizeMethodLogger(objArray, result, method, verbose, stepDelay, driver);
  }

  /**
   * Close browsers once we're done
   * 
   * @param close
   *          - do not close if parameter is false
   */
  @AfterClass(alwaysRun = true)
  @Parameters({ "close" })
  public void closeDrivers(@Optional("true") String close)
  {

    if (close.equalsIgnoreCase("true"))
    {
      // If we can't quit the driver, keep going with other tests
      try
      {
        driver.quit();
      }
      catch (Exception e)
      {
        TestLog.debug("Failed to close driver [" + ExceptionUtils.getErrorMsg(e) + "]");

      }
    }
  }

  /**
   * Helper function to ask the test class to take a picture
   * 
   * @return File pointing to a screenshot
   */
  public ScreenState takeScreenShot()
  {

    return new ScreenState(this.driver);
  }

  /**
   * @return Just returns the driver
   */
  public WebDriver getDriver()
  {
    return driver;
  }

}

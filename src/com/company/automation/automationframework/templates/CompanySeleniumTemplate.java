package com.company.automation.automationframework.templates;

import org.testng.ITestContext;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import com.company.automation.automationframework.exceptions.QualityException;
import com.company.automation.automationframework.navigation.Navigate;
import com.company.automation.automationframework.profile.LoginProfile;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.automationframework.testrail.TestRunnerContext;
import com.company.automation.testautomation.pages.SlashdotEntryPage;

/**
 * This is the standard company Selenium test template.  It includes various default setup
 * routines that allow for quick and easy creation of a test class.  Extending this class
 * will automatically:
 * <P>
 * <UL><LI>Open a new browser window if we specify UI testing parameter </LI>
 * <LI>Read your login information from login.properties</LI>
 * <LI>Go to the company login page on the server provided in login.properties</LI>
 * <LI>Log you in</LI>
 * <LI>Take you to the home page</LI>
 * <LI>Once your test has completed, it will close your browser window</LI>
 * </UL>
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class CompanySeleniumTemplate extends SimpleSeleniumTemplate
{

  /**
   * Easy navigation object, providing conveniece calls to test classes
   */
  protected Navigate navigate;

  /**
   * This loginProfile contains the username, password, server and database,
   * as well as functions to provide base url, etc. 
   */
  protected LoginProfile loginProfile;

  /**
   * Setup the navigation object
   */
  @BeforeClass(dependsOnMethods = { "setupDriver", "setLoginProfile" })
  public void setupNavigation()
  {
    navigate = new Navigate(driver, loginProfile);
  }

  /**
   * Login to the application using credentials from the login.properties file or overridden in the XML file
   * 
   * @throws Exception
   *             If the file cannot be read
   */
  @BeforeClass(dependsOnMethods = "setLoginProfile")
  public void login() throws Exception
  {
    if (driver!=null) CompanyTemplateHelper.login(this.loginProfile, driver);
  }
  
  /**
   * Login to the application using credentials provided in the LoginProfile as parameter
   * @param loginProfile
   * @throws Exception
   */
  public void login(LoginProfile loginProfile) throws Exception
  {
	  CompanyTemplateHelper.login(loginProfile, driver);
  }


  /**
   * Get the login profile so we know where to test
   * 
   * Pulls login information from conf/login.properties.  Requires:
   * <P><UL>
   * <LI>database (aka org code)</LI>
   * <LI>server</LI>
   * <LI>user</LI>
   * <LI>password</LI>
   *</UL>
   * @throws Exception If the file cannot be read
   */
  @BeforeClass(dependsOnMethods = "setupDriver")
  public void setLoginProfile(ITestContext testContext) throws Exception
  {
    
    loginProfile = CompanyTemplateHelper.setLoginProfile(testContext);
  }

  /**
   * Capture details for debugging, including:
   * 
   *  - Server
   *  - Org code
   *  - User
   * @throws Exception 
   */
  @BeforeClass(dependsOnMethods = "login")
  public void captureEnvironmentDetails()
  {
    TestLog.server(loginProfile.getServer());
    TestLog.browser(TestRunnerContext.getBrowser());
    TestLog.remote(TestRunnerContext.getRemote());
    TestLog.orgCode(loginProfile.getOrgCode());
    TestLog.userName(loginProfile.getUserName());
    try
    {
     SlashdotEntryPage mp = new SlashdotEntryPage(driver);
      
      //TestLog.facility(mp.getTopNavigationBar().getFacilityName());
      //TestLog.appVersion(mp.getCompanyVersion());

      //TestRunnerContext.setAppVersion(mp.getCompanyVersion());
      //TestRunnerContext.setAppRevision(mp.getCompanyRevision());

    }
    catch (Exception e)
    {
      // This is just to capture debug information, don't fail on this if it doesn't work
      // If we can't get the info, just don't do it, error messages aren't useful
    }

  }

   /**
   * Removes TestRunnerContext for this test thread
   * @throws Exception 
   * @throws QualityException 
   */
  @AfterClass(alwaysRun = true)
  public void removeTestRunnerContext() throws QualityException, Exception
  {
    //Release connection from Connection Pool
    //if (AutomationProperties.getProperty("company.af.databasepool").equals("true")) ConnectionManager.getInstance().releaseConnection(TestRunnerContext.getOrgCode());
    
    TestRunnerContext.cleanContext();
  }
}
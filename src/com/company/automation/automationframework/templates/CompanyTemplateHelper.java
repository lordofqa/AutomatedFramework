package com.company.automation.automationframework.templates;

import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;

import com.company.automation.automationframework.exceptions.NoSuchPropertyException;
import com.company.automation.automationframework.exceptions.PageValidationException;
import com.company.automation.automationframework.exceptions.QualityException;
import com.company.automation.automationframework.profile.AutomationProperties;
import com.company.automation.automationframework.profile.LoginProfile;
import com.company.automation.automationframework.testrail.TestRunnerContext;
import com.company.automation.testautomation.pages.SlashdotEntryPage;

/**
 * Helper class to contain reusable code at the Test Template level
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
class CompanyTemplateHelper
{

 /**
  * Log into the company web app with the details provided in the loginProfile
  * 
  * @param loginProfile
  * @param driver
  * @throws Exception
  */
 static void login(LoginProfile loginProfile, WebDriver driver) throws PageValidationException, NoSuchPropertyException, QualityException
 {
  // Create a login page object, and give it the profile to login with
  SlashdotEntryPage page = new SlashdotEntryPage(driver);

  // int limit =
  // Integer.parseInt(AutomationProperties.getProperty("login.retries"));
  int limit = 1;

  int index = 0;
  do
  {
   page.getToLoginPage(loginProfile, driver);
   page.login(loginProfile);
  }
  while ((driver.findElements(By.linkText("Logout")).size() == 0) && ++index < limit);

  /*
   * if (driver.findElements(By.linkText("Logout")).size() == 0) { throw new
   * QualityException("Login failed: " + loginProfile.getOrgCode() + "." +
   * loginProfile.getUserName()); }
   */

 }

 /**
  * Setup the login profile using details in the properties file and the
  * testContext
  * 
  * @param testContext
  * @return loginProfile
  * @throws Exception
  */
 static LoginProfile setLoginProfile(ITestContext testContext) throws Exception
 {

  Properties p = AutomationProperties.getProperties();

  // //////////

  String adminConsole = TestRunnerContext.getAdminConsole();
  String restApiUrl = TestRunnerContext.getRestAPIURL();
  String cmsApiUrl = TestRunnerContext.getCmsAPIURL();
  String ignorePassed = TestRunnerContext.getIgnorePassed();

  String appServer = TestRunnerContext.getServer();

  if (adminConsole.compareTo("") == 0)
  {
   adminConsole = p.getProperty("admin.server");
   TestRunnerContext.setAdminConsole(adminConsole);
  }
  if (restApiUrl.compareTo("") == 0)
  {
   restApiUrl = p.getProperty("restapi.server");
   TestRunnerContext.setRestAPIURL(restApiUrl);
  }

  if(cmsApiUrl.compareTo("")==0) 
  {
   cmsApiUrl = p.getProperty("cmsapi.server");
   TestRunnerContext.setCmsAPIURL(cmsApiUrl);
  }

  
  if (appServer.compareTo("") == 0)
  {
   appServer = p.getProperty("app.server");
   TestRunnerContext.setServer(appServer);
  }

  // never supposed to get to "then" part (ignorePassed set to "false" by
  // default)
  if (ignorePassed.compareTo("") == 0)
  {
   ignorePassed = p.getProperty("ignorePassed");
   TestRunnerContext.setServer(ignorePassed);
  }

  // //////////

  // Create a new login profile object
  LoginProfile loginProfile = new LoginProfile(p.getProperty("app.orgcode"), p.getProperty("app.user"),
  // p.getProperty("app.pass"), p.getProperty("app.server"),
    p.getProperty("app.pass"), TestRunnerContext.getServer(), p.getProperty("app.transport"), TestRunnerContext.getBrowser());

  // If we have environment variables that should take its place, use those
  // instead
  loginProfile.overRideWithEnvironmentVariables();

  // If XML tries to override or set login details, let it
  loginProfile.overRideWithXml(testContext);

  // set TestRunnerContext for this test thread
  // If database pool is turned on, then obtain connection from
  // ConnentionManager
  if (AutomationProperties.getProperty("databasepool").equals("true"))
  {
   // handle pseudo orgCode
   String orgCode = null; // =ConnectionManager.getInstance().obtainConnection(loginProfile.getOrgCode());
   TestRunnerContext.setOrgCode(orgCode);
   loginProfile.overrideOrgCode(orgCode);
  }
  else
  {
   TestRunnerContext.setOrgCode(loginProfile.getOrgCode());
  }

  if (loginProfile.getServer().compareTo("") == 0) loginProfile.overrideServer(p.getProperty("app.server"));

  TestRunnerContext.setServer(loginProfile.getServer());
  TestRunnerContext.setUser(loginProfile.getUserName());
  TestRunnerContext.setPassword(loginProfile.getPassWord());

  return loginProfile;
 }
}

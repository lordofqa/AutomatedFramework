package com.company.automation.testautomation.tests;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.company.automation.automationframework.annotations.DataSource;
import com.company.automation.automationframework.dataprovider.CsvDataProvider;
import com.company.automation.automationframework.templates.CompanySeleniumTemplate;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.testautomation.helpers.SlashdotHelper;
import com.company.automation.testautomation.pages.SlashdotEntryPage;
import com.company.automation.testautomation.pages.SlashdotVoteResultsPage;


@DataSource(source = "com/company/automation/testautomation/tests/data/TC_FirstTest.csv")

/**
 * Test objective: Verify that 
 * 
 * Test steps:
 * Step 01: 
 * Step 06: Verify 
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *         Jira#:
 *
 */
public class TC_FirstTest extends CompanySeleniumTemplate
{

 @DataProvider(name = "csvData")
 public Iterator<Object[]> data(ITestContext context, Method method) throws Exception
 {
  return CsvDataProvider.getHashmapFromCsv(context, method);
 }

 @Test(dataProvider = "csvData")
 public void verifySlingshotLogin(HashMap<String, String> csv) throws Exception
 {
  TestLog.setVerbose("I_KNOW_THIS_SHOULD_ONLY_BE_SET_BY_CONFIGURATION_OPTIONS");

  String username = csv.get("username");
  String password = csv.get("password");
  String expectedResult = csv.get("expectedResult");
  boolean expectedTestResult = Boolean.valueOf(expectedResult);

  SlashdotEntryPage slashdotEntryPage = new SlashdotEntryPage(driver);

  int number = slashdotEntryPage.getNumberOfAvailablePolls(driver);
  
  slashdotEntryPage.printListOfUniqueIconsOnTheTitlePage(driver);

  int totalNumberOfVoteOptions = slashdotEntryPage.getNumberOfAvailablePolls(driver);
  int randomNumber = SlashdotHelper.getRandomIntInRange(0,totalNumberOfVoteOptions);

//voting and switching to the new page
  driver = slashdotEntryPage.voteForCertainOptionInPoll(driver,randomNumber);
  
  // Creating corresponding page object
  SlashdotVoteResultsPage slashdotVoteResultsPage = new SlashdotVoteResultsPage();

  String result = slashdotVoteResultsPage.getVoteResultsForSpecificPosition(driver, randomNumber);
  System.out.println("\nNumber of people that have voted for that same option on the daily poll:[" + result + "]");
  
  
  TestLog.verify("Verify " + username + " successfully login to Slashdot ");
  //Assert.assertTrue( *** == expectedTestResult);

 }
}

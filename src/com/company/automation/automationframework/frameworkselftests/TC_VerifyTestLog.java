package com.company.automation.automationframework.frameworkselftests;

/**
 * Test objective: Verify that automation framework TestLog functionality works as it suppose to  
 * 
 * Test steps:
 * Step 1 : Get parameter from scenario file
 * Step 2 : Verify: that we are getting expected data 
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 * Jira#:
 *
 */

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;

import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.company.automation.automationframework.annotations.DataSource;
import com.company.automation.automationframework.dataprovider.CsvDataProvider;
import com.company.automation.automationframework.templates.CompanySeleniumTemplate;
import com.company.automation.automationframework.testlog.TestLog;

@DataSource(source = "com/company/automation/automationframework/frameworkselftests/data/TC_VerifyTestLog.csv")

public class TC_VerifyTestLog extends CompanySeleniumTemplate
{

 @DataProvider(name = "csvData")
 public Iterator<Object[]> data(ITestContext context, Method method) throws Exception
 {
  return CsvDataProvider.getHashmapFromCsv(context, method);
 }

 @Test(dataProvider = "csvData")
 
 public void testCSVDataProvider  (HashMap<String, String> csv)   throws Exception
 {
  
  TestLog.setVerbose("I_KNOW_THIS_SHOULD_ONLY_BE_SET_BY_CONFIGURATION_OPTIONS");
  
  // * Step 1 : Get parameter from scenario file
  TestLog.step("Step 1 : Get parameter's from scenario file");
  String parameterFromCSVFile = csv.get("ScenarioData");
  String expectedParameterFromCSVFile  = csv.get("ExpectedData");
  
  TestLog.step("Scenario Data for this scenario is:[" + parameterFromCSVFile + "]");
  TestLog.step("Expected Data for this scenario is:[" + expectedParameterFromCSVFile + "]");

  // * Step 2 : Verify: that we are getting expected data
  TestLog.verify("Verify: that we are getting expected data");
  Assert.assertTrue(parameterFromCSVFile.compareTo(expectedParameterFromCSVFile) == 0, "Wrong data: Expected:["+expectedParameterFromCSVFile+"], real data:["+parameterFromCSVFile+"]");
 }

}

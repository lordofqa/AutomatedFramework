package com.company.automation.automationframework.frameworkselftests;

/**
 * Test objective: Verify that automation framework DataProveder functionality works as it suppose to  
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

@DataSource(source = "com/company/automation/automationframework/frameworkselftests/data/TC_VerifyCSVDataProvider.csv")

public class TC_VerifyCSVDataProvider
{

 @DataProvider(name = "csvData")
 public Iterator<Object[]> data(ITestContext context, Method method) throws Exception
 {
  return CsvDataProvider.getHashmapFromCsv(context, method);
 }

 @Test(dataProvider = "csvData")
 
 public void testCSVDataProvider  (HashMap<String, String> csv)   throws Exception
 {
  // * Step 1 : Get parameter from scenario file
  String parameterFromCSVFile          = csv.get("ScenarioData");
  String expectedParameterFromCSVFile  = csv.get("ExpectedData");
  
  System.out.println("Scenario Data for this scenario is:[" + parameterFromCSVFile+"]");
  System.out.println("Expected Data for this scenario is:[" + expectedParameterFromCSVFile+"]");

  // * Step 2 : Verify: that we are getting expected data
  Assert.assertTrue(parameterFromCSVFile.compareTo(expectedParameterFromCSVFile) == 0);
 }

}

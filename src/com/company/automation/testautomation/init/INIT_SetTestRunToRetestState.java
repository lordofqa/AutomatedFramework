package com.company.automation.testautomation.init;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.ITestContext;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.company.automation.automationframework.annotations.DataSource;
import com.company.automation.automationframework.constants.TestParameterConstants;
import com.company.automation.automationframework.dataprovider.CsvDataProvider;
import com.company.automation.automationframework.testrail.TestRailConnector;
import com.gurock.testrail.APIClient;

/**
 * Provides initial setting of test in specific test run to the "retest" before 
 * actual execution of test run
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 * Jira#:
 */
@DataSource(source = "com/company/automation/testautomation/init/data/INIT_SetTestRunToRetestState.csv")

public class INIT_SetTestRunToRetestState
{
	
 @DataProvider(name = "csvData")
 public Iterator<Object[]> data(ITestContext context, Method method) throws Exception
 {
  return CsvDataProvider.getHashmapFromCsv(context, method);
 }	
	
 @Test(dataProvider = "csvData")
 public void test_verify_reasons(HashMap<String, String> csv) throws Exception
 {
  TestRailConnector.getConnector();
  String testCaseId;
 
  String testRunId = csv.get("testRunId");
  
  try
  {
  	APIClient client = TestRailConnector.getAPIClient();
   JSONArray jSONArray = (JSONArray) client.sendGet(TestParameterConstants.TESTRAIL_GET_TESTCASES_BY_TESTRUN_ID + testRunId);
   System.out.println("Total test in test run:["+jSONArray.size()+"]");
   for (int i=1; i< jSONArray.size()+1; i++  )
   {
    JSONObject jsonTestCaseObject = (((JSONObject)jSONArray.get(i-1)));
    testCaseId =  jsonTestCaseObject.get(TestParameterConstants.TESTRAIL_TESTCASE_TEST_CASE_ID_FIELD).toString(); 
   
    System.out.println("N"+i+"\t CaseId:["+testCaseId+"]\t Scenario:["+jsonTestCaseObject.get(TestParameterConstants.TESTRAIL_TESTCASE_TEST_SCENARIO_FIELD)+"]\t" + "Class name:["+jsonTestCaseObject.get(TestParameterConstants.TESTRAIL_TESTCASE_TEST_CLASSPATH_FIELD)+"]" );
    
    //Set info for TestRail
    Map data = new HashMap();
    // 1 = pass
    // 2 = blocked 
    // 3 = untested
    // 4 = retest
    // 5 = failed
    data.put(TestParameterConstants.TESTRAIL_TESTCASE_TEST_STATUS_ID_FIELD, new Integer(TestParameterConstants.TESTRAIL_RETEST_CODE));
    
    //to avoid testrail 429 error - too many requests!
    if(i%50==0) Thread.sleep(60000);
    Thread.sleep(2000);
    JSONObject r = (JSONObject) client.sendPost(TestParameterConstants.TESTRAIL_ADDTESTCASE_RESULT_COMMAND + testRunId +"/"+testCaseId, data);  
   }
  }
  catch(Exception e)
  {
   System.out.println(e.toString());
  }	
 }		
}


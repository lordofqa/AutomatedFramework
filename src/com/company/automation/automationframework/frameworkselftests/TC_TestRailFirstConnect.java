package com.company.automation.automationframework.frameworkselftests;

import com.gurock.testrail.APIClient;
import com.gurock.testrail.APIException;
import com.company.automation.automationframework.constants.TestParameterConstants;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.automationframework.constants.TestParameterConstants;

import java.net.HttpURLConnection;
import java.util.Map;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/*import com.company.automation.automationframework.enums.HTTPRequestEnum;
import com.company.automation.automationframework.exceptions.HTTPRequestException;
import com.company.automation.automationframework.exceptions.TestRailException;
import com.company.automation.automationframework.models.HTTPRequestProcessor;
import com.company.automation.automationframework.models.HTTPResponse;
import com.company.automation.automationframework.testrail.TestRailConnector;*/


public class TC_TestRailFirstConnect 
{

 public static void main(String[] args) throws Exception
 {
	 

  APIClient client = new APIClient(TestParameterConstants.TESTRAIL_URL);
  client.setUser(TestParameterConstants.TESTRAIL_LOGIN);
  client.setPassword(TestParameterConstants.TESTRAIL_PASSWORD);

  //Get info from TestRail
  //JSONObject c = (JSONObject) client.sendGet("get_case/5");
  //System.out.println(c.get("title"));		
 
  //JSONArray c = (JSONArray) client.sendGet("get_tests/1");
  //System.out.println(((JSONObject)c.get(2)).get("title"));
  
  //String str = TestRailConnector.TESTRAIL_GET_TESTCASES_BY_TESTRUN_ID +"1" + "&" + "status_id=1";
  String str = TestParameterConstants.TESTRAIL_GET_TESTCASES_BY_TESTRUN_ID +  "1";
  //custom_testclasspath
  
  
 /* HashMap<String, String> header = new HashMap<String, String>();
  header.put(HTTPRequestEnum.HTTPRequestHeader.ACCEPT.getProperty(), "application/json");
  HTTPResponse response = null;
  HTTPRequestProcessor processor;
  try
  {
    processor = new HTTPRequestProcessor("https://company.testrail.com/miniapi/add_result/11&key=***");
    response = processor.sendHttpRequest(header);
  }
  catch (HTTPRequestException e)
  {
    throw new TestRailException(e);
  }*/
  
 
  
  //String result = (String) client.sendGet(str);
  
  JSONArray c = (JSONArray) client.sendGet(str);
  
  for(int i=0; i<c.size(); i++)
  {
   
   String str3 = 	 (String)((JSONObject)c.get(i)).get(TestParameterConstants.TESTRAIL_TESTCASE_TEST_CLASSPATH_FIELD); 
   System.out.println(str3);
   if(str3.compareTo("com.company.automation.automationframework.frameworkselftests.TC_TestRailFirstConnect")==0)
	   System.out.println(((JSONObject)c.get(i)).get(TestParameterConstants.TESTRAIL_TESTCASE_TEST_CASE_ID_FIELD));
  }
  
  //Set info for TestRail
  Map data = new HashMap();
  // 1 = pass
  // 2 = blocked 
  // 3 = untested
  // 4 = retest
  // 5 = failed
  data.put(TestParameterConstants.TESTRAIL_TESTCASE_TEST_STATUS_ID_FIELD, new Integer(1));
  data.put(TestParameterConstants.TESTRAIL_TESTCASE_TEST_COMMENT_FIELD, "And now passed!");
  JSONObject r = (JSONObject) client.sendPost(TestParameterConstants.TESTRAIL_ADDTESTCASE_RESULT_COMMAND + "1" +"/1", data);  
  
 }	
 
 /*public TestRailAPI getTestRail()
 {
  TestRailAPI testRail = new TestRailAPI("http://testrail/");
  xmlTexts = parser.parseXml(pathDirectory + fileName);
  testRail.username = parser.findValue(ref xmlTexts, "username");
  testRail.password = parser.findValue(ref xmlTexts, "password");
  return testRail;
 }
 
 public void sendTCResult(IWebDriver driver, int testRunId, int testCasesId, int result, String message, Exception e = null)
 {
  try
  {
   // TestRail results:
   TestRailAPI testRail = getTestRail();
   JObject json = (JObject)testRail.SendGet("get_case/" + testCasesId);
   string testTitle = json["title"].ToString();
   string priority = json["priority_id"].ToString();
             
   Dictionary<string, object> testResults = null;
   if (result == 1)
   {
    testResults = new Dictionary<string, object>
    {
     { "status_id", 1 },
     { "comment", message }
    };
   }
   else if (result == 5)
   {
    foreach (string exception in exceptions)
    {
     if (e != null && e.GetBaseException().GetType().Name == exception)
     {
      string testId = testRail.getTestId(testRunId, testCasesId);

      defectNum = onTime.onTimeFail(testTitle, testId, priority, e.GetBaseException().GetType().Name); // ontime integration

      break;
     }
    }
    testResults = new Dictionary<string, object>
    {
     { "status_id", 5 },
     { "comment", message },
     { "defects", defectNum }
    };
   }

   json = (JObject)testRail.SendPost("add_result_for_case/" + testRunId + "/" + testCasesId, testResults);
  }
  catch (Exception ex)
  {
   if (ex.GetBaseException().GetType().Name == "APIException")
   {
    Console.WriteLine("Test Case is Invalid :" + ex.Message);
   }
   else if (ex.GetBaseException().GetType().Name == "JsonReaderException")
   {
    Common.endDriverProgram(driver, "Api Failed: " + ex.Message);
   }
   else
   { 
    Console.WriteLine(ex.GetBaseException().GetType().Name);
   }
  }
 }
	*/
//**********************************CSV**********************************
 //	TestCaseId,	TestCaseName,			Execute
 //	111,		Verify_Adding_Invoice,	yes
 
 
}

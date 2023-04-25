package com.company.automation.automationframework.listener;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.openqa.selenium.remote.UnreachableBrowserException;
import org.testng.IResultMap;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import org.testng.xml.XmlTest;

import com.company.automation.automationframework.constants.TestParameterConstants;
import com.company.automation.automationframework.enums.TestResultStatusEnum;
import com.company.automation.automationframework.exceptions.QualityException;
import com.company.automation.automationframework.screenshots.Screenshot;
import com.company.automation.automationframework.screenshots.ScreenshotFtpClient;
import com.company.automation.automationframework.screenshots.ScreenshotSFtpClient;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.automationframework.testrail.TestRailConnector;
import com.company.automation.automationframework.testrail.TestResult;
import com.company.automation.automationframework.testrail.TestResultCapture;
import com.company.automation.automationframework.testrail.TestRunUtils;
import com.company.automation.automationframework.testrail.TestScenarioResult;

/**
 * This test listener is registered with certain tests or test executions. It
 * indirectly listens to test results and attempts to update TestRail with the
 * results. This listener must be registered, and TestRail must be updated with
 * the class and the scenario id of the test. It must also have a way of
 * reaching the scenario id through one of the parameters of the test. Most
 * commonly this happens through passing in a HashMap which includes a scenario
 * key, or through a TDO which implements ScenarioData.
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 */
public class TestRailListener extends TestListenerAdapter
{

  private TestRailConnector trConnect = null;

  public void onTestFailure(ITestResult tr)
  {
    // Validate whether the failure should actually be a skip
    if (isFailureReallySkip(tr))
    {
      onTestSkipped(tr);
    }
    else
    {
      manageTestFailure(tr);
    }
  }

  public void manageTestFailure(ITestResult tr)
  {
    try
    {
      TestRunUtils.takeFailureScreenShot(tr);
      String clazzName = tr.getMethod().getClass().getClass().getName();
      if (TestRunUtils.getTestReportParameter(clazzName) != null)
      {
        String customizedStatus = TestRunUtils
            .getTestReportParameter(clazzName).get("failedStatus");
        if (customizedStatus != null)
        {
          onTestResult(TestResultStatusEnum.getTestResultStatusEnum(customizedStatus), tr);
          return;
        }
      }

      onTestResult(TestResultStatusEnum.FAILED, tr);

    }
    catch (Exception e)
    {
      // TODO write error to special log and/or send email
      e.printStackTrace();
    }
  }

  /**
   * Check to see if a failure is unrelated to the application, and therefore a skip.
   * Modifies status of result to a Skip and returns true if so.
   * 
   * @param tr Test result
   * @return Whether or not the result was determined to be a skip
   */
  private boolean isFailureReallySkip(ITestResult tr)
  {

    Throwable t = tr.getThrowable();

    // NullPointerException can only be a test failure
    if ((t.getClass() == NullPointerException.class) ||
        (t.getClass() == org.openqa.selenium.WebDriverException.class) ||
        (t.getClass() == java.net.SocketException.class) ||
        (t.getClass() == UnreachableBrowserException.class) ||
        (t.getClass() == org.openqa.selenium.UnsupportedCommandException.class))
    {

      tr.setStatus(ITestResult.SKIP);
      return true;
    }

    return false;
  }

  public void onTestSkipped(ITestResult tr)
  {
    try
    {
      String uniqueLogId = TestRunUtils.generateUniqueLogId(tr.getTestClass()
          .getName(), tr.getMethod().getMethodName(), tr.getParameters());

      if (TestLog.getLogEntries(uniqueLogId).isEmpty())
        TestLog.initialize(uniqueLogId);

      TestRunUtils.takeFailureScreenShot(tr);

      if (tr.getThrowable() == null)
      {
        IResultMap failedConfigurations = tr.getTestContext().getFailedConfigurations();

        if (failedConfigurations.size() > 0)
        {
          Iterator<ITestResult> results = failedConfigurations.getAllResults().iterator();
          tr.setThrowable(results.next().getThrowable());
        }
      }
      String clazzName = tr.getMethod().getClass().getClass().getName();
      if (TestRunUtils.getTestReportParameter(clazzName) != null)
      {
        String customizedStatus = TestRunUtils
            .getTestReportParameter(clazzName).get("skippedStatus");
        if (customizedStatus != null)
        {
          onTestResult(TestResultStatusEnum.getTestResultStatusEnum(customizedStatus), tr);
          return;
        }
      }

      onTestResult(TestResultStatusEnum.BLOCKED, tr);
    }
    catch (Exception e)
    {
      // TODO write error to special log and/or send email
      e.printStackTrace();
    }
  }

  public void onTestSuccess(ITestResult tr)
  {
    try
    {
      String clazzName = tr.getTestClass().getName();
      if (TestRunUtils.getTestReportParameter(clazzName) != null)
      {
        String customizedStatus = TestRunUtils
            .getTestReportParameter(clazzName).get("passedStatus");
        if (customizedStatus != null)
        {
          onTestResult(TestResultStatusEnum.getTestResultStatusEnum(customizedStatus), tr);
          return;
        }
      }

      onTestResult(TestResultStatusEnum.PASSED, tr);
    }
    catch (Exception e)
    {
      // TODO write error to special log and/or send email
      e.printStackTrace();
    }
  }

  /**
   * Handler for failure on after configuration methods
   * @param tr : TestNG ITestResult
   */
  public void onConfigurationFailure(ITestResult tr)
  {
    String uniqueLogId = null;
    try
    {
      // Get method from ITestResult
      ITestNGMethod method = tr.getMethod();

      // Handle test result ONLY if fails at after configuration
      if ((tr.getStatus() == ITestResult.FAILURE)
          && (method.isAfterMethodConfiguration()
              || method.isAfterClassConfiguration()
              || method.isAfterTestConfiguration()
              || method.isAfterSuiteConfiguration()
              || method.isAfterGroupsConfiguration()))
      {
        // Get unique log ID for the test configuration method
        uniqueLogId = TestRunUtils.generateUniqueLogId(tr.getTestClass()
            .getName(), tr.getMethod().getMethodName(), tr.getParameters());

        // Initialize TestLog for the unique log ID
        if (TestLog.getLogEntries(uniqueLogId).isEmpty())
          TestLog.initialize(uniqueLogId);

        // Take screen shot for this test result
        TestRunUtils.takeFailureScreenShot(tr);

        // Register test result as PostTestFailed
        onTestResult(TestResultStatusEnum.POSTTESTFAILED, tr);
      }

    }
    catch (Exception e)
    {
      TestLog.debug(e.getMessage());
    }
    finally
    {
      // Close/Clear TestLog for the unique log ID
      if (uniqueLogId != null)
      {
        TestLog.done(uniqueLogId);
        TestLog.removeLogEntries(uniqueLogId, Thread.currentThread().getId());
      }

    }

  }

  
  private String getCaseId(String className, String scenarioNumber, String currentTestRunid) throws Exception
  {
 
   if(currentTestRunid==null) return null;  
	  
   String str = TestParameterConstants.TESTRAIL_GET_TESTCASES_BY_TESTRUN_ID + currentTestRunid; 	  
   JSONArray jSONArray = (JSONArray) TestRailConnector.getAPIClient().sendGet(str);
   
   for(int i=0; i<jSONArray.size(); i++)
   {
    
    String currentClassName = 	 (String)((JSONObject)jSONArray.get(i)).get(TestParameterConstants.TESTRAIL_TESTCASE_TEST_CLASSPATH_FIELD); 
    if(currentClassName.compareTo(className)==0)
    {	
     String currentScenarioNumber = (String)((JSONObject)jSONArray.get(i)).get(TestParameterConstants.TESTRAIL_TESTCASE_TEST_SCENARIO_FIELD).toString(); 
     if(currentScenarioNumber.compareTo(scenarioNumber)==0) return  ((JSONObject)jSONArray.get(i)).get(TestParameterConstants.TESTRAIL_TESTCASE_TEST_CASE_ID_FIELD).toString(); 
   	 //System.out.println(((JSONObject)jSONArray.get(i)).get("case_id"));
    }  
   }
   return "-1";
  }
  
  /**
   * Method to centralize processing of a result
   * 
   * @param result
   * @param tr
   * @throws Exception
   */
  private void onTestResult(TestResultStatusEnum resultStatus, ITestResult tr)
      throws Exception
  {
    this.trConnect = TestRailConnector.getConnector();

    if (TestParameterConstants.TESTRESULTS_REPORTING_MODE
        .equalsIgnoreCase(TestParameterConstants.TESTRESULTS_REPORTING_MODE_LISTENER))
    {

      // Build test result object from testNG test result
      TestResult testResult = TestResultCapture.createTestResult(tr.getMethod());

      // For failed after configuration
      if (testResult.getTmsTestId() == null && testResult.getTmsTestRunId() == null)
      {
        XmlTest xmlTest = tr.getTestContext().getCurrentXmlTest();
        testResult.setTmsTestRunId(xmlTest.getParameter(TestParameterConstants.TEST_RUN_ID));
        testResult.setTmsTestId(xmlTest.getParameter(TestParameterConstants.TEST_ID));
      }

      //added to ignore for passed tests
      XmlTest xmlTest = tr.getTestContext().getCurrentXmlTest();
      String str = xmlTest.getParameter(TestParameterConstants.IGNORE_LOG_FOR_PASSED_TESTS);
      boolean state = Boolean.parseBoolean(str);

      // remove logs only if "passed" and ignorePassedTest parameter = true
      if(resultStatus.getStatus().compareTo("Passed")==0)
      {	  
       testResult.setIgnoreLogForPassedTestsState(state);
      }
      
      ///////////////////////////////////////////////////////////////////////////////////
      //
      // if we have only TestRunId :-) Look for info by class name and scenario
      //
      //////////////////////////////////////////////////////////////////////////////////
      if (testResult.getTmsTestId() == null ) //&& testResult.getTmsTestRunId() == null)
      {
       String scenario = new String();	  
   	   Object[] obj = tr.getParameters();
       if(obj.length!=0)
       {
   	    Object objEntry = obj[0];
        Map<String, String> map = (Map<String, String>) objEntry;
        scenario = map.get("scenario");
       }
       else scenario = "1";
       String caseId = getCaseId(tr.getInstanceName(),scenario, testResult.getTmsTestRunId());
       testResult.setTmsTestId(caseId);
      } 
      
      ///////////////////////////////////////////////////////////////////////////////////
      //
      // if everything failed :-) Look for info by class name and scenario
      //
      //////////////////////////////////////////////////////////////////////////////////
      
      //if (testResult.getTmsTestRunId() == null) return;  //do nothing, TestRail is not gonna be updated
            
      TestScenarioResult tir = TestResultCapture.createScenarioResult(tr, resultStatus);
      
      // added to ignore log info for passed tests
      if(testResult.getIgnoreLogForPassedTestsState()) tir.setLogEntries(null);

      ArrayList<TestScenarioResult> scenarioResults = new ArrayList<TestScenarioResult>();
      scenarioResults.add(tir);

      testResult.setScenarioResults(scenarioResults);
      TestResult testResultModified = TestRunUtils.modifyTestResultForReporting(testResult);

      // Should be only one record as only one scenarioResult is built above.
      // For loop is just to be consistent
      for (TestScenarioResult scenarioResult : testResultModified.getScenarioResults())
      {
        // FTP screenshots of all tests
        // File transfer only required if the reporting succeed
        if (!tir.getStatus().equalsIgnoreCase(TestResultStatusEnum.PASSED.getStatus()))
        {
          ftpScreenshots(scenarioResult.getScreenshots());
        }

        // report test result
        this.trConnect.registerResponse(testResultModified, scenarioResult);
      }
    }
  }

  private void ftpScreenshots(ArrayList<Screenshot> ssToTransfer)
  {

    if (ssToTransfer == null)
      return;

    // Second, Now as we collected all image files into allScreenShots, let's
    // ftp all files
    
    //ScreenshotSFtpClient ftpclient = new ScreenshotSFtpClient();
    ScreenshotFtpClient ftpclient = new ScreenshotFtpClient();

    try
    {
      ftpclient.connect();

      for (Screenshot screenshot : ssToTransfer)
      {
        File imgFileToFtp = screenshot.getFile();
        ftpclient.sendScreenshotFile(imgFileToFtp);
      }
    }
    catch (Exception e)
    {
      // Swallow the exception.
      // Take any actions for reporting error in the log etc. but no need to
      // throw the exception
      // *********************************************
      // TODO: send email to report the failure
      // *********************************************
      e.printStackTrace();
      System.out.println("Error occured while screenshot file transfer to server. "
          + e.getMessage());
    }
    finally
    {
      // disconnect the server
      ftpclient.disconnect();
    }
  }

}

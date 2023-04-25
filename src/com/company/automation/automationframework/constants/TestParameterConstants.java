package com.company.automation.automationframework.constants;

import org.openqa.selenium.Platform;


/**
 * This class contains constants used in Test Suite XML file 
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class TestParameterConstants
{

 public static String TESTRAIL_URL 								                      = "https://companyName.testrail.com/";
 public static String TESTRAIL_LOGIN 							                     = "companyTestRaillogin";
 public static String TESTRAIL_PASSWORD 						                   = "companyTestRailPassword";
 public static String TESTRAIL_GET_TESTCASE_COMMAND 			          = "get_case/";
 public static String TESTRAIL_ADDTESTCASE_RESULT_COMMAND 		     = "add_result_for_case/";
 public static String TESTRAIL_ADDTESTCASE_RESULT_NORUN_COMMAND	 = "add_result/";
 public static String TESTRAIL_GET_TESTCASES_BY_TESTRUN_ID 		    = "get_tests/";
// public static String TESTRAIL_TESTCASE_TEST_CLASSPATH_FIELD	    =  "custom_custom_testclasspath";
 public static String TESTRAIL_TESTCASE_TEST_CLASSPATH_FIELD     =  "custom_testclass";
 public static String TESTRAIL_TESTCASE_TEST_SCENARIO_FIELD		    =  "custom_scenario";
 public static String TESTRAIL_TESTCASE_TEST_CASE_ID_FIELD		     =  "case_id";
 //public static String TESTRAIL_TESTCASE_TEST_CASE_ID_FIELD	=  "id";
 public static String TESTRAIL_TESTCASE_TEST_STATUS_ID_FIELD	    =  "status_id";
 public static String TESTRAIL_TESTCASE_TEST_COMMENT_FIELD		     =  "comment";
 //public static String TESTRAIL_CURRENT_TEST_RUN	=  "11111";

 
 
 public static String TESTRAIL_PASS_CODE 		  ="1";// 	1 = pass
 public static String TESTRAIL_BLOCKED_CODE 	="2";// 	2 = blocked 
 public static String TESTRAIL_UNTESTED_CODE	="3";// 	3 = untested 
 public static String TESTRAIL_RETEST_CODE 		="4";// 	4 = retest
 public static String TESTRAIL_FAIL_CODE 		  ="5";// 	5 = failed	
	
  public static final String BROWSER                     = "browser";
  public static final String HEADER                      = "header";
  public static final String CLOSE_BROWSER               = "close";
  public static final String ORG_CODE                    = "orgCode";
  public static final String USERNAME                    = "userName";
  public static final String PASSWORD                    = "passWord";
  public static final String SERVER                      = "server";
  public static final String TRANSPORT                   = "transport";
  public static final String STEP_DELAY                  = "stepDelay";
  public static final String VERBOSE                     = "verbose";
  public static final String BROWSER_VERSION             = "version";
  public static final String PLATFORM                    = "platform";
  public static final String REMOTE                      = "remote";
  public static final String SERVER_URL                  = "serverUrl";
  public static final String AUTOMATION_PROPERTIES       = "automationProperties";
  public static final String IGNORE_LOG_FOR_PASSED_TESTS = "ignorePassed";
  

  /** Browser would default to 'chrome' if no browser is specified **/
  public static final String DEFAULT_BROWSER = "chrome";

  /** Populate with the desired browser version **/
  public static final String DEFAULT_BROWSER_VERSION = "";

  /** Remote testing defaults to false  **/
  public static final boolean DEFAULT_REMOTE_EXECUTION_SETTING = false;
  
  /** Platform defaults to 'any' if none is specified  **/
  public static final String DEFAULT_BROWSER_PLATFORM = Platform.ANY.toString(); 
  
  /**
   * Used for providing data file for test being run
   */
  public static final String CSV_FILE_PARAMETER = "csvFile";

  /**
   * Used for generating baseline output file
   */
  public static final String CSV_BASELINE_OUTPUT_FILE_PARAMETER = "csvBaselineOutputFile";

  /**
   * Used by Listener to report back the test results
   */
  public static final String TEST_RUN_ID = "testRunId";

  /**
   * Used by Listener to report back the test results
   */
  public static final String TEST_ID = "testId";

  /**
   * Used for filtering data from CSV
   */
  public static final String DATA_GROUP_PARAMETER = "dataGroup";

  /**
   * Used for marking under construction test scenario
   */
  public static final String UNDER_CONSTRUCTION = "Under-Construction";

  /**
   * Used for filtering data row from CSV
   */
  public static final String SCENARIO_PARAMETER = "scenario";

  /**
   * Listener mode is used when reporting is done for each test execution.
   * This mode is very useful as the TestRail is getting the test results as the tests
   * are being run. So it is as instant as possible and also less load on the system.
   */
  public static final String TESTRESULTS_REPORTING_MODE_LISTENER = "listener";

  /**
   * Reporter mode is used when reporting is done at the end after all tests are completed.
   * This mode is ideal to use when there us a need to report results at the end, or 
   * system requires some cleanup after tests are completed.
   */
  public static final String TESTRESULTS_REPORTING_MODE_REPORTER = "reporter";

  /**
   * Use this constant to decide if the test result reporting is done instantly or at the end...
   */
  public static final String TESTRESULTS_REPORTING_MODE = TESTRESULTS_REPORTING_MODE_LISTENER;

  public static final String TESTNG_DEFAULT_REPORTER = "defaultReporter";

  public static final String SEPARATOR_COMMA = ",";

  /**
   * CSV data file contains the data for child objects with suffix as "_1", "_2" etc. For now
   * only 5 child objects are allowed. This method can handle bigger size however passing
   * more child object data gets difficult because of nature of csv file.
   *  
   */
  public final static int CSV_MAX_CHILD_OBJECT_LIMIT = 5;
  
   /**
   * Config file for init tests
   */
  public final static String INIT_CONFIG = "init-config.xml";

 
}

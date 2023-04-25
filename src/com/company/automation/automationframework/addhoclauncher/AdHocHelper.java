package com.company.automation.automationframework.addhoclauncher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.testng.TestNG;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.automation.automationframework.constants.TestParameterConstants;
import com.company.automation.automationframework.enums.BrowserEnum;
import com.company.automation.automationframework.enums.TestResultStatusEnum;
import com.company.automation.automationframework.exceptions.QualityException;
import com.company.automation.automationframework.models.TRTestCaseRun;
import com.company.automation.automationframework.profile.AutomationProperties;
import com.company.automation.automationframework.testrail.TestRailConnector;
import com.company.automation.automationframework.testrail.TestResult;
import com.company.automation.automationframework.testrail.TestRunUtils;
import com.company.automation.automationframework.testrail.TestScenarioResult;
import com.company.automation.automationframework.utils.StringUtils;

/**
 * Helper class for Add hock launch actions
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class AdHocHelper
{

 public static final int     NUMBER_OF_EXPECTED_PARAMETERS       = 4;
 public static final String  TEST_CLASS_MATCH_PATTERN            = "^com\\.company\\.automation\\.testautomation\\.tests.+";
 public static final String  TESTRAIL_LISTENER_LOCATION          = "com.company.automation.automationframework.listener.TestRailListener";
 public static final String  EXECUTE_ALL_SCENARIOUS              = "ALL";
 public static final String  DEBUG_CLASS_NAME                    = "ProductClassTester.xml";
 public static final String  REGULAR_EXPRESSION_FOR_NUMBERS      = "[0-9]+"; 
 
 private Set<String>         orgCodeSet                    = null;
 private Set<String>         serverSet                     = null;
 private Set<String>         browserSet                    = null;
 private boolean             debug                         = false;

 private Map<String, String> testnames                     = new HashMap<String, String>(); // hold all test names and its test id

 public void validateCorrectUsage(String msg, String[] args) throws QualityException
 {
  if ((args.length != 1) && (args.length != 4))
  {
   System.out.println("Usage: ");
   System.out.println("java" + msg + " <com.package.TestClass> <scenario #> <test #> ");
   System.out.println("java" + msg + "<com.package.TestClass> <scenario #> <test #> <browser>");
   System.out.println("java" + msg + " <testrun #>");

   throw new QualityException("Wrong usage!");
  }
 }

 public void validatePackageClassAndBrowser(String packageClass, String browser) throws QualityException
 {
  if (!validateClassName(packageClass))
  {
   throw new QualityException("First argument needs to be a package+class: " + packageClass);
  }
  if (!validateBrowser(browser))
  {
   throw new QualityException("Forth argument needs to be a browser: " + browser);
  }
 }

 /**
  * validate args and set class variables for TestId case
  * 
  * @param args
  * @throws Exception
  */
 public void validateArgumentsWithTestId(String[] args) throws Exception
 {
  validateCorrectUsage("AdHocClassTesterByTestId.class", args);

  if (args.length == NUMBER_OF_EXPECTED_PARAMETERS)
  {
   validatePackageClassAndBrowser(args[0], args[3]);

   if (!validateScenarioIdWithProvidedTestId(args[1]))
   {
    throw new QualityException("Second argument needs to be a numeric scenario: " + args[1]);
   }
   if (!validateTestId(args[2]))
   {
    throw new QualityException("Third argument needs to be a numeric test id: " + args[2]);
   }

  }

 }

 /**
  * validate args and set class variables RunId case
  * 
  * @param args
  * @throws Exception
  */
 public void validateArgumentsWithRunId(String[] args) throws Exception
 {
  validateCorrectUsage("AdHocClassTesterByRunId.class", args);

  if (args.length == NUMBER_OF_EXPECTED_PARAMETERS)
  {
   validatePackageClassAndBrowser(args[0], args[3]);
   if (!validateScenarioIdWithProvidedRunId(args[1]))
   {
    throw new QualityException("Second argument needs to be a numeric scenario or \"ALL\": " + args[1]);
   }
   if (!validateRunId(args[2]))
   {
    throw new QualityException("Third argument needs to be a numeric run id: " + args[2]);
   }
  }

 }

 /**
  * Make sure the test class/scenario exist. If not, register this test to
  * "Blocked" in TestRail
  * 
  * @param testClass
  * @param scenarioId
  * @param testId
  */
 private boolean registerUntestableTestCase(String testClass, String scenarioId, String testId)
 {
  boolean untestable = false;
  try
  {
   TestRailTestCaseChecker.isTestable(testClass, scenarioId);
  }
  catch (Exception e)
  {
   try
   {
    TRTestCaseRun testrun = new TRTestCaseRun();
    testrun.setClazz(testClass);
    testrun.setScenario(scenarioId);
    testrun.setTestid(testId);

    registerTRResult(testrun, e);
    untestable = true;
   }
   catch (Exception ex)
   {
    // TODO
   }
  }

  return untestable;

 }

 /**
  * start TestNg to run a given test case
  * 
  * @throws Exception
  */
 public void execute(String testClass, String scenarioId, String testId, String browser, String runId) throws Exception
 {
  // Make sure the test class/scenario exist. If not, register
  // this test to "Blocked" in TestRail
  if (registerUntestableTestCase(testClass, scenarioId, testId)) return;

  // Create suite name based on test class and scenario
  String suiteName = scenarioId == null ? "AdHocTest[" + testClass + "]" : "AdHocTest[" + testClass + "(Scenario_" + scenarioId + ")]";

  // Create a new TestNG test suite using the suite name returned by previous
  // step
  // And add TestNG Listener to the test suite
  XmlSuite suite = createXmlSuiteWithListeners(suiteName, null, true);

  // Create TestNG test for the given test class
  XmlTest test = createXmlTest(suite, testClass);

  // Set TestNG parameter, especially browser
  if (scenarioId.compareTo(AdHocHelper.EXECUTE_ALL_SCENARIOUS) == 0 && runId != null) scenarioId = null;
  setTestParameters(test, scenarioId, testId, runId, "false", browser);

  // Add the newly create TestNG test to the test suite
  List<XmlSuite> suites = new ArrayList<XmlSuite>();
  suites.add(suite);

  // Create a new instance of TestNG and run the test suite
  TestNG tng = new TestNG(true);
  tng.setXmlSuites(suites);
  tng.run();
 }

 /**
  * run all test cases which fulfill the testFilter conditions
  * 
  * @param runid
  * @throws Exception
  */
 public void execute(TRTestCaseRun testFilter) throws Exception
 {
  // Create a new test suite using test run id as suite name
  // And add TestNG Listener to the test suite
  XmlSuite suite = createXmlSuiteWithListeners(testFilter.getRunid(), "tests", false);

  // Retrieve all test cases from TestRail based on selection criteria
  List<TRTestCaseRun> testruns = TestRailConnector.getConnector().retrieveTestCaseRuns(testFilter);

  // Throw exception if fail to retrieve test cases from TestRail
  if (testruns == null) throw new Exception("Cannot retrieve test cases from TestRail!");

  // Shuffle the execution order
  // ListUtils.shuffle(testruns);
  long seed = System.nanoTime();
  Collections.shuffle(testruns, new Random(seed));
  // System.out.println("Total Test Rail: " + testruns.size());

  // Iterate each test case returned from TestRail
  // Check if the test case is testable,
  // if not, register the test case to blocked in TestRail
  // if testable, then create a TestNG test for the test case and add it to the
  // test suite
  for (TRTestCaseRun testrun : testruns)
  {
   String clazz = testrun.getClazz();
   String scenario = testrun.getScenario() == null ? "" : testrun.getScenario();
   String browser = getBrowser(testrun.getConfig());
   String testId = testrun.getTestid();
   String testRunId = testrun.getRunid();
   String orgCode = getOrgCode(testrun.getConfig());
   String appServer = getAppServer(testrun.getConfig());

   // Ignore test where clazz or scenario is empty
   if (clazz == null) continue;

   // Make sure the test class/scenario exist. If not, register
   // this test to "Blocked" in TestRail
   if (registerUntestableTestCase(clazz, scenario, testId)) continue;

   // Ignore duplicated test case (same combination of test class and scenario)
   // in the same test run
   String testname = testRunId + "-" + clazz.substring(clazz.lastIndexOf(".") + 1) + "-" + scenario;
   if (testnames.containsKey(testname))
   {
    registerTRResult(testrun, new QualityException("Duplicated test case to test [" + testnames.get(testname)
      + "]. Please make sure no duplicated combination of Automation Test Class and Scenario Id in one single test run!"));
    continue;
   }

   // Create a TestNG test for the testable test case
   XmlTest test = createXmlTest(suite, clazz, scenario, testname);

   // Set TestNG parameters, passing in overridden browser, orgcode and
   // appserver setting
   setTestParameters(test, scenario, testId, testRunId, "true", browser, orgCode, appServer);

   // Added the test case into the global hash map for avoiding duplicated test
   // case
   testnames.put(testname, testId);
  }

  // If debug is set, then print the whole test suite to a XML file
  if (isDebug())
  {
   BufferedWriter out = new BufferedWriter(new FileWriter(new File(AdHocHelper.DEBUG_CLASS_NAME)));
   out.write(suite.toXml());
   out.close();
  }

  // Add the test suite to TestNG suites List
  List<XmlSuite> suites = new ArrayList<XmlSuite>();
  suites.add(suite);

  // Create a new TestNG instance
  // Turn on the default reporter based on system parameter
  TestNG tng = null;
  if (System.getProperty(TestParameterConstants.TESTNG_DEFAULT_REPORTER) != null && System.getProperty(TestParameterConstants.TESTNG_DEFAULT_REPORTER).equalsIgnoreCase("true")) tng = new TestNG();
  else tng = new TestNG(false); // disable default listeners

  // Add the test suites to the TestNG instance, and run the instance
  tng.setXmlSuites(suites);
  tng.run();

  // System.out.println("Total test: " + suite.getTests().size());

 }

 /**
  * @return the debug
  */
 public boolean isDebug()
 {
  return debug;
 }

 /**
  * @param debug
  *         the debug to set
  */
 public void setDebug(boolean debug)
 {
  this.debug = debug;
 }

 /**
  * Return browser setting from TestRail test run configuration string
  * 
  * @param config
  * @return
  * */
 private String getBrowser(String config)
 {
  String browser = "chrome";

  if (!StringUtils.isEmpty(config))
  {

   String[] configAry = config.split(",");
   for (int i = 0; i < configAry.length; i++)
   {
    if (browserSet.contains(configAry[i].trim()))
    {
     return BrowserEnum.getSeleniumGridValue(configAry[i].trim());
    }
   }
  }
  return browser;
 }

 /**
  * To get orgcode from TestRail test run configuration string
  * 
  * @param config
  * @return
  * @throws Exception
  */
 private String getOrgCode(String config) throws Exception
 {

  if (config == null || config.isEmpty()) return config;

  String[] configAry = config.split(",");
  for (int i = 0; i < configAry.length; i++)
  {
   if (orgCodeSet.contains(configAry[i].trim()))
   {
    return configAry[i].trim();
   }
  }
  return null;
 }

 /**
  * To get application server from TestRail test run configuration string
  * 
  * @param config
  * @return
  */
 private String getAppServer(String config)
 {
  if (config == null || config.isEmpty()) return config;

  String[] configAry = config.split(",");

  for (int i = 0; i < configAry.length; i++)
  {
   if (serverSet.contains(configAry[i].trim()))
   {
    return configAry[i].trim();
   }
  }
  return null;
 }

 /**
  * Construct application version based on the given org code
  * 
  * @param orgCode
  * @return
  */
 private String getAppVersionFromOrgCode(String orgCode)
 {
  StringBuilder builder = new StringBuilder();
  char[] chars = orgCode.toCharArray();

  builder.append(chars[0]);
  for (int i = 1; i < chars.length; i++)
  {
   builder.append('.');
   builder.append(chars[i]);
  }

  return builder.toString();

 }

 /**
  * Register a test result in TestRail for given test case
  * If exception is null, then mark the test case "PASSED"
  * otherwise, then mark the test case "BLOCKED"
  * 
  * @param testrun
  * @param e
  * @throws Exception
  */
 private void registerTRResult(TRTestCaseRun testrun, Exception e) throws Exception
 {
  TestResult tr = new TestResult();
  TestScenarioResult tsr = new TestScenarioResult();

  tr.setTestClassName(testrun.getClazz());
  tr.setTmsTestId(testrun.getTestid());
  tr.setTmsTestRunId(testrun.getRunid());
  tr.setTmsTestCaseId(testrun.getCaseid());

  tsr.setScenarioId(testrun.getScenario());
  if (e != null)
  {
   tsr.setStatusId(TestResultStatusEnum.BLOCKED.getStatusId());
   tsr.setError(e);
  }
  else
  {
   tsr.setStatusId(TestResultStatusEnum.PASSED.getStatusId());
  }

  // Set orgCode
  String orgCode = System.getProperty("orgCode");
  if (orgCode == null || orgCode.equals("${orgCode}")) orgCode = AutomationProperties.getProperty("app.orgcode");
  tsr.setOrgCode(orgCode);

  // Set version
  tsr.setAppVersion(getAppVersionFromOrgCode(orgCode));

  // Set server
  String server = System.getProperty("server");
  if (server != null && !server.equals("${server}")) tsr.setServer(server);
  else tsr.setServer(AutomationProperties.getProperty("app.server"));

  // Set user
  String user = System.getProperty(TestParameterConstants.USERNAME);
  if (user != null && !user.equals("${userName}")) tsr.setUserName(user);
  else tsr.setUserName(AutomationProperties.getProperty("app.user"));

  TestRailConnector.getConnector().registerResponse(tr, tsr);

 }

 /**
  * Set parameters for a given testng XML Test
  * 
  * @param test
  * @param scenario
  * @param testId
  * @param testRunId
  * @param remote
  * @return
  */
 private XmlTest setTestParameters(XmlTest test, String scenario, String testId, String testRunId, String remote)
 {

  Map<String, String> parameters = new HashMap<String, String>();

  // If no scenario is chosen, run the whole class
  // If scenario is a method, add it to the included method list
  if (scenario != null && !scenario.isEmpty())
  {
   if (!StringUtils.isInteger(scenario))
   {
    test.getClasses().get(0).setIncludedMethods(createXmlInclude(scenario));
   }
   else parameters.put("scenario", scenario);
  }

  parameters.put("remote", "false");
  if (testId != null) parameters.put("testId", testId);
  if (testRunId != null) parameters.put("testRunId", testRunId);
  test.setParameters(parameters);

  return test;
 }

 /**
  * Set parameters for a given testng XML Test
  * 
  * @param test
  * @param scenario
  * @param testId
  * @param testRunId
  * @param remote
  * @param browser
  * @return
  */
 private XmlTest setTestParameters(XmlTest test, String scenario, String testId, String testRunId, String remote, String browser)
 {

  test = setTestParameters(test, scenario, testId, testRunId, remote);
  Map<String, String> parameters = test.getAllParameters();
  parameters.put("browser", browser);
  test.setParameters(parameters);

  return test;
 }

 /**
  * Set parameters for a given testng XML Test
  * 
  * @param test
  * @param scenario
  * @param testId
  * @param testRunId
  * @param remote
  * @param browser
  * @param orgCode
  * @param appServer
  * @return
  */
 private XmlTest setTestParameters(XmlTest test, String scenario, String testId, String testRunId, String remote, String browser, String orgCode, String appServer)
 {
  test = setTestParameters(test, scenario, testId, testRunId, remote, browser);
  Map<String, String> parameters = test.getAllParameters();
  if (StringUtils.isNotEmpty(orgCode)) parameters.put(TestParameterConstants.ORG_CODE, orgCode);
  if (StringUtils.isNotEmpty(appServer)) parameters.put(TestParameterConstants.SERVER, appServer);
  test.setParameters(parameters);

  return test;
 }

 /**
  * return a new XmlTest for a given class
  * 
  * @param suite
  * @param clazz
  * @return
  */
 private XmlTest createXmlTest(XmlSuite suite, String clazz)
 {
  return this.createXmlTest(suite, clazz, null, "Adhoc Test: " + clazz.substring(clazz.lastIndexOf(".") + 1));
 }

 /**
  * return a new XmlTest for a given class and scenario
  * 
  * @param suite
  * @param clazz
  * @param prefix
  * @return
  */
 private XmlTest createXmlTest(XmlSuite suite, String clazz, String scenario, String testname)
 {

  XmlTest test = new XmlTest(suite);
  test.setName(testname);
  List<XmlClass> classes = new ArrayList<XmlClass>();
  XmlClass xmlClass = new XmlClass(clazz);

  classes.add(xmlClass);
  test.setXmlClasses(classes);

  // //The maximum number of milliseconds this test should take. 35 mins for
  // each test
  // test.setTimeOut(35*60*1000);

  return test;
 }

 /**
  * Include a method in XML test
  * 
  * @param method
  * @return
  */
 private List<XmlInclude> createXmlInclude(String method)
 {
  List<XmlInclude> list = new ArrayList<XmlInclude>();
  list.add(new XmlInclude(method));
  return list;
 }

 /**
  * create a new XmlSuite with default listeners
  * 
  * @param suiteName
  * @param parallel
  * @return
  * @throws Exception
  * @throws NumberFormatException
  */
 private XmlSuite createXmlSuiteWithListeners(String suiteName, String parallel, boolean light) throws NumberFormatException, Exception
 {
  XmlSuite suite = new XmlSuite();

  suite.setName(suiteName);

  if (parallel != null && !parallel.isEmpty())
  {
   suite.setParallel(parallel);

   suite.setThreadCount(Integer.valueOf(AutomationProperties.getProperty("testng.defaultThreadCount").trim()));
  }

  suite.setConfigFailurePolicy("continue");

  suite.addListener(AdHocHelper.TESTRAIL_LISTENER_LOCATION);

  // suite.addListener("automation.lib.listener.PerformanceListener");

  if (!light)
  {
   // TODO
   // suite.addListener("com.pointclickcare.automation.lib.listener.ReproductionStepListener");
   // suite.addListener("org.uncommons.reportng.HTMLReporter");
   // suite.addListener("org.uncommons.reportng.JUnitXMLReporter");
  }

  // Add JIRAListener if the system parameter jiraListener is set to true or not
  // set
  if (isJIRAListenerOn())
  { // TODO
    // suite.addListener("automation.lib.listener.JIRAListener");
  }
  return suite;

 }

 /**
  * Return if JIRAListener should be added on.
  * 
  * If system parameter jiraListener is not set, then we will add JIRAListener
  * If system parameter jiraListener is set and value is "true"/"TRUE", then we
  * will add JIRAListener
  * otherwise, not add JIRAListener
  * 
  * @return
  */
 private boolean isJIRAListenerOn()
 {
  boolean isOn = false;
  String jiraSetting = System.getProperty("jiraListener");

  if (StringUtils.isEmpty(jiraSetting)) isOn = true;
  else if (jiraSetting.equalsIgnoreCase("${jiraListener}") || jiraSetting.equalsIgnoreCase("true")) isOn = true;

  return isOn;
 }

 /**
  * Validate test class. a test case must follow a pattern of
  * ^com\\.company\\.automation\\.testautomation\\.tests.+
  * 
  * @param clazz
  * @return
  */
 private boolean validateClassName(String clazz)
 {
  if (!clazz.matches(AdHocHelper.TEST_CLASS_MATCH_PATTERN))
  {
   return false;
  }
  return true;
 }

 /**
  * Validate scenario id, a scenario id must be an integer
  * 
  * @param scenario
  * @return
  */
 private boolean validateScenarioIdWithProvidedTestId(String scenario)
 {
  // In situation where no scenario id or an id
  // return (StringUtils.isInteger(scenario) || scenario.trim()
  // .equalsIgnoreCase("$scenarioId"));

  if (!scenario.matches(AdHocHelper.REGULAR_EXPRESSION_FOR_NUMBERS))
  {
   return false;
  }

  return true;
 }

 /**
  * Validate scenario id, a scenario id must be an integer or "ALL"
  * 
  * @param scenario
  * @return
  */
 private boolean validateScenarioIdWithProvidedRunId(String scenario)
 {
  // In situation where no scenario id or an id
  // return (StringUtils.isInteger(scenario) || scenario.trim()
  // .equalsIgnoreCase("$scenarioId"));
  if (scenario.compareTo(AdHocHelper.EXECUTE_ALL_SCENARIOUS) == 0) return true;

  if (!scenario.matches(AdHocHelper.REGULAR_EXPRESSION_FOR_NUMBERS))
  {
   return false;
  }

  return true;
 }

 /**
  * validate test case test id. A test id is must follow a pattern of ^T[0-9]+
  * 
  * @param testId
  * @return
  */
 private boolean validateTestId(String testId)
 {
  if (!testId.matches(AdHocHelper.REGULAR_EXPRESSION_FOR_NUMBERS))
  {
   return false;
  }
  return true;
 }

 /**
  * validate test case run id. A test id is must follow a pattern of ^T[0-9]+
  * 
  * @param testRunId
  * @return
  */
 private boolean validateRunId(String testRunId)
 {
  if (!testRunId.matches(AdHocHelper.REGULAR_EXPRESSION_FOR_NUMBERS))
  {
   return false;
  }
  return true;
 }

 /**
  * validate browser. It should be a string
  * 
  * @param testcaserun
  * @return
  */
 private boolean validateBrowser(String browser)
 {
  if (browser.equalsIgnoreCase("chrome") || browser.equalsIgnoreCase("firefox") || browser.equalsIgnoreCase("Internet explorer") || browser.equalsIgnoreCase("none"))
  {
   return true;
  }
  return false;
 }

 public void runTestPlanTest(String param) throws Exception
 {
  TRTestCaseRun filter = null;
  // debug
  System.out.println(param);

  if (param.startsWith("{"))
  {
   // {project:null,testids:[1,3,5],testid:null,status_id:\"2\",runid:\"144\",caseid:null,title:null,clazz:null,scenario:null,automated:null,type_id:null,priority_id:\"4\",estimate:null,user:null,section:null,suite:null,milestone:null,config:\"Broken\"}
   ObjectMapper mapper = new ObjectMapper();
   mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

   filter = mapper.readValue(param, TRTestCaseRun.class);
  }
  else
  {
   // First arg is just the run to execute
   filter = new TRTestCaseRun();
   filter.setRunid(param);
  }

  execute(filter);
 }

}

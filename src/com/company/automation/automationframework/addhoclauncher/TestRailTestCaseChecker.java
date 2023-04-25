/**
 * 
 */
package com.company.automation.automationframework.addhoclauncher;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.testng.annotations.Test;

import com.company.automation.automationframework.annotations.DataSource;
import com.company.automation.automationframework.addhoclauncher.UnderConstruction;
import com.company.automation.automationframework.constants.TestParameterConstants;
import com.company.automation.automationframework.exceptions.QualityException;
//import com.company.automation.automationframework.addhoclauncher.TestCaseType;
import com.company.automation.automationframework.models.TRTestCase;
import com.company.automation.automationframework.dataprovider.CsvDataProvider;
//import com.company.automation.automationframework.addhoclauncher.XMLParser;
import com.company.automation.automationframework.utils.StringUtils;
import com.company.automation.automationframework.profile.AutomationProperties;
//import com.company.automation.automationframework.addhoclauncher.EmailUtils;
import com.company.automation.automationframework.testrail.TestRailConnector;
import com.company.automation.automationframework.testrail.TestRunUtils;

/**
 * This class provides functions to query TestRail for information on
 * scenarios/test cases
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class TestRailTestCaseChecker
{
 //private static XMLParser                parser            = null;
 private static List<String>             underConstruction = new ArrayList<String>();
 private static Map<String, Set<String>> scenariosMap      = new HashMap<String, Set<String>>();

 /**
  * Check if a test case (test class + scenario) is testable.
  * Throws exception if the test case is under construction or not defined
  * 
  * @param clazz
  * @param scenario
  * @throws Exception
  */
 public static void isTestable(String clazz, String scenario) throws Exception
 {
  // Check if the test class is under construction first
  if (underConstruction.contains(clazz.trim()))
  {
   throw new QualityException(clazz + " is under construction!");
  }
  else
  {
   UnderConstruction isUnderConstruction = TestRunUtils.getAnnotation(UnderConstruction.class, clazz.trim());
   if (isUnderConstruction != null)
   {
    underConstruction.add(clazz.trim());
    throw new QualityException(clazz + " is under construction!");
   }
  }

  // Retrieve all scenarios for the test class
  Set<String> scenarios = retrieveAllScenariosForClass(clazz.trim());

  if (StringUtils.isNotEmpty(scenario) && !scenarios.contains(scenario))
  {
   if (underConstruction.contains(clazz + "[" + scenario + "]")) throw new QualityException(clazz + "[" + scenario + "] is either under construction or disabled");

   throw new QualityException("No scenario enabled! Please check csv/xml file, or test method name . [" + scenario + "]");
  }

 }

 /**
  * Return all scenarios defined in test Automation for a given test class
  * 
  * First, read csv data source definition from test class, add all scenarios
  * defined in csv file to the return list
  * Second, if cannot find csv data source, then read from XML test data files,
  * add all scenarios to the return list
  * Last, if cannot find both csv data source and XML test data files, then
  * return all methods in the test class as
  * scenarios.
  * 
  * Also, will ignore all under-construction, and add to under-construction list
  * 
  * @param testClass
  * @return
  * @throws Exception
  */
 public static Set<String> retrieveAllScenariosForClass(String testClass) throws Exception
 {
  if (scenariosMap.containsKey(testClass)) return scenariosMap.get(testClass);

  Set<String> scenarios = new HashSet<String>();

  if (StringUtils.isEmpty(testClass) || !testClass.contains(".")) throw new QualityException("Wrong format of test class [" + testClass + "]");

  // 1: Read from CSV data file.
  DataSource ds = TestRunUtils.getAnnotation(DataSource.class, testClass);

  if (ds != null)
  {
   String fileName = ds.source();
   if (StringUtils.isEmpty(fileName))
   {
    throw new QualityException("Wrong CSV data source!");
   }

   HashMap<String, HashMap<String, String>> scenarioMap = CsvDataProvider.retrieveMetaData(fileName);
   // Ignore all under construction
   Iterator<String> it = scenarioMap.keySet().iterator();
   while (it.hasNext())
   {
    String scenario = it.next();
    String dataGroup = scenarioMap.get(scenario).get("dataGroup");
    if (TestParameterConstants.UNDER_CONSTRUCTION.equals(dataGroup)) underConstruction.add(testClass + "[" + scenario + "]");
    else scenarios.add(scenario);
   }
  }
  // 2: Read from XML test data file
  else
  {
   String path = testClass.substring(0, testClass.lastIndexOf('.')).replace('.', System.getProperty("file.separator").charAt(0)) + System.getProperty("file.separator") + "testdata"
     + System.getProperty("file.separator");
   String clazz = testClass.substring(testClass.lastIndexOf('.') + 1);

   URL url = Thread.currentThread().getContextClassLoader().getResource(path);
   if (url != null)
   {
    List<File> xmlFiles = com.company.automation.automationframework.utils.FileUtils.openFiles(path, clazz, ".xml");

    for (File xmlFile : xmlFiles)
    {
     // Get scenario from XML file name
     String scenario = xmlFile.getName();
     scenario = scenario.substring(0, scenario.lastIndexOf('.')).replaceFirst(clazz, "");
     // Ignore those XML test data file which not match test class
     if (!StringUtils.isEmpty(scenario) && !scenario.startsWith("_")) continue;
     // Escape '_' between test class and scenario
     if (!StringUtils.isEmpty(scenario) && scenario.startsWith("_") && scenario.lastIndexOf("_") == 0) scenario = scenario.substring(1);
     // Ignore scenarios which is under construction, and add under construction
     // to the
     // under-construction list

//*********************************************************************************     
     
//     if (parser == null) parser = new XMLParser();
     
     
//     TestCaseType testcase = parser.getObject(xmlFile, TestCaseType.class);
     
     
//     if (testcase.isUnderConstruction())
//     {
//      underConstruction.add(testClass + "[" + scenario + "]");
//      continue;
//     }

//*********************************************************************************     
     
     scenarios.add(scenario);
    }
   }
  }

  // 3: If cannot get scenario from CSV nor XML, add all enabled test methods as
  // scenarios
  if (scenarios.isEmpty())

  {
   Method[] methods = Class.forName(testClass).getDeclaredMethods();
   for (Method m : methods)
   {
    Test t = m.getAnnotation(Test.class);
    if (t != null && t.enabled()) scenarios.add(m.getName());
    else if (t != null && !t.enabled()) underConstruction.add(testClass + "[" + m.getName() + "]");
   }
  }

  scenariosMap.put(testClass, scenarios);
  return scenarios;
 }

 /**
  * Retrieve all automated test case from TestRail for given suites.
  * 
  * If suites is null, then return all automated test cases from TestRail for
  * the default suites,
  * which is configured in init.properties - "testrail.suites"
  * 
  * @param suites
  * @return Map<String, List<String>>
  *         Key - test class
  *         Value - list of scenarios
  * @throws QualityException
  */
 protected static Map<String, Map<String, String>> retrieveAllAutomatedCasesFromTestRail(String suites) throws QualityException
 {
  Map<String, Map<String, String>> automatedCases = new HashMap<String, Map<String, String>>();
  List<TRTestCase> cases;

  if (StringUtils.isEmpty(suites))
  {
   try
   {
    suites = AutomationProperties.getProperty("testrail.suites");
   }
   catch (Exception e)
   {
    // Do nothing
   }
  }
  // Get all automated test case from TestRail
  try
  {
   cases = TestRailConnector.getConnector().retrieveCases(suites);
  }
  catch (Exception e)
  {
   throw new QualityException("Cannot retrieve test cases from TestRail ! [" + e.getMessage() + "]");
  }

  // Construct map - automatedCases for each test class
  for (TRTestCase c : cases)
  {
   String testClass = c.getCustom_automation_script().trim();
   String scenario = StringUtils.isEmpty(c.getCustom_scenario_id()) ? "" : c.getCustom_scenario_id();
   String suiteAndTestId = "Test Suite: " + c.getSuite_id() + " Test Id: " + c.getId();
   if (automatedCases.containsKey(testClass))
   {
    automatedCases.get(testClass).put(scenario, suiteAndTestId);
   }
   else
   {
    Map<String, String> scenarios = new HashMap<String, String>();
    scenarios.put(scenario, suiteAndTestId);
    automatedCases.put(testClass, scenarios);
   }
  }

  return automatedCases;
 }

 /**
  * Return a list of unmatched test case which cannot find test class in
  * ProductAutomation
  * 
  * Iterate the given map which contains all automated test cases from TestRail,
  * Return an empty list if the given map is empty.
  * Otherwise, for each test class,
  * 1: check if there is a corresponding test class in ProductAutomation
  * if no corresponding test class, the add it to the return list
  * 2: check all scenarios from either CSV data source or XML test files.
  * if no corresponding scenario, then add it to the return list
  * 
  * @param automatedCases
  *         - all automated test cases from TestRail
  */
 public static List<String> findUnmatchedTestClassesForTestRailSuite(Map<String, Map<String, String>> automatedCases)
 {

  List<String> unmatchedCases = new ArrayList<String>();

  // Return an empty list if the given map is empty
  if (automatedCases == null || automatedCases.isEmpty()) return unmatchedCases;

  // Iterate automatedCases for each test class
  Set<String> testClasses = automatedCases.keySet();
  for (String testClass : testClasses)
  {
   // Check if the test class exists first
   // And Ignore UnderConstruction class
   try
   {
    UnderConstruction underConstruction = TestRunUtils.getAnnotation(UnderConstruction.class, testClass.trim());
    if (underConstruction != null) continue;
   }
   catch (ClassNotFoundException e)
   {
    unmatchedCases.add("No Test Class[" + testClass + "[" + automatedCases.get(testClass).toString() + "]] with error [ClassNotFoundException]");
    continue;
   }

   // check all scenarios from TestRail with the scenarios from the test class
   try
   {
    // retrieve all scenarios for the test class.
    Set<String> scenariosFromClass = retrieveAllScenariosForClass(testClass);
    Set<String> scenariosFromTestRail = automatedCases.get(testClass).keySet();
    for (String scenario : scenariosFromTestRail)
    {
     if (StringUtils.isEmpty(scenario)) continue;
     if (!scenariosFromClass.contains(scenario) && !underConstruction.contains(testClass + "[" + scenario + "]"))
     {
      unmatchedCases.add("No Test Class[" + testClass + "[" + scenario + "][" + automatedCases.get(testClass).get(scenario) + "]]");
     }
    }
   }
   catch (Exception e)
   {
    unmatchedCases.add("No Test Class[" + testClass + "[" + automatedCases.get(testClass).toString() + "]] with error [" + e.getMessage() + "]");
    continue;
   }

  }
  return unmatchedCases;

 }

 /**
  * Return a list of unmatched test cases which cannot find test case in
  * TestRail for the given package name.
  * 
  * If the given packageName is a dictionary, then check all sub-folder and
  * files under one by one.
  * 
  * @param packageName
  *         - the given package or file
  * @param automatedCases
  *         - all automated test cases from TestRail
  */
 public static List<String> findUnmatchedTestRailCasesForPackage(String packageName, Map<String, Map<String, String>> automatedCases)
 {
  List<String> unmatchedCases = new ArrayList<String>();

  if (StringUtils.isEmpty(packageName)) return unmatchedCases;

  String path = packageName.replace('.', System.getProperty("file.separator").charAt(0));

  URL url = Thread.currentThread().getContextClassLoader().getResource(path);

  if (url == null) return unmatchedCases;

  // System.out.println("Checking package [" + path +"]");
  File f;
  try
  {
   f = new File(url.toURI());

   // directory -- iterate all including files
   if (f.isDirectory())
   {
    File[] files = f.listFiles();
    for (File file : files)
    {
     if (file.isDirectory() && !file.getName().equals("helper") && !file.getName().equals("testdata")) unmatchedCases.addAll(findUnmatchedTestRailCasesForPackage(packageName + "." + file.getName(),
       automatedCases));
     else if (file.isFile() && file.getName().endsWith(".class")) unmatchedCases.addAll(findUnmatchedTestRailCasesForClass(packageName, file.getName(), automatedCases));
    }
   }
   else if (f.isFile() && f.getName().endsWith(".class"))
   {
    unmatchedCases.addAll(findUnmatchedTestRailCasesForClass(f.getCanonicalPath(), f.getName(), automatedCases));
   }
  }
  catch (Exception e)
  {
   // do nothing
  }

  return unmatchedCases;

 }

 /**
  * Return a list of unmatched test cases which cannot find corresponding test
  * case in TestRail for the given file.
  * 
  * If the given file is a class file,
  * 1: check if the given class contains any testNG test method,if not, return
  * an empty list
  * 2: get all scenarios from its CSV data source or XML test data files or
  * methods
  * 3: if found any scenario, then check if all scenarios are in the given map
  * which contains all automated test cases from TestRail,
  * adding all unmatched records in the return list.
  * 
  * 
  * @param path
  * @param file
  * @param automatedCases
  *         - all automated test cases from TestRail
  * 
  * @return List<String> - all unmatched records
  */
 public static List<String> findUnmatchedTestRailCasesForClass(String path, String file, Map<String, Map<String, String>> automatedCases)
 {
  List<String> unmatchedCases = new ArrayList<String>();
  String pathName = path.replace(System.getProperty("file.separator").charAt(0), '.');
  String testClass = file.substring(0, file.lastIndexOf('.'));

  try
  {
   // Ignore the class which contains no @Test
   Test test = TestRunUtils.getAnnotation(Test.class, pathName + "." + testClass);
   if (test == null) return unmatchedCases;

   // Ignore test class which is under construction
   UnderConstruction isUnderConstruction = TestRunUtils.getAnnotation(UnderConstruction.class, pathName + "." + testClass);
   if (isUnderConstruction != null)
   {
    underConstruction.add(pathName + "." + testClass);
    return unmatchedCases;
   }

   // Check if the test class created in TestRail first.
   // If not, add the test class to unmatchedCases list
   if (!automatedCases.containsKey(pathName + "." + testClass))
   {
    unmatchedCases.add("No Test Rail Case [" + pathName + "." + testClass + "]");
    return unmatchedCases;
   }

   // Get all scenarios for the test class
   Set<String> scenariosFromClass = retrieveAllScenariosForClass(pathName + "." + testClass);
   Set<String> scenariosFromTestRail = automatedCases.get(pathName + "." + testClass).keySet();

   // Check each scenario to make sure the scenario is defined in TestRail
   for (String scenario : scenariosFromClass)
   {
    // Ignore the case where scenario in TestRail is empty, and Test class has
    // more than 1 scenarios
    if (scenariosFromTestRail != null && (scenariosFromTestRail.isEmpty() || (scenariosFromTestRail.size() == 1 && scenariosFromTestRail.contains("")))) break;
    // Add the senario to unmatchedCases if cannot find match in TestTail
    if (scenariosFromTestRail == null || !scenariosFromTestRail.contains(scenario)) unmatchedCases.add("No Test Rail Case [" + pathName + "." + testClass + "[" + scenario + "]]");
   }
  }
  catch (Exception e)
  {
   // do nothing
  }

  return unmatchedCases;
 }

 /**
  * Main entry point for checking test cases/classes between TestRail and
  * ProductAutomation.
  * 
  * 1: Get all automated test cases from TestRail.
  * 2: Check if test classes under the given path have corresponding test case
  * in TestRail
  * 3: Check if the test cases from TestRail have corresponding test
  * class/scenario in ProductAutoamtion
  * 4: Send email for the report if the unmatched list is not empty
  * 
  * @throws Exception
  */
 public static void check() throws Exception
 {
  /***************************************************
   * 1. Validate arguments
   ****************************************************/
  // path
  String path = System.getProperty("path");
  if (StringUtils.isEmpty(path) || path.equals("${path}")) path = "com.company.automation.testautomation.tests";

  // suite
  String suites = System.getProperty("suites");
  if (StringUtils.isEmpty(suites) || suites.equals("${suites}")) suites = null;

  // verbose
  boolean verbose = false;
  if (!StringUtils.isEmpty(System.getProperty("verbose")) && System.getProperty("verbose").equalsIgnoreCase("true")) verbose = true;

  // emailto
  String emailto = System.getProperty("emailto");
  if (StringUtils.isEmpty(emailto) || emailto.equals("${emailto}")) emailto = null;

  /*******************************************************
   * 2. Retrieve all automated test case from TestRail
   *******************************************************/
  Map<String, Map<String, String>> automatedCases = retrieveAllAutomatedCasesFromTestRail(suites);

  /*******************************************************
   * 3. Check if each test class under the given package path
   * has a corresponding test case in TestRail.
   * 
   * If not, add to unmatchedCase list
   * *****************************************************/
  List<String> unmatchedCases = findUnmatchedTestRailCasesForPackage(path, automatedCases);
  /*******************************************************
   * 4.Check if each test case from the returned automated
   * test case from TestRail has a corresponding test class
   * in PCCQualityAutomation
   * 
   * If not, add to unmatchedCase list
   * *****************************************************/
  unmatchedCases.addAll(findUnmatchedTestClassesForTestRailSuite(automatedCases));

  /*******************************************************
   * 5. Construct email message
   * *****************************************************/
  StringBuilder msg = null;

  // Unmatched list
  if (unmatchedCases.size() > 0)
  {
   if (msg == null) msg = new StringBuilder();
   msg.append("There are total [" + unmatchedCases.size() + "] test case not matched!");
   msg.append(System.getProperty("line.separator"));
   msg.append("========================================================================");
  }

  for (String c : unmatchedCases)
  {
   msg.append(System.getProperty("line.separator"));
   msg.append(c);
  }
  // Under Construction list
  if (underConstruction.size() > 0)
  {
   if (msg == null) msg = new StringBuilder();
   msg.append(System.getProperty("line.separator"));
   msg.append(System.getProperty("line.separator"));
   msg.append("There are total [" + underConstruction.size() + "] test case under construction!");
   msg.append(System.getProperty("line.separator"));
   msg.append("========================================================================");
  }

  for (String c : underConstruction)
  {
   msg.append(System.getProperty("line.separator"));
   msg.append(c);
  }

  /*******************************************************
   * 6. Print out message on the console if verbose is set to true,
   * *****************************************************/
  if (verbose && msg != null) System.out.println(msg.toString());

  /*******************************************************
   * 7. Send a report email only if the list is not empty
   * *****************************************************/
  // TODO
  // if (msg != null)
  // EmailUtils.sendEmail(msg.toString(),
  // "[TestAutomation] Test Cases Check Email Report", emailto);

  System.exit(0);
 }

 public static void main(String[] args) throws Exception
 {
  check();
 }

}

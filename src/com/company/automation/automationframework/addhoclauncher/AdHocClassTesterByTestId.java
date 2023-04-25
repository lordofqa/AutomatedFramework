package com.company.automation.automationframework.addhoclauncher;

import com.company.automation.automationframework.testrail.TestRunUtils;

/**
 * Ad hoc single test launch by CaseId/testId from Jenkins
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class AdHocClassTesterByTestId
{

 /**
  * Constructor. Initialize orgCodeSet, serverSet and browserSet
  * 
  * @throws Exception
  */
 public AdHocClassTesterByTestId() throws Exception
 {
  /*
   * orgCodeSet = TestRailConnector.getConnector().retrieveConfigsSet(
   * TestRailConfigEnum.ORGCODE.getConfig());
   * serverSet = TestRailConnector.getConnector().retrieveConfigsSet(
   * TestRailConfigEnum.SERVER.getConfig());
   * browserSet = TestRailConnector.getConnector().retrieveConfigsSet(
   * TestRailConfigEnum.BROWSER.getConfig());
   */
 }

 /**
  * @param args
  * @throws Exception
  */
 public static void main(String[] args) throws Exception
 {

  AdHocHelper tester = new AdHocHelper();

  tester.validateArgumentsWithTestId(args);

  // For SSL certificate
  //if (System.getProperty("javax.net.ssl.trustStore") == null) System.setProperty("javax.net.ssl.trustStore", "config/security/jssecacerts");

  // tester.setDebug(true);

  // Override automation properties using VM system parametres
  TestRunUtils.overrideProperties();

  // Run Adhoc test if the given arguments are 4
  if (args.length == AdHocHelper.NUMBER_OF_EXPECTED_PARAMETERS)
  {
   // debug
   // com.company.automation.testautomation.tests.xmlLoader.TC_XmlLoader
   System.out.println("TestClass: " + args[0]);
   // 1
   System.out.println("Scenario: " + args[1]);
   // 3
   System.out.println("TestID: " + args[2]);
   // none
   System.out.println("Browser: " + args[3]);
   System.out.println("RunID: " + "NULL");

   tester.execute(args[0], args[1].trim().equals("$scenarioId") ? null : args[1], args[2], args[3], null);
  }
  // Otherwise, Run test plan test
  else
  {
   tester.runTestPlanTest(args[0]);
  }

  System.exit(0);

 }
}

package com.company.automation.automationframework.testrail;


import java.util.ArrayList;
import java.util.List;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class TestResult
{

  private String testName;
  private String testClassName;
  private String testMethodName;
  private String TmsTestCaseId;
  // private String TmsTestSuiteId;
  private String TmsTestRunId;
  private String TmsTestId;
  private List<TestScenarioResult> scenarioResults;
  
  private boolean ignoreLogForPassedTestsState = false;

  public String getTestName()
  {
    return testName;
  }

  public void setTestName(String testName)
  {
    this.testName = testName;
  }

  public String getTestClassName()
  {
    return testClassName;
  }

  public void setTestClassName(String testClassName)
  {
    this.testClassName = testClassName;
  }

  public String getTestMethodName()
  {
    return testMethodName;
  }

  public void setTestMethodName(String testMethodName)
  {
    this.testMethodName = testMethodName;
  }

  public String getTmsTestCaseId()
  {
    return TmsTestCaseId;
  }

  public void setTmsTestCaseId(String tmsTestCaseId)
  {
    TmsTestCaseId = tmsTestCaseId;
  }

  /*
   * public String getTmsTestSuiteId() {
   * return TmsTestSuiteId;
   * }
   * public void setTmsTestSuiteId(String tmsTestSuiteId) {
   * TmsTestSuiteId = tmsTestSuiteId;
   * }
   */

  public String getTmsTestRunId()
  {
    return TmsTestRunId;
  }

  public void setTmsTestRunId(String tmsTestRunId)
  {
    TmsTestRunId = tmsTestRunId;
  }

  public List<TestScenarioResult> getScenarioResults()
  {
    return scenarioResults;
  }

  public void setScenarioResults(ArrayList<TestScenarioResult> scenarioResults)
  {
    this.scenarioResults = scenarioResults;
  }

  public void addScenarioResult(TestScenarioResult scenarioResult)
  {

    if (this.scenarioResults == null)
    {
      this.scenarioResults = new ArrayList<TestScenarioResult>();
    }

    this.scenarioResults.add(scenarioResult);
  }

  public String getTmsTestId()
  {
    return TmsTestId;
  }

  public void setTmsTestId(String tmsTestId)
  {
    TmsTestId = tmsTestId;
  }

  public boolean getIgnoreLogForPassedTestsState()
  {
    return ignoreLogForPassedTestsState;
  }

  public void setIgnoreLogForPassedTestsState(boolean ignoreLogForPassedTestsState)
  {
    this.ignoreLogForPassedTestsState = ignoreLogForPassedTestsState;
  }
  
}

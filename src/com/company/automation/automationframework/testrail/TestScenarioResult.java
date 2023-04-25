package com.company.automation.automationframework.testrail;

import java.util.ArrayList;

import com.company.automation.automationframework.screenshots.Screenshot;
import com.company.automation.automationframework.testlog.TestLog.Entry;

public class TestScenarioResult
{

  private String uniqueLogId;

  private String status;
  private int statusId;
  private Throwable error;
  private ArrayList<Entry> logEntries;
  private String logString;
  private ArrayList<Screenshot> screenshots = new ArrayList<Screenshot>();
  private String server;
  private String orgCode;
  private String facility;
  private String appVersion;
  private String userName;
  private String scenarioId;
  private Object[] parameters;
  private long duration; //Test Execution duration in seconds

  public String getUniqueLogId()
  {
    return uniqueLogId;
  }

  public void setUniqueLogId(String uniqueLogId)
  {
    this.uniqueLogId = uniqueLogId;
  }

  public String getStatus()
  {
    return status;
  }

  public void setStatus(String status)
  {
    this.status = status;
  }

  public Throwable getError()
  {
    return error;
  }

  public void setError(Throwable error)
  {
    this.error = error;
  }

  public ArrayList<Entry> getLogEntries()
  {
    return logEntries;
  }

  public void setLogEntries(ArrayList<Entry> stepsLog)
  {
    this.logEntries = stepsLog;
  }

  public int getStatusId()
  {
    return statusId;
  }

  public void setStatusId(int statusId)
  {
    this.statusId = statusId;
  }

  public ArrayList<Screenshot> getScreenshots()
  {
    return screenshots;
  }

  public String getServer()
  {
    return server;
  }

  public void setServer(String server)
  {
    this.server = server;
  }

  public String getOrgCode()
  {
    return orgCode;
  }

  public void setOrgCode(String orgCode)
  {
    this.orgCode = orgCode;
  }

  public String getFacility()
  {
    return facility;
  }

  public void setFacility(String facility)
  {
    this.facility = facility;
  }

  public String getAppVersion()
  {
    return appVersion;
  }

  public void setAppVersion(String appVersion)
  {
    this.appVersion = appVersion;
  }

  public String getUserName()
  {
    return userName;
  }

  public void setUserName(String userName)
  {
    this.userName = userName;
  }

  public String getScenarioId()
  {
    return scenarioId;
  }

  public void setScenarioId(String scenarioId)
  {
    this.scenarioId = scenarioId;
  }

  public void setScreenshots(ArrayList<Screenshot> screenshots)
  {
    this.screenshots = screenshots;
  }

  public String getLogString()
  {
    return logString;
  }

  public void setLogString(String logString)
  {
    this.logString = logString;
  }

  /**
   * @return the parameters
   */
  public Object[] getParameters()
  {
    return parameters;
  }

  /**
   * @param parameters the parameters to set
   */
  public void setParameters(Object[] parameters)
  {
    this.parameters = parameters;
  }

  public long getDuration()
  {
    return duration;
  }

  public void setDuration(long duration)
  {
    this.duration = duration;
  }



}
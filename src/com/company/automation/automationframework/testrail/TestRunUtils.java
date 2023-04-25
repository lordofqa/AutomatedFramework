package com.company.automation.automationframework.testrail;

/**
 * 
 */
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.testng.ITestContext;
import org.testng.ITestResult;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.company.automation.automationframework.annotations.DataSource;
import com.company.automation.automationframework.constants.TestParameterConstants;
import com.company.automation.automationframework.enums.TestResultStatusEnum;
import com.company.automation.automationframework.exceptions.QualityException;
import com.company.automation.automationframework.exceptions.NoSuchPropertyException;
import com.company.automation.automationframework.profile.AutomationProperties;
import com.company.automation.automationframework.screenshots.ScreenState;
import com.company.automation.automationframework.screenshots.Screenshot;
import com.company.automation.automationframework.templates.SimpleSeleniumTemplate;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.automationframework.testlog.TestLog.Entry;
import com.company.automation.automationframework.utils.DateUtils;


/**
 * This utility class is used to provide methods to deal with test execution
 * process.
 * <P>
 * For e.g.
 * <UL>
 * <LI>CSV (Comma Separated Values) files for automation tests.</LI>
 * <LI>Preparing screenshot images for file transfer to FTP and result reporting
 * </UL>
 * 
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 * 
 */
public class TestRunUtils
{
  private static HashMap<String, HashMap<String, String>> testReportParameters = null;

  /**
   * Reads the file path from the TestNG test context and returns back
   * 
   * @param context
   *            ITestContext
   * @return The file path of CSV file loaded from the test context
   * @throws Exception
   */
  public static String getCsvFromContext(ITestContext context)
      throws Exception
  {
    String csvFileName = context.getCurrentXmlTest().getParameter(
        TestParameterConstants.CSV_FILE_PARAMETER);
    if ((csvFileName == null) || csvFileName.isEmpty())
      throw new Exception(
          "<csvFile> parameter expected in the TestNg XML file for this test");
    return csvFileName;
  }

  /**
   * Reads the file path from the TestNG test context and returns back
   * 
   * @param context
   *            ITestContext
   * @return The file path of CSV file loaded from the test context
   * @throws Exception
   */
  public static String getCsvFromMethodOrContext(ITestContext context,
      Method method) throws Exception
  {

    // If a CSV is defined in the XML, this overrides anything else, use it.
    String csvFileName = context.getCurrentXmlTest().getParameter(
        TestParameterConstants.CSV_FILE_PARAMETER);
    if ((csvFileName != null) && !csvFileName.isEmpty())
    {
      return csvFileName;
    }

    // Try to get the CSV from the DataSource annotation
    if (method != null)
    {
      DataSource ds = (DataSource) method.getAnnotation(DataSource.class);

      // If no method DS, try the class
      if (ds == null)
      {
        ds = (DataSource) method.getDeclaringClass().getAnnotation(
            DataSource.class);
      }

      // If the DataSource exists, use it
      if (ds != null)
      {
        csvFileName = ds.source();

        // If source is non-empty, return it
        if ((csvFileName != null) && !csvFileName.isEmpty())
        {
          return csvFileName;
        }
      }
    }

    // If no datasource or XML specifies the CSV, die
    throw new Exception(
        "<csvFile> parameter expected in the TestNg XML file or method annotation for this test");

  }

  /**
   * Reads the data group from the TestNG test context and returns back
   * 
   * @param context
   *            ITestContext
   * @return The dataGroup from the test context
   */
  public static String getDataGroupFromContext(ITestContext context)
  {
    String dataGroup = context.getCurrentXmlTest().getParameter(
        TestParameterConstants.DATA_GROUP_PARAMETER);
    return dataGroup;
  }

  /**
   * Reads the scenario from the TestNG test context and returns back
   * @param context
   * @return
   */
  public static String getScenarioFromContext(ITestContext context)
  {
    String scenario = context.getCurrentXmlTest().getParameter(
        TestParameterConstants.SCENARIO_PARAMETER);

    return scenario;
  }

  /**
   * Reads the baseline output file path from the TestNG test context and
   * returns back
   * 
   * @param context
   *            ITestContext
   * @return The file path of baseline output CSV file loaded from the test
   *         context
   * @throws Exception
   */
  public static String getBaselineCsvFromContext(ITestContext context)
      throws Exception
  {
    String csvFileName = context.getCurrentXmlTest().getParameter(
        TestParameterConstants.CSV_BASELINE_OUTPUT_FILE_PARAMETER);
    if ((csvFileName == null) || csvFileName.isEmpty())
      throw new Exception(
          "<csvBaselineOutputFile> parameter expected in the TestNg XML file for this test");
    return csvFileName;
  }

  /**
   * Generates unique log ID based on class name and the object array of test
   * method parameters.
   * 
   * @param className
   *            Name of java class
   * @param objArray
   *            object array generated from test method parameters
   * @return unique class run id
   */
  public static String generateUniqueLogId(String className,
      String methodName, Object[] objArray)
  {

    return className + "." + methodName + "[" + Arrays.hashCode(objArray)
        + "]";
  }

  /**
   * Generates the folder name for uploading screenshot images based on the
   * date of the day.
   * 
   * @return Folder name for uploading screenshot images
   */
  public static String getScreenshotUploadFolderName()
  {

    String directory = null;

    directory = DateUtils.getDateInMMddyyyy(new Date());
    directory = directory.replaceAll("/", "");

    return directory;
  }

  public static ArrayList<Screenshot> prepareScreenShotsForTestRailReporting(
      String testClassName, TestScenarioResult scenarioResult) throws IOException
  {

    ArrayList<Screenshot> scenarioScreenshots = scenarioResult
        .getScreenshots();

    // Do not add the screenshots to list for PASSED tests
    if (!scenarioResult.getStatus().equalsIgnoreCase(
        TestResultStatusEnum.PASSED.getStatus()))
    {

      ArrayList<Screenshot> screenshotsForReporting = null;
      SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmmssSSS");

      if (scenarioScreenshots != null && scenarioScreenshots.size() > 0)
      {

        screenshotsForReporting = new ArrayList<Screenshot>();

        for (int i = 0; i < scenarioScreenshots.size(); i++)
        {
          Screenshot ss = scenarioScreenshots.get(i);

          // Make defensive copy before altering the file name and
          // path
          Screenshot ssCopy = ss.copy();
          File screenshotFile = ssCopy.getFile();
          Date timeTakenAt = ssCopy.getTimeTakenAt();
          if (timeTakenAt == null)
          {
            timeTakenAt = new Date();
          }
          String date = sdf.format(timeTakenAt);
          String filename = "SS-" + testClassName + "["
              + scenarioResult.getScenarioId() + "]" + date
              + ".png";
          File scrCopy = new File(filename);
          if (screenshotFile != null)
          {
            FileUtils.copyFile(screenshotFile, scrCopy);
            ssCopy.setFile(scrCopy);

            try
            {
              String imgWebServerHostUrl = AutomationProperties.
                  getProperty("image.web.server.host.url");

              ssCopy.setFileUrl(imgWebServerHostUrl + "/"
                  + TestRunUtils.getScreenshotUploadFolderName()
                  + "/" + filename);
            }
            catch (Exception e)
            {
              throw new IOException("Properties file not available.");
            }

          }

          screenshotsForReporting.add(ssCopy);
        } // for end
      }

      return screenshotsForReporting;
    }
    else
    {
      return scenarioScreenshots;
    }

  }

  public static TestResult modifyTestResultForReporting(TestResult testResult)
  {

    ArrayList<TestScenarioResult> updatedScenarioResults = new ArrayList<TestScenarioResult>();

    List<TestScenarioResult> scenarioResults = testResult
        .getScenarioResults();
    if (scenarioResults != null)
    {
      for (TestScenarioResult scenarioResult : scenarioResults)
      {

        try
        {
          ArrayList<Screenshot> ssReporting = TestRunUtils
              .prepareScreenShotsForTestRailReporting(
                  testResult.getTestClassName(),
                  scenarioResult);

          // These calls should always be in sequence
          scenarioResult.setScreenshots(ssReporting);

          scenarioResult.setLogString(TestRunUtils.buildTestLogText(
              scenarioResult.getLogEntries(), ssReporting));
          updatedScenarioResults.add(scenarioResult);

        }
        catch (IOException e)
        {
          e.printStackTrace();
        }

      }
    }

    // Update the scenario results back to map
    testResult.setScenarioResults(updatedScenarioResults);

    return testResult;
  }

  /**
   * This method is used to take failure screenshots and add to TestLog if the
   * TestListeners come across a failure or skipped test.
   * 
   * @param tr
   * @throws IOException
   */
  public static void takeFailureScreenShot(ITestResult tr) throws IOException
  {

    if (!(tr.getInstance() instanceof SimpleSeleniumTemplate))
      return;

    if (TestLog.getFailureScreenShotTaken())
      return;

    SimpleSeleniumTemplate testClass = (SimpleSeleniumTemplate) tr
        .getInstance();
    ScreenState scr = null;

    if (testClass != null)
    {
      scr = testClass.takeScreenShot();

      // pass screenshot even if null to log the failure
      TestLog.screenshot(scr, "Failure screenshot");
      TestLog.setFailureScreenShotTaken(true);
    }
    else
    {

      System.out
          .println("Could not retrieve test class from test result: "
              + tr);
    }
  }

  private static String buildTestLogText(ArrayList<Entry> entries,
      ArrayList<Screenshot> screenshots)
  {

    String logInText = "";

    if (entries != null && entries.size() > 0)
    {
      int i = 0;
      for (Entry entry : entries)
      {
        i += (entry.getType() == TestLog.STEP) ? 1 : 0;
        String str = entry.toString(i);

        if (entry.getType() == TestLog.SCREENSHOT)
        {
          String url = getFileUrlByOriginalFileName(entry
              .getScreenshot().getOriginalFileName(), screenshots);
          if (url != null)
          {
            str += "[ " + url + " ]";
          }
        }
        logInText += str + "\n";
      }
    }

    return logInText;
  }

  private static String getFileUrlByOriginalFileName(String originalFileName,
      ArrayList<Screenshot> screenshots)
  {

    if (screenshots != null)
    {
      for (Screenshot ss : screenshots)
      {
        if (ss.getOriginalFileName().equalsIgnoreCase(originalFileName))
        {
          return ss.getFileUrl();
        }
      }
    }
    return null;
  }

  /**
   * Given a stack trace, remove all non-essential debugging information,
   * leaving only:
   * 
   * - The exception message (first line) - All lines referring to
   * com.company.* lines of code
   * 
   * @param stackTrace
   * @return
   */
  public static String simplifyStackTrace(String stackTrace)
  {

    String newTrace = "";
    boolean pastError = false;

    String[] traceLines = stackTrace.split("\n");

    for (int i = 0; i < traceLines.length; i++)
    {

      // If we're not past the error, make sure to include it all. If
      // we've reached the end, check
      // other conditions
      if (!pastError)
      {
        if (traceLines[i].startsWith("  at")
            || traceLines[i].startsWith("Build info"))
        {
          pastError = true;
        }
        else
        {
          newTrace += traceLines[i] + "\n";
          continue;
        }
      }

      // Always include lines which contain "company" which will be
      // part of the class path
      if (i == 0 || traceLines[i].contains("company"))
      {
        newTrace += traceLines[i] + "\n";
        continue;
      }

    }

    return newTrace;
  }

  /**
   * Retrieve scenario id from test parameters.
   * 
   * @param objArray
   * @return
   */
  @SuppressWarnings("unchecked")
  public static String retriveScenarioId(Object[] objArray, String method)
  {
    String scenarioId = null;

    for (Object o : objArray)
    {
      if (o != null)
      {
        if (o instanceof HashMap
            && ((HashMap<String, String>) o)
                .containsKey(TestParameterConstants.SCENARIO_PARAMETER))
        {
          scenarioId = (((HashMap<String, String>) o)
              .get(TestParameterConstants.SCENARIO_PARAMETER));
          break;
        }
        if (o instanceof ScenarioData
            && ((ScenarioData) o).getScenarioId() != null)
        {
          scenarioId = ((ScenarioData) o).getScenarioId();
          break;
        }
      }
    }

    return scenarioId == null ? method : scenarioId;
  }

  /**
   * set up Test Report Parameter for a class
   * 
   * @param clazz
   * @param parameters
   */
  public static synchronized void setTestReportParameter(String clazz,
      HashMap<String, String> parameters)
  {
    if (testReportParameters == null)
    {
      testReportParameters = new HashMap<String, HashMap<String, String>>();
    }
    if (!testReportParameters.containsKey(clazz))
    {
      testReportParameters.put(clazz, parameters);
    }
  }

  /**
   * retrieve Test Report parameters for a class
   * 
   * @param clazz
   * @return
   */
  public static HashMap<String, String> getTestReportParameter(String clazz)
  {
    if (testReportParameters == null)
      return null;
    return testReportParameters.get(clazz);
  }

  /**
   * get annotation from a given class
   * @param clazz
   * @param source
   * @return
   * @throws ClassNotFoundException
   */
  public static <T extends Annotation> T getAnnotation(Class<T> clazz, String source)
      throws ClassNotFoundException
  {
    Class<?> sourceClazz = Class.forName(source);

    T annotation = sourceClazz.getAnnotation(clazz);
    if (annotation == null)
    {
      Method[] methods = sourceClazz.getDeclaredMethods();
      for (Method method : methods)
      {
        annotation = method.getAnnotation(clazz);
        if (annotation != null)
          break;
      }
    }

    return annotation;
  }

  /**
   * Override AutomationProperties using system parameters
   * @throws QualityException 
   * @throws IOException 
   * @throws JsonMappingException 
   * @throws JsonParseException 
   * @throws Exception 
   */
  public static void overrideProperties() throws QualityException, JsonParseException,
      JsonMappingException, IOException
  {
    String properties = System.getProperty(TestParameterConstants.AUTOMATION_PROPERTIES);
//    properties = "Bla-Bla-Bla";
    if (properties != null && !properties.equals("${automationProperties}"))
    {
      ObjectMapper mapper = new ObjectMapper();
      mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

      HashMap<String, String> propertiesMap = mapper.readValue(properties,
          new TypeReference<HashMap<String, String>>()
          {
          });

      try
      {
        Iterator<String> keys = propertiesMap.keySet().iterator();
        while (keys.hasNext())
        {
          String key = keys.next();
          try
          {
            AutomationProperties.getProperty(key);
          }
          catch (NoSuchPropertyException ex)
          {
            continue;
          }
          AutomationProperties.setProperty(key, propertiesMap.get(key));
        } 
      }
      catch (Exception e)
      {
        throw new QualityException("Problem on setting properties! [" + e.toString() + "]");
      }
    }
  }
}
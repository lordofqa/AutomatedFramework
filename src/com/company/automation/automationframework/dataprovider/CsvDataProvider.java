package com.company.automation.automationframework.dataprovider;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.testng.ITestContext;
import org.testng.annotations.DataProvider;

import com.company.automation.automationframework.constants.TestParameterConstants;
import com.company.automation.automationframework.testrail.TestRunUtils;
import com.company.automation.automationframework.utils.StringUtils;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class CsvDataProvider
{

 @DataProvider(name = "hashmapDataProvider")
 public static Iterator<Object[]> getHashmapFromCsv(ITestContext context, Method method)
   throws Exception
 {
  return getHashmapDataGroupProvider(context, method);
 }

 /**
  * Generates the Iterator of object arrays of hashmap.
  * 
  * @param context
  *         ITestContext
  * @return Iterator of Object array of hashmap for data provider of the TestNG
  *         test.
  * @throws Exception
  */
 public static Iterator<Object[]> getHashmapDataGroupProvider(ITestContext context, Method method)
   throws Exception
 {

  return getHashmapDataProvider(TestRunUtils.getCsvFromMethodOrContext(context, method),
    TestRunUtils.getDataGroupFromContext(context), TestRunUtils.getScenarioFromContext(context));

 }

 /**
  * Generates the Iterator of object arrays of hashmap.
  * 
  * @param csvFilePath
  *         Full path name of the CSV file including file name
  * @param dataGroup
  *         String representing what data group to include in data provider
  * @param scenario
  *         String representing which scenario to be included in data provider
  * 
  * @return Iterator of Object array of hashmap for data provider of the TestNG
  *         test.
  * @throws Exception
  */
 public static Iterator<Object[]> getHashmapDataProvider(String csvFilePath, String dataGroup,
   String scenario) throws Exception
 {

  ArrayList<Object[]> hashmapObjectList = new ArrayList<Object[]>();

  URL furl = Thread.currentThread().getContextClassLoader().getResource(csvFilePath);
  File csvFile = new File(furl.toURI());

  CsvDataFetcher csvDf = new CsvDataFetcher(csvFile);
  ArrayList<HashMap<String, String>> arMap = csvDf.getHashmapList();

  for (HashMap<String, String> row : arMap)
  {
   String csvDataGroup = row.get(TestParameterConstants.DATA_GROUP_PARAMETER);
   String csvScenario = row.get(TestParameterConstants.SCENARIO_PARAMETER);
   // Ignore unmatched scenario
   if (StringUtils.isNotEmpty(scenario) && !matchScenarios(csvScenario, scenario))
   {
    continue;
   }
   // Only add the data if no data group was declared or defined, or if the
   // group
   // matches the group defined in the XML
   if (dataGroup == null || csvDataGroup == null || matchGroups(csvDataGroup, dataGroup))
   {
    Object[] add = new Object[] { row };
    hashmapObjectList.add(add);
   }
  }

  return hashmapObjectList.iterator();
 }

 /**
  * @param csvGroup
  *         One or more group names from the CSV divided by commas
  * @param xmlGroup
  *         One or more group names from the XML divided by commas
  * @return
  */
 private static boolean matchGroups(String csvGroup, String xmlGroup)
 {
  String[] csvGroups = csvGroup.split(TestParameterConstants.SEPARATOR_COMMA);
  String[] xmlGroups = xmlGroup.split(TestParameterConstants.SEPARATOR_COMMA);

  for (String csv : csvGroups)
  {
   for (String xml : xmlGroups)
   {
    if (csv.equalsIgnoreCase(xml.trim()))
    {
     return true;
    }
   }
  }

  return false;
 }

 /**
  * @param scenarioGroup
  *         One scenario
  * @param xmlScenarios
  *         One or more scenarios from the XML divided by commas
  * @return
  */
 private static boolean matchScenarios(String csvScenario, String xmlScenarios)
 {
  if (csvScenario == null) return false;

  String[] xmlScenarioAr = xmlScenarios.split(TestParameterConstants.SEPARATOR_COMMA);

  for (String xml : xmlScenarioAr)
  {
   if (csvScenario.equalsIgnoreCase(xml.trim()))
   {
    return true;
   }
  }

  return false;
 }

 public static Iterator<Object[]> getHashmapDataProvider(String csvFilePath) throws Exception
 {
  return getHashmapDataProvider(csvFilePath, null, null);
 }

 // --------------------------------------------------------------

 /**
  * Return meta data(scenario, dataGroup) for a given data source file
  * 
  * @param csvFilePath
  * @return
  */
 public static HashMap<String, HashMap<String, String>> retrieveMetaData(String csvFilePath)
   throws Exception
 {
  HashMap<String, HashMap<String, String>> metaData = new HashMap<String, HashMap<String, String>>();

  URL furl = Thread.currentThread().getContextClassLoader().getResource(csvFilePath);
  if (furl == null) throw new Exception("CSV file provided is not valid: " + csvFilePath);

  File csvFile = new File(furl.toURI());
  CsvDataFetcher csvDf = new CsvDataFetcher(csvFile);
  ArrayList<HashMap<String, String>> inputData = csvDf.getHashmapList();

  for (HashMap<String, String> dataRow : inputData)
  {
   String csvScenario = dataRow.get(TestParameterConstants.SCENARIO_PARAMETER);
   String csvDataGroup = dataRow.get(TestParameterConstants.DATA_GROUP_PARAMETER);
   if (StringUtils.isEmpty(csvScenario) && StringUtils.isEmpty(csvDataGroup)) continue;
   HashMap<String, String> dataGroup = new HashMap<String, String>();
   dataGroup.put("dataGroup", csvDataGroup);
   metaData.put(csvScenario, dataGroup);
  }

  return metaData;

 }

}

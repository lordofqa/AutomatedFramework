package com.company.automation.automationframework.profile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;

import com.company.automation.automationframework.exceptions.NoSuchPropertyException;
import com.company.automation.automationframework.exceptions.QualityException;
import com.company.automation.automationframework.utils.FileUtils;

/**
 * This class initializes/access the Properties object that contains all framework properties 
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 */

public class AutomationProperties
{
  private static ArrayList<String> propertiesfiles = new ArrayList<String>();
  private static Properties properties;  
  
  private static boolean isTest = false;

  /**
   * Return  configuration Properties object that contains all framework properties 
   * 
   * Always read from init.properties, if init.properteries does not exist or error, then read from init.properties-template.
   * If it is test project, then append properties as below
   *  If login.properties exists, then add properties from login.properties
   *  If login.properties-template exists, then add properties from login.properties-template
   * 
   * @return Properties
   * @throws Exception
   */
  public static Properties getProperties() throws Exception
  {

    if (properties == null)
    {
      synchronized (AutomationProperties.class)
      {
        if (properties == null)
          properties = initProperties();
      }
    }

    return properties;

  }

  /**
   * Set a property in the configuration Properties object
   * @param key
   * @param value
   * @throws Exception
   */
  public static void setProperty(String key, String value) throws Exception
  {
    getProperties().put(key, value);

  }

  /**
   * Return a property from the configuration Properties object
   * @param key
   * @return
   * @throws NoSuchPropertyException if property DNE
   * NullPointerException if cannot access properties
   */
  public static String getProperty(String key) throws NoSuchPropertyException, QualityException
  {
    String property = null;
    try
    {
      property = getProperties().getProperty(key);
    }
    catch (Exception ex)
    {
      throw new QualityException("Unable to access property file", ex);
    }
    if (property == null)
    {
      throw new NoSuchPropertyException(key);
    }
    return property;
  }

  /**
   * Initialize properties files.
   * @return Properties
   * @throws Exception
   */
  private static Properties initProperties() throws Exception
  {
    // 1. Load properties from init.properties*
    Properties p = new Properties();
    // since init.properties is inside the framework jar, we will have to load it from the jar
    p.load(openPropertiesFileAsInputStream("config/init.properties"));   
    propertiesfiles.add("init.properties");

    // 2. check if it is test project
    setTest();

    // 3. append properties from login.properties* if it is test project
    if (isTest())
    {

      addFileToProperties(p, "config/login.properties");
      propertiesfiles.add("login.properties");
    }

    return p;

  }

  /**
   * set isTest. 
   * If certain package from test project exist, then isTest is set to true.
   */
  private static void setTest()
  {
    isTest = FileUtils.exists("config/login.properties-template");
  }

  /**
   * check if it is test project
   * @return
   */
  private static boolean isTest()
  {
    return isTest;
  }

  /**
   * Return File pointer for a given filename. If fails, then try to return from filename + "-template"
   * @param filename
   * @return
   * @throws Exception
   */
  private static File openPropertiesFile(String filename) throws Exception
  {
    File f;

    try
    {
      f = FileUtils.openFile(filename);
    }
    catch (Exception e)
    {
      try
      {
        f = FileUtils.openFile(filename + "-template");
      }
      catch (Exception e2)
      {
        throw new Exception(
            "Failed to read from both " + filename + " and " + filename + "-template. ["
                + e.getMessage() + "][" + e2.getMessage() + "]");
      }
    }
    return f;
  }

  /**
   * Returns Properties file as an InputStream. Used for reading init properties file from framework jar.
   * @param fileName
   * @return
   * @throws Exception
   */
  private static InputStream openPropertiesFileAsInputStream(String fileName) throws Exception
  {
    InputStream is;
    try
    {
      is = FileUtils.openFileAsInputStream(fileName);
    }
    catch (Exception e)
    {
      try
      {
        is = FileUtils.openFileAsInputStream(fileName + "-template");
      }
      catch (Exception e1)
      {
        throw new Exception(
            "Failed to read from both " + fileName + " and " + fileName + "-template. ["
                + e.getMessage() + "][" + e1.getMessage() + "]");
      }
    }
    return is;
  }
  
  /**
   * Return the default wait time for chrome
   * @return
   */
  public static int getDefaultWaitChrome()
  {
    int wait = 45;
    try 
    {
      wait = Integer.parseInt(getProperty("selenium.defaultWaitInSecond"));      
    }
    catch (Exception e)
    {
  
    }
    
    return wait;
  }
  
  /**
   * Return the default wait time for firefox
   * @return
   */
  public static int getDefaultWaitFirefox()
  {
    int wait = 45;
    try 
    {
      wait = Integer.parseInt(getProperty("selenium.firefox.defaultWaitInSecond"));      
    }
    catch (Exception e)
    {
  
    }
    
    return wait;
  }
  
  /**
   * Return the default wait time for ie
   * @return
   */
  public static int getDefaultWaitIE()
  {
    int wait = 50;
    try 
    {
      wait = Integer.parseInt(getProperty("selenium.ie.defaultWaitInSecond"));      
    }
    catch (Exception e)
    {
  
    }
    
    return wait;
  }

  /**
   * Return the ajax time out for chrome
   * @return
   */
  public static int getAjaxTimeoutChrome()
  {
    int wait = 5;
    try 
    {
      wait = Integer.parseInt(getProperty("selenium.ajaxTimeOutInSeconds"));      
    }
    catch (Exception e)
    {
  
    }
    
    return wait;
  }
  
  /**
   * Return the  ajax time out for firefox
   * @return
   */
  public static int getAjaxTimeoutFirefox()
  {
    int wait = 10;
    try 
    {
      wait = Integer.parseInt(getProperty("selenium.firefox.ajaxTimeOutInSeconds"));      
    }
    catch (Exception e)
    {
  
    }
    
    return wait;    
  }
  
  /**
   * Return the  ajax time out for ie
   * @return
   */
  public static int getAjaxTimeoutIE()
  {
    int wait = 10;
    try 
    {
      wait = Integer.parseInt(getProperty("selenium.ie.ajaxTimeOutInSeconds"));      
    }
    catch (Exception e)
    {
  
    }
    
    return wait;    
  }
  /**
   * Adds another properties file to automation properties
   * @param properties
   * @param propertiesfile
   * @throws Exception 
   */
  public static void addFileToProperties(Properties properties, String propertiesfile) throws Exception
  {  
    File f = openPropertiesFile(propertiesfile);
    Properties pt = new Properties();
    pt.load(new FileInputStream(f));
    properties.putAll(pt);
  }
  /**
   * Adds another properties file to automation properties
   * @param propertiesfile
   * @throws Exception 
   */
  public static void addFileToProperties(String propertiesfile) throws Exception
  {
    
    if(!propertiesfiles.contains(propertiesfile)) // if this properties file is already loaded, just skip
    {
      if (properties == null) // if the properties singleton is null, initialize it
      {
        synchronized (AutomationProperties.class)
        {
          if (properties == null)
            properties = initProperties();
        }
      }
      addFileToProperties(properties, propertiesfile);
    }
  }
}
package com.company.automation.automationframework.utils;

import java.io.IOException;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.company.automation.automationframework.profile.AutomationProperties;
import com.company.automation.automationframework.testrail.TestRunnerContext;

/**
 * Browser Library Utils
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class BrowserLibraryUtils
{

  public static WebDriver loadChrome() throws IOException, Exception
  {

    // Set System properties for chrome
    String[] chromeKeys = {"webdriver.chrome.driver", "webdriver.chrome.logfile"};
    for (String chromeKey : chromeKeys)
      setSystemProperties(chromeKey, AutomationProperties.getProperty(chromeKey));
    
    WebDriver driver = new ChromeDriver(buildConsoleLoggingCapabilities(DesiredCapabilities.chrome()));
    driver.manage().window().maximize();
    return driver;
  }

  public static WebDriver loadIE() throws Exception
  {

    // Set System property for ie
    String ieKey = "webdriver.ie.driver";
    setSystemProperties(ieKey, AutomationProperties.getProperty(ieKey));

    DesiredCapabilities caps = DesiredCapabilities.internetExplorer();
    caps.setCapability("ignoreZoomSetting", true);

    WebDriver driver = new InternetExplorerDriver(caps);
    driver.manage().window().maximize();
    
    return driver;
  }

  public static WebDriver loadFirefox() throws IOException, Exception
  {

    // Set System property for firefox
    String ffKey = "webdriver.firefox.bin";
    setSystemProperties(ffKey, AutomationProperties.getProperty(ffKey));

    WebDriver driver = new FirefoxDriver(buildConsoleLoggingCapabilities(DesiredCapabilities.firefox()));
    driver.manage().window().maximize();

    return driver;
  }
  
  /**
   * build new capabilities by adding console logging
   * @param caps
   * @return
   */
  protected static DesiredCapabilities buildConsoleLoggingCapabilities(DesiredCapabilities caps)
  {
    LoggingPreferences logPrefs = new LoggingPreferences();
    logPrefs.enable(LogType.BROWSER, Level.SEVERE);
    //logPrefs.enable(LogType.DRIVER, Level.SEVERE);  // turn this on for driver logs
    caps.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
    
    return caps;
  }

  public static WebDriver loadHtmlUnit() throws IOException, Exception
  {

    // Set System property for HtmlUnit
    WebDriver driver = new HtmlUnitDriver();
    ((HtmlUnitDriver) driver).setJavascriptEnabled(true);

    return driver;
  }

  public static WebDriver loadSauce() throws Exception
  {
    DesiredCapabilities capabillities = DesiredCapabilities.firefox();
    capabillities.setCapability("version", "5");
    capabillities.setCapability("platform", Platform.XP);
    capabillities.setCapability("name", "Testing Selenium 2 with Java on Sauce");

    WebDriver driver = new RemoteWebDriver(new URL(AutomationProperties
        .getProperty("sauceLabHubURL")), capabillities);
    driver.manage().timeouts().implicitlyWait(TestRunnerContext.getDefaultWait(), TimeUnit.SECONDS);

    return driver;
  }

  public static WebDriver loadEC2() throws Exception
  {

    DesiredCapabilities capabillities = new DesiredCapabilities();
    capabillities.setCapability("browser", "chrome");
    capabillities.setCapability("platform", Platform.ANY);

    WebDriver driver = new RemoteWebDriver(new URL(AutomationProperties
        .getProperty("amazonEC2HubURL")), capabillities);
    driver.manage().timeouts().implicitlyWait(TestRunnerContext.getDefaultWait(), TimeUnit.SECONDS);

    return driver;

  }

  public static WebDriver loadiPhone() throws Exception
  {
    DesiredCapabilities capabilities = DesiredCapabilities.iphone();
    capabilities.setJavascriptEnabled(true);

    return loadRemote(capabilities);
  }

  public static WebDriver loadRemote(DesiredCapabilities cap) throws Exception
  {
    URL remoteAddr = new URL(AutomationProperties.getProperty("selenium.hub.url"));

    WebDriver driver = null;

    int limit = Integer.parseInt(AutomationProperties.getProperty(
        "remotedriver.retries"));
    int index = 0;
    do
    {
      try
      {
    	if (cap.getBrowserName().equalsIgnoreCase("chrome") || cap.getBrowserName().equalsIgnoreCase("firefox"))
        {
          cap = buildConsoleLoggingCapabilities(cap);
        }
    	  
        driver = new RemoteWebDriver(remoteAddr, cap);
        
        //Upload local file        
        ((RemoteWebDriver)driver).setFileDetector(new LocalFileDetector());

        if (cap.getBrowserName().equalsIgnoreCase("internet explorer"))
        {
          driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
          String script = "if (window.screen){window.moveTo(0,0);window.resizeTo(window.screen.availWidth,window.screen.availHeight);};";
          ((JavascriptExecutor) driver).executeScript(script);
        }

        /******************************
         * Maximizing the Web Browser *
         ******************************/
        driver.manage().window().maximize();
      }
      catch (WebDriverException e)
      {
        if (index < limit - 1)
        {
          Thread.sleep(1000);
        }
        else
        {
          throw e;
        }
      }
    }
    while (++index < limit && driver == null);

    return driver;
  }

  private static void setSystemProperties(String key, String value)
  {

    // If there is nothing to set, don't bother setting
    if (StringUtils.isEmpty(value) || StringUtils.isEmpty(key))
      return;

    System.setProperty(key.trim(), value.trim());

  }

  public static WebDriver loadBrowser(String browser, String version, String platform, String remote)
      throws Exception
  {
    // If its not local, prepare remote information
    if (!remote.equalsIgnoreCase("false"))
    {
      DesiredCapabilities cap = new DesiredCapabilities();
      if (!StringUtils.isEmpty(browser))
      {
        cap.setBrowserName(browser);
      }
      if (!StringUtils.isEmpty(version))
      {
        cap.setVersion(version);
      }
      if (!StringUtils.isEmpty(platform))
      {
        cap.setPlatform(Platform.valueOf(platform));
      }
      cap.setJavascriptEnabled(true);

      // Sauce labs execution
      if (remote.equalsIgnoreCase("sauce"))
      {
        return loadSauce();
      }
      // EC2 execution
      else if (remote.equalsIgnoreCase("ec2"))
      {
        return loadEC2();
      }
      // Selenium GRID execution
      return loadRemote(cap);
    }

    // Local execution
    else
    {
      if (browser.equalsIgnoreCase("chrome"))
      {
        return loadChrome();
      }
      else if (browser.equalsIgnoreCase("firefox"))
      {
        return loadFirefox();
      }
      else if (browser.equalsIgnoreCase("internet explorer"))
      {
        return loadIE();
      }
      else if (browser.equalsIgnoreCase("htmlunit"))
      {
        return loadHtmlUnit();
      }
    }

    // default
    return loadChrome();
  }
  

}

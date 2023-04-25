package com.company.automation.automationframework.profile;

import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.xml.XmlTest;

import com.company.automation.automationframework.constants.TestParameterConstants;
import com.company.automation.automationframework.navigation.Urls;

/**
 * This object contain login profile data
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class LoginProfile
{
  private static final String DEFAULT_TRANSPORT = "https";

  private String orgCode;
  private String userName;
  private String passWord;
  private String server;
  private String transport;
  private String operatingServer;
  private String browser;
  private String ignorePassed;

  public LoginProfile(String orgCode, String userName, String passWord, String server)
  {
    this(orgCode, userName, passWord, server, DEFAULT_TRANSPORT,"chrome");
  }

  public LoginProfile(String orgCode, String userName, String passWord, String server,
      String transport, String browser)
  {
    this.orgCode = orgCode;
    this.userName = userName;
    this.passWord = passWord;
    this.server = server;
    this.operatingServer = null;

    this.transport = transport;
    if (this.transport == null)
    {
      this.transport = DEFAULT_TRANSPORT;
    }
    this.browser = browser;
  }

  public String getBrowser()
  {
    return this.browser;
  }

  
  public void setOperatingServer(WebDriver driver) throws MalformedURLException
  {
    URL url = new URL(driver.getCurrentUrl());
    this.operatingServer = url.getProtocol() + "://" + url.getHost()
        + ((url.getPort() > -1) ? ":" + url.getPort() : "");
  }

  public String getUserName()
  {
    return this.userName;
  }

  public String getPassWord()
  {
    return this.passWord;
  }

  public String getOrgCode()
  {
    return this.orgCode;
  }

  public String getServer()
  {
    return this.server;
  }

  public String getLoginBaseUrl()
  {
    return this.transport + "://" + this.server;
  }

  public String getBaseUrl()
  {
    if (null != this.operatingServer)
      return this.operatingServer;
    return this.server;
  }

  public String getBaseOperatingUrl()
  {
    return this.transport + "://" + this.operatingServer;
  }

  /**
   * @deprecated  Must pass in a Urls to get a URL from
   *              {@link #getLoginUrl(Urls)}
   */
  @Deprecated
  public String getLoginUrl()
  {
    return getLoginBaseUrl() + "/home/login.jsp";
  }
  
  public String getLoginUrl(Urls loginPage)
  {
    return getLoginBaseUrl() + loginPage.getUrl();
  }

  public String getFullUrl(String relativeUrl)
  {
    return getBaseUrl() + relativeUrl;
  }

  public void overRideWithXml(ITestContext context)
  {

    XmlTest xml = context.getCurrentXmlTest();

    if (xml.getParameter(TestParameterConstants.ORG_CODE) != null
        && !xml.getParameter(TestParameterConstants.ORG_CODE).isEmpty())
    {
      this.orgCode = xml.getParameter(TestParameterConstants.ORG_CODE);
    }
    if (xml.getParameter(TestParameterConstants.USERNAME) != null
        && !xml.getParameter(TestParameterConstants.USERNAME).isEmpty())
    {
      this.userName = xml.getParameter(TestParameterConstants.USERNAME);
    }
    if (xml.getParameter(TestParameterConstants.PASSWORD) != null
        && !xml.getParameter(TestParameterConstants.PASSWORD).isEmpty())
    {
      this.passWord = xml.getParameter(TestParameterConstants.PASSWORD);
    }
    if (xml.getParameter(TestParameterConstants.SERVER) != null
        && !xml.getParameter(TestParameterConstants.SERVER).isEmpty())
    {
      this.server = xml.getParameter(TestParameterConstants.SERVER);
    }
    if (xml.getParameter(TestParameterConstants.TRANSPORT) != null
            && !xml.getParameter(TestParameterConstants.TRANSPORT).isEmpty())
    {
      this.transport = xml.getParameter(TestParameterConstants.TRANSPORT);
    }
    if (xml.getParameter(TestParameterConstants.BROWSER) != null
            && !xml.getParameter(TestParameterConstants.BROWSER).isEmpty())
    {
      this.transport = xml.getParameter(TestParameterConstants.BROWSER);
    }

  }

  /**
   * Overrides login credentials from the environment variables only if they are provided/supplied
   * <P> Used by build server to pass the login credentials as environment variables. Login credentials
   * overridden by environment variables are:
   * <UL>
   * <LI>userName: Application User Name</LI>
   * <LI>passWord: Account Password</LI>
   * <LI>server: Web application domain name</LI>
   * <LI>orgCode: Organization Code</LI>
   * </UL>
   */
  public void overRideWithEnvironmentVariables()
  {

    String userName = System.getProperty(TestParameterConstants.USERNAME); 
    if ((userName != null) && !userName.equals("${userName}"))
    {
      this.userName = userName;
    }

    String passWord = System.getProperty(TestParameterConstants.PASSWORD);
    if ( (passWord!= null) && !passWord.equals("${passWord}"))
    {
      this.passWord = passWord;
    }

    String server = System.getProperty(TestParameterConstants.SERVER);
    if ( server!= null && !server.equals("${server}"))
    {
      this.server = System.getProperty(TestParameterConstants.SERVER);
    }

    String orgCode = System.getProperty(TestParameterConstants.ORG_CODE);
    if ( orgCode!= null && !orgCode.equals("${orgCode}"))
    {
      this.orgCode = System.getProperty(TestParameterConstants.ORG_CODE);
    }
    
    String transport = System.getProperty(TestParameterConstants.TRANSPORT);
    if (transport != null && !transport.equals("${transport}"))
    {
      this.transport = System.getProperty(TestParameterConstants.TRANSPORT);
    }
    
    String browser = System.getProperty(TestParameterConstants.BROWSER);
    if (browser != null && !browser.equals("${transport}"))
    {
      this.transport = System.getProperty(TestParameterConstants.BROWSER);
    }

    //String ignorePassed = System.getProperty(TestParameterConstants.IGNORE_LOG_FOR_PASSED_TESTS);
    //if (ignorePassed != null)
    //{
    //  this.ignorePassed = System.getProperty(TestParameterConstants.IGNORE_LOG_FOR_PASSED_TESTS);
    //} 
    
  }

  public String getTransport()
  {
    return this.transport;
  }
  
  /**
   * Override LoginProfile orgCode 
   * @param orgCode
   */
  public void overrideOrgCode(String orgCode)
  {
    this.orgCode = orgCode;
  }

  /**
   * Override LoginProfile server 
   * @param server
   */
  public void overrideServer(String server)
  {
    this.server = server;
  }

  
}

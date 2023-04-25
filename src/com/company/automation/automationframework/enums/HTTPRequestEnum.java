package com.company.automation.automationframework.enums;


/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class HTTPRequestEnum
{
  public enum HTTPRequestHeader
  {
    ACCEPT("Accept"),
    CONTENT_TYPE("Content-Type"),
    COOKIE("Cookie"),
    AUTHORIZATION("Authorization"),
    USER_AGENT("User-Agent");

    private String property;

    HTTPRequestHeader(String property)
    {
      this.property = property;

    }

    /**
     * @return the property
     */
    public String getProperty()
    {
      return property;
    }

    /**
     * @param property the property to set
     */
    public void setProperty(String property)
    {
      this.property = property;
    }

  }
  
  public enum HTTPRequestMethod
  {
    GET, POST, PUT, DELETE,HEAD,OPTIONS,TRACE;
  }
}

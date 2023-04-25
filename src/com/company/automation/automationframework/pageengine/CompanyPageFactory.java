package com.company.automation.automationframework.pageengine;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;

/**
 * Page Factory class to make using Page Objects simpler and easier.
 * 
 * @see <a href="http://code.google.com/p/webdriver/wiki/PageObjects">Page
 *      Objects Wiki</a>
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class CompanyPageFactory extends PageFactory
{

 public static <T> T initElements(WebDriver driver, Class<T> pageClassToProxy)
 {
  T page = instantiatePage(driver, pageClassToProxy);
  initElements(driver, page);
  return page;
 }

 public static void initElements(WebDriver driver, Object page)
 {
  final WebDriver driverRef = driver;
  initElements(new CompanyElementLocatorFactory(driverRef), page);
 }

 public static void initElements(ElementLocatorFactory factory, Object page)
 {
  final ElementLocatorFactory factoryRef = factory;
  initElements(new CompanyFieldDecorator(factoryRef), page);
 }

 private static <T> T instantiatePage(WebDriver driver, Class<T> pageClassToProxy)
 {
  try
  {
   try
   {
    Constructor<T> constructor = pageClassToProxy.getConstructor(WebDriver.class);
    return constructor.newInstance(driver);
   }
   catch (NoSuchMethodException e)
   {
    return pageClassToProxy.newInstance();
   }
  }
  catch (InstantiationException e)
  {
   throw new RuntimeException(e);
  }
  catch (IllegalAccessException e)
  {
   throw new RuntimeException(e);
  }
  catch (InvocationTargetException e)
  {
   throw new RuntimeException(e);
  }
 }
}

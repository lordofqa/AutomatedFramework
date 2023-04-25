/**
 * 
 */
package com.company.automation.automationframework.pageengine;

import java.lang.reflect.Field;

import org.openqa.selenium.SearchContext;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;



/**
 *  version of element locator factory for producing
 *         {@link ElementLocator}s. It is expected that a new ElementLocator
 *         will be returned per call.
 *  @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 */

public class CompanyElementLocatorFactory implements ElementLocatorFactory
{

  private final SearchContext searchContext;

  private int ajaxTimeOutInSeconds;

  public CompanyElementLocatorFactory(SearchContext searchContext)
  {
    this.searchContext = searchContext;

   
    
    if (ajaxTimeOutInSeconds < 0)
    {

      ajaxTimeOutInSeconds = 5;
    }
  }

  /*
   * @see
   * org.openqa.selenium.support.pagefactory.ElementLocatorFactory#createLocator
   * (java.lang.reflect .Field)
   */
  @Override
  public ElementLocator createLocator(Field field)
  {
    AjaxElementLocatorFactory ajaxFactory = new AjaxElementLocatorFactory(
        searchContext, ajaxTimeOutInSeconds);
    return ajaxFactory.createLocator(field);

  }

}

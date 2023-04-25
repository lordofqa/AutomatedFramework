/**
 * 
 */
package com.company.automation.automationframework.pageengine;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.List;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.Locatable;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.FindBys;
import org.openqa.selenium.support.pagefactory.DefaultFieldDecorator;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import org.openqa.selenium.support.pagefactory.internal.LocatingElementHandler;
import org.openqa.selenium.support.ui.Select;

/**
 * <P>
 * Company Field decorator for use with PageFactory. Will decorate:
 * <P>
 * <OL>
 * <LI>All of the WebElement fields: if the field type is not "Select", the
 * DefaultFieldDecorator is called to decorate the field.</LI>
 * <LI>All the Select fields with @FindBy annotation.</LI>
 * <LI>List<WebElement> fields that have @FindBy or @FindBys annotation with a
 * proxy that locates the elements using the passed in ElementLocatorFactory.
 * Again, for list elements, DefaultFieldDecorator is called to decorate the
 * field.</LI>
 * </OL>
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 */
public class CompanyFieldDecorator extends DefaultFieldDecorator implements FieldDecorator
{

  public CompanyFieldDecorator(ElementLocatorFactory factory)
  {

    super(factory);
  }

  /**
   * Override decorate to call the overridden proxyForLocator
   * @param ClassLoader loader
   * @param Field field
   */
  @Override
  public Object decorate(ClassLoader loader, Field field)
  {
    Object obj = null;

    if (!(WebElement.class.isAssignableFrom(field.getType())
        || isDecoratableList(field)
        || isSelect(field)))
    {
      return null;
    }

    ElementLocator locator = factory.createLocator(field);
    if (locator == null)
    {
      return null;
    }

    // Select
    if (isDecoratableField(field) && isSelect(field))
    {
      WebElement element = proxyForLocator(loader, locator);
      try
      {
        obj = new Select(element);
      }
      catch (NoSuchElementException e)
      {
        return obj;
      }
    }
    else if (WebElement.class.isAssignableFrom(field.getType()))
    {
      obj = proxyForLocator(loader, locator);
    }
    else if (List.class.isAssignableFrom(field.getType()))
    {
      obj = proxyForListLocator(loader, locator);
    }

    return obj;

  }

  /**
   * Overload proxyForLocator method in DefaultFieldDecorator. Changing
   * handler from LocatingElementHandler to StaleReferenceAwareElementLocator. 
   */
  protected WebElement proxyForLocator(ClassLoader loader, ElementLocator locator)
  {
    InvocationHandler handler = new LocatingElementHandler(locator);
    //InvocationHandler handler = new StaleReferenceAwareElementLocator(locator);
    
    WebElement proxy;
    proxy = (WebElement) Proxy.newProxyInstance(
        loader, new Class[] { WebElement.class, WrapsElement.class, Locatable.class }, handler);
    return proxy;
  }

  @SuppressWarnings("unchecked")
  protected List<WebElement> proxyForListLocator(ClassLoader loader, ElementLocator locator)
  {
    InvocationHandler handler = new LocatingElementHandler(locator);
    //InvocationHandler handler = new StaleReferenceAwareElementLocator(locator);
    
    List<WebElement> proxy;
    proxy = (List<WebElement>) Proxy.newProxyInstance(
        loader, new Class[] { List.class }, handler);
    return proxy;
  }

  /**
   * Copied from DefailtFieldDecorator.
   * @param field
   * @return
   */
  public boolean isDecoratableList(Field field)
  {
    if (!List.class.isAssignableFrom(field.getType()))
    {
      return false;
    }

    // Type erasure in Java isn't complete. Attempt to discover the generic
    // type of the list.
    Type genericType = field.getGenericType();
    if (!(genericType instanceof ParameterizedType))
    {
      return false;
    }

    Type listType = ((ParameterizedType) genericType).getActualTypeArguments()[0];

    if (!WebElement.class.equals(listType))
    {
      return false;
    }

    if (field.getAnnotation(FindBy.class) == null &&
        field.getAnnotation(FindBys.class) == null)
    {
      return false;
    }

    return true;
  }

  /**
   * Checks if the field is a Select
   * @param field
   * @return
   */
  private boolean isSelect(Field field)
  {
    Class<?> type = field.getType();

    if (!type.isPrimitive() && type.equals(org.openqa.selenium.support.ui.Select.class))
      return true;

    return false;
  }

  /**
   * Checks if the field is decorate-able. Checks if it has FindBy annotation.
   * 
   * @param field
   * @return true if FindBy annotation exists
   */
  private boolean isDecoratableField(Field field)
  {

    if (field.getAnnotation(FindBy.class) == null)
    {
      return false;
    }
    else
    {
      return true;
    }
  }

}

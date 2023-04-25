package com.company.automation.testautomation.helpers;

import java.util.NoSuchElementException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;

/**
 * Helper class containing reusable actions
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class SlashdotHelper 
{
 /**
 * Method that handle waiting until specific elements are populated on the page
 * @param driver
 * @param css
 */
 public static void waitUntilElementsPresentedOnPage(WebDriver driver, String css)
 {
  FluentWait<WebDriver> fWait = new FluentWait<WebDriver>(driver).withTimeout(30, TimeUnit.SECONDS)
	        .pollingEvery(1, TimeUnit.SECONDS)
	        .ignoring(NoSuchElementException.class);
  fWait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector(css)));
  
 }
 
 /**
 * Get Random Integer number in specified range(inclusive)
 * @param min
 * @param max
 * @return
 */
 public static int getRandomIntInRange(int min, int max)
 {
  int randomInt = ThreadLocalRandom.current().nextInt(min, max);
  return randomInt;
 } 
 
}

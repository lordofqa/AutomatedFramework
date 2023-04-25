package com.company.automation.testautomation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.company.automation.testautomation.helpers.SlashdotHelper;

/**
 * PageObject for Slashdot Vote Results Page
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class SlashdotVoteResultsPage
{
 
 private static final String POLL_RESULTS_CSS	=".poll-bar-text";
	
	/**
	 * Get Vote Results For Specific Position in vote list (zero based list)
	 * @param driver
	 * @param position
	 * @return
	 */
 public String getVoteResultsForSpecificPosition(WebDriver driver, int position)
 {
  SlashdotHelper.waitUntilElementsPresentedOnPage(driver,POLL_RESULTS_CSS);	 
  List<WebElement> pollVoteResultList = driver.findElements(By.cssSelector(POLL_RESULTS_CSS));
  String str = pollVoteResultList.get(position).getText().split(" ")[0];
  return str;
 }
	 
}

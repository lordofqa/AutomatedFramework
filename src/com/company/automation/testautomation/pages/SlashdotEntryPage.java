package com.company.automation.testautomation.pages;

/*
 *****************************************************************************************
 *																						                                                                 *
 *																						                                                                 *
 * Location: This is dummy SignIn page 										                                        *
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)																                                               *
 * Jira#:				 																                                                           *
 * 																						                                                                *	
 *****************************************************************************************
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.company.automation.automationframework.exceptions.PageValidationException;
import com.company.automation.automationframework.pageengine.Page;
import com.company.automation.automationframework.profile.LoginProfile;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.testautomation.helpers.SlashdotHelper;

/**
 * PageObject for SlashdotEntryPage
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class SlashdotEntryPage extends Page
{
 public SlashdotEntryPage(WebDriver driver) throws PageValidationException
 {
  super(driver);
 }

 private static final String ARTICLE_TITLES_CSS           = ".story-title";
 private static final String ARTICLE_ICON_IMAGES_CSS      = "article>header>span>a>img";
 private static final String POLL_CHECKBOXES_CSS          = "#pollBooth>label>input";
 private static final String POLL_CHOICE_DESCRIPTIONS_CSS = "#pollBooth>label";
 private static final String POLL_VOTE_BUTTON_CSS         = ".btn-polls";

 /**
  * Method returning number of articles on the initial page
  * 
  * @param driver
  * @return
  */
 public int getNumberOfArticlesOnTitlePage(WebDriver driver)
 {
  SlashdotHelper.waitUntilElementsPresentedOnPage(driver, ARTICLE_TITLES_CSS);
  List<WebElement> articlesTitlesList = driver.findElements(By.cssSelector(ARTICLE_TITLES_CSS));
  return articlesTitlesList.size();
 }

 /**
  * Print List Of Icons links On The Title Page along with how many times each
  * was used
  * 
  * @param driver
  */
 public void printListOfUniqueIconsOnTheTitlePage(WebDriver driver)
 {
  SlashdotHelper.waitUntilElementsPresentedOnPage(driver, ARTICLE_ICON_IMAGES_CSS);
  List<WebElement> articleImageElementsList = driver.findElements(By.cssSelector(ARTICLE_ICON_IMAGES_CSS));

  HashMap<String, Integer> map = new HashMap<String, Integer>();

  for (WebElement webElement : articleImageElementsList)
  {
   String key = webElement.getAttribute("src");
   populateHashMap(map, key);
  }
  printHashMap(map);
 }

 /**
  * Method for populating Icons HashMap
  * 
  * @param map
  * @param key
  */
 private void populateHashMap(HashMap<String, Integer> map, String key)
 {
  if (map.containsKey(key))
  {
   int oldValue = (int) map.get(key);
   map.put(key, oldValue + 1);
  }
  else
  {
   map.put(key, 1);
  }
 }

 /**
  * Print HashMap
  * 
  * @param map
  */
 private void printHashMap(HashMap<String, Integer> map)
 {
  for (Map.Entry<String, Integer> entry : map.entrySet())
  {
   String format = "%-70s%s%n";
   System.out.printf(format, "Image:[" + entry.getKey() + "] was used ", "[" + entry.getValue() + "] times on page");
  }
 }

 /**
  * Vote For Certain position In Poll (zero based list)
  * 
  * @param driver
  * @param position
  * @return
  */
 public WebDriver voteForCertainOptionInPoll(WebDriver driver, int position)
 {
  SlashdotHelper.waitUntilElementsPresentedOnPage(driver, POLL_CHECKBOXES_CSS);
  List<WebElement> pollItemsList = driver.findElements(By.cssSelector(POLL_CHECKBOXES_CSS));

  SlashdotHelper.waitUntilElementsPresentedOnPage(driver, POLL_CHOICE_DESCRIPTIONS_CSS);
  List<WebElement> pollItemsLabelList = driver.findElements(By.cssSelector(POLL_CHOICE_DESCRIPTIONS_CSS));

  String voteOptionText = pollItemsLabelList.get(position).getText();

  if (position >= 0 && position <= pollItemsList.size() - 1)
  {
   // System.out.println("\nNow voting for:["+voteOptionText+"] on the daily poll");
   pollItemsList.get(position).click();
   clickPollVoteButton(driver);
  }
  return driver;
 }

 /**
  * Click Poll Vote Button
  * 
  * @param driver
  */
 public void clickPollVoteButton(WebDriver driver)
 {
  TestLog.step("Click Vote button");
  SlashdotHelper.waitUntilElementsPresentedOnPage(driver, POLL_VOTE_BUTTON_CSS);
  List<WebElement> pollButtonList = driver.findElements(By.cssSelector(POLL_VOTE_BUTTON_CSS));
  pollButtonList.get(0).click();
 }

 /**
  * Get Number Of Available Polls
  * 
  * @param driver
  * @return
  */
 public int getNumberOfAvailablePolls(WebDriver driver)
 {
  SlashdotHelper.waitUntilElementsPresentedOnPage(driver, POLL_CHECKBOXES_CSS);
  List<WebElement> pollItemsList = driver.findElements(By.cssSelector(POLL_CHECKBOXES_CSS));
  return pollItemsList.size() - 1;
 }

 public void loginAs(String login, String password)
 {
  // placeholder
 };

 public void login(LoginProfile loginProfile)
 {
  // placeholder
 }

 /**
  * Get to the app. SignIn page
  * 
  * @param loginProfile
  * @param driver
  */
 public void getToLoginPage(LoginProfile loginProfile, WebDriver driver)
 {
  driver.get(loginProfile.getBaseUrl());
 }

}

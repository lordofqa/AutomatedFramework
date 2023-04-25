package com.company.automation.automationframework.navigation;

import org.openqa.selenium.WebDriver;

import com.company.automation.automationframework.profile.LoginProfile;
import com.company.automation.automationframework.testlog.TestLog;
import com.company.automation.automationframework.utils.StringUtils;

public class Navigate {

	private WebDriver driver;
	private LoginProfile loginProfile;

	public Navigate(WebDriver driver, LoginProfile loginProfile) {
		this.driver = driver;
		this.loginProfile = loginProfile;
	}

  /**
   * Build new URL using base URL, page URL, and other information (i.e.
   * resident ID), then navigate to the page.
   * 
   * @param page
   * @return return true if the new page title match the expected
   * @throws Exception
   */
  public boolean goTo(Urls page, String extra) throws Exception
  {
    TestLog.step("Navigate to page [" + page.getTitle() + "] at URL [" + page.getUrl() + extra
        + "]");

    if (!page.isDirectLink() && StringUtils.isEmpty(extra))
    {
      throw new Exception("Cannot navigate directly to page ["
          + page.getTitle() + "] at URL [" + page.getUrl() + extra + "]");
    }

    driver.get(loginProfile.getFullUrl(page.getUrl()) + extra);
    return page.getTitle().equals(driver.getTitle());

  }

	/**
	 * Build new URL with the base URL and page URL, then navigate to the URL
	 * 
	 * @param page
	 * @param criteria
	 * @return return true if the new page title match the expected
	 * @throws Exception 
	 */
	public boolean goTo(Urls page) throws Exception {
		return this.goTo(page, "");
	}

}


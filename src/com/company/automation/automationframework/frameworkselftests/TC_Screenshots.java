package com.company.automation.automationframework.frameworkselftests;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.company.automation.automationframework.templates.CompanySeleniumTemplate;

public class TC_Screenshots extends CompanySeleniumTemplate
{

 @Test()
 public void testScreenshots() throws Exception
 {

  Assert.assertTrue(false, "This test should always Fail");
 }

}

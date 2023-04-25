package com.company.automation.automationframework.frameworkselftests;

import java.io.File;

import com.company.automation.automationframework.screenshots.ScreenshotFtpClient;
import com.company.automation.automationframework.screenshots.ScreenshotSFtpClient;

public class TC_VerifySFTPConnection 
{

 public static void main(String[] args) throws Exception
 {

  //ScreenshotSFtpClient ftpclient = new ScreenshotSFtpClient();
  ScreenshotFtpClient ftpclient = new ScreenshotFtpClient();

  File imgFileToFtp  = new File("C:\\testdata\\badScript.jpg");
  
  try
  {
    ftpclient.connect();

    ftpclient.sendScreenshotFile(imgFileToFtp);

  }
  catch (Exception e)
  {
   e.printStackTrace();
   System.out.println("Error occured while screenshot file transfer to server. " + e.getMessage());
  }
  finally
  {
    ftpclient.disconnect();
  }
  
 }//main  
 
}

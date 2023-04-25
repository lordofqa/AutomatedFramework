package com.company.automation.automationframework.screenshots;

import java.io.File;
import java.util.Date;

/**
 * A object to store the information about the screenshot taken during the test.
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class Screenshot
{

  private File file;
  private String originalFileName;
  private String description;
  private String fileUrl;
  private Date timeTakenAt;
  private boolean lastScreenshot = false;

  public File getFile()
  {
    return this.file;
  }

  public void setFile(File file)
  {
    this.file = file;
  }

  public String getDescription()
  {
    return this.description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getFileUrl()
  {
    return this.fileUrl;
  }

  public void setFileUrl(String fileUrl)
  {
    this.fileUrl = fileUrl;
  }

  public boolean isLastScreenshot()
  {
    return this.lastScreenshot;
  }

  public Date getTimeTakenAt()
  {
    return this.timeTakenAt;
  }

  public void setTimeTakenAt(Date timeTakenAt)
  {
    this.timeTakenAt = timeTakenAt;
  }

  public String getOriginalFileName()
  {
    return this.originalFileName;
  }

  public void setOriginalFileName(String originalFileName)
  {
    this.originalFileName = originalFileName;
  }

  /**
   * Makes defensive copy of screenshot object leaving the original object
   * unchanged.
   * 
   * @return Screenshot copy
   */
  public Screenshot copy()
  {

    Screenshot screenshot = new Screenshot();

    screenshot.setDescription(this.getDescription());
    screenshot.setFile(this.getFile());
    screenshot.setOriginalFileName(this.getOriginalFileName());
    screenshot.setFileUrl(this.getFileUrl());
    screenshot.setTimeTakenAt(this.getTimeTakenAt());

    return screenshot;
  }

}

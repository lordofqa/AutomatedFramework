package com.company.automation.automationframework.screenshots;

/**
 * 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import com.company.automation.automationframework.profile.AutomationProperties;
import com.company.automation.automationframework.testrail.TestRunUtils;

/**
 * ScreenshotFtpClient (was a Singleton class) which provides the interface to
 * interact with Apache Commons FTP client library and hides all the painful
 * tasks of connecting to ftp server, creating the folder for file transfer if
 * it does not exist and so on...
 * <P>
 * The sequence of transferring the screenshot ftp files is:
 * 
 * <PRE>
 * ScreenshotFtpClient ftpclient = ScreenshotFtpClient.getInstance();
 * ftpclient.connect();
 * </PRE>
 * 
 * For single file transfer user can call
 * 
 * <PRE>
 * ftpclient.sendScreenshotFile(imgFileToFtp);
 * </PRE>
 * 
 * or for multiple file transfer user can call
 * 
 * <PRE>
 * ftpclient.sendScreenshotFiles(screenshots);
 * </PRE>
 * 
 * at the end when done with ftp, disconnect the ftp server
 * 
 * <PRE>
 * ftpclient.disconnect();
 * </PRE>
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 */

public class ScreenshotFtpClient
{
  /**  
    * Modified this class so that each client has its own
    * instance. Having a singleton client was causing issues when we introduced
    * parallelised tests and threads were fighting over the single resource
    * TODO - Check the performance of this setup and investigate having 
    * a pool of clients if resources are limited
  */
  
  private FTPClient ftp = new FTPClient();

  public void connect() throws Exception
  {

    if (this.ftp == null)
    {
      this.ftp = new FTPClient();
    }

    // check if the connection is already active with this host and port, if so
    // do nothing.
    // also check if the connection is dropped. if so, disconnect and re-connect
    if (this.ftp.isConnected() && this.ftp.isAvailable())
    {
      return;
    }

    if (!this.ftp.isAvailable())
    {
      this.ftp.disconnect();
    }

    // Now make a new connection
    try
    {
      int reply;
      
      String imgFtpServerHostUrl = AutomationProperties.getProperty("image.sftp.server.host.url");
      int port = Integer.parseInt(AutomationProperties.getProperty("image.sftp.server.host.port"));
      String ftpUser = AutomationProperties.getProperty("image.sftp.server.host.ftpUser");
      String ftpPwd = AutomationProperties.getProperty("image.sftp.server.host.ftpPwd");
      
      this.ftp.connect(imgFtpServerHostUrl, port);
      this.ftp.login(ftpUser, ftpPwd);

      this.ftp.setControlKeepAliveTimeout(180); // set timeout to 3 minutes. FTP
                                                // client will send
                                                // NOOP signals to keep ftp
                                                // connection alive

      // After connection attempt, you should check the reply code to verify
      // success.   230 = User logged in, proceed. Logged out if appropriate.
      reply = this.ftp.getReplyCode();

      if (!FTPReply.isPositiveCompletion(reply))
      {
        this.ftp.disconnect();
        throw new IOException("FTP server refused connection.");
      }

    }
    catch (IOException e)
    {
      this.ftp.disconnect();
      e.printStackTrace();
      throw new IOException(e);
    }

  }

  /**
   * Sends (Upload) the file to FTP server. Based on the current date, it
   * automatically changes the working directory so the file is uploaded in
   * correct directory.
   * 
   * @param imgFileToFtp
   * @return true if the file upload is successful
   * @throws IOException
   */
  public boolean sendScreenshotFile(File imgFileToFtp) throws IOException
  {
    boolean succeed = false;

    this.ftp.changeToParentDirectory();

    String directoryToWorkWith = TestRunUtils.getScreenshotUploadFolderName();
    goToDirectory(directoryToWorkWith);

    succeed = transferFile(imgFileToFtp);

    return succeed;

  }

  private boolean transferFile(File imgFileToFtp) throws IOException
  {

    boolean succeed = false;

    // transfer the file
    InputStream in = null;
    try
    {

      // Images are uploaded in binary format
      this.ftp.setFileType(FTP.BINARY_FILE_TYPE);
      String remoteFilename = imgFileToFtp.getName();
      in = new FileInputStream(imgFileToFtp);
      succeed = this.ftp.storeFile(remoteFilename, in);
    }
    catch (FileNotFoundException fne)
    {
      throw new IOException(
          "Invalid filename or screenshot image file is missing ["
              + imgFileToFtp.getName() + "]", fne);
    }
    finally
    {
      if (in != null)
      {
        in.close();
      }
    }
    return succeed;
  }

  /**
   * Sends (Upload) multiple files to FTP server. Based on the current date, it
   * automatically changes the working directory so the file is uploaded in
   * correct directory.
   * 
   * @param screenshots
   * @return true if the file upload is successful
   * @throws IOException
   */
  public boolean sendScreenshotFiles(ArrayList<Screenshot> screenshots)
      throws IOException
  {

    boolean succeed = false;
    boolean allSuceed = true;
    this.ftp.changeToParentDirectory();

    String directoryToWorkWith = TestRunUtils.getScreenshotUploadFolderName();
    goToDirectory(directoryToWorkWith);

    for (Screenshot ss : screenshots)
    {
      File imgFileToFtp = ss.getFile();

      succeed = transferFile(imgFileToFtp);

      if (!succeed && allSuceed)
      {
        allSuceed = false;
      }
    }

    return allSuceed;
  }

  /**
   * Based on the directory name, changes the remote directory to this
   * directory. If the directory is not found, it creates the directory on the
   * server.
   * <P>
   * <B>Assumption:</B> Assumes that all directories are in the FTP Root. The
   * folder structure assumed on ftp server is described below:
   * 
   * <PRE>
   * 		FTPRoot
   * 		|
   * 		|-----01012012 (mmddyyyy format)
   * 		|
   * 		|-----01022012
   * 		|
   * </PRE>
   * 
   * @param directoryName
   * @throws IOException
   */
  public void goToDirectory(String directoryName) throws IOException
  {

    FTPFile[] directories = this.ftp.listDirectories();
    boolean directoryFound = false;

    // Currently we assume that all the image directories are under ftp root.
    // So pathToDirectory is same as name of the directory but this could change
    // in future.
    String pathToDirectory = directoryName;

    for (FTPFile directory : directories)
    {
      if (directory.getName().equals(directoryName))
      {
        directoryFound = true;
        this.ftp.changeWorkingDirectory(pathToDirectory);
        break;
      }
    }

    if (!directoryFound)
    {
      createDirectory(pathToDirectory);
      this.ftp.changeWorkingDirectory(pathToDirectory);
    }

  }

  private void createDirectory(String pathname) throws IOException
  {

    this.ftp.makeDirectory(pathname);

  }

  /**
   * Closes the connection to the FTP server
   */
  public void disconnect()
  {

    try
    {
      this.ftp.disconnect();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      // Do nothing
    }
    finally
    {
      this.ftp = null;
    }
  }

}

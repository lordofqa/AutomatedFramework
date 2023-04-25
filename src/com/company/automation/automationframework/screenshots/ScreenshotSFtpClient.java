package com.company.automation.automationframework.screenshots;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.SftpException;
import com.company.automation.automationframework.profile.AutomationProperties;
import com.company.automation.automationframework.testrail.TestRunUtils;


/**
 * ScreenshotSFtpClient (was a Singleton class) which provides the interface to
 * interact with JSCH library and hides all the painful
 * tasks of connecting to sftp server, creating the folder for file transfer if
 * it does not exist and so on...
 * <P>
 * The sequence of transferring the screenshot sftp files is:
 * 
 * <PRE>
 * ScreenshotSFtpClient ftpclient = ScreenshotSFtpClient();
 * sftpClient.connect();
 * </PRE>
 * 
 * For single file transfer user can call
 * 
 * <PRE>
 * sftpClient.sendScreenshotFile(imgFileToFtp);
 * </PRE>
 * 
 * or for multiple file transfer user can call
 * 
 * <PRE>
 * sftpClient.sendScreenshotFiles(screenshots);
 * </PRE>
 * 
 * at the end when done with sftp, disconnect the sftp server
 * 
 * <PRE>
 * sftpClient.disconnect();
 * </PRE>
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 */

public class ScreenshotSFtpClient
{

 private ChannelSftp sftpChannel = null;
 private Session sftpSession = null;
 private String uploadFolder = null;
	
	/**  
    * Modified this class so that each client has its own
    * instance. Having a singleton client was causing issues when we introduced
    * parallelized tests and threads were fighting over the single resource
    * TODO - Check the performance of this setup and investigate having 
    * a pool of clients if resources are limited
  */
  

 /**
 * 
 * Create new connection  or do nothing if connection already active
 *  
 * @throws Exception
 */
  public void connect() throws Exception
  {

    if (this.sftpChannel == null)
    {
      this.sftpChannel = new ChannelSftp();
    }

    // check if the connection is already active with this host and port, if so
    // do nothing.
    // also check if the connection is dropped. if so, disconnect and re-connect
    if (this.sftpChannel.isConnected()  && this.sftpSession.isConnected())
    {
      return;
    }

    // Now make a new connection
    try
    {
      String imgSftpServerHostUrl = AutomationProperties.
          getProperty("image.sftp.server.host.url");
      int port = Integer.parseInt(AutomationProperties.
          getProperty("image.sftp.server.host.port"));
      String sftpUser = AutomationProperties.
          getProperty("image.sftp.server.host.ftpUser");
      String sftpPassword = AutomationProperties.
          getProperty("image.sftp.server.host.ftpPwd");
      this.uploadFolder = AutomationProperties.
              getProperty("image.sftp.server.upload.folder");
      
      JSch jsch = new JSch();
      sftpSession = jsch.getSession(sftpUser, imgSftpServerHostUrl, port);
      sftpSession.setPassword(sftpPassword);

      // to avoid authentication key problem
      java.util.Properties config = new java.util.Properties();
      config.put("StrictHostKeyChecking", "no");
      sftpSession.setConfig(config);

      sftpSession.connect();
      sftpSession.setTimeout(180);	// set timeout to 3 minutes. FTP
      // client will send
      // NOOP signals to keep ftp
      // connection alive

      Channel channel = sftpSession.openChannel("sftp");
      channel.connect();
      sftpChannel = (ChannelSftp) channel;
    }
    catch (Exception e)
    {
      // If connection attempt failed.
      this.sftpChannel.disconnect();
      this.sftpSession.disconnect();
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
  public boolean sendScreenshotFile(File imgFileToFtp) throws SftpException, IOException
  {
    boolean succeed = false;

    this.sftpChannel.cd(uploadFolder);

    String directoryToWorkWith = TestRunUtils.getScreenshotUploadFolderName();
    goToDirectory(directoryToWorkWith);

    succeed = transferFile(imgFileToFtp);

    return succeed;
    
  }

 /*
 * Transfer single file
 * @param imgFileToFtp
 * @return
 * @throws IOException
 * @throws SftpException
 */
  private boolean transferFile(File imgFileToFtp) throws IOException, SftpException
  {

    boolean succeed = false;

    InputStream in = null;
    try
    {

      // Images are uploaded in binary format
      String remoteFilename = imgFileToFtp.getName();
      in = new FileInputStream(imgFileToFtp);
      this.sftpChannel.put( remoteFilename, remoteFilename);
      // if failed -  SftpException exception thrown 
      succeed =  true;
    }
    catch (FileNotFoundException fne)
    {
      throw new IOException(
          "Invalid filename or screenshot image file is missing ["
              + imgFileToFtp.getName() + "]", fne);
    }
    catch (SftpException sftpE)
    {
    	succeed =  false; 
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
 * @throws SftpException 
   */
  public boolean sendScreenshotFiles(ArrayList<Screenshot> screenshots)
      throws IOException, SftpException
  {

    boolean succeed = false;
    boolean allSucceed = true;
    this.sftpChannel.cd(uploadFolder);

    String directoryToWorkWith = TestRunUtils.getScreenshotUploadFolderName();
    goToDirectory(directoryToWorkWith);

    for (Screenshot ss : screenshots)
    {
      File imgFileToFtp = ss.getFile();

      succeed = transferFile(imgFileToFtp);

      if (!succeed && allSucceed)
      {
        allSucceed = false;
      }
    }

    return allSucceed;
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
 * @throws SftpException 
   */
  public void goToDirectory(String directoryName) throws IOException, SftpException
  {

    boolean directoryFound = false;

    Vector<ChannelSftp.LsEntry> directories = this.sftpChannel.ls("*");
    ArrayList<String> listOfDirectories =  new ArrayList<String>();
    
    for (int i=0;i<directories.size();i++)
    {	
     listOfDirectories.add(directories.get(i).getFilename());
    }
    

    // Currently we assume that all the image directories are under ftp root.
    // So pathToDirectory is same as name of the directory but this could change
    // in future.
    String pathToDirectory = directoryName;

    for (String directory : listOfDirectories)
    {
      if (directory.equals(directoryName))
      {
        directoryFound = true;
        this.sftpChannel.cd(pathToDirectory);
        break;
      }
    }

    if (!directoryFound)
    {
      this.sftpChannel.mkdir(pathToDirectory);
      this.sftpChannel.cd(pathToDirectory);
    }

  }

  /**
   * Closes the connection to the FTP server
   */
  public void disconnect()
  {

    try
    {
      this.sftpChannel.disconnect(); 
      this.sftpSession.disconnect();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      // Do nothing
    }
    finally
    {
      this.sftpChannel = null;
      this.sftpSession = null;
    }
  }
  
}

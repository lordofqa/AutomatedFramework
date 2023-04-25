package com.company.automation.automationframework.utils;

import java.io.File;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * File Utils
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class FileUtils
{
    
 /**
  * open a file for given filename. return exception at the following situations:
  * 1: when filename is empty
  * 2: the filename cannot be located
  * 3: the file does not exist
  * 4: the filename is not in right format.
  * @param filename
  * @return
  * @throws URISyntaxException
  */
 public static File openFile(String filename) throws URISyntaxException
 {
   if (StringUtils.isEmpty(filename))
     throw new RuntimeException("Filename is empty!");
   
   URL url = Thread.currentThread().getContextClassLoader().getResource(filename);
   
   if (url == null)
     throw new RuntimeException("File[" + filename + "] does not exist!");

   File f = new File(url.toURI());
   
   if (!f.exists())
     throw new RuntimeException("File[" + filename + "] does not exist!");
   
   if (!f.canRead())
     throw new RuntimeException("File[" + filename + "] cannot be read!");

   return f;

 }
 
 /**
  * Open a file as an input stream
  * @param filename file to open
  * @return Input stream read from file
  * @throws URISyntaxException
  */
 public static InputStream openFileAsInputStream(String filename) throws URISyntaxException
 {
   if (StringUtils.isEmpty(filename)) {
   	throw new RuntimeException("Filename is empty!");
   }
     
   InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filename);
   if (stream == null) {
       throw new RuntimeException("File[" + filename + "] does not exist!");
   }
   //System.out.println("fileName: " + filename);
	return stream;

 }
 

 /**
  * check if a file exist
  * @param filename
  * @return true -- if exists
  *         false -- if not
  */
 public static boolean exists(String filename) 
 {
   boolean ifExists = true;
   
   URL url = Thread.currentThread().getContextClassLoader().getResource(filename);

   if (url == null)
     ifExists = false;
   
   return ifExists;
 }
 
 /**
  * Return a list of files under the given path. The filename should start with 
  * the given prefix and the file type should be match the given type(file extension)
  * @param path
  * @param prefix
  * @param type
  * @return
  * @throws URISyntaxException
  */
 public static List<File> openFiles(String path, String prefix, String type)
     throws URISyntaxException
 {
   if (StringUtils.isEmpty(path))
     throw new RuntimeException("Path is empty!");

   URL url = Thread.currentThread().getContextClassLoader().getResource(path);

   if (url == null)
     throw new RuntimeException("Path[" + path + "] does not exist!");

   File f = new File(url.toURI());
   if (!f.exists() || !f.isDirectory())
     throw new RuntimeException("Path[" + path + "] does not exist or is not a directory!");

   List<File> fileList = new ArrayList<File>();
   File[] files = f.listFiles();
   for (File file : files)
   {
     if (!StringUtils.isEmpty(prefix)) // Prefix is not empty
     {
       if (file.getName().startsWith(prefix)
           && (StringUtils.isEmpty(type) || file.getName().endsWith(type)))
       {
         fileList.add(file);
       }
     }
     else if (!StringUtils.isEmpty(type)) // Type is not empty
     {
       if (file.getName().endsWith(type) && StringUtils.isEmpty(prefix))
       {
         fileList.add(file);
       }
     }
     else
     // both prefix and type is empty
     {
       fileList.add(file);
     }

   }

   return fileList;
 }

 
 
}

package com.company.automation.automationframework.dataprovider;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

import au.com.bytecode.opencsv.CSVReader;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class CsvDataFetcher
{
 private File      source;
 private CSVReader csv;
 private String[]  header;

 public CsvDataFetcher(File csvFile) throws Exception
 {
  this.source = csvFile;

  // Open the file and get all the data
  this.csv = new CSVReader(new FileReader(this.source));
  this.header = this.csv.readNext();

  boolean isValidHeader = true;
  // validate that all header rows have a valid column name
  for (String column : this.header)
  {
   if (column == null || column.trim().length() == 0)
   {
    isValidHeader = false;
    break;
   }
  }

  if (!isValidHeader)
  {
   throw new Exception(
     "CSV file header row must have valid [non-blank] column names. Please fix the CSV file and try again.");
  }
 }

 public String[] getHeader()
 {
  return this.header;
 }

 /**
  * @return A list of String array of data read from CSV File. This method is
  *         mainly used to prepare quick test case to test a specific UI page
  *         with quick data.
  * 
  *         <P>
  *         <B>NOTE:</B> This method should not be used for automated data
  *         driven tests.
  * 
  * @throws Exception
  */
 public ArrayList<String[]> getStringArrayList() throws Exception
 {

  ArrayList<String[]> arList = new ArrayList<String[]>();

  String[] nextLine;

  // Go through test data and create object array
  while ((nextLine = this.csv.readNext()) != null)
  {

   arList.add(nextLine);
  }

  // Close csv related resources
  this.csv.close();
  return arList;

 }

 /**
  * @return An arraylist of hashmap data read from CSV file. Each hashmap is
  *         basically a mapping of column header vs column value for a
  *         particular row
  * @throws Exception
  */
 public ArrayList<HashMap<String, String>> getHashmapList() throws Exception
 {

  ArrayList<HashMap<String, String>> arMap = new ArrayList<HashMap<String, String>>();

  String[] nextLine;

  // Go through test data and create object array
  while ((nextLine = this.csv.readNext()) != null)
  {
   HashMap<String, String> hm = new LinkedHashMap<String, String>();

   if (this.header.length < nextLine.length)
   {
    String exceptionMessage = "Incorrect CSV format / Data not matched to a heading "
      + Arrays.deepToString(nextLine);
    throw new Exception(exceptionMessage);
   }

   for (int i = 0; i < nextLine.length; i++)
   {
    hm.put(this.header[i], nextLine[i]);
   }

   arMap.add(hm);
  }

  // Close csv related resources
  this.csv.close();
  return arMap;

 }

 /**
  * @param mappingColumnName
  *         Mapping column name is a key reference between the input data file
  *         and the master data file
  * @return Map of the csv data. mappingColumnName is used as a key
  * @throws Exception
  */
 public HashMap<String, HashMap<String, String>> getMapOfHashmap(
   String mappingColumnName) throws Exception
 {

  HashMap<String, HashMap<String, String>> arMap = new HashMap<String, HashMap<String, String>>();

  String[] nextLine;

  // Go through test data and create object array
  while ((nextLine = this.csv.readNext()) != null)
  {
   // for each line in the master csv
   HashMap<String, String> hm = new LinkedHashMap<String, String>();
   // hashmap of (<column headers>, <value under that column>
   for (int i = 0; i < nextLine.length; i++)
   {
    hm.put(this.header[i], nextLine[i]);
   }

   // Get the mapping column Value
   String columnValue = hm.get(mappingColumnName);
   arMap.put(columnValue, hm); // returns a hashmap of each value under
   // its that key column vs.
   // the hashmap of (<column headers>,
   // <value under that column>)
  }

  return arMap;

 }

}

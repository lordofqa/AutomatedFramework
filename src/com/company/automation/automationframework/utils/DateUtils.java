package com.company.automation.automationframework.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import com.company.automation.automationframework.addhoclauncher.CalendarFieldEnum;

/**
 * Utility class to deal with dates 
 * 
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 */

public class DateUtils
{
	
 public static String getDateAsString(Date date, String format)
 {
  return new SimpleDateFormat(format).format(date);
 }	

 /*
 * Formats the date in MM/dd/yyyy format (Without HH:mm)
 * 
 * @param date
 * @return String Date in MM/dd/yyyy format (Without HH:mm)
 */
 public static String getDateInMMddyyyy(Date date)
 {
   return getDateAsString(date, "MM/dd/yyyy");
 }
 
 /**
  * Gets timestamp which differs from timestamp on the field given as parameter
  * It will differ by the amount in the difference parameter. For example:
  * getTimeStampFromNow(CalendarFieldEnum.YEAR, -2) will return a timestamp 2
  * years before current time
  * 
  * @param field
  * @param difference
  *          - can be positive or negative
  * @return time stamp which differs by 'difference' param and on the 'field'
  *         param
  */
 public static Date getTimeStampFromNow(CalendarFieldEnum field, int difference)
 {
   Calendar cal = Calendar.getInstance();
   cal.add(field.getId(), difference);
   return cal.getTime();
 }
 
 /**
 * @param date
 * @return
 */
 public static String getDateInYYYY_MM_dd(Date date)
 {
   return getDateAsString(date, "yyyy-MM-dd");
 }

 
}
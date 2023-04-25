package com.company.automation.automationframework.addhoclauncher;

import java.security.InvalidParameterException;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public enum CalendarFieldEnum
{
 SECOND("s", Calendar.SECOND), 
 MINUTE("mi", Calendar.MINUTE), 
 HOUR("h", Calendar.HOUR), 
 DAY("d", Calendar.DATE), 
 MONTH("m", Calendar.MONTH), 
 YEAR("y", Calendar.YEAR);

 private String representation;
 private int    id;

 private static final HashMap<String, CalendarFieldEnum> lookup = new HashMap<String, CalendarFieldEnum>();

 static
 {
  for (CalendarFieldEnum field : CalendarFieldEnum.values())
  {
   lookup.put(field.getRepresentation(), field);
  }
 }

 /**
  * Return Enum of the field represented by the parameter
  * 
  * @param fieldRep
  * @return
  */
 public static CalendarFieldEnum get(String fieldRep)
 {
  if (isValidCalendarField(fieldRep))
  {
   return lookup.get(fieldRep);
  }
  else
  {
   throw new InvalidParameterException("Invalid Calendar field: " + fieldRep);
  }

 }

 private static boolean isValidCalendarField(String fieldRep)
 {
  return lookup.containsKey(fieldRep);
 }

 CalendarFieldEnum(String rep, int id)
 {
  this.representation = rep;
  this.id = id;
 }

 public int getId()
 {
  return id;
 }

 public String getRepresentation()
 {
  return representation;
 }

}

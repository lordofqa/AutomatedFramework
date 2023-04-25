package com.company.automation.automationframework.testlog;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ListIterator;

import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;

import com.company.automation.automationframework.screenshots.ScreenState;
import com.company.automation.automationframework.screenshots.Screenshot;

/**
 * A static logging tool that automatically keeps track of what tests are being
 * executed by what threads, and allows for parsing of the global log specific
 * to an individual test at a later point. The beginning of a test must be
 * initialized using a unique test identifier, and should be deinitialized after
 * the test is complete in order to prevent that same thread from logging
 * actions against the test accidentally.
 *
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class TestLog
{

 private static ArrayList<Entry>     logEntries             = new ArrayList<Entry>();
 private static ThreadLocal<Boolean> beVerbose              = new ThreadLocal<Boolean>();
 private static ThreadLocal<Integer> stepDelay              = new ThreadLocal<Integer>();
 private static ThreadLocal<Boolean> failureScreenShotTaken = new ThreadLocal<Boolean>();

 public static final int             STEP                   = 0;
 public static final int             DEBUG                  = 1;
 public static final int             INIT                   = 2;
 public static final int             SCREENSHOT             = 3;
 public static final int             DONE                   = 4;
 public static final int             SERVER                 = 5;
 public static final int             ORGCODE                = 6;
 public static final int             FACILITY               = 7;
 public static final int             APP_VERSION            = 8;
 public static final int             USERNAME               = 9;
 public static final int             SCENARIO_ID            = 10;
 public static final int             VERIFY                 = 11;
 public static final int             CONSOLE                = 12;
 public static final int             BROWSER                = 13;
 public static final int             REMOTE                 = 14;

 /**
  * Initialize the test logger to tell it that the current thread will be
  * running the given test
  * 
  * @param uniqueLogId
  */
 public static void initialize(String uniqueLogId)
 {
  beVerbose.set(false);
  failureScreenShotTaken.set(false);
  newEntry(uniqueLogId, INIT);
 }

 /**
  * Deinitialize the test logger from the given test for the current thread.
  * This should be done after the test is complete to prevent accidental logging
  * against the test thread.
  * 
  * @param uniqueLogId
  */
 public static void done(String uniqueLogId)
 {
  newEntry(uniqueLogId, DONE);
  beVerbose.set(false);
  failureScreenShotTaken.set(false);
 }

 /**
  * Log a step for future debugging or failure analysis
  * 
  * @param step
  */
 public static void step(String step)
 {
  newEntry(step, STEP);
  if (stepDelay.get() != null && stepDelay.get().intValue() > 0)
  {
   try
   {
    Thread.sleep(stepDelay.get().intValue());
   }
   catch (InterruptedException e)
   {
    // ignore
   }
  }
 }

 /**
  * Log a verification for future debugging or failure analysis
  * 
  * @param verification
  */
 public static void verify(String verify)
 {
  newEntry(verify, VERIFY);
 }

 /**
  * Log a piece of console information. Not used externally at the moment.
  * 
  * @param verification
  */
 @SuppressWarnings("unused")
 private static void console(String log)
 {
  newEntry(log, CONSOLE);
 }

 /**
  * Log debug information for future debugging or failure analysis
  * 
  * @param step
  */
 public static void debug(String debug)
 {
  newEntry(debug, DEBUG);
 }

 public static void server(String server)
 {
  newEntry(server, SERVER);
 }

 public static void orgCode(String orgCode)
 {
  newEntry(orgCode, ORGCODE);
 }

 public static void facility(String facility)
 {
  newEntry(facility, FACILITY);
 }

 public static void appVersion(String appVersion)
 {
  newEntry(appVersion, APP_VERSION);
 }

 public static void userName(String userName)
 {
  newEntry(userName, USERNAME);
 }

 public static void scenarioId(String scenarioId)
 {
  newEntry(scenarioId, SCENARIO_ID);
 }

 public static void browser(String browser)
 {
  newEntry(browser, BROWSER);
 }

 public static void remote(String remote)
 {
  newEntry(remote, REMOTE);
 }

 /**
  * @param action
  * @param logType
  */
 private static void newEntry(String action, int logType)
 {
  synchronized (logEntries)
  {
   Entry entry = new Entry(logType, action);
   logEntries.add(entry);
   if (beVerbose.get() != null && beVerbose.get()) System.out.println(entry);
  }
 }

 public static void screenshot(File scr)
 {
  screenshot(scr, "");
 }

 public static void screenshot(ScreenState scr)
 {
  screenshot(scr, "");
 }

 /**
  * Take a temporary screenshot which can be accessed at a later point for
  * debugging purposes. The screenshot will be deleted upon exit unless copied
  * for use elsewhere.
  * 
  * @param scr
  *         File pointing to the screenshot
  * @param description
  *         Description of what is being captured
  * @param lastScreenshot
  *         true if the screenshot being created is last (failure) screenshot
  */
 public static void screenshot(File scr, String description)
 {
  synchronized (logEntries)
  {

   // If the image file is null, there is no need to create entry in testlog.
   // It's meaningless
   if (scr != null)
   {
    Entry entry = new Entry(SCREENSHOT, scr, description);
    logEntries.add(entry);
    if (beVerbose.get() != null && beVerbose.get()) System.out.println(entry);
   }
   else
   {

    Entry entry = new Entry(STEP, description + " - Capturing Screenshot Failed!");
    logEntries.add(entry);

    if (beVerbose.get() != null && beVerbose.get()) System.out.println("Skipping adding screenshot to log as image file is NULL");
   }
  }
 }

 /**
  * Take a temporary screenshot which can be accessed at a later point for
  * debugging purposes. The screenshot will be deleted upon exit unless copied
  * for use elsewhere.
  * 
  * @param scr
  *         File pointing to the screenshot
  * @param description
  *         Description of what is being captured
  * @param lastScreenshot
  *         true if the screenshot being created is last (failure) screenshot
  */
 public static void screenshot(ScreenState scr, String description)
 {
  synchronized (logEntries)
  {

   // For each alert, record it, accept it and move on.
   // When no more alerts exist, we can finally take a screen shot
   String alert = scr.popAlert();
   while (alert != null)
   {
    Entry entry = new Entry(DEBUG, description + " ALERT PRESENT - " + alert);
    logEntries.add(entry);
    alert = scr.popAlert();
   }

   if (scr.getScreenShot() != null)
   {
    Entry entry = new Entry(SCREENSHOT, scr.getScreenShot(), description);
    logEntries.add(entry);
    if (beVerbose.get() != null && beVerbose.get()) System.out.println(entry);
   }
   else
   {

    Entry entry = new Entry(STEP, description + " - Capturing Screenshot Failed!");
    logEntries.add(entry);

    if (beVerbose.get() != null && beVerbose.get()) System.out.println("Skipping adding screenshot to log as image file is NULL");
   }

   LogEntries logs = scr.getConsoleLog();
   if (logs != null) 
   for (LogEntry logEntry : logs)
   {
    if (!(logEntry.getMessage().contains("callHelp") // TODO remove once they
                                                     // fix this
    || logEntry.getMessage().contains("WePocImageServer"))) // QA image server
                                                            // out of sync
    {
     Entry entry = new Entry(CONSOLE, logEntry.getMessage());
     logEntries.add(entry);
    }
   }
  }
 }

 /**
  * Provides the full log for the given test unique id
  * 
  * @param uniqueLogId
  * @return list of log entries
  */
 public static ArrayList<Entry> getLogEntries(String uniqueLogId)
 {
  boolean inTest = false;
  long threadId = -1;
  ArrayList<Entry> entries = new ArrayList<Entry>();

  for (int i = 0; i < logEntries.size(); i++)
  {
   Entry entry = logEntries.get(i);

   if (inTest && threadId == entry.getThreadId())
   {
    // End of test, hand it off to someone else
    if (entry.getType() == INIT || entry.getType() == DONE)
    {
     break;
    }
    // Capture this as part of the log
    else
    {
     entries.add(entry);
    }
   }
   // Dedicate this thread to this test
   else
   {
    if (entry.getType() == INIT )
    {
     threadId = entry.getThreadId();
     inTest = true;
    }
   }
  }

  // Now we have the thread id, go back through the log and get any
  // setup log entries that came directly before this initialization
  ArrayList<Entry> setup = new ArrayList<Entry>();
  inTest = false;

  for (int i = 0; i < logEntries.size(); i++)
  {

   Entry entry = logEntries.get(i);

   // if thread id is the one we're looking for
   if (entry.getThreadId() == threadId)
   {

    // if we're in a test outside of our own
    if (inTest)
    {
     if (entry.getType() == DONE)
     {
      inTest = false;
      continue;
     }
     else if (entry.getType() != INIT)
     {
      continue;
     }
     else if (entry.getType() == INIT)
     {
      inTest = false;
     }
    }
    // if we're initialized by a version of our test
    if (entry.getType() == INIT && stripParameters(entry.getAction()).equals(stripParameters(uniqueLogId)))
    {
     // prepend onto our output
     setup.addAll(entries);
     entries = setup;
     break;
    }
    // if we're initialized by another test
    else if (entry.getType() == INIT)
    {
     // drop the entries
     setup.clear();
     inTest = true;
    }
    // otherwise its probably a setup step for our test
    else
    {
     // capture each entry
     setup.add(entry);
    }
   }
  }

  return entries;
 }

 public static void removeLogEntries(String uniqueLogId)
 {
  removeLogEntries(uniqueLogId, -1);
 }

 /**
  * Remove log entries for the given test unique id
  * 
  * @param uniqueLogId
  */
 public static void removeLogEntries(String uniqueLogId, long threadId)
 {

  if (threadId == -1)
  {
   // Initialize threadId,using the first match
   for (int i = 0; i < logEntries.size(); i++)
   {
    Entry entry = logEntries.get(i);
    if (entry.getType() == INIT && entry.getAction().equals(uniqueLogId))
    {
     if (threadId == -1)
     {
      threadId = entry.getThreadId();
      break;
     }
    }
   }
  }

  synchronized (logEntries)
  {
   for (ListIterator<Entry> it = logEntries.listIterator(logEntries.size()); it.hasPrevious();)
   {
    Entry entry = it.previous();

    // if thread id is the one we're looking for
    if (entry.getThreadId() == threadId)
    {
     it.remove();
    }
   }
  }
 }

 public static String getLogAsText(String uniqueLogId)
 {
  return entriesToString(getLogEntries(uniqueLogId));
 }

 public static ArrayList<String> getLogList(String uniqueLogId)
 {

  ArrayList<String> logList = new ArrayList<String>();

  ArrayList<Entry> entries = getLogEntries(uniqueLogId);
  int i = 0;
  for (Entry entry : entries)
  {
   i += (entry.getType() == STEP) ? 1 : 0;
   logList.add(entry.toString(i));
  }
  return logList;
 }

 public static String stripParameters(String test)
 {
  return test.replaceAll("\\[[-0123456789]+\\]", "");
 }

 /**
  * Returns the entire log, inclusive of all running tests in entered order
  * 
  * @return String
  */
 public static String getEntireLog()
 {
  ArrayList<Entry> entries = new ArrayList<Entry>();

  for (Entry entry : logEntries)
  {
   entries.add(entry);
  }
  return entriesToString(entries);
 }

 /**
  * Given an array list of entries, prints out those entries
  * 
  * @param entries
  * @return
  */
 public static String entriesToString(ArrayList<Entry> entries)
 {
  String out = "";
  int i = 0;
  for (Entry entry : entries)
  {
   i += (entry.getType() == STEP) ? 1 : 0;
   out += entry.toString(i) + "\n";
  }
  return out;
 }

 /**
  * Allows you to set the verbosity. Passing true into this function will cause
  * all logged logEntries to be printed to the console automatically. This
  * setting is test specific, and is reset upon the beginning of a new test
  * method.
  * 
  * VERBOSITY SHOULD BE SET BY CONFIGURATION OPTION
  * 
  * @param verbose
  */
 public static void setVerbose(String verbose)
 {
  if ("I_KNOW_THIS_SHOULD_ONLY_BE_SET_BY_CONFIGURATION_OPTIONS".equals(verbose)) beVerbose.set(true);
  else beVerbose.set(false);
 }

 /**
  * Return verbosity of test log
  * 
  * @return true means verbose
  */
 public static boolean getVerbose()
 {
  return beVerbose.get();
 }

 /**
  * Delay each logged step by a number of milliseconds to facilitate a more user
  * observable test
  * 
  * @param delay
  *         seconds to delay each step
  */
 public static void delaySteps(int delay)
 {
  stepDelay.set(new Integer(delay * 1000));
 }

 /**
  * Flag to prevent multiple Listeners storing the same failure screenshot in
  * TestLog multiple times
  * 
  * @param flag
  */
 public static void setFailureScreenShotTaken(boolean flag)
 {
  failureScreenShotTaken.set(flag);
 }

 public static boolean getFailureScreenShotTaken()
 {
  if (failureScreenShotTaken.get() != null) return failureScreenShotTaken.get();
  else return false;
 }

 /**
  * Gets screenshots initiated by the test as snapshots for later debugging
  * 
  * @param uniqueLogId
  *         Id to uniquely identify the test
  * @return array of files representing screenshots from that run
  */
 public static ArrayList<Screenshot> getScreenShots(String uniqueLogId)
 {
  ArrayList<Screenshot> screenshots = new ArrayList<Screenshot>();

  ArrayList<Entry> entries = getLogEntries(uniqueLogId);

  for (int i = 0; i < entries.size(); i++)
  {
   Entry entry = entries.get(i);
   if (entry.getType() == SCREENSHOT)
   {
    screenshots.add(entry.getScreenshot());
   }
  }

  return screenshots;
 }

 /**
  * Check for entry, if null, return null
  * 
  * @param uniqueLogId
  * @param entryType
  * @return
  */
 private static String getEntryOrNull(String uniqueLogId, int entryType)
 {
  Entry e = getEnvironmentEntry(uniqueLogId, entryType);
  return e == null ? null : e.getAction();
 }

 public static String getServer(String uniqueLogId)
 {
  return getEntryOrNull(uniqueLogId, SERVER);
 }

 public static String getOrgCode(String uniqueLogId)
 {
  return getEntryOrNull(uniqueLogId, ORGCODE);
 }

 public static String getAppVersion(String uniqueLogId)
 {
  return getEntryOrNull(uniqueLogId, APP_VERSION);
 }

 public static String getFacility(String uniqueLogId)
 {
  return getEntryOrNull(uniqueLogId, FACILITY);
 }

 public static String getUser(String uniqueLogId)
 {
  return getEntryOrNull(uniqueLogId, USERNAME);
 }

 public static String getScenarioId(String uniqueLogId)
 {
  return getEntryOrNull(uniqueLogId, SCENARIO_ID);
 }

 private static Entry getEnvironmentEntry(String uniqueLogId, int entryType)
 {
  ArrayList<Entry> entries = getLogEntries(uniqueLogId);
  for (int i = 0; i < entries.size(); i++)
  {
   Entry entry = entries.get(i);
   if (entry.getType() == entryType)
   {
    return entry;
   }
  }
  return null;
 }

 /**
  *
  *         An inner Step class, tracking information about each step
  *         @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
  *
  */
 public static class Entry
 {
  private int  logType;
  private long threadId;
  String       action;
  Date         timeStamp;
  int          stepNumber = 0;
  Screenshot   screenshot;

  public Entry(int logType, String action)
  {
   this.logType = logType;
   this.action = action;
   this.timeStamp = new Date();
   this.threadId = Thread.currentThread().getId();
  }

  public Entry(int logType, File scr, String action)
  {
   this.logType = logType;
   this.action = action;
   this.timeStamp = new Date();
   this.threadId = Thread.currentThread().getId();

   Screenshot ss = new Screenshot();
   ss.setFile(scr);
   ss.setOriginalFileName(scr.getName());
   scr.deleteOnExit(); // ensure we kill the screenshot once we're done
   ss.setDescription(action);
   ss.setTimeTakenAt(new Date());
   ss.setFileUrl(null); // TODO: set the url based on server configuration.
                        // Functionality to add
                        // later
   this.screenshot = ss;
  }

  public long getThreadId()
  {
   return this.threadId;
  }

  public String getAction()
  {
   return this.action;
  }

  public int getType()
  {
   return this.logType;
  }

  public Screenshot getScreenshot()
  {
   return this.screenshot;
  }

  public String toString(int stepNum)
  {
   this.stepNumber = stepNum;

   String entryStr = "";
   String timestamp = new SimpleDateFormat("HH:mm:ss").format(this.timeStamp);
   entryStr = timestamp + " " + this.threadId + " " + this.logTypeToPrint() + " " + this.action;
   return entryStr;
  }

  public String toString()
  {
   return toString(0);
  }

  private String logTypeToPrint()
  {
   if (this.logType == STEP)
   {
    if (this.stepNumber == 0)
    {
     return "[STEP]";
    }
    else
    {
     return "[STEP " + this.stepNumber + "]";
    }
   }
   else if (this.logType == VERIFY)
   {
    return "[VERIFY]";
   }
   else if (this.logType == DEBUG)
   {
    return "[DEBUG]";
   }
   else if (this.logType == INIT)
   {
    return "[INIT]";
   }
   else if (this.logType == SCREENSHOT)
   {
    return "[SCREENSHOT]";
   }
   else if (this.logType == DONE)
   {
    return "[DONE]";
   }
   else if (this.logType == SERVER)
   {
    return "[SERVER]";
   }
   else if (this.logType == ORGCODE)
   {
    return "[ORGCODE]";
   }
   else if (this.logType == FACILITY)
   {
    return "[FACILITY]";
   }
   else if (this.logType == APP_VERSION)
   {
    return "[VERSION]";
   }
   else if (this.logType == USERNAME)
   {
    return "[USER]";
   }
   else if (this.logType == SCENARIO_ID)
   {
    return "[SCENARIO]";
   }
   else if (this.logType == CONSOLE)
   {
    return "[CONSOLE]";
   }
   else if (this.logType == BROWSER)
   {
    return "[BROWSER]";
   }
   else if (this.logType == REMOTE)
   {
    return "[REMOTE]";
   }
   return "";
  }
 }
}
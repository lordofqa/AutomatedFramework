/**
 * 
 */
package com.company.automation.automationframework.addhoclauncher;

import java.util.Random;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
public class NumberUtils
{

 /**
  * Generate random number of the given length
  * 
  * @param charLength
  * @return Random number
  */
 public static String generateRandomNumber(int charLength)
 {
  return String.valueOf(charLength < 1 ? 0 : new Random().nextInt((9 * (int) Math.pow(10, charLength - 1)) - 1) + (int) Math.pow(10, charLength - 1));
 }

}

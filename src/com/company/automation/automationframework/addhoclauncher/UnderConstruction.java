/**
 * 
 */
package com.company.automation.automationframework.addhoclauncher;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author lordofqa & ARTIFICIAL INTELLIGENCE SOLUTIONS(http://aarde.ai/)
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface UnderConstruction
{
 String description() default "";
}

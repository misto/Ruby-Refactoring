/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.rubypeople.rdt.core.parser;

/**
 * Description of a Ruby problem, as detected by the compiler or some of the
 * underlying technology reusing the compiler. A problem provides access to:
 * <ul>
 * <li> its location (originating source file name, source position, line
 * number), </li>
 * <li> its message description and a predicate to check its severity (warning
 * or error). </li>
 * <li> its ID : an number identifying the very nature of this problem. All
 * possible IDs are listed as constants on this interface. </li>
 * </ul>
 * 
 * Note: the compiler produces IProblems internally, which are turned into
 * markers by the RubyBuilder so as to persist problem descriptions. This
 * explains why there is no API allowing to reach IProblem detected when
 * compiling. However, the Ruby problem markers carry equivalent information to
 * IProblem, in particular their ID (attribute "id") is set to one of the IDs
 * defined on this interface.
 * 
 * @since 2.0
 */
public interface IProblem {

	/**
	 * Answer a localized, human-readable message string which describes the
	 * problem.
	 * 
	 * @return a localized, human-readable message string which describes the
	 *         problem
	 */
	String getMessage();

	/**
	 * Answer the file name in which the problem was found.
	 * 
	 * @return the file name in which the problem was found
	 */
	char[] getOriginatingFileName();

	/**
	 * Answer the end position of the problem (inclusive), or -1 if unknown.
	 * 
	 * @return the end position of the problem (inclusive), or -1 if unknown
	 */
	int getSourceEnd();

	/**
	 * Answer the line number in source where the problem begins.
	 * 
	 * @return the line number in source where the problem begins
	 */
	int getSourceLineNumber();

	/**
	 * Answer the start position of the problem (inclusive), or -1 if unknown.
	 * 
	 * @return the start position of the problem (inclusive), or -1 if unknown
	 */
	int getSourceStart();

	/**
	 * Checks the severity to see if the Error bit is set.
	 * 
	 * @return true if the Error bit is set for the severity, false otherwise
	 */
	boolean isError();

	/**
	 * Checks the severity to see if the Warning bit is not set.
	 * 
	 * @return true if the Warning bit is not set for the severity, false
	 *         otherwise
	 */
	boolean isWarning();
	
	/**
	 * Checks the severity to see if the Task bit is not set.
	 * 
	 * @return true if the Task bit is not set for the severity, false
	 *         otherwise
	 */
	boolean isTask();

}
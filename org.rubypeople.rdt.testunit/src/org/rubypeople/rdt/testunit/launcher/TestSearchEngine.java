/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.testunit.launcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;
import org.rubypeople.rdt.internal.core.parser.ast.RubyScript;

/**
 * @author Chris
 *  
 */
public class TestSearchEngine {

	public static RubyElement[] findTests(final IFile file) {
		try {
			// TODO Check each class to see if its a descendant of TestCase or TestSuite
			String contents = readFile(file);
			RubyScript script = RubyParser.parse(contents);

			List classes = getClasses(script.getElements());
			Object[] classesArray = classes.toArray();
			RubyElement[] outArray = new RubyElement[classesArray.length];
			System.arraycopy(classesArray, 0, outArray, 0, classesArray.length);

			return outArray;
		} catch (ParseException e) {} catch (CoreException e) {} catch (IOException e) {}
		return new RubyElement[0];
	}

	/**
	 * @param script
	 * @return
	 */
	private static List getClasses(Object[] elements) {
		List classes = new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			RubyElement element = (RubyElement) elements[i];
			if (element.isType(RubyElement.CLASS)) classes.add(element);
			classes.addAll(getClasses(element.getElements()));
		}
		return classes;
	}

	/**
	 * @param file
	 * @return
	 * @throws CoreException
	 * @throws IOException
	 */
	private static String readFile(IFile file) throws CoreException, IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(file.getContents()));
		StringBuffer buffer = new StringBuffer();
		String line = null;
		while ((line = reader.readLine()) != null) {
			buffer.append(line);
			buffer.append("\n");
		}
		return buffer.toString();
	}

}
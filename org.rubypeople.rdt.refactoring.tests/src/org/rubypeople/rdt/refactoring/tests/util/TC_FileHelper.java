/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
 * Copyright (C) 2007 Mirko Stocker <me@misto.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.refactoring.tests.util;

import org.rubypeople.rdt.refactoring.util.FileHelper;

import junit.framework.TestCase;


public class TC_FileHelper extends TestCase {

	private static final String LF = "\n";
	private static final String CR = "\r";
	private static final String EMPTY_DOCUMENT = "";
	private static final String TEST_DOCUMENT_LF = "class Triangle\n\tdef hypotenuse a, b\n\t\tcSquare = a ** 2 + b ** 2\n\tcSquare ** 0.5\n\tend\nend";
	private static final String TEST_DOCUMENT_CR = "class Triangle\r\tdef hypotenuse a, b\r\t\tcSquare = a ** 2 + b ** 2\r\tcSquare ** 0.5\r\tend\rend";
	private static final String TEST_DOCUMENT_CRLF = "class Triangle\r\n\tdef hypotenuse a, b\r\n\t\tcSquare = a ** 2 + b ** 2\r\n\tcSquare ** 0.5\r\n\tend\r\nend";
	private static final String TEST_DOCUMENT_MIXED = "class Triangle\r\n\tdef hypotenuse a, b\n\t\tcSquare = a ** 2 + b ** 2\r\tcSquare ** 0.5\n\tend\r\nend";
	
	public void testGetLineDelimiter() {
		testDelimiter(EMPTY_DOCUMENT, FileHelper.DEFAULT_LINE_DELIMITER);
		testDelimiter(TEST_DOCUMENT_LF, LF);
		testDelimiter(TEST_DOCUMENT_CR, CR);
		testDelimiter(TEST_DOCUMENT_CRLF, CR + LF);
		testDelimiter(TEST_DOCUMENT_MIXED, CR + LF);
	}

	private void testDelimiter(String document, String expectedDelimiter){
		String resultDelimiter = FileHelper.getLineDelimiter(document);
		assertEquals(expectedDelimiter, resultDelimiter);
	}
	
	public void testFileNameFromPath() {
		assertEquals("file", FileHelper.getFileNameFromPath("file"));
		assertEquals("file.rb", FileHelper.getFileNameFromPath("file.rb"));
		assertEquals("file.rb", FileHelper.getFileNameFromPath("/file.rb"));
		assertEquals("file.rb", FileHelper.getFileNameFromPath("/dir/file.rb"));
		assertEquals("file.rb", FileHelper.getFileNameFromPath("L/dir/file.rb"));
	}
}

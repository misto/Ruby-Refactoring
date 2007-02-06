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
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
 * Copyright (C) 2006 Thomas Corbat <tcorbat@hsr.ch>
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

package org.rubypeople.rdt.refactoring.tests.core.overridemethod;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.text.edits.MalformedTreeException;
import org.rubypeople.rdt.refactoring.core.overridemethod.MethodsOverrider;
import org.rubypeople.rdt.refactoring.documentprovider.StringDocumentProvider;
import org.rubypeople.rdt.refactoring.tests.FilePropertyData;
import org.rubypeople.rdt.refactoring.tests.FileTestData;
import org.rubypeople.rdt.refactoring.tests.TwoLayerTreeEditProviderTester;

public class OverrideMethodTester extends TwoLayerTreeEditProviderTester {

	private String testName;

	public OverrideMethodTester(String fileName) {
		super(true);
		testName = fileName;
	}

	@Override
	public void runTest() throws FileNotFoundException, IOException, MalformedTreeException, BadLocationException {
		FileTestData testData;
		testData = new FileTestData(testName, getClass());
		StringDocumentProvider docProvider = new StringDocumentProvider(testData.getFileName(), testData.getActiveFileContent());
		String superClassFileName = testData.getProperty("superclassfilename");
		docProvider.addFile(superClassFileName, testData.getFileContent(superClassFileName));
		MethodsOverrider overrider = new MethodsOverrider(docProvider);
		Collection<String> strSelections = testData.getNumberedProperty("selection");
		for (String aktSelection : strSelections) {
			String[] selection = FilePropertyData.seperateString(aktSelection);
			if (selection.length == 2) {
				addSelection(selection[0], selection[1]);
			} else if (selection.length == 1) {
				addSelection(selection[0]);
			} else {
				fail();
			}
		}
		createEditAndCompareResult(testData.getSource(), testData.getExpectedResult(), overrider);
	}

	@Override
	public String getName() {
		return testName;
	}
}

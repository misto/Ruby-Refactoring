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
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
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

package org.rubypeople.rdt.refactoring.tests.core.renameclass;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.rubypeople.rdt.refactoring.core.renameclass.RenameClassConditionChecker;
import org.rubypeople.rdt.refactoring.core.renameclass.RenameClassConfig;
import org.rubypeople.rdt.refactoring.core.renameclass.RenameClassEditProvider;
import org.rubypeople.rdt.refactoring.core.renameclass.RenameClassFileNameChangeProvider;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentWithIncluding;
import org.rubypeople.rdt.refactoring.tests.FileTestCase;
import org.rubypeople.rdt.refactoring.tests.MultiFileTestData;

public class ClassRenameTester extends FileTestCase {
	
	public ClassRenameTester(String fileName) {
		super(fileName);
	}
	
	@Override
	public void runTest() throws FileNotFoundException, IOException, BadLocationException {
		
		MultiFileTestData testData = new MultiFileTestData(getName());
		
		int caretPosition = testData.getIntProperty("pos");
		
		RenameClassConfig renameClassConfig = new RenameClassConfig(new DocumentWithIncluding(testData), caretPosition);
		new RenameClassConditionChecker(renameClassConfig);
	
		renameClassConfig.setNewName(testData.getProperty("name"));
		
		RenameClassEditProvider editProvider = new RenameClassEditProvider(renameClassConfig);
		
		if (testData.hasProperty("newFileName")) {
			RenameClassFileNameChangeProvider fileNameChangeProvider = new RenameClassFileNameChangeProvider(renameClassConfig);
			
			ArrayList<String> filesThatChanged = new ArrayList<String>();
			filesThatChanged.add(testData.getActiveFileName());
			
			for (Map.Entry<String, String> tuple : fileNameChangeProvider.getFilesToRename(filesThatChanged).entrySet()) {
				assertEquals(testData.getActiveFileName(), tuple.getKey());
				assertEquals(testData.getProperty("newFileName"), tuple.getValue());
			}
		}
		
		checkMultiFileEdits(editProvider, testData);
	}
}

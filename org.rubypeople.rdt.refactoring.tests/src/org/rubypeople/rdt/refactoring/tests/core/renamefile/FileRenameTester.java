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

package org.rubypeople.rdt.refactoring.tests.core.renamefile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.rubypeople.rdt.refactoring.core.IRefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.renamefile.RenameFileConditionChecker;
import org.rubypeople.rdt.refactoring.core.renamefile.RenameFileConfig;
import org.rubypeople.rdt.refactoring.core.renamefile.RenameFileEditProvider;
import org.rubypeople.rdt.refactoring.core.renamefile.RenameFileNameChangeProvider;
import org.rubypeople.rdt.refactoring.tests.MultiFileTestData;
import org.rubypeople.rdt.refactoring.tests.RefactoringTestCase;

public class FileRenameTester extends RefactoringTestCase {
	public FileRenameTester(String fileName) {
		super(fileName);
	}

	@Override
	public void runTest() throws FileNotFoundException, IOException, BadLocationException {
		MultiFileTestData testData = new MultiFileTestData(getName());

		RenameFileConfig config = new RenameFileConfig(testData, testData.getActiveFileName());
		IRefactoringConditionChecker checker = new RenameFileConditionChecker(config);
		
		if(!checker.shouldPerform()) {
			fail();
		}
		
		RenameFileEditProvider editProvider = new RenameFileEditProvider(config);
		config.setNewName(testData.getProperty("newName"));
		
		RenameFileNameChangeProvider fileNameChangeProvider = new RenameFileNameChangeProvider(config);
		for (Map.Entry<String, String> tuple : fileNameChangeProvider.getFilesToRename().entrySet()) {
			assertEquals(testData.getActiveFileName(), tuple.getKey());
			assertEquals(config.getNewName(), tuple.getValue());
		}
		
		checkMultiFileEdits(editProvider, testData);
	}
}

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

package org.rubypeople.rdt.refactoring.core.rename;

import org.eclipse.jface.wizard.IWizardPage;
import org.rubypeople.rdt.refactoring.core.IRefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.ITextSelectionProvider;
import org.rubypeople.rdt.refactoring.core.RubyRefactoring;
import org.rubypeople.rdt.refactoring.core.renameclass.RenameClassRefactoring;
import org.rubypeople.rdt.refactoring.core.renamefield.RenameFieldRefactoring;
import org.rubypeople.rdt.refactoring.core.renamefile.RenameFileRefactoring;
import org.rubypeople.rdt.refactoring.core.renamelocal.RenameLocalRefactoring;
import org.rubypeople.rdt.refactoring.core.renamemethod.RenameMethodRefactoring;
import org.rubypeople.rdt.refactoring.core.renamemodule.RenameModuleRefactoring;

public class RenameRefactoring extends RubyRefactoring {

	public static final String NAME = Messages.RenameRefactoring_Name;

	public RenameRefactoring(ITextSelectionProvider selectionProvider) {
		super(NAME);
				
		if (selectionProvider.getActiveDocument() != null) {
			doTheRefactoring(new RenameFileRefactoring(selectionProvider));
			return;
		}
		
		RenameConfig config = new RenameConfig(getDocumentProvider(), selectionProvider.getCarretPosition());
		RenameConditionChecker checker = new RenameConditionChecker(config);
		setRefactoringConditionChecker(checker);
		
		if(checker.shouldPerform()) {
			if(checker.shouldRenameLocal()) {
				doTheRefactoring(new RenameLocalRefactoring(selectionProvider));
			} else if (checker.shouldRenameField()) {
				doTheRefactoring(new RenameFieldRefactoring(selectionProvider));
			} else if(checker.shouldRenameMethod()) {
				doTheRefactoring(new RenameMethodRefactoring(selectionProvider));
			} else if(checker.shouldRenameClass()) {
				doTheRefactoring(new RenameClassRefactoring(selectionProvider));
			} else if(checker.shouldRenameModule()) {
				doTheRefactoring(new RenameModuleRefactoring(selectionProvider));
			}
		}
	}

	private void doTheRefactoring(RubyRefactoring delegateRenameRefactoring) {
		IRefactoringConditionChecker delegateConditionChecker = delegateRenameRefactoring.getConditionChecker();
		setRefactoringConditionChecker(delegateConditionChecker);
		if(delegateConditionChecker.shouldPerform()) {
			setName(delegateRenameRefactoring.getName());
			setEditProvider(delegateRenameRefactoring.getMultiFileEditProvider());
			setEditProvider(delegateRenameRefactoring.getEditProvider());
			setFileNameChangeProvider(delegateRenameRefactoring.getFileNameChangeProvider());
			for(IWizardPage aktPage : delegateRenameRefactoring.getPages()) {
				pages.add(aktPage);
			}
		}
	}
}

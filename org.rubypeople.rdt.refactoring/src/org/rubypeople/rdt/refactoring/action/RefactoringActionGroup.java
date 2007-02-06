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

package org.rubypeople.rdt.refactoring.action;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionGroup;
import org.rubypeople.rdt.refactoring.core.converttemptofield.ConvertTempToFieldRefactoring;
import org.rubypeople.rdt.refactoring.core.encapsulatefield.EncapsulateFieldRefactoring;
import org.rubypeople.rdt.refactoring.core.extractmethod.ExtractMethodRefactoring;
import org.rubypeople.rdt.refactoring.core.generateaccessors.GenerateAccessorsRefactoring;
import org.rubypeople.rdt.refactoring.core.generateconstructor.GenerateConstructorRefactoring;
import org.rubypeople.rdt.refactoring.core.inlineclass.InlineClassRefactoring;
import org.rubypeople.rdt.refactoring.core.inlinemethod.InlineMethodRefactoring;
import org.rubypeople.rdt.refactoring.core.inlinetemp.InlineTempRefactoring;
import org.rubypeople.rdt.refactoring.core.mergeclasspartsinfile.MergeClassPartsInFileRefactoring;
import org.rubypeople.rdt.refactoring.core.mergewithexternalclassparts.MergeWithExternalClassPartsRefactoring;
import org.rubypeople.rdt.refactoring.core.movefield.MoveFieldRefactoring;
import org.rubypeople.rdt.refactoring.core.movemethod.MoveMethodRefactoring;
import org.rubypeople.rdt.refactoring.core.overridemethod.OverrideMethodRefactoring;
import org.rubypeople.rdt.refactoring.core.pushdown.PushDownRefactoring;
import org.rubypeople.rdt.refactoring.core.rename.RenameRefactoring;
import org.rubypeople.rdt.refactoring.core.splittemp.SplitTempRefactoring;

public class RefactoringActionGroup extends ActionGroup {

	private static final String INSERT_AFTER_GROUP_NAME = "group.edit";

	public void fillContextMenu(IMenuManager menu) {
		menu.insertAfter(INSERT_AFTER_GROUP_NAME, new Separator());
		menu.insertAfter(INSERT_AFTER_GROUP_NAME, getSourceMenu());
		menu.insertAfter(INSERT_AFTER_GROUP_NAME, getRefactorMenu());
		menu.insertAfter(INSERT_AFTER_GROUP_NAME, new Separator());
	}

	private IMenuManager getRefactorMenu() {
		IMenuManager submenu = new MenuManager("Refactor");
		submenu.add(new RefactoringAction(ConvertTempToFieldRefactoring.class, ConvertTempToFieldRefactoring.NAME));
		submenu.add(new RefactoringAction(EncapsulateFieldRefactoring.class, EncapsulateFieldRefactoring.NAME));
		submenu.add(new RefactoringAction(ExtractMethodRefactoring.class, ExtractMethodRefactoring.NAME));
		submenu.add(new RefactoringAction(InlineClassRefactoring.class, InlineClassRefactoring.NAME));
		submenu.add(new RefactoringAction(InlineTempRefactoring.class, InlineTempRefactoring.NAME));
		submenu.add(new RefactoringAction(InlineMethodRefactoring.class, InlineMethodRefactoring.NAME));
		submenu.add(new RefactoringAction(MergeClassPartsInFileRefactoring.class, MergeClassPartsInFileRefactoring.NAME));
		submenu.add(new RefactoringAction(MergeWithExternalClassPartsRefactoring.class, MergeWithExternalClassPartsRefactoring.NAME));
		submenu.add(new RefactoringAction(MoveFieldRefactoring.class, MoveFieldRefactoring.NAME));
		submenu.add(new RefactoringAction(MoveMethodRefactoring.class, MoveMethodRefactoring.NAME));
		submenu.add(new RefactoringAction(PushDownRefactoring.class, PushDownRefactoring.NAME));
		submenu.add(new RefactoringAction(RenameRefactoring.class, RenameRefactoring.NAME));
		submenu.add(new RefactoringAction(SplitTempRefactoring.class, SplitTempRefactoring.NAME));
		return submenu;
	}
	
	private IMenuManager getSourceMenu() {
		IMenuManager submenu = new MenuManager("Source");
		submenu.add(new RefactoringAction(GenerateAccessorsRefactoring.class, GenerateAccessorsRefactoring.NAME));
		submenu.add(new RefactoringAction(GenerateConstructorRefactoring.class, GenerateConstructorRefactoring.NAME));
		submenu.add(new RefactoringAction(OverrideMethodRefactoring.class, OverrideMethodRefactoring.NAME));
		return submenu;
	}
}
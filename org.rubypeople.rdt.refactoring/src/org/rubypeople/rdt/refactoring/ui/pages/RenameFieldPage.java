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

package org.rubypeople.rdt.refactoring.ui.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.rubypeople.rdt.refactoring.ui.ICheckboxListener;
import org.rubypeople.rdt.refactoring.ui.LabeledTextField;
import org.rubypeople.rdt.refactoring.ui.NewNameListener;

public class RenameFieldPage extends RenamePage {

	private static final String NAME = Messages.RenameFieldPage_Name;
	private final ICheckboxListener checkListener;

	public RenameFieldPage(String selectedVariable, NewNameListener listener, ICheckboxListener checkListener) {
		super(NAME, selectedVariable, listener);
		this.checkListener = checkListener;
	}

	public void createControl(Composite parent) {
		Composite main = new Composite(parent, SWT.None);
		
		main.setLayout(new GridLayout());

		LabeledTextField textField = createTextField(main);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button button = new Button(main, SWT.CHECK);
		button.setText(Messages.RenameFieldPage_RenameAccessors);
		button.setLayoutData( new GridData());
		button.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				checkListener.setChecked(((Button) event.widget).getSelection());
			}});
		
		setControl(main);
	}
	protected void setHelpContextIDs(){
		IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
		helpSystem.setHelp(getControl(), "org.rubypeople.rdt.refactoring.refactoring_rename");
	}
}

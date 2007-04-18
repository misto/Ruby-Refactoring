/**
 * Copyright (c) 2007 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.rubypeople.rdt.refactoring.ui.pages;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.rubypeople.rdt.internal.refactoring.RefactoringMessages;
import org.rubypeople.rdt.refactoring.core.extractconstant.ConstantExtractor;
import org.rubypeople.rdt.refactoring.ui.LabeledTextField;
import org.rubypeople.rdt.refactoring.util.NameValidator;

public class ExtractConstantPage extends UserInputWizardPage {

	private ConstantExtractor extractor;

	public ExtractConstantPage(ConstantExtractor extractor) {
		super(RefactoringMessages.ExtractConstantWizard_defaultPageTitle);
		this.extractor = extractor;
	}
	
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.None);
		setControl(control);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.verticalSpacing = 8;
		control.setLayout(layout);

		final LabeledTextField labeledText = new LabeledTextField(control, RefactoringMessages.ExtractConstantInputPage_constant_name, extractor.getConstantName());
		GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
		labeledText.setLayoutData(layoutData);
		labeledText.getText().addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				String newName = labeledText.getText().getText();
				extractor.setConstantName(newName);
				checkInput(newName);
			}

			private void checkInput(String newName) {
				if (NameValidator.isValidConstantName(newName)) {
					ExtractConstantPage.this.setMessage(null);
					ExtractConstantPage.this.setPageComplete(true);
				} else {
					ExtractConstantPage.this.setMessage("'" + newName + "' is not a valid local variable name in Ruby.", IMessageProvider.ERROR);
					ExtractConstantPage.this.setPageComplete(false);
				}

			}
		});
	}

}

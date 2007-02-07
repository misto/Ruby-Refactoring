/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.util.PixelConverter;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;

/**
  */
public class ProblemSeveritiesConfigurationBlock extends OptionsConfigurationBlock {

	private static final String SETTINGS_SECTION_NAME= null; //"ProblemSeveritiesConfigurationBlock"; 
	
	// Preference store keys, see RubyCore.getOptions
	// TODO Actually implement checking for these things in the builders!
	private static final Key PREF_PB_ENSURE_BLOCK_NOT_COMPLETING = getRDTCoreKey(RubyCore.COMPILER_PB_ENSURE_BLOCK_NOT_COMPLETING);
	private static final Key PREF_PB_EMPTY_STATEMENT = getRDTCoreKey(RubyCore.COMPILER_PB_EMPTY_STATEMENT);
	private static final Key PREF_PB_HIDDEN_RESCUE_BLOCK = getRDTCoreKey(RubyCore.COMPILER_PB_HIDDEN_RESCUE_BLOCK);
	private static final Key PREF_PB_FALLTHROUGH_CASE = getRDTCoreKey(RubyCore.COMPILER_PB_FALLTHROUGH_CASE);
	private static final Key PREF_PB_NULL_REFERENCE = getRDTCoreKey(RubyCore.COMPILER_PB_NULL_REFERENCE);
	private static final Key PREF_PB_UNUSED_LOCAL = getRDTCoreKey(RubyCore.COMPILER_PB_UNUSED_LOCAL);
	private static final Key PREF_PB_UNUSED_PARAMETER = getRDTCoreKey(RubyCore.COMPILER_PB_UNUSED_PARAMETER);
	private static final Key PREF_PB_UNUSED_PRIVATE = getRDTCoreKey(RubyCore.COMPILER_PB_UNUSED_PRIVATE_MEMBER);
	private static final Key PREF_PB_UNNECESSARY_ELSE = getRDTCoreKey(RubyCore.COMPILER_PB_UNNECESSARY_ELSE);	
	// values
	private static final String ERROR= RubyCore.ERROR;
	private static final String WARNING= RubyCore.WARNING;
	private static final String IGNORE= RubyCore.IGNORE;

	private static final String ENABLED= RubyCore.ENABLED;
	private static final String DISABLED= RubyCore.DISABLED;



	private PixelConverter fPixelConverter;
	
	public ProblemSeveritiesConfigurationBlock(IStatusChangeListener context, IProject project, IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);
	}
	
	private static Key[] getKeys() {
		return new Key[] {
//				PREF_PB_ENSURE_BLOCK_NOT_COMPLETING, 
				PREF_PB_EMPTY_STATEMENT, 
//				PREF_PB_HIDDEN_RESCUE_BLOCK,
//				PREF_PB_FALLTHROUGH_CASE, PREF_PB_NULL_REFERENCE, PREF_PB_UNUSED_LOCAL,
//				PREF_PB_UNUSED_PARAMETER, PREF_PB_UNUSED_PRIVATE, PREF_PB_UNNECESSARY_ELSE
			};
	}
	
	/*
	 * @see org.eclipse.jface.preference.PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		fPixelConverter= new PixelConverter(parent);
		setShell(parent.getShell());
		
		Composite mainComp= new Composite(parent, SWT.NONE);
		mainComp.setFont(parent.getFont());
		GridLayout layout= new GridLayout();
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		mainComp.setLayout(layout);
		
		Composite commonComposite= createStyleTabContent(mainComp);
		GridData gridData= new GridData(GridData.FILL, GridData.FILL, true, true);
		gridData.heightHint= fPixelConverter.convertHeightInCharsToPixels(20);
		commonComposite.setLayoutData(gridData);
		
		validateSettings(null, null, null);
	
		return mainComp;
	}
	
	private Composite createStyleTabContent(Composite folder) {
		String[] errorWarningIgnore= new String[] { ERROR, WARNING, IGNORE };
		
		String[] errorWarningIgnoreLabels= new String[] {
			PreferencesMessages.ProblemSeveritiesConfigurationBlock_error,  
			PreferencesMessages.ProblemSeveritiesConfigurationBlock_warning, 
			PreferencesMessages.ProblemSeveritiesConfigurationBlock_ignore
		};
		
		String[] enabledDisabled= new String[] { ENABLED, DISABLED };
		
		int nColumns= 3;
		
		final ScrolledPageContent sc1 = new ScrolledPageContent(folder);
		
		Composite composite= sc1.getBody();
		GridLayout layout= new GridLayout(nColumns, false);
		layout.marginHeight= 0;
		layout.marginWidth= 0;
		composite.setLayout(layout);
		
		Label description= new Label(composite, SWT.LEFT | SWT.WRAP);
		description.setFont(description.getFont());
		description.setText(PreferencesMessages.ProblemSeveritiesConfigurationBlock_common_description); 
		description.setLayoutData(new GridData(GridData.BEGINNING, GridData.CENTER, true, false, nColumns - 1, 1));
				
		int indentStep=  fPixelConverter.convertWidthInCharsToPixels(1);
		
		int defaultIndent= indentStep * 0;
		int extraIndent= indentStep * 2;
		String label;
		ExpandableComposite excomposite;
		Composite inner;
		
		// --- potential_programming_problems
		
		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_section_potential_programming_problems; 
		excomposite= createStyleSection(composite, label, nColumns);
		
		inner= new Composite(excomposite, SWT.NONE);
		inner.setFont(composite.getFont());
		inner.setLayout(new GridLayout(nColumns, false));
		excomposite.setClient(inner);

//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_ensure_block_not_completing_label; 
//		addComboBox(inner, label, PREF_PB_ENSURE_BLOCK_NOT_COMPLETING, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);

		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_empty_statement_label; 
		addComboBox(inner, label, PREF_PB_EMPTY_STATEMENT, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);

//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_hidden_rescueblock_label; 
//		addComboBox(inner, label, PREF_PB_HIDDEN_RESCUE_BLOCK, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
//
//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_fall_through_case;
//		addComboBox(inner, label, PREF_PB_FALLTHROUGH_CASE, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
//		
//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_null_reference;
//		addComboBox(inner, label, PREF_PB_NULL_REFERENCE, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);

		// --- unnecessary_code
		
//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_section_unnecessary_code; 
//		excomposite= createStyleSection(composite, label, nColumns);
//	
//		inner= new Composite(excomposite, SWT.NONE);
//		inner.setFont(composite.getFont());
//		inner.setLayout(new GridLayout(nColumns, false));
//		excomposite.setClient(inner);
		
//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_unused_local_label; 
//		addComboBox(inner, label, PREF_PB_UNUSED_LOCAL, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
//
//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_unused_parameter_label; 
//		addComboBox(inner, label, PREF_PB_UNUSED_PARAMETER, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
//		
//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_unused_private_label; 
//		addComboBox(inner, label, PREF_PB_UNUSED_PRIVATE, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
//		
//		label= PreferencesMessages.ProblemSeveritiesConfigurationBlock_pb_unnecessary_else_label; 
//		addComboBox(inner, label, PREF_PB_UNNECESSARY_ELSE, errorWarningIgnore, errorWarningIgnoreLabels, defaultIndent);
			
		IDialogSettings section= RubyPlugin.getDefault().getDialogSettings().getSection(SETTINGS_SECTION_NAME);
		restoreSectionExpansionStates(section);
		
		return sc1;
	}
	
	/* (non-javadoc)
	 * Update fields and validate.
	 * @param changedKey Key that changed, or null, if all changed.
	 */	
	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		if (!areSettingsEnabled()) {
			return;
		}
		
		if (changedKey != null) {
				return;
		} else {
			updateEnableStates();
		}		
		fContext.statusChanged(new StatusInfo());
	}
	
	private void updateEnableStates() {
		// TODO Handle enabling/disabling checkboxes as prefs change
	}

	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		String title= PreferencesMessages.ProblemSeveritiesConfigurationBlock_needsbuild_title; 
		String message;
		if (workspaceSettings) {
			message= PreferencesMessages.ProblemSeveritiesConfigurationBlock_needsfullbuild_message; 
		} else {
			message= PreferencesMessages.ProblemSeveritiesConfigurationBlock_needsprojectbuild_message; 
		}
		return new String[] { title, message };
	}
	
	/* (non-Rubydoc)
	 * @see org.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#dispose()
	 */
	public void dispose() {
		IDialogSettings section= RubyPlugin.getDefault().getDialogSettings().addNewSection(SETTINGS_SECTION_NAME);
		storeSectionExpansionStates(section);
		super.dispose();
	}
	
}

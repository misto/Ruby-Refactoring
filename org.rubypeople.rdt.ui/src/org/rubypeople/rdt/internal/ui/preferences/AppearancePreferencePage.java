/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.preferences;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.StatusUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.Separator;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class AppearancePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static final String SHOW_CU_CHILDREN= PreferenceConstants.SHOW_CU_CHILDREN;
	private static final String PREF_METHOD_PARAMETER_NAMES= PreferenceConstants.APPEARANCE_METHOD_PARAMETER_NAMES;
	private static final String STACK_BROWSING_VIEWS_VERTICALLY= PreferenceConstants.BROWSING_STACK_VERTICALLY;


	private SelectionButtonDialogField fStackBrowsingViewsVertically;
	private SelectionButtonDialogField fShowMembersInPackageView;
	private SelectionButtonDialogField fShowMethodParameterNames;

	
	public AppearancePreferencePage() {
		setPreferenceStore(RubyPlugin.getDefault().getPreferenceStore());
		setDescription(PreferencesMessages.AppearancePreferencePage_description); 
	
		IDialogFieldListener listener= new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				doDialogFieldChanged(field);
			}
		};
	
		fShowMethodParameterNames= new SelectionButtonDialogField(SWT.CHECK);
		fShowMethodParameterNames.setDialogFieldListener(listener);
		fShowMethodParameterNames.setLabelText(PreferencesMessages.AppearancePreferencePage_methodtypeparams_label); 

		fShowMembersInPackageView= new SelectionButtonDialogField(SWT.CHECK);
		fShowMembersInPackageView.setDialogFieldListener(listener);
		fShowMembersInPackageView.setLabelText(PreferencesMessages.AppearancePreferencePage_showMembersInPackagesView); 

		fStackBrowsingViewsVertically= new SelectionButtonDialogField(SWT.CHECK);
		fStackBrowsingViewsVertically.setDialogFieldListener(listener);
		fStackBrowsingViewsVertically.setLabelText(PreferencesMessages.AppearancePreferencePage_stackViewsVerticallyInTheRubyBrowsingPerspective); 
	}	

	private void initFields() {
		IPreferenceStore prefs= getPreferenceStore();
		fShowMethodParameterNames.setSelection(prefs.getBoolean(PREF_METHOD_PARAMETER_NAMES));
		fShowMembersInPackageView.setSelection(prefs.getBoolean(SHOW_CU_CHILDREN));
		fStackBrowsingViewsVertically.setSelection(prefs.getBoolean(STACK_BROWSING_VIEWS_VERTICALLY));
	}
	
	/*
	 * @see PreferencePage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		super.createControl(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(getControl(), IRubyHelpContextIds.APPEARANCE_PREFERENCE_PAGE);
	}	

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);
		int nColumns= 1;
				
		Composite result= new Composite(parent, SWT.NONE);
		result.setFont(parent.getFont());
		
		GridLayout layout= new GridLayout();
		layout.marginHeight= convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth= 0;
		layout.numColumns= nColumns;
		result.setLayout(layout);
				
		fShowMethodParameterNames.doFillIntoGrid(result, nColumns);
		fShowMembersInPackageView.doFillIntoGrid(result, nColumns);		

		new Separator().doFillIntoGrid(result, nColumns);
		
		fStackBrowsingViewsVertically.doFillIntoGrid(result, nColumns);
		
		String noteTitle= PreferencesMessages.AppearancePreferencePage_note; 
		String noteMessage= PreferencesMessages.AppearancePreferencePage_preferenceOnlyEffectiveForNewPerspectives; 
		Composite noteControl= createNoteComposite(JFaceResources.getDialogFont(), result, noteTitle, noteMessage);
		GridData gd= new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan= 2;
		noteControl.setLayoutData(gd);
		
		initFields();
		
		Dialog.applyDialogFont(result);
		return result;
	}
	
	private void doDialogFieldChanged(DialogField field) {
		updateStatus(getValidationStatus());
	}
	
	private IStatus getValidationStatus(){
		return new StatusInfo();
	}
	
	private void updateStatus(IStatus status) {
		setValid(!status.matches(IStatus.ERROR));
		StatusUtil.applyToStatusLine(this, status);
	}		
	
	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	/*
	 * @see IPreferencePage#performOk()
	 */
	public boolean performOk() {
		IPreferenceStore prefs= getPreferenceStore();
		prefs.setValue(PREF_METHOD_PARAMETER_NAMES, fShowMethodParameterNames.isSelected());
		prefs.setValue(SHOW_CU_CHILDREN, fShowMembersInPackageView.isSelected());
		prefs.setValue(STACK_BROWSING_VIEWS_VERTICALLY, fStackBrowsingViewsVertically.isSelected());
		RubyPlugin.getDefault().savePluginPreferences();
		return super.performOk();
	}	
	
	/*
	 * @see PreferencePage#performDefaults()
	 */
	protected void performDefaults() {
		IPreferenceStore prefs= getPreferenceStore();
		fShowMethodParameterNames.setSelection(prefs.getDefaultBoolean(PREF_METHOD_PARAMETER_NAMES));
		fShowMembersInPackageView.setSelection(prefs.getDefaultBoolean(SHOW_CU_CHILDREN));
		fStackBrowsingViewsVertically.setSelection(prefs.getDefaultBoolean(STACK_BROWSING_VIEWS_VERTICALLY));
		super.performDefaults();
	}
}


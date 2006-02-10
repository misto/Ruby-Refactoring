/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.preferences;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbenchPropertyPage;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.dialogs.StatusUtil;
import org.rubypeople.rdt.internal.ui.wizards.IStatusChangeListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.SelectionButtonDialogField;

/**
 * Base for project property and preference pages
 */
public abstract class PropertyAndPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage, IWorkbenchPropertyPage {

    private Control fConfigurationBlockControl;
    private ControlEnableState fBlockEnableState;
    private Hyperlink fChangeWorkspaceSettings;
    private SelectionButtonDialogField fUseProjectSettings;
    private IStatus fBlockStatus;

    private IProject fProject; // project or null

    public PropertyAndPreferencePage() {
        fBlockStatus = new StatusInfo();
        fBlockEnableState = null;
        fProject = null;
    }


    /*
     * @see org.eclipse.jface.preference.IPreferencePage#createContents(Composite)
     */
    protected Control createContents(Composite parent) {
        Composite composite= new Composite(parent, SWT.NONE);
        GridLayout layout= new GridLayout();
        layout.marginHeight= 0;
        layout.marginWidth= 0;
        composite.setLayout(layout);
        composite.setFont(parent.getFont());
            
        GridData data= new GridData(GridData.FILL, GridData.FILL, true, true);
        
        fConfigurationBlockControl= createPreferenceContent(composite);
        fConfigurationBlockControl.setLayoutData(data);

        if (isProjectPreferencePage()) {
            boolean useProjectSettings= hasProjectSpecificOptions(getProject());
            enableProjectSpecificSettings(useProjectSettings);
        }

        Dialog.applyDialogFont(composite);
        return composite;
    }

    protected abstract Control createPreferenceContent(Composite composite);
    protected abstract boolean hasProjectSpecificOptions(IProject project);
    protected abstract String getPreferencePageID();
    protected abstract String getPropertyPageID();
    
    protected final void openWorkspacePreferences(Object data) {
        String id= getPreferencePageID();
        PreferencesUtil.createPreferenceDialogOn(getShell(), id, new String[] { id }, data).open();
    }
    
    protected boolean useProjectSettings() {
        return isProjectPreferencePage() && fUseProjectSettings != null
                && fUseProjectSettings.isSelected();
    }

    protected boolean isProjectPreferencePage() {
        return fProject != null;
    }

    protected IProject getProject() {
        return fProject;
    }

    private void doProjectWorkspaceStateChanged() {
        enablePreferenceContent(useProjectSettings());
        fChangeWorkspaceSettings.setVisible(!useProjectSettings());
        doStatusChanged();
    }

    protected void setPreferenceContentStatus(IStatus status) {
        fBlockStatus = status;
        doStatusChanged();
    }

    /**
     * Returns a new status change listener that calls
     * {@link #setPreferenceContentStatus(IStatus)} when the status has changed
     * 
     * @return The new listener
     */
    protected IStatusChangeListener getNewStatusChangedListener() {
        return new IStatusChangeListener() {

            public void statusChanged(IStatus status) {
                setPreferenceContentStatus(status);
            }
        };
    }

    protected IStatus getPreferenceContentStatus() {
        return fBlockStatus;
    }

    protected void doStatusChanged() {
        if (!isProjectPreferencePage() || useProjectSettings()) {
            updateStatus(fBlockStatus);
        } else {
            updateStatus(new StatusInfo());
        }
    }

    protected void enablePreferenceContent(boolean enable) {
        if (enable) {
            if (fBlockEnableState != null) {
                fBlockEnableState.restore();
                fBlockEnableState = null;
            }
        } else {
            if (fBlockEnableState == null) {
                fBlockEnableState = ControlEnableState.disable(fConfigurationBlockControl);
            }
        }
    }

    /*
     * @see org.eclipse.jface.preference.IPreferencePage#performDefaults()
     */
    protected void performDefaults() {
        if (useProjectSettings()) {
            fUseProjectSettings.setSelection(false);
            // fUseWorkspaceSettings.setSelection(true);
        }
        super.performDefaults();
    }

    private void updateStatus(IStatus status) {
        setValid(!status.matches(IStatus.ERROR));
        StatusUtil.applyToStatusLine(this, status);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPropertyPage#getElement()
     */
    public IAdaptable getElement() {
        return fProject;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.IWorkbenchPropertyPage#setElement(org.eclipse.core.runtime.IAdaptable)
     */
    public void setElement(IAdaptable element) {
        fProject = (IProject) element.getAdapter(IResource.class);
    }

    protected void enableProjectSpecificSettings(boolean useProjectSpecificSettings) {
        fUseProjectSettings.setSelection(useProjectSpecificSettings);
        enablePreferenceContent(useProjectSpecificSettings);
        updateLinkVisibility();
        doStatusChanged();
    }
    
    private void updateLinkVisibility() {
        if (fChangeWorkspaceSettings == null || fChangeWorkspaceSettings.isDisposed()) {
            return;
        }
        
        if (isProjectPreferencePage()) {
            fChangeWorkspaceSettings.setEnabled(!useProjectSettings());
        }
    }
}

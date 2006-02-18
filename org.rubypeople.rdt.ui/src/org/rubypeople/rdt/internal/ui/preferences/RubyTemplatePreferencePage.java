/*******************************************************************************
 * Copyright (c) 2000, 2004  John-Mason P. Shackelford and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 * 	   John-Mason P. Shackelford - initial API and implementation
 *     IBM Corporation - bug fixes
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.preferences;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplatePersistenceData;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.RubyTemplateAccess;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.RubySourceViewerConfiguration;

/**
 * @see org.eclipse.jface.preference.PreferencePage
 */
public class RubyTemplatePreferencePage extends TemplatePreferencePage {

	public RubyTemplatePreferencePage() {
		setPreferenceStore(RubyPlugin.getDefault().getPreferenceStore());
		setTemplateStore(RubyTemplateAccess.getDefault().getTemplateStore());
		setContextTypeRegistry(RubyTemplateAccess.getDefault().getContextTypeRegistry());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean ok = super.performOk();
		RubyPlugin.getDefault().savePluginPreferences();
		return ok;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#createViewer(org.eclipse.swt.widgets.Composite)
	 */
	protected SourceViewer createViewer(Composite parent) {
		SourceViewer viewer = new SourceViewer(parent, null, null, false, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

		// FIXME Pass in the current editor!
		SourceViewerConfiguration configuration = new RubySourceViewerConfiguration(RubyPlugin.getDefault().getRubyTextTools(), null);
		IDocument document = new Document();
		// FIXME Do we need this?
		//new AntDocumentSetupParticipant().setup(document);
		viewer.configure(configuration);
		viewer.setDocument(document);
		viewer.setEditable(false);
		Font font = JFaceResources.getFont(JFaceResources.TEXT_FONT);
		viewer.getTextWidget().setFont(font);

		return viewer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#getFormatterPreferenceKey()
	 */
	protected String getFormatterPreferenceKey() {
		return PreferenceConstants.TEMPLATES_USE_CODEFORMATTER;
	}

	/*
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#updateViewerInput()
	 */
	protected void updateViewerInput() {
		IStructuredSelection selection = (IStructuredSelection) getTableViewer().getSelection();
		SourceViewer viewer = getViewer();

		if (selection.size() == 1 && selection.getFirstElement() instanceof TemplatePersistenceData) {
			TemplatePersistenceData data = (TemplatePersistenceData) selection.getFirstElement();
			Template template = data.getTemplate();
			if (RubyPlugin.getDefault().getPreferenceStore().getBoolean(getFormatterPreferenceKey())) {
				String formatted = RubyPlugin.getDefault().getCodeFormatter().formatString(template.getPattern());
				viewer.getDocument().set(formatted);
			} else {
				viewer.getDocument().set(template.getPattern());
			}
		} else {
			viewer.getDocument().set(""); //$NON-NLS-1$
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#isShowFormatterSetting()
	 */
	protected boolean isShowFormatterSetting() {
		return false;
	}
}
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

package org.rubypeople.rdt.internal.ui.rubyeditor.templates;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class RubySourceViewerInformationControl extends DefaultInformationControl {
	
	public RubySourceViewerInformationControl(Shell parent, int shellStyle, int style, IInformationPresenter presenter) {
		super(parent, shellStyle, style, presenter);
	}
	
	public RubySourceViewerInformationControl(Shell parent, int shellStyle, int style, IInformationPresenter presenter, String statusFieldText) {
		super(parent, shellStyle, style, presenter, statusFieldText);
	}
	
	public RubySourceViewerInformationControl(Shell parent, int style, IInformationPresenter presenter) {
		super(parent, style, presenter);
	}
	
	public RubySourceViewerInformationControl(Shell parent, int style, IInformationPresenter presenter, String statusFieldText) {
		super(parent, style, presenter, statusFieldText);
	}
	
	public RubySourceViewerInformationControl(Shell parent) {
		super(parent);
	}
	
	public RubySourceViewerInformationControl(Shell parent, IInformationPresenter presenter) {
		super(parent, presenter);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.IInformationControl#setInformation(java.lang.String)
	 */
	public void setInformation(String content) {
		if (content != null 
			&& RubyPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.TEMPLATES_USE_CODEFORMATTER)) {
			content= RubyPlugin.getDefault().getCodeFormatter().formatString(content);
		}
		super.setInformation(content);
	}
}

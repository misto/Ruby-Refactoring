/**********************************************************************
Copyright (c) 2000, 2004 IBM Corp. and others.
All rights reserved. This program and the accompanying materials
are made available under the terms of the Common Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/cpl-v10.html

Contributors:
	IBM Corporation - Initial implementation
**********************************************************************/
package org.rubypeople.rdt.internal.ui.rubyeditor.templates;

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlCreatorExtension;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Shell;


public class RubyTemplateInformationControlCreator implements IInformationControlCreator, IInformationControlCreatorExtension {
	
	private RubySourceViewerInformationControl fControl;
	
	public RubyTemplateInformationControlCreator()  {
	}
	
	/*
	 * @see org.eclipse.jface.text.IInformationControlCreator#createInformationControl(org.eclipse.swt.widgets.Shell)
	 */
	public IInformationControl createInformationControl(Shell parent) {
		fControl= new RubySourceViewerInformationControl(parent);
		fControl.addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				fControl= null;
			}
		});
		return fControl;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlCreatorExtension#canReuse(org.eclipse.jface.text.IInformationControl)
	 */
	public boolean canReuse(IInformationControl control) {
		return fControl == control && fControl != null;
	}

	/*
	 * @see org.eclipse.jface.text.IInformationControlCreatorExtension#canReplace(org.eclipse.jface.text.IInformationControlCreator)
	 */
	public boolean canReplace(IInformationControlCreator creator) {
		return (creator != null && getClass() == creator.getClass());
	}
}
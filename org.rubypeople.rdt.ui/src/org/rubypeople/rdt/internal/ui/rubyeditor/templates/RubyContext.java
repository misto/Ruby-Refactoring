/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.rubyeditor.templates;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.rubypeople.rdt.internal.core.RubyModel;

public class RubyContext extends DocumentTemplateContext {
	
	
	public RubyContext(TemplateContextType type, IDocument document, RubyModel model, int completionOffset, int completionLength) {
		super(type, document, completionOffset, completionLength);
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.TemplateContext#evaluate(org.eclipse.jface.text.templates.Template)
	 */
	public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException {
		TemplateBuffer templateBuffer= super.evaluate(template);	
		if (templateBuffer == null) {
			return null;
		}
		return templateBuffer;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.DocumentTemplateContext#getEnd()
	 */
	public int getEnd() {
		int replacementOffset = getCompletionOffset();
		int replacementLength = getCompletionLength();
		if (replacementOffset > 0 && getDocument().get().charAt(replacementOffset - 1) == '<') {
			replacementLength++;
		}
		return replacementLength;
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.text.templates.DocumentTemplateContext#getStart()
	 */
	public int getStart() {
		int replacementOffset= getCompletionOffset();
		if (replacementOffset > 0 && getDocument().get().charAt(replacementOffset - 1) == '<') {
			replacementOffset--;
		}
		return replacementOffset;
	}
}

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

package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyDocumentProvider;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.DocumentModelChangeEvent;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyCore;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyModel;

public class RubyReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {

	/**
	 * How long the reconciler will wait for further text changes before
	 * reconciling
	 */
	public static final int DELAY = 500;

	private ITextEditor fEditor;

	public RubyReconcilingStrategy(ITextEditor editor) {
		fEditor = editor;
	}

	private void internalReconcile(DirtyRegion dirtyRegion) {
		try {
			IDocumentProvider provider = fEditor.getDocumentProvider();
			if (provider instanceof RubyDocumentProvider) {
				RubyDocumentProvider documentProvider = (RubyDocumentProvider) provider;
				IDocument doc = documentProvider.getDocument(fEditor.getEditorInput());
				RubyModel model = documentProvider.getRubyModel(fEditor.getEditorInput()) ;
				model.setScript(RubyParser.parse(doc.get()));
				RubyCore.getDefault().notifyDocumentModelListeners(new DocumentModelChangeEvent(model));
			}
		} catch (Exception e) {
			RdtUiPlugin.log(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(IRegion partition) {
		internalReconcile(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#reconcile(org.eclipse.jface.text.reconciler.DirtyRegion,
	 *      org.eclipse.jface.text.IRegion)
	 */
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		internalReconcile(dirtyRegion);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategy#setDocument(org.eclipse.jface.text.IDocument)
	 */
	public void setDocument(IDocument document) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#setProgressMonitor(org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setProgressMonitor(IProgressMonitor monitor) {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension#initialReconcile()
	 */
	public void initialReconcile() {
		internalReconcile(null);
	}
}
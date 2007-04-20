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
package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.util.List;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHoverExtension;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommand;
import org.eclipse.ui.commands.ICommandManager;
import org.eclipse.ui.commands.IKeySequenceBinding;
import org.eclipse.ui.keys.KeySequence;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.HTMLTextPresenter;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.actions.IRubyEditorActionDefinitionIds;
import org.rubypeople.rdt.ui.text.ruby.hover.IRubyEditorTextHover;

/**
 * Abstract class for providing hover information for Ruby elements.
 * 
 * @since 2.1
 */
public abstract class AbstractRubyEditorTextHover implements IRubyEditorTextHover, ITextHoverExtension {


	private IEditorPart fEditor;
	private ICommand fCommand;
	{
		ICommandManager commandManager= PlatformUI.getWorkbench().getCommandSupport().getCommandManager();
		fCommand= commandManager.getCommand(IRubyEditorActionDefinitionIds.SHOW_RDOC);
		if (!fCommand.isDefined())
			fCommand= null;
	}

	/*
	 * @see IRubyEditorTextHover#setEditor(IEditorPart)
	 */
	public void setEditor(IEditorPart editor) {
		fEditor= editor;
	}

	protected IEditorPart getEditor() {
		return fEditor;
	}
	
	/*
	 * @see ITextHover#getHoverRegion(ITextViewer, int)
	 */
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return RubyWordFinder.findWord(textViewer.getDocument(), offset);
	}
	
	/*
	 * @see ITextHover#getHoverInfo(ITextViewer, IRegion)
	 */
	public abstract String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion);
	
	/**
	 * Provides hover information for the given Ruby elements.
	 * 
	 * @param javaElements the Ruby elements for which to provide hover information
	 * @return the hover information string
	 * @since 2.1
	 */
	protected String getHoverInfo(IRubyElement[] rubyElements) {
		return null;
	}

	/*
	 * @see ITextHoverExtension#getHoverControlCreator()
	 * @since 3.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true), getTooltipAffordanceString());
			}
		};
	}
	
	/**
	 * Returns the tool tip affordance string.
	 * 
	 * @return the affordance string or <code>null</code> if disabled or no key binding is defined
	 * @since 3.0
	 */
	protected String getTooltipAffordanceString() {
		if (!RubyPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SHOW_TEXT_HOVER_AFFORDANCE))
			return null;
		
		KeySequence[] sequences= getKeySequences();
		if (sequences == null)
			return null;
		
		String keySequence= sequences[0].format();
		return RubyHoverMessages.getFormattedString(RubyHoverMessages.RubyTextHover_makeStickyHint, keySequence);
	}

	/**
	 * Returns the array of valid key sequence bindings for the
	 * show tool tip description command.
	 * 
	 * @return the array with the {@link KeySequence}s
	 * 
	 * @since 3.0
	 */
	private KeySequence[] getKeySequences() {
		if (fCommand != null) {
			List list= fCommand.getKeySequenceBindings();
			if (!list.isEmpty()) {
				KeySequence[] keySequences= new KeySequence[list.size()];
				for (int i= 0; i < keySequences.length; i++) {
					keySequences[i]= ((IKeySequenceBinding) list.get(i)).getKeySequence();
				}
				return keySequences;
			}		
		}
		return null;
	}
}

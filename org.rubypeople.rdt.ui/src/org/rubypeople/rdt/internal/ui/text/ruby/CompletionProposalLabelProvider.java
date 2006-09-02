/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.resource.ImageDescriptor;

import org.eclipse.jface.text.Assert;
import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.ui.RubyElementImageDescriptor;

/**
 * Provides labels for ruby content assist proposals. The functionality is
 * similar to the one provided by {@link org.rubypeople.rdt.ui.RubyElementLabels},
 * but based on signatures and {@link CompletionProposal}s.
 * 
 * @see Signature
 * @since 3.1
 */
public class CompletionProposalLabelProvider {
	/**
	 * Creates and returns a decorated image descriptor for a completion
	 * proposal.
	 * 
	 * @param proposal
	 *            the proposal for which to create an image descriptor
	 * @return the created image descriptor, or <code>null</code> if no image
	 *         is available
	 */
	public ImageDescriptor createImageDescriptor(CompletionProposal proposal) {
		final int flags = proposal.getFlags();

		ImageDescriptor descriptor;
		switch (proposal.getKind()) {
		case CompletionProposal.METHOD_DECLARATION:
		case CompletionProposal.METHOD_NAME_REFERENCE:
		case CompletionProposal.METHOD_REF:
		case CompletionProposal.POTENTIAL_METHOD_DECLARATION:
			descriptor = RubyElementImageProvider.getMethodImageDescriptor(flags);
			break;
		case CompletionProposal.TYPE_REF:
			descriptor = RubyElementImageProvider.getTypeImageDescriptor(
						false, false, false);
			break;
		case CompletionProposal.FIELD_REF:
			descriptor = RubyElementImageProvider.getFieldImageDescriptor();
			break;
		case CompletionProposal.LOCAL_VARIABLE_REF:
		case CompletionProposal.VARIABLE_DECLARATION:
			descriptor = RubyPluginImages.DESC_OBJS_LOCAL_VAR;
			break;
		case CompletionProposal.KEYWORD:
			descriptor = null;
			break;
		default:
			descriptor = null;
			Assert.isTrue(false);
		}

		if (descriptor == null)
			return null;
		return decorateImageDescriptor(descriptor, proposal);
	}
	
	/**
	 * Returns a version of <code>descriptor</code> decorated according to
	 * the passed <code>modifier</code> flags.
	 *
	 * @param descriptor the image descriptor to decorate
	 * @param proposal the proposal
	 * @return an image descriptor for a method proposal
	 * @see Flags
	 */
	private ImageDescriptor decorateImageDescriptor(ImageDescriptor descriptor, CompletionProposal proposal) {
		int adornments= 0;
		int flags= proposal.getFlags();
		int kind= proposal.getKind();

		if (kind == CompletionProposal.FIELD_REF || kind == CompletionProposal.METHOD_DECLARATION || kind == CompletionProposal.METHOD_DECLARATION || kind == CompletionProposal.METHOD_NAME_REFERENCE || kind == CompletionProposal.METHOD_REF)
			if (Flags.isStatic(flags))
				adornments |= RubyElementImageDescriptor.STATIC;

		return new RubyElementImageDescriptor(descriptor, adornments, RubyElementImageProvider.SMALL_SIZE);
	}

}

package com.aptana.rdt.internal.ui.text.ruby.hover;

import java.util.Collection;

import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jruby.ast.CommentNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ui.text.HTMLPrinter;
import org.rubypeople.rdt.internal.ui.text.HTMLTextPresenter;
import org.rubypeople.rdt.internal.ui.text.ruby.IInformationControlExtension4;
import org.rubypeople.rdt.internal.ui.text.ruby.hover.AbstractReusableInformationControlCreator;
import org.rubypeople.rdt.internal.ui.text.ruby.hover.AbstractRubyEditorTextHover;
import org.rubypeople.rdt.internal.ui.text.ruby.hover.BrowserInformationControl;
import org.rubypeople.rdt.ui.RubyElementLabels;

public class CommentHoverProvider extends AbstractRubyEditorTextHover {

	private final long LABEL_FLAGS= RubyElementLabels.ALL_FULLY_QUALIFIED | RubyElementLabels.M_PARAMETER_NAMES | RubyElementLabels.USE_RESOLVED;
	private final long LOCAL_VARIABLE_FLAGS= LABEL_FLAGS & ~RubyElementLabels.F_FULLY_QUALIFIED | RubyElementLabels.F_POST_QUALIFIED;
	
	/**
	 * The hover control creator.
	 * 
	 * @since 1.0
	 */
	private IInformationControlCreator fHoverControlCreator;
	/**
	 * The presentation control creator.
	 * 
	 * @since 1.0
	 */
	private IInformationControlCreator fPresenterControlCreator;
	
	/**
	 * Grabs the comment from any comment node that follows this element (has to be on the same line)
	 * @param comments
	 * @param elementStart
	 * @param src
	 * @return
	 */
	private String getFollowingComment(Collection<CommentNode> comments, int elementStart, String src) {
		for (CommentNode comment : comments) {
			ISourcePosition pos = comment.getPosition();
			if (pos.getStartOffset() < elementStart) continue;
			String between = src.substring(elementStart, pos.getStartOffset());
			if (between.contains("\n"))	continue;	// if there's a newline between the positions - it's not on same line
			String com = comment.getContent();
			if (com != null && com.length() > 0)
				return removePrecedingHashes(com);
		}
		return null;
	}

	/**
	 * Grabs and merges together all comment nodes which immediately preced the elementStart offset.
	 * @param comments
	 * @param elementStart
	 * @param src
	 * @return a combined string of all immediately preceding comments
	 */
	private String getPrecedingComment(Collection<CommentNode> comments, int elementStart, String src) {
		for (CommentNode comment : comments) {
			ISourcePosition pos = comment.getPosition();
			if (pos.getEndOffset() > elementStart) continue;
			String between = src.substring(pos.getEndOffset(), elementStart);
			if (between.trim().length() > 0)
				continue; // if there's anything but whitespace between (\n\r\t ), move to next comment			
			String preceding = getPrecedingComment(comments, pos.getStartOffset(), src);
			if (preceding == null) {
				preceding = removePrecedingHashes(comment.getContent());
			} else {
				preceding += "\n" + removePrecedingHashes(comment.getContent());
			}
			return preceding;
		}
		return null;
	}

	/**
	 * Trims the string and drops the beginning hash mark (#)
	 * @param comment
	 * @return
	 */
	private String removePrecedingHashes(String comment) {
		comment = comment.trim();
		return comment.substring(1);
	}
	
	/*
	 * @see RubyElementHover
	 */
	protected String getHoverInfo(IRubyElement[] result) {

		StringBuffer buffer= new StringBuffer();
		int nResults= result.length;
		if (nResults == 0)
			return null;

		boolean hasContents= false;
		if (nResults > 1) {

			for (int i= 0; i < result.length; i++) {
				HTMLPrinter.startBulletList(buffer);
				IRubyElement curr= result[i];
				if (curr instanceof IMember || curr.getElementType() == IRubyElement.LOCAL_VARIABLE) {
					HTMLPrinter.addBullet(buffer, getInfoText(curr));
					hasContents= true;
				}
				HTMLPrinter.endBulletList(buffer);
			}

		} else {

			IRubyElement curr= result[0];
			if (curr instanceof IMember) {
				IMember member= (IMember) curr;
				HTMLPrinter.addSmallHeader(buffer, getInfoText(member));
				String contents = getContents(member);				
				if (contents != null) {
					HTMLPrinter.addParagraph(buffer, contents);
				}
				hasContents= true;
			} else if (curr.getElementType() == IRubyElement.LOCAL_VARIABLE) {
				HTMLPrinter.addSmallHeader(buffer, getInfoText(curr));
				hasContents= true;
			}
		}
		
		if (!hasContents)
			return null;

		if (buffer.length() > 0) {
			HTMLPrinter.insertPageProlog(buffer, 0, getStyleSheet());
			HTMLPrinter.addPageEpilog(buffer);
			return buffer.toString();
		}

		return null;
	}
	
	private String getContents(IMember member) {
		String src = "";
		int elementOffset = -1;
		try {
			src = member.getRubyScript().getSource();
			elementOffset = member.getSourceRange().getOffset();
		} catch (RubyModelException e) {
			return null;
		}
		RubyParser parser = new RubyParser();		
		parser.parse(src); // parse so we can grab the comment nodes
		Collection<CommentNode> comments = parser.getComments();		
		if (member.isType(IRubyElement.TYPE) || member.isType(IRubyElement.METHOD) || member.isType(IRubyElement.CONSTANT)) {
			return getPrecedingComment(comments, elementOffset, src);
		}
		return getFollowingComment(comments, elementOffset, src);
	}

	/*
	 * @see IInformationProviderExtension2#getInformationPresenterControlCreator()
	 * @since 1.0
	 */
	public IInformationControlCreator getInformationPresenterControlCreator() {
		if (fPresenterControlCreator == null) {
			fPresenterControlCreator= new AbstractReusableInformationControlCreator() {

				/*
				 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
				 */
				public IInformationControl doCreateInformationControl(Shell parent) {
					int shellStyle= SWT.RESIZE | SWT.TOOL;
					int style= SWT.V_SCROLL | SWT.H_SCROLL;
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent, shellStyle, style);
					else
						return new DefaultInformationControl(parent, shellStyle, style, new HTMLTextPresenter(false));
				}
			};
		}
		return fPresenterControlCreator;
	}
	
	/*
	 * @see ITextHoverExtension#getHoverControlCreator()
	 * @since 1.0
	 */
	public IInformationControlCreator getHoverControlCreator() {
		if (fHoverControlCreator == null) {
			fHoverControlCreator= new AbstractReusableInformationControlCreator() {
				
				/*
				 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#doCreateInformationControl(org.eclipse.swt.widgets.Shell)
				 */
				public IInformationControl doCreateInformationControl(Shell parent) {
					if (BrowserInformationControl.isAvailable(parent))
						return new BrowserInformationControl(parent, SWT.TOOL | SWT.NO_TRIM, SWT.NONE, getTooltipAffordanceString());
					else
						return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true), getTooltipAffordanceString());
				}
				
				/*
				 * @see org.eclipse.jdt.internal.ui.text.java.hover.AbstractReusableInformationControlCreator#canReuse(org.eclipse.jface.text.IInformationControl)
				 */
				public boolean canReuse(IInformationControl control) {
					boolean canReuse= super.canReuse(control);
					if (canReuse && control instanceof IInformationControlExtension4)
						((IInformationControlExtension4)control).setStatusText(getTooltipAffordanceString());
					return canReuse;
						
				}
			};
		}
		return fHoverControlCreator;
	}
	
	private String getInfoText(IRubyElement member) {
		long flags= member.getElementType() == IRubyElement.LOCAL_VARIABLE ? LOCAL_VARIABLE_FLAGS : LABEL_FLAGS;
		String label= RubyElementLabels.getElementLabel(member, flags);
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < label.length(); i++) {
			char ch= label.charAt(i);
			if (ch == '<') {
				buf.append("&lt;"); //$NON-NLS-1$
			} else if (ch == '>') {
				buf.append("&gt;"); //$NON-NLS-1$
			} else {
				buf.append(ch);
			}
		}
		return buf.toString();
	}
}
package org.rubypeople.rdt.core.util;

import java.util.Collection;

import org.jruby.ast.CommentNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class RDocUtil {
	private RDocUtil() {}
	
	public static String getDocumentation(IRubyElement element) {
		if (element instanceof IMember) {
			return getContents((IMember)element);
		}
		return "";
	}
	
	private static String getContents(IMember member) {
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
	
	/**
	 * Grabs and merges together all comment nodes which immediately preced the elementStart offset.
	 * @param comments
	 * @param elementStart
	 * @param src
	 * @return a combined string of all immediately preceding comments
	 */
	private static String getPrecedingComment(Collection<CommentNode> comments, int elementStart, String src) {
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
	 * Grabs the comment from any comment node that follows this element (has to be on the same line)
	 * @param comments
	 * @param elementStart
	 * @param src
	 * @return
	 */
	private static String getFollowingComment(Collection<CommentNode> comments, int elementStart, String src) {
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
	 * Trims the string and drops the beginning hash mark (#)
	 * @param comment
	 * @return
	 */
	private static String removePrecedingHashes(String comment) {
		return comment.trim().substring(1);
	}
}

package org.rubypeople.rdt.internal.core.parser;

import org.jruby.ast.Node;
import org.jruby.lexer.yacc.ISourcePosition;

public class NodeUtil {
	
	private NodeUtil() {}
	
	public static String getSource(String contents, Node node) {
		ISourcePosition pos = node.getPosition();
		return contents.substring(pos.getStartOffset(), pos.getEndOffset());
	}
}

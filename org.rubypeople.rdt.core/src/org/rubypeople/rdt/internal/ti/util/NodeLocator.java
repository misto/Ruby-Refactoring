package org.rubypeople.rdt.internal.ti.util;

import org.jruby.ast.Node;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;

/**
 * Provides basic functionality available to all node locator visitors.
 * @author Jason
 *
 */
public class NodeLocator extends InOrderVisitor {
	
	/**
	 * Determines whether the node spans the specified source offset.
	 * @param node Node to test.
	 * @param offset Offset to test.
	 * @return Wthether the node spans the specified offset.
	 */
	protected boolean nodeDoesSpanOffset(Node node, int offset) {
		return (node.getPosition().getStartOffset() <= offset)
				&& (node.getPosition().getEndOffset() >= offset);
	}

	/**
	 * Determines the length of the node's span in the source
	 * @param node Node to test.
	 * @return Number of characters the specified node spans.
	 */
	protected int nodeSpanLength(Node node) {
		if ( node == null || node.getPosition() == null )
		{
			return 0;
		}
		else
		{
			return node.getPosition().getEndOffset() - node.getPosition().getStartOffset();
		}
	}

}

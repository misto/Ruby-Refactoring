package org.rubypeople.rdt.refactoring.tests;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.lexer.yacc.IDESourcePosition;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.parser.BlockStaticScope;

/**
 * A bunch of helpers that create empty nodes.
 * 
 * @author Mirko Stocker
 * 
 */
public class FakeNodes {
	private static final ISourcePosition EMPTY_POSITION = new IDESourcePosition();
	
	public static DefnNode defn() {
		return defn("");
	}
	
	public static DefnNode defn(String name) {
		return new DefnNode(EMPTY_POSITION, new ArgumentNode(EMPTY_POSITION, name), new ArgsNode(EMPTY_POSITION, null, null, 0, null, null), new BlockStaticScope(null), null, null);
	}
	
	public static DefsNode defs() {
		return new DefsNode(EMPTY_POSITION, null, null, new ArgsNode(EMPTY_POSITION ,null, null, 0, null, null), new BlockStaticScope(null), null);
	}
	
}

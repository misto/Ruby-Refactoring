/*
 * Created on Feb 27, 2005
 *
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.ISourcePositionFactory;
import org.jruby.lexer.yacc.LexerSource;

/**
 * @author Chris
 * 
 */
public class RdtPositionFactory implements ISourcePositionFactory {

	public RdtPositionFactory() {}

	/**
	 * Extra simple caching mechanism. By calling this instead of direct
	 * instantiation, close grammatical elements will end up sharing the same
	 * instance of SourcePosition. This scheme will not work properly in
	 * environment where multiple threads are parsing files. The concept of
	 * caching should be moved into LexerSource.
	 */
	public ISourcePosition getPosition(LexerSource source, ISourcePosition startPosition) {
		int start = 0;
//		int line = 1;
		if(startPosition != null) {
			start = startPosition.getEndOffset();
//			line = startPosition.getLine();
		}
		return new RdtPosition(source.getLine(), start, source.getOffset());
	}

	public ISourcePosition getDummyPosition() {
		return new RdtPosition(1, 0, 0);
	}

}

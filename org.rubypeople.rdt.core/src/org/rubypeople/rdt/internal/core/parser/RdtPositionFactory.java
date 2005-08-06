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
		int startOffset = 0;
		
		/* If startPosition is null then we are starting a new block/token of
		 * ruby code. */
		if( startPosition == null ){
			return new RdtPosition( source.getLine(), startOffset+1, 
					source.getOffset() ); }
		
		/* If startPosition isn't null then we are closing an existing
		 * block/token of ruby code, and the source.getLine() is the line that 
		 * the block/token of ruby code ends on.*/
		else{
			startOffset = startPosition.getEndOffset();
			return new RdtPosition( startPosition.getStartLine(), 
					source.getLine(), startOffset, source.getOffset() );
		}
	}

	public ISourcePosition getDummyPosition() {
		return new RdtPosition(1, 0, 0);
	}

}

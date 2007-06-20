package org.rubypeople.rdt.internal.ui.text.ruby;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.LexState;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.RubyYaccLexer;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.parser.ParserSupport;
import org.jruby.parser.RubyParserConfiguration;
import org.jruby.parser.RubyParserResult;
import org.jruby.parser.Tokens;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.ui.text.IColorManager;

public class RubyTokenScanner extends AbstractRubyTokenScanner {

	private static final int MIN_KEYWORD = 257;
	private static final int MAX_KEYWORD = 303;
	private static final int COMMA = 44;
	private static final int COLON = 58;
	private static final int NEWLINE = 10;

	protected String[] keywords;

	private static String[] fgTokenProperties = { IRubyColorConstants.RUBY_KEYWORD, IRubyColorConstants.RUBY_DEFAULT, 
		IRubyColorConstants.RUBY_FIXNUM, IRubyColorConstants.RUBY_CHARACTER, IRubyColorConstants.RUBY_SYMBOL, 
		IRubyColorConstants.RUBY_INSTANCE_VARIABLE, IRubyColorConstants.RUBY_GLOBAL, 
		IRubyColorConstants.RUBY_REGEXP, IRubyColorConstants.RUBY_ERROR
	// TODO Add Ability to set colors for return and operators
	// IRubyColorConstants.RUBY_METHOD_NAME,
	// IRubyColorConstants.RUBY_KEYWORD_RETURN,
	// IRubyColorConstants.RUBY_OPERATOR
	};

	private RubyYaccLexer lexer;
	private LexerSource lexerSource;
	private ParserSupport parserSupport;
	private int tokenLength;
	private int oldOffset;
	private boolean isInRegexp;
	private boolean isInSymbol;
	private boolean inAlias;
	private RubyParserResult result;
	private int origOffset;
	private int origLength;
	private String contents;	

	public RubyTokenScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		lexer = new RubyYaccLexer();
		parserSupport = new ParserSupport();
		parserSupport.setConfiguration(new RubyParserConfiguration());
		result = new RubyParserResult();
		parserSupport.setResult(result);
		lexer.setParserSupport(parserSupport);
		lexer.setWarnings(new NullWarnings());
		initialize();
	}

	public int getTokenLength() {
		return tokenLength;
	}

	public int getTokenOffset() {
		return oldOffset;
	}

	public IToken nextToken() {
		oldOffset = getOffset();
		tokenLength = 0;
		IToken returnValue = getToken(IRubyColorConstants.RUBY_DEFAULT);
		boolean isEOF = false;
		try {
			isEOF = !lexer.advance(); // FIXME if we're assigning a string to a variable we may get a NumberformatException here!
			if (isEOF) {
				returnValue = Token.EOF;
			} else {
				returnValue = token(lexer.token());
			}
		} catch (SyntaxException se) {
			if (lexerSource.getOffset() - origLength == 0)
				return Token.EOF; // return eof if we hit a problem found at
									// end of parsing			
			tokenLength = getOffset() - oldOffset;
			return getToken(IRubyColorConstants.RUBY_ERROR);
		} catch (NumberFormatException nfe) {
			tokenLength = getOffset() - oldOffset;
			return returnValue;
		} catch (IOException e) {
			RubyPlugin.log(e);
		}
		if (!isEOF)
			tokenLength = getOffset() - oldOffset;
		return returnValue;
	}

	private int getOffset() {
		return lexerSource.getOffset() + origOffset;
	}

	private Token doGetToken(String key) {
		if (isInSymbol)
			return super.getToken(IRubyColorConstants.RUBY_SYMBOL);
		if (isInRegexp)
			return super.getToken(IRubyColorConstants.RUBY_REGEXP);
		return super.getToken(key);
	}

	private IToken token(int i) {		
		if (isInSymbol) {
			if (isSymbolTerminator(i)) {
				isInSymbol = false; // we're at the end of the symbol
				if (shouldReturnDefault(i))
					return doGetToken(IRubyColorConstants.RUBY_DEFAULT);				
			}
			return doGetToken(IRubyColorConstants.RUBY_SYMBOL);
		}
		// The next two conditionals work around a JRuby parsing bug
		// JRuby returns the number for ':' on second symbol's beginning in alias calls
		if (i == Tokens.kALIAS) {
			inAlias = true;
		}
		if (i == COLON && inAlias) {
			isInSymbol = true;
			inAlias = false;
			return doGetToken(IRubyColorConstants.RUBY_SYMBOL);
		} // end JRuby parsing hack for alias
		if (isKeyword(i))
			return doGetToken(IRubyColorConstants.RUBY_KEYWORD);
		switch (i) {
		case Tokens.tSYMBEG:		
			isInSymbol = true;
			return doGetToken(IRubyColorConstants.RUBY_SYMBOL);
		case Tokens.tGVAR:
			return doGetToken(IRubyColorConstants.RUBY_GLOBAL);
		case Tokens.tIVAR:
		case Tokens.tCVAR: // FIXME Allow for unique coloring of class variables...
			return doGetToken(IRubyColorConstants.RUBY_INSTANCE_VARIABLE);
		case Tokens.tFLOAT:
		case Tokens.tINTEGER:
			// A character is marked as an integer, lets check for that special case...
			if ((((oldOffset - origOffset) + 1) < contents.length()) && (contents.charAt((oldOffset - origOffset) + 1) == '?'))
				return doGetToken(IRubyColorConstants.RUBY_CHARACTER);
			return doGetToken(IRubyColorConstants.RUBY_FIXNUM);
		case Tokens.tREGEXP_BEG:
			isInRegexp = true;
			return doGetToken(IRubyColorConstants.RUBY_REGEXP);
		case Tokens.tREGEXP_END:
			isInRegexp = false;
			return doGetToken(IRubyColorConstants.RUBY_REGEXP);
		default:
			return doGetToken(IRubyColorConstants.RUBY_DEFAULT);
		}
	}

	private boolean shouldReturnDefault(int i) {
		switch (i) {
		case NEWLINE:
		case COMMA:
		case Tokens.tASSOC:
		case Tokens.tRPAREN:
			return true;
		default:
			return false;
		}
	}

	private boolean isSymbolTerminator(int i) {
		if (isKeyword(i)) return true;
		switch (i) {
		case Tokens.tAREF:
		case Tokens.tCVAR:
		case Tokens.tMINUS:
		case Tokens.tPLUS:
		case Tokens.tPIPE:
		case Tokens.tCARET:
		case Tokens.tLT:
		case Tokens.tGT:
		case Tokens.tAMPER:
		case Tokens.tSTAR2:
		case Tokens.tDIVIDE:
		case Tokens.tPERCENT:
		case Tokens.tBACK_REF2:
		case Tokens.tTILDE:
		case Tokens.tCONSTANT: 
		case Tokens.tFID:
		case Tokens.tASET:
		case Tokens.tIDENTIFIER: 
		case Tokens.tIVAR:
		case Tokens.tGVAR:
		case Tokens.tASSOC:
		case Tokens.tLSHFT:
		case Tokens.tRPAREN:
		case COMMA:
		case NEWLINE:
			return true;
		default:
			return false;
		}
	}

	private boolean isKeyword(int i) {
		return (i >= MIN_KEYWORD && i <= MAX_KEYWORD);
	}

	public void setRange(IDocument document, int offset, int length) {
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		parserSupport.initTopLocalVariables();
		isInSymbol = false;
		if (offset == 0) {
			isInRegexp = false;
		}
		try {
			contents = document.get(offset, length);
			lexerSource = new LexerSource("filename", new StringReader(contents), 0);
			lexer.setSource(lexerSource);
		} catch (BadLocationException e) {
			lexerSource = new LexerSource("filename", new StringReader(""), 0);
			lexer.setSource(lexerSource);
		}
		origOffset = offset;
		origLength = length;
	}

	/*
	 * @see AbstractRubyScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}
}

package org.rubypeople.rdt.internal.ui.text.ruby;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jruby.ast.CommentNode;
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

	protected String[] keywords;

	private static String[] fgTokenProperties = { IRubyColorConstants.RUBY_KEYWORD, IRubyColorConstants.RUBY_DEFAULT, 
		IRubyColorConstants.RUBY_FIXNUM, IRubyColorConstants.RUBY_CHARACTER, IRubyColorConstants.RUBY_SYMBOL, 
		IRubyColorConstants.RUBY_INSTANCE_VARIABLE, IRubyColorConstants.RUBY_GLOBAL, IRubyColorConstants.RUBY_STRING, 
		IRubyColorConstants.RUBY_REGEXP, IRubyColorConstants.RUBY_ERROR, IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT
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
	private boolean isInString;
	private boolean isInSymbol;
	private RubyParserResult result;
	private int origOffset;
	private int origLength;
	private String contents;

	private IToken fSavedToken = null;
	private int fSavedLength = -1;
	private int fSavedOffset = -1;
	private boolean lastWasComment;

	public RubyTokenScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		lexer = new RubyYaccLexer();
		parserSupport = new ParserSupport();
		parserSupport.setConfiguration(new RubyParserConfiguration());
		result = new RubyParserResult();
		parserSupport.setResult(result);
		lexer.setParserSupport(parserSupport);
		initialize();
	}

	public int getTokenLength() {
		if (lastWasComment) {
			lastWasComment = false;
			return tokenLength;
		}
		if (fSavedLength != -1) {
			int length = fSavedLength;
			fSavedLength = -1;
			return length;
		}
		return tokenLength;
	}

	public int getTokenOffset() {
		if (lastWasComment) {			
			return oldOffset;
		}
		if (fSavedOffset != -1) {
			int offset = fSavedOffset;
			fSavedOffset = -1;
			return offset;
		}
		return oldOffset;
	}

	public IToken nextToken() {
		if (fSavedToken != null) {
			IToken returnToken = fSavedToken;
			fSavedToken = null;
			return returnToken;
		}
		oldOffset = getOffset();
		tokenLength = 0;
		IToken returnValue = getToken(IRubyColorConstants.RUBY_DEFAULT);
		boolean isEOF = false;
		try {
			isEOF = !lexer.advance();
			if (isEOF) {
				returnValue = Token.EOF;
			} else {
				returnValue = token(lexer.token());
			}
			List comments = result.getCommentNodes();
			if (comments != null && !comments.isEmpty()) {
				CommentNode comment;
				while (!comments.isEmpty()) {
					comment = (CommentNode) comments.remove(0);
					tokenLength += comment.getContent().length() + 1;
				}
				fSavedToken = returnValue;
				fSavedOffset = oldOffset + tokenLength;
				if (!isEOF) {
					fSavedLength = getOffset() - fSavedOffset;
				} else {
					fSavedOffset--;
					tokenLength -= 2;
					fSavedLength = 0;
				}
				lastWasComment = true;
				return getToken(IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT);
			}
		} catch (SyntaxException se) {
			if (lexerSource.getOffset() - origLength == 0)
				return Token.EOF; // return eof if we hit a problem found at
									// end of parsing
			else
				tokenLength = getOffset() - oldOffset;
			return getToken(IRubyColorConstants.RUBY_ERROR);
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

	@Override
	public Token getToken(String key) {
		if (key.equals(IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT)) // if we know it's a comment, force it!
			return super.getToken(key);
		if (isInSymbol)
			return super.getToken(IRubyColorConstants.RUBY_SYMBOL);
		if (isInRegexp)
			return super.getToken(IRubyColorConstants.RUBY_REGEXP);
		if (isInString)
			return super.getToken(IRubyColorConstants.RUBY_STRING);
		return super.getToken(key);
	}

	private IToken token(int i) {
		if (isInSymbol) {
			if ((i == Tokens.tAREF) || (i == Tokens.tASET) || (i == Tokens.tIDENTIFIER) 
					|| (i == Tokens.tIVAR) || (i == Tokens.tCVAR) || (i == Tokens.tMINUS)
					|| (i == Tokens.tPLUS) || (i == Tokens.tPIPE) || (i == Tokens.tCARET)
					|| (i == Tokens.tLT) || (i == Tokens.tGT) || (i == Tokens.tAMPER)
					|| (i == Tokens.tSTAR2) || (i == Tokens.tDIVIDE) || (i == Tokens.tPERCENT)
					|| (i == Tokens.tBACK_REF2) || (i == Tokens.tTILDE)) {
				isInSymbol = false;
				return getToken(IRubyColorConstants.RUBY_SYMBOL);
			}
			if (i == Tokens.tASSOC || i == 44 /* ',' */) {
				isInSymbol = false;
				return getToken(IRubyColorConstants.RUBY_DEFAULT);
			}
		}
		if (i >= 257 && i <= 303)
			return getToken(IRubyColorConstants.RUBY_KEYWORD);
		switch (i) {
		case Tokens.tSYMBEG:
			isInSymbol = true;
			return getToken(IRubyColorConstants.RUBY_SYMBOL);
		case Tokens.tGVAR:
			return getToken(IRubyColorConstants.RUBY_GLOBAL);
		case Tokens.tIVAR:
		case Tokens.tCVAR: // FIXME Allow for unique coloring of class variables...
			return getToken(IRubyColorConstants.RUBY_INSTANCE_VARIABLE);
		case Tokens.tFLOAT:
		case Tokens.tINTEGER:
			// A character is marked as an integer, lets check for that special case...
			if ((((oldOffset - origOffset) + 1) < contents.length()) && (contents.charAt((oldOffset - origOffset) + 1) == '?'))
				return getToken(IRubyColorConstants.RUBY_CHARACTER);
			return getToken(IRubyColorConstants.RUBY_FIXNUM);
		case Tokens.tSTRING_CONTENT:
			return getToken(IRubyColorConstants.RUBY_STRING);
		case Tokens.tSTRING_BEG:
			isInString = true;
			return getToken(IRubyColorConstants.RUBY_STRING);
		case Tokens.tSTRING_END:
			isInString = false;
			return getToken(IRubyColorConstants.RUBY_STRING);
		case Tokens.tREGEXP_BEG:
			isInRegexp = true;
			return getToken(IRubyColorConstants.RUBY_REGEXP);
		case Tokens.tREGEXP_END:
			isInRegexp = false;
			return getToken(IRubyColorConstants.RUBY_REGEXP);
		default:
			return getToken(IRubyColorConstants.RUBY_DEFAULT);
		}
	}

	public void setRange(IDocument document, int offset, int length) {
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		parserSupport.initTopLocalVariables();
		lastWasComment = false;
		fSavedLength = -1;
		fSavedToken = null;
		fSavedOffset = -1;
		if (offset == 0) {
			isInRegexp = false;
			isInString = false;
			isInSymbol = false;
		}
		try {
			contents = document.get(offset, length);
			lexerSource = new LexerSource("filename", new StringReader(contents));
			lexer.setSource(lexerSource);
		} catch (BadLocationException e) {
			RubyPlugin.log(e);
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

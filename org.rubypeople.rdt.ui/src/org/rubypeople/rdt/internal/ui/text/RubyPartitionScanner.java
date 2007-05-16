package org.rubypeople.rdt.internal.ui.text;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.jruby.ast.CommentNode;
import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.LexState;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.RubyYaccLexer;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.parser.ParserSupport;
import org.jruby.parser.RubyParserConfiguration;
import org.jruby.parser.RubyParserResult;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyPartitionScanner implements IPartitionTokenScanner {

	private RubyYaccLexer lexer;
	private ParserSupport parserSupport;
	private RubyParserResult result;
	private boolean lastWasComment;
	private int fSavedLength;
	private IToken fSavedToken;
	private int fSavedOffset;
	private String contents;
	private LexerSource lexerSource;
	private int origOffset;
	private int origLength;
	private int tokenLength;
	private int oldOffset;
	
	// XXX Also do strings, regex partitions!
	public final static String RUBY_MULTI_LINE_COMMENT = IRubyPartitions.RUBY_MULTI_LINE_COMMENT;
	public final static String RUBY_SINGLE_LINE_COMMENT = IRubyPartitions.RUBY_SINGLE_LINE_COMMENT;

	public static final String[] LEGAL_CONTENT_TYPES = {
			RUBY_MULTI_LINE_COMMENT, RUBY_SINGLE_LINE_COMMENT
			};

	public RubyPartitionScanner() {
		lexer = new RubyYaccLexer();
		parserSupport = new ParserSupport();
		parserSupport.setConfiguration(new RubyParserConfiguration());
		result = new RubyParserResult();
		parserSupport.setResult(result);
		lexer.setParserSupport(parserSupport);
		lexer.setWarnings(new NullWarnings());
	}

	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		parserSupport.initTopLocalVariables();
		lastWasComment = false;
		fSavedLength = -1;
		fSavedToken = null;
		fSavedOffset = -1;
		try {
			contents = document.get(offset, length);
			lexerSource = new LexerSource("filename", new StringReader(contents));
			lexer.setSource(lexerSource);
		} catch (BadLocationException e) {
			lexerSource = new LexerSource("filename", new StringReader(""));
			lexer.setSource(lexerSource);
		}
		origOffset = offset;
		origLength = length;
	}

	public int getTokenLength() {
		if (lastWasComment) {
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
		if (lastWasComment) {
			lastWasComment = false;
		}
		if (fSavedToken != null) {
			IToken returnToken = fSavedToken;
			fSavedToken = null;
			return returnToken;
		}
		oldOffset = getOffset();
		tokenLength = 0;
		IToken returnValue = new Token(null);
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
				boolean firstComment = true;
				int endOffset = 0;
				boolean multiline = false;
				while (!comments.isEmpty()) {
					comment = (CommentNode) comments.remove(0);
					if (firstComment) {
						String src = ASTUtil.getSource(contents, comment);
						if (src != null && src.startsWith("=begin")) multiline = true;
						firstComment = false;
					    oldOffset = origOffset + comment.getPosition().getStartOffset(); // correct start offset, since when a line with nothing but spaces on it appears before comment, we get messed up positions
					}
					endOffset = origOffset + comment.getPosition().getEndOffset();					
				}
				tokenLength = endOffset - oldOffset;
				fSavedToken = returnValue;
				fSavedOffset = oldOffset + tokenLength;
				if (!isEOF) {
					fSavedLength = getOffset() - fSavedOffset;
				} else {
					fSavedOffset--;
					fSavedLength = 0;
				}
				lastWasComment = true;
				// FIXME What about multiline comments?!
				String contentType = RUBY_SINGLE_LINE_COMMENT;
				if (multiline) contentType = RUBY_MULTI_LINE_COMMENT;
				return new Token(contentType);
			}
		} catch (SyntaxException se) {
			if (lexerSource.getOffset() - origLength == 0)
				return Token.EOF; // return eof if we hit a problem found at
									// end of parsing
			else
				tokenLength = getOffset() - oldOffset;
			return new Token(null);
		} catch (IOException e) {
			RubyPlugin.log(e);
		}
		if (!isEOF)
			tokenLength = getOffset() - oldOffset;
		return returnValue;
	}
	
	private IToken token(int i) {
		return new Token(null);
	}

	private int getOffset() {
		return lexerSource.getOffset() + origOffset;
	}

	public void setRange(IDocument document, int offset, int length) {
		setPartialRange(document, offset, length, null, 0);
	}

}

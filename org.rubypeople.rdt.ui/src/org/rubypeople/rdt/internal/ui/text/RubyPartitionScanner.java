package org.rubypeople.rdt.internal.ui.text;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
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
import org.jruby.parser.Tokens;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyPartitionScanner implements IPartitionTokenScanner {
		
	private static final String BEGIN = "=begin";

	private static class QueuedToken {
		private IToken token;
		private int length;
		private int offset;

		QueuedToken(IToken token, int offset, int length) {
			this.token = token;
			this.length = length;
			this.offset = offset;
		}
		
		public int getLength() {
			return length;
		}
		
		public int getOffset() {
			return offset;
		}
		
		public IToken getToken() {
			return token;
		}
	}
	
	private RubyYaccLexer lexer;
	private ParserSupport parserSupport;
	private RubyParserResult result;
	private String fContents;
	private LexerSource lexerSource;
	private int origOffset;
	private int origLength;
	private int fLength;
	private int fOffset;
	
	private List<QueuedToken> fQueue = new ArrayList<QueuedToken>();
	private String fContentType = RUBY_DEFAULT;
	private boolean inSingleQuote;
	

	public final static String RUBY_MULTI_LINE_COMMENT = IRubyPartitions.RUBY_MULTI_LINE_COMMENT;
	public final static String RUBY_SINGLE_LINE_COMMENT = IRubyPartitions.RUBY_SINGLE_LINE_COMMENT;
	public final static String RUBY_STRING = IRubyPartitions.RUBY_STRING;
	public final static String RUBY_REGULAR_EXPRESSION = IRubyPartitions.RUBY_REGULAR_EXPRESSION;
	public static final String RUBY_DEFAULT = IDocument.DEFAULT_CONTENT_TYPE;
	public static final String RUBY_COMMAND = IRubyPartitions.RUBY_COMMAND;
	
	public static final String[] LEGAL_CONTENT_TYPES = {
			RUBY_DEFAULT, RUBY_MULTI_LINE_COMMENT, RUBY_SINGLE_LINE_COMMENT, RUBY_REGULAR_EXPRESSION, RUBY_STRING, RUBY_COMMAND
			};

	public RubyPartitionScanner() {
		lexer = new RubyYaccLexer();
		parserSupport = new ParserSupport();
		parserSupport.setConfiguration(new RubyParserConfiguration(false));
		result = new RubyParserResult();
		parserSupport.setResult(result);
		lexer.setParserSupport(parserSupport);
		lexer.setWarnings(new NullWarnings());
	}

	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
		reset();
		int myOffset = offset;
		if (contentType != null) {
			int diff = offset - partitionOffset;
			myOffset = partitionOffset; // backtrack to beginning of partition so we don't get in weird state
			length += diff;
		}
		if (myOffset == -1) myOffset = 0;
		try {			
			fContents = document.get(myOffset, length);
			lexerSource = new LexerSource("filename", new StringReader(fContents), 0, true);
			lexer.setSource(lexerSource);
		} catch (BadLocationException e) {
			lexerSource = new LexerSource("filename", new StringReader(""), 0, true);
			lexer.setSource(lexerSource);
		}
		origOffset = myOffset;
		origLength = length;
	}

	private void reset() {
		lexer.reset();
		lexer.setState(LexState.EXPR_BEG);
		parserSupport.initTopLocalVariables();
		fQueue.clear();
		inSingleQuote = false;
	}

	public int getTokenLength() {
		return fLength;
	}

	public int getTokenOffset() {
		return fOffset;
	}

	public IToken nextToken() {
		if (!fQueue.isEmpty()) {
			return popTokenOffQueue();
		}
		fOffset = getOffset();
		fLength = 0;
		IToken returnValue = new Token(RUBY_DEFAULT);
		boolean isEOF = false;
		try {
			isEOF = !lexer.advance();
			if (isEOF) {
				returnValue = Token.EOF;
			} else {
				int lexerToken = lexer.token();
				if (!inSingleQuote && lexerToken == Tokens.tSTRING_DVAR) { // we hit a single dynamic variable
					addPoundToken();
					scanDynamicVariable();
					setLexerPastDynamicSectionOfString();
					return popTokenOffQueue();
				} else if (!inSingleQuote && lexerToken == Tokens.tSTRING_DBEG) { // if we hit dynamic code inside a string
					addPoundBraceToken();
					scanTokensInsideDynamicPortion();			
					addClosingBraceToken();
					setLexerPastDynamicSectionOfString();
					return popTokenOffQueue();
				}
				returnValue = getToken(lexerToken);				
			}
			List comments = result.getCommentNodes();
			if (comments != null && !comments.isEmpty()) {
				parseOutComments(comments);
				addQueuedToken(returnValue, isEOF); // Queue the normal token we just ate up
				comments.clear();
				return popTokenOffQueue();
			}
		} catch (SyntaxException se) {
			if (se.getMessage().equals("embedded document meets end of file")) {
				// TODO recover somehow by removing this chunk out of the fContents?
				setOffset(se.getPosition().getStartOffset());
				fLength = fContents.length() - se.getPosition().getStartOffset();
				return new Token(RUBY_MULTI_LINE_COMMENT);
			}
			
			if (lexerSource.getOffset() - origLength == 0)
				return Token.EOF; // return eof if we hit a problem found at
									// end of parsing
			else
				fLength = getOffset() - fOffset;
			return new Token(RUBY_DEFAULT);
		} catch (IOException e) {
			RubyPlugin.log(e);
		}
		if (!isEOF)
			fLength = getOffset() - fOffset;
		return returnValue;
	}

	private void setOffset(int offset) {
		fOffset = offset;
	}

	private void addPoundToken() {
		addStringToken(1);// add token for the #
	}

	private void scanDynamicVariable() {
		int whitespace = fContents.indexOf(' ', fOffset - origOffset); // read until whitespace or '"'
		if (whitespace == -1) whitespace = Integer.MAX_VALUE;
		int doubleQuote = fContents.indexOf('"', fOffset - origOffset);
		if (doubleQuote == -1) doubleQuote = Integer.MAX_VALUE;
		int end = Math.min(whitespace, doubleQuote);
		// FIXME If we can't find whitespace or doubleQuote, we are pretty screwed.
		String possible = null;
		if (end == -1) {
			possible = fContents.substring(fOffset - origOffset);
		} else {
			possible = fContents.substring(fOffset - origOffset, end);
		}
		RubyPartitionScanner scanner = new RubyPartitionScanner();
		IDocument document = new Document(possible);
		scanner.setRange(document, 0, possible.length());
		IToken token;
		while (!(token = scanner.nextToken()).isEOF()) {
			push(new QueuedToken(token, scanner.getTokenOffset() + (fOffset), scanner.getTokenLength()));
		}
		setOffset(fOffset + possible.length());
	}

	private void scanTokensInsideDynamicPortion() {
		String possible = new String(fContents.substring(fOffset - origOffset));			
		int end = findEnd(possible);
		if (end != -1) {
			possible = possible.substring(0, end); 
		} else {
			possible = possible.substring(0);
		}
		RubyPartitionScanner scanner = new RubyPartitionScanner();
		IDocument document = new Document(possible);
		scanner.setRange(document, 0, possible.length());
		IToken token;
		while (!(token = scanner.nextToken()).isEOF()) {
			push(new QueuedToken(token, scanner.getTokenOffset() + fOffset, scanner.getTokenLength()));
		}
		setOffset(fOffset + possible.length());
	}

	private int findEnd(String possible) {
		return new EndBraceFinder(possible).find();
	}

	private void addPoundBraceToken() {
		addStringToken(2); // add token for the #{
	}
	
	private void addStringToken(int length) {
		push(new QueuedToken(new Token(fContentType), fOffset, length));
		setOffset(fOffset + length); // move past token
	}

	private void addClosingBraceToken() {
		addStringToken(1);
	}

	private void setLexerPastDynamicSectionOfString() throws IOException {
		IDocument document;
		StringBuffer fakeContents = new StringBuffer();
		int start = fOffset - 1;
		for (int i = 0; i < start; i++) {
			fakeContents.append(" ");
		}
		if (fContentType.equals(RUBY_REGULAR_EXPRESSION)) {
			fakeContents.append('/');
		} else if (fContentType.equals(RUBY_COMMAND)) {
			fakeContents.append('`');
		} else {
			fakeContents.append('"');
		}
		if ((fOffset - origOffset) < origLength) {
			fakeContents.append(new String(fContents.substring((fOffset - origOffset)))); // BLAH removed + 1 from end here
		}
		document = new Document(fakeContents.toString());
		List<QueuedToken> queueCopy = new ArrayList<QueuedToken>(fQueue);
		setPartialRange(document, start, fakeContents.length() - start, null, start);
		fQueue = new ArrayList<QueuedToken>(queueCopy);
		lexer.advance();
	}

	private void parseOutComments(List comments) {
		int i = 0;
		for (Iterator iter = comments.iterator(); iter.hasNext();) {
			CommentNode comment = (CommentNode) iter.next();
			int offset = correctOffset(comment);
			int length = comment.getContent().length();
			Token token = new Token(getContentType(comment));
			push(new QueuedToken(token, offset, length));
			i++;
		}
	}

	private IToken popTokenOffQueue() {
		QueuedToken token = fQueue.remove(0);
		setOffset(token.getOffset());
		Assert.isTrue(token.getLength() >= 0);
		fLength = token.getLength();
		return token.getToken();
	}

	private IToken getToken(int i) {
		// If we hit a 32 (space) inside a qword, just return string content type (not default)
		// FIXME IF we're in qwords, we should inspect the contents because it may be a variable
		if (i == 32) {
			return new Token(fContentType);
		}
		switch (i) {
		case Tokens.tSTRING_CONTENT:
			return new Token(fContentType);
		case Tokens.tSTRING_BEG:
			String token = fContents.substring(fOffset - origOffset, lexerSource.getOffset());
			if (token.trim().equals("'")) {
				inSingleQuote = true;
			}
			fContentType = RUBY_STRING;
			return new Token(RUBY_STRING);
		case Tokens.tXSTRING_BEG:
			fContentType = RUBY_COMMAND;
			return new Token(RUBY_COMMAND);
		case Tokens.tQWORDS_BEG:
			fContentType = RUBY_STRING;
			return new Token(RUBY_STRING);
		case Tokens.tSTRING_END:
			String oldContentType = fContentType;
			fContentType = RUBY_DEFAULT;
			inSingleQuote = false;
			return new Token(oldContentType);
		case Tokens.tREGEXP_BEG:
			fContentType = RUBY_REGULAR_EXPRESSION;
			return new Token(RUBY_REGULAR_EXPRESSION);
		case Tokens.tREGEXP_END:
			fContentType = RUBY_DEFAULT;
			return new Token(RUBY_REGULAR_EXPRESSION);
		default:
			return new Token(RUBY_DEFAULT);
		}
	}

	/**
	 * correct start offset, since when a line with nothing but spaces on it appears before comment, 
	 * we get messed up positions
	 */
	private int correctOffset(CommentNode comment) {
		return origOffset + comment.getPosition().getStartOffset();
	}

	private boolean isCommentMultiLine(CommentNode comment) {
		String src = ASTUtil.getSource(fContents, comment);
		if (src != null && src.startsWith(BEGIN)) return true;
		return false;
	}

	private String getContentType(CommentNode comment) {
		if (isCommentMultiLine(comment)) return RUBY_MULTI_LINE_COMMENT;
		return RUBY_SINGLE_LINE_COMMENT;
	}

	private void addQueuedToken(IToken returnValue, boolean isEOF) {
		// grab end of last comment (last thing in queue)
		QueuedToken token = peek();
		setOffset(token.getOffset() + token.getLength());
		int length = getOffset() - fOffset;
		if (length < 0 ) {
			length = 0;
		}
		push(new QueuedToken(returnValue, fOffset, length));
	}
	
	private QueuedToken peek() {
		return fQueue.get(fQueue.size() - 1);
	}

	private void push(QueuedToken token) {
		Assert.isTrue(token.getLength() >= 0);
		fQueue.add(token);
	}

	private int getOffset() {
		return lexerSource.getOffset() + origOffset;
	}

	public void setRange(IDocument document, int offset, int length) {
		setPartialRange(document, offset, length, null, -1);
	}

	private static class EndBraceFinder {
		private String input;
		private List<String> stack;
		
		public EndBraceFinder(String possible) {
			this.input = possible;
			stack = new ArrayList<String>();
		}
		
		public int find() {
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				switch (c) {
				case '\\':
					// skip next character
					i++;
					break;
				case '"':
					if (topEquals("\"")) {
						pop();
					} else {
						push("\"");
					}
					break;
				case '\'':
					if (topEquals("'")) {
						pop();
					} else if (!topEquals("\"")){
						push("'");
					}
					break;
				case '#':
					// Only add if we're inside a double quote string
					if (topEquals("\"")) {
						c = input.charAt(i + 1);
						if (c == '{')
							push("#{");
					}					
					break;
				case '}':
					if (stack.isEmpty()) { // if not in open state
						return i;
					}
					if (topEquals("#{")) {
						pop();
					} 
					break;
				default:
					break;
				}
			}		
			return -1;
		}

		private boolean topEquals(String string) {
			String open = peek();
			return open != null && open.equals(string);
		}

		private boolean push(String string) {
			return stack.add(string);
		}

		private String pop() {
			return stack.remove(stack.size() - 1);
		}

		private String peek() {
			if (stack.isEmpty())
				return null;				
			return stack.get(stack.size() - 1);
		}
	}
}

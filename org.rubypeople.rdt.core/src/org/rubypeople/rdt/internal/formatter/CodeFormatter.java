package org.rubypeople.rdt.internal.formatter;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public class CodeFormatter {

	private final static String BLOCK_BEGIN_RE = "(class|module|def|if|unless|case|while|until|for|begin|do)";
	private final static String BLOCK_MID_RE = "(else|elsif|when|rescue|ensure)";
	private final static String BLOCK_END_RE = "(end)";
	private final static String DELIMITER_RE = "[?$/(){}#\\`.:\\]\\[]";
	private final static String[] LITERAL_BEGIN_LITERALS = { "\"", "'", "=begin", "%[Qqrxw]?.", "/", "<<[\\-]?[']?[a-zA-Z_]+[']?" };
	private final static String[] LITERAL_END_RES = { "[^\\\\](\\\\\\\\)*\"", "[^\\\\](\\\\\\\\)*'", "=end", "", "", "" };
	private final int BLOCK_BEGIN_PAREN = 2;
	private final int BLOCK_MID_PAREN = 5;
	private final int BLOCK_END_PAREN = 8;
	private final int LITERAL_BEGIN_PAREN = 10;
	
	private static RE MODIFIER_RE ;
	private static RE OPERATOR_RE ;
	private static RE NON_BLOCK_DO_RE ;
	
	private static String LITERAL_BEGIN_RE; // automatically concatenated from LITERAL_BEGIN_LITERALS	
	private static RE[] LITERAL_END_RES_COMPILED;
	static {
		LITERAL_END_RES_COMPILED = new RE[LITERAL_END_RES.length];
		for (int i = 0; i < LITERAL_END_RES.length; i++) {
			try {
				LITERAL_END_RES_COMPILED[i] = new RE(LITERAL_END_RES[i]);
			} catch (RESyntaxException e) {
				System.out.println(e);
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		for (int i = 0; i < LITERAL_BEGIN_LITERALS.length; i++) {
			sb.append(LITERAL_BEGIN_LITERALS[i]);
			if (i < LITERAL_BEGIN_LITERALS.length - 1) {
				sb.append("|");
			}
		}
		sb.append(")");
		LITERAL_BEGIN_RE = sb.toString();
		try {
			MODIFIER_RE = new RE("if|unless|while|until|rescue");
			OPERATOR_RE = new RE("[\\-,.+*/%&|\\^~=<>:]") ;
			NON_BLOCK_DO_RE = new RE("(^|[:space:])(while|until|for|rescue)[:space:]") ;
		} catch (RESyntaxException e) {
			System.out.println(e);
		}
	}

	private char fillCharacter;

	public CodeFormatter() {
		this(' ');
	}

	public CodeFormatter(char fillCharacter) {
		this.fillCharacter = fillCharacter;
	}

	public String formatString(String unformatted) {
		AbstractBlockMarker firstAbstractBlockMarker = this.createBlockMarkerList(unformatted);
		firstAbstractBlockMarker.print();
		try {
			return this.formatString(unformatted, firstAbstractBlockMarker);
		} catch (RESyntaxException ex) {
			return unformatted;
		}
	}

	protected String formatString(String unformatted, AbstractBlockMarker abstractBlockMarker) throws RESyntaxException {
		RE re = new RE("\n");
		String[] lines = re.split(unformatted);
		IndentationState state = null;
		StringBuffer formatted = new StringBuffer();
		RE whitespaceMatcher = new RE("^[\t ]*");
		for (int i = 0; i < lines.length; i++) {
			whitespaceMatcher.match(lines[i]);
			int leadingWhitespace = whitespaceMatcher.getParenEnd(0);
			if (state == null) {
				state = new IndentationState(unformatted, 2, leadingWhitespace, fillCharacter);
			}
			state.incPos(leadingWhitespace);
			String strippedLine = lines[i].substring(leadingWhitespace);
			AbstractBlockMarker newBlockMarker = this.findNextBlockMarker(abstractBlockMarker, state.getPos(), state);
			if (newBlockMarker != null) {
				newBlockMarker.indentBeforePrint(state);
				newBlockMarker.appendIndentedLine(formatted, state, lines[i], strippedLine);
				state.saveIndentation();
				newBlockMarker.indentAfterPrint(state);
				abstractBlockMarker = newBlockMarker;
			} else {
				abstractBlockMarker.appendIndentedLine(formatted, state, lines[i], strippedLine);
			}
			if (i != lines.length - 1) {
				formatted.append("\n");
			}
			state.incPos(strippedLine.length() + 1);
		}
		if (unformatted.lastIndexOf("\n") == unformatted.length() - 1) {
			formatted.append("\n");
		}
		return formatted.toString();
	}

	private AbstractBlockMarker findNextBlockMarker(AbstractBlockMarker abstractBlockMarker, int pos, IndentationState state) {
		AbstractBlockMarker startBlockMarker = abstractBlockMarker;
		while (abstractBlockMarker.getNext() != null && abstractBlockMarker.getNext().getPos() <= pos) {
			if (abstractBlockMarker != startBlockMarker) {
				abstractBlockMarker.indentBeforePrint(state);
				abstractBlockMarker.indentAfterPrint(state);
			}
			abstractBlockMarker = abstractBlockMarker.getNext();
		}
		return startBlockMarker == abstractBlockMarker ? null : abstractBlockMarker;
	}

	protected AbstractBlockMarker createBlockMarkerList(String unformatted) {
		RE blockBegin = null;
		RE blockMid = null;
		RE blockEnd = null;
		RE re = null;

		try {
			re =
				new RE(
					"(^|[:space:])"
						+ BLOCK_BEGIN_RE
						+ "($|[:space:])|(^|[:space:])"
						+ BLOCK_MID_RE
						+ "($|[:space:])|(^|[:space:])"
						+ BLOCK_END_RE
						+ "($|[:space:])|"
						+ LITERAL_BEGIN_RE
						+ "|"
						+ DELIMITER_RE);
		} catch (RESyntaxException e) {
			System.out.println(e);
		}
		int pos = 0;
		AbstractBlockMarker lastBlockMarker = new NeutralMarker("start", 0);
		AbstractBlockMarker firstBlockMarker = lastBlockMarker;
		while (pos != -1 && re.match(unformatted, pos)) {
			AbstractBlockMarker newBlockMarker = null;
			if (re.getParen(BLOCK_BEGIN_PAREN) != null) {
				pos = re.getParenEnd(BLOCK_BEGIN_PAREN);
				String blockBeginStr = re.getParen(BLOCK_BEGIN_PAREN) ;				
				if (MODIFIER_RE.match(blockBeginStr) && !this.isRubyExprBegin(unformatted, re.getParenStart(BLOCK_BEGIN_PAREN), "modifier") ) {
					continue ;
				}
				if (blockBeginStr.equals("do") && this.isNonBlockDo(unformatted, re.getParenStart(BLOCK_BEGIN_PAREN)) ) {
					continue ;
				}
				newBlockMarker = new BeginBlockMarker(re.getParen(BLOCK_BEGIN_PAREN), re.getParenStart(BLOCK_BEGIN_PAREN));				
			} else if (re.getParen(BLOCK_MID_PAREN) != null) {
				pos = re.getParenEnd(BLOCK_MID_PAREN);
				newBlockMarker = new MidBlockMarker(re.getParen(BLOCK_MID_PAREN), re.getParenStart(BLOCK_MID_PAREN));
			} else if (re.getParen(BLOCK_END_PAREN) != null) {
				pos = re.getParenEnd(BLOCK_END_PAREN);
				newBlockMarker = new EndBlockMarker(re.getParen(BLOCK_END_PAREN), re.getParenStart(BLOCK_END_PAREN));
			} else if (re.getParen(LITERAL_BEGIN_PAREN) != null) {
				pos = re.getParenEnd(LITERAL_BEGIN_PAREN);
				String matchedLiteralBegin = re.getParen(LITERAL_BEGIN_PAREN);
				if (matchedLiteralBegin.startsWith("%")) {
					int delimitChar = matchedLiteralBegin.charAt(matchedLiteralBegin.length() - 1);
					boolean expand = matchedLiteralBegin.charAt(1) != 'q';
					if (delimitChar == '[') {
						pos = this.forwardString(unformatted, pos, '[', ']', expand);
					} else if (delimitChar == '(') {
						pos = this.forwardString(unformatted, pos, '(', ')', expand);
					} else if (delimitChar == '{') {
						pos = this.forwardString(unformatted, pos, '{', '}', expand);
					} else if (delimitChar == '<') {
						pos = this.forwardString(unformatted, pos, '<', '>', expand);
					} else {
						pos = unformatted.indexOf(delimitChar, pos);
					}
				} else if (matchedLiteralBegin.startsWith("/")) {
					pos = this.forwardString(unformatted, pos, ' ', "/", true);
				} else if (matchedLiteralBegin.startsWith("'")) {
					if (pos > 1 && unformatted.charAt(pos - 2) == '$') {
						continue;
					}
					pos = this.forwardString(unformatted, pos, ' ', "'", true);
				} else if (matchedLiteralBegin.startsWith("<<")) {
					int startId = 2 ;
					int endId = matchedLiteralBegin.length()  ;
					boolean isMinus = (matchedLiteralBegin.charAt(startId) == '-') ;
					if (isMinus) {
						startId += 1 ;
					}
					if (startId < matchedLiteralBegin.length() -1 && matchedLiteralBegin.charAt(startId) == '\'' ) {
						startId += 1 ;
						endId -= 1 ;  
					}
					String reStr = (isMinus ? "" : "\n") + matchedLiteralBegin.substring(startId, endId) ;
					try {
						RE idSearch = new RE(reStr) ;
						if (idSearch.match(unformatted, pos)) {
							pos = idSearch.getParenEnd(0) ;
						}
						else {
							pos = -1 ;
						}
					} catch (RESyntaxException e1) {
						continue ;					
					}					
				} else {
					for (int i = 0; i < LITERAL_BEGIN_LITERALS.length; i++) {
						if (LITERAL_BEGIN_LITERALS[i].equals(matchedLiteralBegin)) {
							RE matchEnd = LITERAL_END_RES_COMPILED[i];
							pos = -1;
							if (matchEnd.match(unformatted, re.getParenEnd(LITERAL_BEGIN_PAREN) - 1)) {
								pos = matchEnd.getParenEnd(0);
							}
							break;
						}
					}
				}
				newBlockMarker = new NoFormattingMarker(matchedLiteralBegin, re.getParenStart(LITERAL_BEGIN_PAREN));
				if (pos != -1) {
					lastBlockMarker.setNext(newBlockMarker);
					lastBlockMarker = newBlockMarker;
					newBlockMarker = new NeutralMarker("", pos);
				}

			} else {
				String delimiter = re.getParen(0);
				if (delimiter.equals("#")) {
					pos = unformatted.indexOf("\n", re.getParenEnd(0));
					continue;
				} else if (delimiter.equals("{")) {
					newBlockMarker = new BeginBlockMarker("{", re.getParenStart(0));
				} else if (delimiter.equals("}")) {
					newBlockMarker = new EndBlockMarker("}", re.getParenStart(0));
				} else if (delimiter.equals("(")) {
					newBlockMarker = new FixLengthMarker("(", re.getParenStart(0));
				} else if (delimiter.equals(")")) {
					newBlockMarker = new NeutralMarker(")", re.getParenStart(0));
				}
				pos = re.getParenEnd(0);
			}

			if (newBlockMarker == null) {
				continue;
			}

			if (lastBlockMarker != null) {
				lastBlockMarker.setNext(newBlockMarker);
			}
			lastBlockMarker = newBlockMarker;
		}
		return firstBlockMarker;
	}

	/*
	(defun ruby-forward-string (term &optional end no-error expand)
	  (let ((n 1) (c (string-to-char term))
		(re (if expand
			(concat "[^\\]\\(\\\\\\\\\\)*\\([" term "]\\|\\(#{\\)\\)")
			  (concat "[^\\]\\(\\\\\\\\\\)*[" term "]"))))
		(while (and (re-search-forward re end no-error)
			               (if (match-beginning 3)
				              (ruby-forward-string "}{" end no-error nil)
			                  (> (setq n (if (eq (char-before (point)) c)
						          (1- n) (1+ n))) 0)))
		  (forward-char -1))
		(cond ((zerop n))
		  (no-error nil)
		  (error "unterminated string"))))
	*/

	protected int forwardString(String unformatted, int pos, char opening, char closing, boolean expand) {
		return this.forwardString(unformatted, pos, opening, "\\" + opening + "\\" + closing, expand);
	}

	protected int forwardString(String unformatted, int pos, char opening, String term, boolean expand) {
		int n = 1;
		try {
			RE re = new RE(expand ? "[" + term + "]|(#\\{)" : "[" + term + "]");
			while (re.match(unformatted, pos) && n > 0) {
				if (re.getParen(1) != null) {
					pos = this.forwardString(unformatted, re.getParenEnd(1), '{', "\\{\\}", expand);
				} else {
					pos = re.getParenEnd(0);
					if (pos > 2 && unformatted.charAt(pos - 2) == '\\' && unformatted.charAt(pos - 3) != '\\') {
						continue;
					}
					if (re.getParen(0).charAt(0) == opening) {
						n += 1;
					} else {
						n -= 1;
					}
				}
			}
		} catch (RESyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		}
		return pos;
	}
	/*	
		(defun ruby-expr-beg (&optional option)
		  (save-excursion
			(store-match-data nil)
			(skip-chars-backward " \t")
			(cond
			 ((bolp) t)
			 ((looking-at "\\?")
			  (or (bolp) (forward-char -1))
			  (not (looking-at "\\sw")))
			 (t
			  (forward-char -1)
			  (or (looking-at ruby-operator-re)
			       (looking-at "[\\[({,;]")
			       (and (not (eq option 'modifier))
				           (looking-at "[!?]"))
			      (and (looking-at ruby-symbol-re)
				     (skip-chars-backward ruby-symbol-chars)
				   (cond
				((or (looking-at ruby-block-beg-re)
					 (looking-at ruby-block-op-re)
					 (looking-at ruby-block-mid-re))
				 (goto-char (match-end 0))
				 (looking-at "\\>"))
				(t
				 (and (not (eq option 'expr-arg))
					  (looking-at "[a-zA-Z][a-zA-z0-9_]* +/[^ \t]"))))))))))
	*/

	protected int skipCharsBackward(String unformatted, int pos) {
		// skipCharsBackward returns the position of the first char which is not tab or space left from pos and is in the 
		// same line as pos
		do {
			if (pos == 0) {
				return 0;
			}
			if (unformatted.charAt(pos - 1) == '\n') {
				return pos;
			}
			pos -= 1;
		} while (unformatted.charAt(pos) == '\t' || unformatted.charAt(pos) == ' ');
		return pos;

	}
	
	protected int backToIndentation(String unformatted, int pos) {
		do {
			if (pos == 0) {
				return 0;
			}
			if (unformatted.charAt(pos - 1) == '\n') {
				break ;
			}
			pos -= 1;
		}  while (true);		
		while (unformatted.charAt(pos) == '\t' || unformatted.charAt(pos) == ' ' ) {
			pos += 1 ;
			if (pos == unformatted.length()) {
				break ;
			}
		}
		return pos ;
	}

	protected int posOfLineStart(String unformatted, int pos) {
		do {
			if (pos == 0) {
				return 0;
			}
			if (unformatted.charAt(pos - 1) == '\n') {
				break ;
			}
			pos -= 1;
		}  while (true);		
		return pos ;
	}

	protected boolean matchREBackward(String str, RE re) {
		int pos = str.length() -1 ;
		while (pos >= 0) {
			if (str.charAt(pos) == ';') {
				return false ;
			}
			if (re.match(str, pos)) {
				return true;
			}			
			pos -= 1;
		}  		
		return false ;
	}


	protected boolean isRubyExprBegin(String unformatted, int pos, String option) {
		int firstNonSpaceCharInLine = this.skipCharsBackward(unformatted, pos);
		if (firstNonSpaceCharInLine == 0 || unformatted.charAt(firstNonSpaceCharInLine - 1) == '\n') {
			return true;
		}
		char c = unformatted.charAt(firstNonSpaceCharInLine);
		if (c == ';') {
			return true;
		}
		String c_str = "" + c ;
		if (OPERATOR_RE.match(c_str )) {
			return true ;		
		}
		return false;
	}
	
	protected boolean isNonBlockDo(String unformatted, int pos) {
		int lineStart = this.posOfLineStart(unformatted, pos) ;
		return this.matchREBackward(unformatted.substring(lineStart, pos), NON_BLOCK_DO_RE) ;
	}
	
}

package org.rubypeople.rdt.core;


public class CompletionProposal {

	public static final int FIELD_REF = 2;
	public static final int KEYWORD = 3;
	public static final int LOCAL_VARIABLE_REF = 5;
	public static final int METHOD_REF = 6;
	public static final int METHOD_DECLARATION = 7;
	public static final int TYPE_REF = 9;
	public static final int VARIABLE_DECLARATION = 10;
	public static final int POTENTIAL_METHOD_DECLARATION = 11;
	public static final int METHOD_NAME_REFERENCE = 12;
	
	protected static final int FIRST_KIND = FIELD_REF;
	protected static final int LAST_KIND = METHOD_NAME_REFERENCE;

	/**
	 * Kind of completion request.
	 */
	private int completionKind;
	
	/**
	 * Offset in original buffer where ICodeAssist.codeComplete() was
	 * requested.
	 */
	private int completionLocation;
	
	/**
	 * Start position (inclusive) of source range in original buffer 
	 * containing the relevant token
	 * defaults to empty subrange at [0,0).
	 */
	private int tokenStart = 0;
	
	/**
	 * End position (exclusive) of source range in original buffer 
	 * containing the relevant token;
	 * defaults to empty subrange at [0,0).
	 */
	private int tokenEnd = 0;
	
	/**
	 * Completion string; defaults to empty string.
	 */
	private String completion = "";
	
	/**
	 * Start position (inclusive) of source range in original buffer 
	 * to be replaced by completion string; 
	 * defaults to empty subrange at [0,0).
	 */
	private int replaceStart = 0;
	
	/**
	 * End position (exclusive) of source range in original buffer 
	 * to be replaced by completion string;
	 * defaults to empty subrange at [0,0).
	 */
	private int replaceEnd = 0;
	
	/**
	 * Relevance rating; positive; higher means better;
	 * defaults to minimum rating.
	 */
	private int relevance = 1;
	
	/**
	 * Parameter names (for method completions), or
	 * <code>null</code> if none. Lazily computed.
	 * Defaults to <code>null</code>.
	 */
	private String[] parameterNames = null;
	
	/**
	 * Indicates whether parameter names have been computed.
	 */
	private boolean parameterNamesComputed = false;
	
	/**
	 * Simple name of the method, field,
	 * member, or variable relevant in the context, or
	 * <code>null</code> if none.
	 * Defaults to null.
	 */
	private String name = null;

	public CompletionProposal(int kind, String completion, int relevance) {
		this.completionKind = kind;
		this.completion = completion;
		this.relevance = relevance;
	}

	public int getKind() {
		return completionKind;
	}

	public String getCompletion() {
		return completion;
	}

	public int getReplaceStart() {
		return replaceStart;
	}

	public int getReplaceEnd() {
		return replaceEnd;
	}

	public int getCompletionLocation() {
		return completionLocation;
	}

	public int getRelevance() {
		return relevance;
	}

	public String getName() {
		return name;
	}

	public void setReplaceRange(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex < startIndex) {
			throw new IllegalArgumentException();
		}
		this.replaceStart = startIndex;
		this.replaceEnd = endIndex;		
	}
}

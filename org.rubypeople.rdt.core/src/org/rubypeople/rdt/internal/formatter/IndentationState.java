package org.rubypeople.rdt.internal.formatter;

public class IndentationState {
	private int lastIndentation ;
	private int indentationLevel ;
	private int indentationLength ;
	private int offset ;
	private int pos ;
	private int indentation ;
	private String unformattedText ;
	private char fillCharacter ;
	
	public IndentationState(String unformattedText, int indentationLength, int offset, char fillCharacter) {
		this.unformattedText = unformattedText ;
		this.indentationLength = indentationLength ;
		this.fillCharacter = fillCharacter ;
		this.offset = offset ;
		indentationLevel = 0 ;		
		pos = 0 ;
		this.calculateIndentation() ;		
	}
	
	public void decIndentationLevel() {
		indentationLevel -= 1 ;	
		this.calculateIndentation() ;
	}
	
	
	public void incIndentationLevel() {
		indentationLevel += 1 ;	
		this.calculateIndentation() ;
	}
	
	public void incPos(int increment) {
		pos += increment ;
	}
	
	public void calculateIndentation() {
		indentation = offset + indentationLength * indentationLevel ;	
	}
	



	public int getIndentation() {
		return indentation;
	}


	public int getIndentationLength() {
		return indentationLength;
	}


	public int getIndentationLevel() {
		return indentationLevel;
	}


	public int getOffset() {
		return offset;
	}


	public int getPos() {
		return pos;
	}


	public String getUnformattedText() {
		return unformattedText;
	}


	public void setIndentation(int indentation) {
		this.indentation = indentation;
	}


	public void setIndentationLevel(int indentationLevel) {
		this.indentationLevel = indentationLevel;
		this.calculateIndentation() ;
	}


	public void setOffset(int offset) {
		this.offset = offset;
		this.calculateIndentation() ;
	}


	public void setPos(int pos) {
		this.pos = pos;
	}
	
	public void saveIndentation() {
		this.lastIndentation = indentation ;	
	}
	
	public int getLastIndentation() {
		return lastIndentation ;	
	}
	
	protected String getIndentationString() {
		if (indentation <= 0) {
			return "";
		}
		char[] chars = new char[indentation];
		for (int i = 0; i < indentation; i++) {
			chars[i] = fillCharacter;
		}
		return new String(chars);
	}
	

}

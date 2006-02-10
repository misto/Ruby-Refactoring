package org.rubypeople.rdt.internal.formatter;

import java.util.Map;

import org.rubypeople.rdt.core.formatter.Indents;

public abstract class AbstractBlockMarker {
	protected int pos;
	private AbstractBlockMarker next;
	private String keyword;

	protected AbstractBlockMarker(String aKeyword, int aPos) {
		this.keyword = aKeyword;
		this.pos = aPos;
	}

	protected abstract void indentBeforePrint(IndentationState state);
	protected abstract void indentAfterPrint(IndentationState state);

	public int getPos() {
		return pos;
	}

	public AbstractBlockMarker getNext() {
		return next;
	}

	public String getKeyword() {
		return keyword;
	}

	protected void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setNext(AbstractBlockMarker next) {
		this.next = next;
	}

	public void appendIndentedLine(StringBuffer sb, IndentationState state, String originalLine, String strippedLine, Map options) {
        String spaces = "";
        for (int i = 0; i < state.getOffset(); i++) {
            spaces+= " ";
        }
        sb.append(spaces);
        sb.append(Indents.createIndentString(state.getIndentationLevel(), options));
		sb.append(strippedLine);
	}

	public void print() {
		System.out.println("Pos: " + pos + ", type: " + this.getClass().getName() + ", keyword: " + this.getKeyword()) ;
		if (next != null) {
			next.print() ;	
		}	
	}
	

}

package org.rubypeople.rdt.internal.ui.rubyeditor;


public class ExternalRubyEditor extends RubyAbstractEditor {
	
	public ExternalRubyEditor() {
		// TODO: should support ruler context menu for adding breakpoints
		// but have to solve problem regarding markers and resources first. 
		super();
	}
	
	public boolean isEditable() {
		return false;
	}

}

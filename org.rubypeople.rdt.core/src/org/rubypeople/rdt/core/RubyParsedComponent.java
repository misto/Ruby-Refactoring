package org.rubypeople.rdt.core;

import java.util.ArrayList;
import java.util.List;

public class RubyParsedComponent {

	protected String name;
	protected List children = new ArrayList();
	protected int nameLength;
	protected int offset;
	protected int length;
	protected int nameOffset;
	
	public RubyParsedComponent(String name) {
		this.name = name;
	}
	
	public List getChildren() {
		return children;
	}
	
	public String getName() {
		return name;
	}
	
	public void addChild(RubyParsedComponent childComponent) {
		children.add(childComponent);
	}

	public int nameOffset() {
		return nameOffset;
	}
	
	public void nameOffset(int nameOffset) {
		this.nameOffset = nameOffset;
	}

	public int nameLength() {
		return nameLength;
	}
	
	public void nameLength(int length) {
		nameLength = length;
	}
	
	public int offset() {
		return offset;
	}
	
	public void offset(int offset) {
		this.offset = offset;
	}
	
	public int length() {
		return length;
	}
	
	public void length(int length) {
		this.length = length;
	}
}

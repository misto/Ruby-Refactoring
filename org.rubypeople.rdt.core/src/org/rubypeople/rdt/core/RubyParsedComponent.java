package org.rubypeople.rdt.core;

import java.util.ArrayList;
import java.util.List;

public class RubyParsedComponent {

	protected String name;
	protected List children = new ArrayList();
	
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
		return -1;
	}

	public int nameLength() {
		return -1;
	}
	
	public int offset() {
		return -1;
	}
	
	public int length() {
		return -1;
	}
}

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

}

package org.rubypeople.rdt.internal.core.parser;

public class RubyFile extends RubyParsedComponent {
	public RubyFile(String fileName) {
		super(fileName);
	}
	
	public void add(Object object) {
		children.add(object);
	}
}

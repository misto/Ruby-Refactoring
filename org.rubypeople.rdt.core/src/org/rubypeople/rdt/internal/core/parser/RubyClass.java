package org.rubypeople.rdt.internal.core.parser;


public class RubyClass extends RubyParsedComponent {
	public RubyClass(String name) {
		super(name);
	}

	public void addMethod(String methodName) {
		children.add(new RubyMethod(methodName));
	}

	public void addInstanceVariable(String variableName) {
		children.add(new RubyInstanceVariable(variableName));
	}

	public void addClassVariable(String variableName) {
		children.add(new RubyClassVariable(variableName));
	}

}
package org.rubypeople.rdt.internal.core.parser;
import java.util.ArrayList;
import java.util.List;

public class RubyClass implements RubyParsedComponent {
	protected String name;
	protected List methods = new ArrayList();
	protected List instanceVariables = new ArrayList();
	protected List classVariables = new ArrayList();

	public RubyClass(String name) {
		this.name = name;
	}

	public void addMethod(String methodName) {
		methods.add(methodName);
	}

	public boolean equals(Object other) {
		return name.equals(((RubyClass)other).name);
	}

	public void addInstanceVariable(String variableName) {
		instanceVariables.add(variableName);
	}

	public List getInstanceVariables() {
		return instanceVariables;
	}

	public List getMethods() {
		return methods;
	}

	public void addClassVariable(String variableName) {
		classVariables.add(variableName);
	}

	public List getClassVariables() {
		return classVariables;
	}


	/* RubyParsedComponent */
	public List getChildren() {
		List allMyChildren = new ArrayList();

		allMyChildren.addAll(getClassVariables());
		allMyChildren.addAll(getInstanceVariables());
		allMyChildren.addAll(getMethods());

		return allMyChildren;
	}

	public int length() {
		return 0;
	}

	public int nameLength() {
		return 0;
	}

	public int nameOffset() {
		return 0;
	}

	public int offset() {
		return 0;
	}

	public String getName() {
		return name;
	}

}
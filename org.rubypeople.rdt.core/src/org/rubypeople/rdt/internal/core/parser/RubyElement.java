package org.rubypeople.rdt.internal.core.parser;

import java.util.ArrayList;
import java.util.List;

public class RubyElement implements IRubyElement {
	protected String name;
	protected List elements = new ArrayList();

	public RubyElement(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public Object[] getElements() {
		return elements.toArray();
	}

	public boolean equals(Object obj) {
		return (obj != null && obj instanceof IRubyElement) && this.name.equals(((IRubyElement) obj).getName());
	}

	public int hashCode() {
		return name.hashCode();
	}

	protected void addElement(IRubyElement element) {
		elements.add(element);
	}
	public boolean hasElements() {
		return !elements.isEmpty();
	}
}

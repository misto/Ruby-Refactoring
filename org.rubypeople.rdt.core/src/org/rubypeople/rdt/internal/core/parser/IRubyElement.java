package org.rubypeople.rdt.internal.core.parser;

public interface IRubyElement {
	String getName();
	boolean hasElements();
	Object[] getElements();
}

package org.rubypeople.rdt.internal.core.parser;

import java.util.List;

public interface RubyParsedComponent {
	List getChildren();
	String getName();
	int nameOffset();
	int nameLength();
	int offset();
	int length();
}

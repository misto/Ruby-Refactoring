package org.rubypeople.rdt.internal.core.parser;

import java.util.ArrayList;
import java.util.List;

public abstract class RubyParsedComponent {
	protected String name;
	protected List children = new ArrayList();

	protected RubyParsedComponent(String name) {
		this.name = name;
	}

	public List getChildren() {
		return children;
	}

	public String getName() {
		return name;
	}

	public int nameOffset() {
		return 0;
	}

	public int nameLength() {
		return 0;
	}

	public int offset() {
		return 0;
	}

	public int length() {
		return 0;
	}

	public boolean equals(Object other) {
		if (other != null)
			if (other instanceof RubyParsedComponent)
				return name.equals(((RubyParsedComponent)other).name);

		return false;
	}
}

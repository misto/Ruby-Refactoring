package org.rubypeople.rdt.internal.core.parser;

import java.util.ArrayList;
import java.util.List;

public class RubyFile extends ArrayList implements RubyParsedComponent {
	protected String fileName;

	public RubyFile(String fileName) {
		this.fileName = fileName;
	}

	public List getChildren() {
		return this;
	}

	public String getName() {
		return fileName;
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
}

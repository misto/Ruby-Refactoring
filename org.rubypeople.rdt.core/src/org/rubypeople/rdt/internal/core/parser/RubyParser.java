package org.rubypeople.rdt.internal.core.parser;

import org.eclipse.core.resources.IFile;

public class RubyParser {
	public RubyParsedComponent getComponentHierarchy(IFile rubyFile) {
		return JRubyParser.parse(rubyFile.getLocation().toOSString());
	}
}

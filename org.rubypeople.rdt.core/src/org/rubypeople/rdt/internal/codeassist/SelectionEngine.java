package org.rubypeople.rdt.internal.codeassist;

import java.io.CharArrayReader;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class SelectionEngine {
	
	public IRubyElement[] select(IRubyScript script, int start, int end) throws RubyModelException {
		char[] contents = script.getBuffer().getCharacters();
		RubyParser parser = new RubyParser();
		Node node = parser.parse((IFile) script.getResource(), new CharArrayReader(contents));
		
		SelectionVisitor visitor = new SelectionVisitor(script, start, end);
		if (node != null) node.accept(visitor);
		
		return visitor.getElements();
	}

}

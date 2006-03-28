package org.rubypeople.rdt.internal.codeassist;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.core.Openable;

public class SelectionEngine {

	private Openable openable;

	public SelectionEngine(Openable openable) {
		this.openable = openable;
	}

	public IRubyElement[] select(IRubyScript cu, int start, int end) {
		// TODO Auto-generated method stub
		return new IRubyElement[0];
	}

}

package org.rubypeople.rdt.internal.ui.rubyeditor.outline;

import org.rubypeople.rdt.internal.core.parser.ast.IRubyElement;
import org.rubypeople.rdt.internal.core.parser.ast.RubyScript;

public class RubyModel implements IRubyElement {

	private RubyScript script;

	public RubyModel() {
	}

	public String getName() {
		return script.getName();
	}

	public boolean hasElements() {
		return script.hasElements();
	}

	public Object[] getElements() {
		return script.getElements();
	}

	public RubyScript getScript() {
		return script;
	}

	public void setScript(RubyScript script) {
		this.script = script;
	}
}
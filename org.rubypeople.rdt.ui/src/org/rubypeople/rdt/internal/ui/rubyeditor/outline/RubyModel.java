package org.rubypeople.rdt.internal.ui.rubyeditor.outline;

import org.rubypeople.rdt.core.IRubyScript;

public class RubyModel {

	private IRubyScript script;

	public RubyModel() {}

	public IRubyScript getScript() {
		return script;
	}

	public void setScript(IRubyScript script) {
		this.script = script;
	}
}
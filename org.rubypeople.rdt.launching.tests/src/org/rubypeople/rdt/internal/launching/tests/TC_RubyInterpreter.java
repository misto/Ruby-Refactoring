package org.rubypeople.rdt.internal.launching.tests;

import junit.framework.TestCase;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;

public class TC_RubyInterpreter extends TestCase {

	public TC_RubyInterpreter(String name) {
		super(name);
	}

	public void testEquals() {
		RubyInterpreter interpreterOne = new RubyInterpreter("InterpreterOne", new Path("/InterpreterOnePath"));
		RubyInterpreter similarInterpreterOne = new RubyInterpreter("InterpreterOne", new Path("/InterpreterOnePath"));
		assertTrue("Interpreters should be equal.", interpreterOne.equals(similarInterpreterOne));
		
		RubyInterpreter interpreterTwo = new RubyInterpreter("InterpreterTwo", new Path("/InterpreterTwoPath"));
		assertTrue("Interpreters should not be equal.", !interpreterOne.equals(interpreterTwo));
	}
}

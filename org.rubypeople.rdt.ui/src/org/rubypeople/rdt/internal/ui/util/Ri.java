package org.rubypeople.rdt.internal.ui.util;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.RubyRuntime;

public class Ri {
	
	public final static String DEFAULT_CMD = "ri";
	
	private Ri(){}
	
	public static IPath getDefaultPath(){
		RubyInterpreter interpreter = RubyRuntime.getDefault().getSelectedInterpreter();
		assert interpreter != null;
		IPath path = interpreter.getInstallLocation();
		path = path.uptoSegment(path.segmentCount() - 1).append(DEFAULT_CMD);
		return path;
	}
}


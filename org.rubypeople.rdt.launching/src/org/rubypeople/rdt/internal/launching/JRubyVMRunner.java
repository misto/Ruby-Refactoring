package org.rubypeople.rdt.internal.launching;

import java.util.List;

import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMRunner;

public class JRubyVMRunner extends StandardVMRunner implements IVMRunner {

	public JRubyVMRunner(IVMInstall vmInstance) {
		super(vmInstance);
	}
	
	@Override
	protected void addArguments(String[] args, List<String> v, boolean isVMArgs) {
		if (args == null) {
			return;
		}
		for (int i= 0; i < args.length; i++) {
			if (isVMArgs && args[i].charAt(0) == '-') {
				v.add("\"" + args[i] + " " + args[++i] + "\""); // FIXME Only do this if we're launching a .bat file?
			} else {
				v.add(args[i]);
			}
		}
	}

}

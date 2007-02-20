package org.rubypeople.rdt.refactoring.action;

import org.rubypeople.rdt.refactoring.core.rename.RenameRefactoring;

public class RenameAction extends WorkbenchWindowActionDelegate {

	public static final Class[] renamableClasses = {};
	
	@Override
	public void run() {
		run(RenameRefactoring.class, RenameRefactoring.NAME);
	}
}

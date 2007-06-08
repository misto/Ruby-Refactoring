package org.rubypeople.rdt.refactoring.action;

import org.rubypeople.rdt.refactoring.core.renamefile.RenameFileRefactoring;

public class RenameFileAction extends WorkbenchWindowActionDelegate {

	@Override
	void run() {
		run(RenameFileRefactoring.class, RenameFileRefactoring.NAME);
	}
}

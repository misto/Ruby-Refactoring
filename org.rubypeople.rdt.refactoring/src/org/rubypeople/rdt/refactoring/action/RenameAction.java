package org.rubypeople.rdt.refactoring.action;

import org.eclipse.jface.action.IAction;
import org.rubypeople.rdt.refactoring.core.rename.RenameRefactoring;

public class RenameAction extends WorkbenchWindowActionDelegate {

	public static final Class[] renamableClasses = {};
	
	@Override
	public void run(IAction action) {
		run(RenameRefactoring.class, RenameRefactoring.NAME);
	}
}

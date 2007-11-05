package org.rubypeople.rdt.refactoring;
import org.eclipse.ui.IStartup;
import org.rubypeople.rdt.refactoring.core.RdtRefactoringObjectFactory;


public class RdtRefactoringActivator implements IStartup {

	public void earlyStartup() {
		RefactoringPlugin.setRefactoringObjectFactory(new RdtRefactoringObjectFactory());
	}
}

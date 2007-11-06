package ch.hsr.ch.refactoring;
import org.eclipse.ui.IStartup;
import org.rubypeople.rdt.refactoring.RefactoringPlugin;

import ch.hsr.ch.refactoring.core.RdtRefactoringObjectFactory;


public class DltkRefactoringActivator implements IStartup {

	public void earlyStartup() {
		RefactoringPlugin.setRefactoringObjectFactory(new RdtRefactoringObjectFactory());
	}
}

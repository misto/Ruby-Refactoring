package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.rubypeople.rdt.internal.ui.rdocexport.RDocUtility;

public class GenerateRdocAction implements IWorkbenchWindowActionDelegate {

	private ISelection fSelection;

	private Shell fCurrentShell;

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
		fCurrentShell = window.getShell();
	}

	public void run(IAction action) {
		IStructuredSelection selection = null;
		if (fSelection instanceof IStructuredSelection) {
			selection = (IStructuredSelection) fSelection;
			Object first = selection.getFirstElement();
			if (first instanceof IResource) {
				RDocUtility.generateDocumentation((IResource) first);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		fSelection = selection;
	}
}

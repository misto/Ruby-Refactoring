package org.rubypeople.rdt.internal.debug.ui.actions;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IDebugView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.rubypeople.rdt.internal.debug.core.model.RubyExpression;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class InspectAction extends AbstractInspectAction implements IViewActionDelegate, IEditorActionDelegate {

    protected RubyStackFrame getRubyStackFrame() {
        IViewPart part = page.findView(IDebugUIConstants.ID_DEBUG_VIEW);
        if (part == null) {
            return null;
        }
        IDebugView launchView = (IDebugView) part;
        StructuredSelection selected = (StructuredSelection) launchView.getViewer().getSelection();
        if (selected.isEmpty()) {
            return null;
        }
        if (!(selected.getFirstElement() instanceof RubyStackFrame)) {
            return null;
        }
        return (RubyStackFrame) selected.getFirstElement();

    }

    public void run(IAction action) {
        final RubyStackFrame stackFrame = this.getRubyStackFrame();
        if (stackFrame == null) {
            MessageDialog.openInformation(
                page.getActivePart().getSite().getShell(),
                "No suitable stack frame",
                "Could not inspect because there is no context (a ruby stack frame) for inspection selected.");
            return;
        }
        if (!(selection instanceof TextSelection)) {
            return;
        }
        Display.getCurrent().asyncExec(new Runnable() {
            public void run() {
                String selectedText = ((TextSelection) selection).getText().replaceAll("\r\n", "");
                try {
					RubyVariable rubyVariable = stackFrame.getRubyDebuggerProxy().readInspectExpression(stackFrame, selectedText);
					showExpressionView();
					DebugPlugin.getDefault().getExpressionManager().addExpression(new RubyExpression(selectedText, rubyVariable));
                } catch (RubyProcessingException e) {
                    MessageDialog.openInformation(page.getActivePart().getSite().getShell(), e.getRubyExceptionType(), "Could not inspect '" + selectedText + "': " + e.getMessage());
                }
            }
        });
    }

    public void setActiveEditor(IAction action, IEditorPart targetEditor) {
        if (targetEditor == null || targetEditor.getEditorSite() == null) {
            this.page = null;
        } else {
            this.page = targetEditor.getEditorSite().getPage();

        }
    }

}

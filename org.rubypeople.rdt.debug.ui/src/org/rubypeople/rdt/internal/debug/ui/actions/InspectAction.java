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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.rubypeople.rdt.internal.debug.core.model.RubyExpression;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;

public class InspectAction implements IViewActionDelegate, IWorkbenchWindowActionDelegate, IEditorActionDelegate {

  protected IWorkbenchPage page;
  protected ISelection selection;

  public void init(IViewPart view) {

  }

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

  protected void showExpressionView() {
    IViewPart part = page.findView(IDebugUIConstants.ID_EXPRESSION_VIEW);
    if (part == null) {
      try {
        page.showView(IDebugUIConstants.ID_EXPRESSION_VIEW);
      } catch (PartInitException e) {
        RdtDebugUiPlugin.log(e);
      }
    } else {
      page.bringToTop(part);
    }

  }

  public void run(IAction action) {
    final RubyStackFrame stackFrame = this.getRubyStackFrame();
    if (stackFrame == null) {
      return;
    }
    if (!(selection instanceof TextSelection)) {
      return;
    }
    Display.getCurrent().asyncExec(new Runnable() {
      public void run() {
        String selectedText = ((TextSelection) selection).getText();
        RubyVariable rubyVariable = stackFrame.getRubyDebuggerProxy().readInspectExpression(stackFrame, selectedText);
        if (rubyVariable == null) {
          MessageDialog.openInformation(page.getActivePart().getSite().getShell(), "Inspection error", "Could not inspect '" + selectedText + "'");
          return;
        }
        showExpressionView();
        DebugPlugin.getDefault().getExpressionManager().addExpression(new RubyExpression(selectedText, rubyVariable));
      }
    });
  }

  public void selectionChanged(IAction action, ISelection selection) {
    this.selection = selection;

  }

  public void dispose() {
  }

  public void init(IWorkbenchWindow window) {
  }

  public void setActiveEditor(IAction action, IEditorPart targetEditor) {
    if (targetEditor == null || targetEditor.getEditorSite() == null) {
      this.page = null;
    } else {
      this.page = targetEditor.getEditorSite().getPage();

    }
  }

}

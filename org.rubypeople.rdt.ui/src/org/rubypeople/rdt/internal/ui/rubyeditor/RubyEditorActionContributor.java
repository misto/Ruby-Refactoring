package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.rubypeople.rdt.internal.ui.rubyeditor.ruby.*;

public class RubyEditorActionContributor extends BasicTextEditorActionContributor {
	protected RetargetTextEditorAction contentAssistProposal;
	protected RetargetTextEditorAction contentAssistTip;

	public RubyEditorActionContributor() {
		super();

		contentAssistProposal = new RetargetTextEditorAction(RubyEditorMessages.getResourceBundle(), "ContentAssistProposal.");
		contentAssistTip = new RetargetTextEditorAction(RubyEditorMessages.getResourceBundle(), "ContentAssistTip.");
	}

	public void contributeToMenu(IMenuManager menuManager) {
		IMenuManager editMenu = menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.add(new Separator());
			editMenu.add(contentAssistProposal);
			editMenu.add(contentAssistTip);
		}
	}

	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		ITextEditor editor = null;
		if (part instanceof ITextEditor)
			editor = (ITextEditor) part;

		contentAssistProposal.setAction(getAction(editor, "ContentAssistProposal"));
		contentAssistTip.setAction(getAction(editor, "ContentAssistTip"));
	}
}
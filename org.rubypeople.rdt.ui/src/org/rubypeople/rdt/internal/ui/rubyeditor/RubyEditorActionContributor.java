package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.editors.text.TextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.rubypeople.rdt.internal.ui.RdtUiMessages;
import org.rubypeople.rdt.ui.actions.RubyActionIds;

public class RubyEditorActionContributor extends TextEditorActionContributor {
	protected RetargetTextEditorAction contentAssistProposal;

	public RubyEditorActionContributor() {
		super();

		contentAssistProposal = new RetargetTextEditorAction(RdtUiMessages.getResourceBundle(), "ContentAssistProposal.");
	}

	public void contributeToMenu(IMenuManager menuManager) {
		IMenuManager editMenu = menuManager.findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		if (editMenu != null) {
			editMenu.add(new Separator());
			editMenu.add(contentAssistProposal);
		}
	}

	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		ITextEditor editor = null;
		if (part instanceof ITextEditor)
			editor = (ITextEditor) part;

		contentAssistProposal.setAction(getAction(editor, "ContentAssistProposal"));

		IActionBars bars= getActionBars();		
		bars.setGlobalActionHandler(RubyActionIds.COMMENT, getAction(editor, "Comment"));
		bars.setGlobalActionHandler(RubyActionIds.UNCOMMENT, getAction(editor, "Uncomment"));
	}
}

package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.rubypeople.rdt.internal.ui.RdtUiMessages;
import org.rubypeople.rdt.ui.actions.FormatAction;
import org.rubypeople.rdt.ui.actions.RubyActionGroup;
import org.rubypeople.rdt.ui.actions.RubyEditorActionDefinitionIds;

public class RubyEditor extends RubyAbstractEditor {
	protected RubyActionGroup actionGroup;
	public RubyEditor() {
		super();
		this.setRulerContextMenuId("org.rubypeople.rdt.ui.rubyeditor.rulerContextMenu") ; //$NON-NLS-1$
		this.setEditorContextMenuId("org.rubypeople.rdt.ui.rubyeditor.contextMenu") ; //$NON-NLS-1$
		setKeyBindingScopes(new String[] { "org.rubypeople.rdt.ui.rubyEditorScope" });  //$NON-NLS-1$
	}

	protected void createActions() {
		super.createActions();

		Action action = new ContentAssistAction(RdtUiMessages.getResourceBundle(), "ContentAssistProposal.", this);
		action.setActionDefinitionId(RubyEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);		
		setAction("ContentAssistProposal", action);

		action= new TextOperationAction(RdtUiMessages.getResourceBundle(), "Comment.", this, ITextOperationTarget.PREFIX);
		action.setActionDefinitionId(RubyEditorActionDefinitionIds.COMMENT);		
		setAction("Comment", action);

		action= new TextOperationAction(RdtUiMessages.getResourceBundle(), "Uncomment.", this, ITextOperationTarget.STRIP_PREFIX);
		action.setActionDefinitionId(RubyEditorActionDefinitionIds.UNCOMMENT);		
		setAction("Uncomment", action);

		action= new FormatAction(RdtUiMessages.getResourceBundle(), "Format.", this);
		action.setActionDefinitionId(RubyEditorActionDefinitionIds.FORMAT);		
		setAction("Format", action);

		actionGroup = new RubyActionGroup(this, ITextEditorActionConstants.GROUP_EDIT);
	}

	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		actionGroup.fillContextMenu(menu);
	}
}

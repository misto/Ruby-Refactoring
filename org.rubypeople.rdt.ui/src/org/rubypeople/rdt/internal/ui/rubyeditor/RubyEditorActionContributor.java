package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.texteditor.BasicTextEditorActionContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.RetargetTextEditorAction;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.ui.actions.RubyActionIds;

public class RubyEditorActionContributor extends BasicTextEditorActionContributor {
	protected RetargetTextEditorAction contentAssistProposal;
	private GotoAnnotationAction fPreviousAnnotation;
	private GotoAnnotationAction fNextAnnotation;

	public RubyEditorActionContributor() {
		super();

		contentAssistProposal = new RetargetTextEditorAction(RubyUIMessages.getResourceBundle(), "ContentAssistProposal.");
	
		fPreviousAnnotation= new GotoAnnotationAction("PreviousAnnotation.", false); //$NON-NLS-1$

		fNextAnnotation= new GotoAnnotationAction("NextAnnotation.", true); //$NON-NLS-1$
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
	
		fPreviousAnnotation.setEditor(editor);
		fNextAnnotation.setEditor(editor);
	}
	
	/*
	 * @see IEditorActionBarContributor#init(IActionBars, IWorkbenchPage)
	 */
	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		
		// register actions that have a dynamic editor. 
		bars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_NEXT_ANNOTATION, fNextAnnotation);
		bars.setGlobalActionHandler(ITextEditorActionDefinitionIds.GOTO_PREVIOUS_ANNOTATION, fPreviousAnnotation);
		bars.setGlobalActionHandler(ActionFactory.NEXT.getId(), fNextAnnotation);
		bars.setGlobalActionHandler(ActionFactory.PREVIOUS.getId(), fPreviousAnnotation);
	}
}

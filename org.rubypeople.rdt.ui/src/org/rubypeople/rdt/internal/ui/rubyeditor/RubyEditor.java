package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.TextOperationAction;

public class RubyEditor extends AbstractTextEditor {

	public RubyEditor() {
		initializeEditor();
	}
	
	protected void initializeEditor() {
		RubyEditorEnvironment.setUp();

		setSourceViewerConfiguration(new RubySourceViewerConfiguration());
		setRangeIndicator(new DefaultRangeIndicator());
	}
	
	protected void createActions() {
		super.createActions();
		setAction("ContentAssistProposal", new TextOperationAction(RubyEditorMessages.getResourceBundle(), "ContentAssistProposal.", this, ISourceViewer.CONTENTASSIST_PROPOSALS));
		setAction("ContentAssistTip", new TextOperationAction(RubyEditorMessages.getResourceBundle(), "ContentAssistTip.", this, ISourceViewer.CONTENTASSIST_CONTEXT_INFORMATION));
	}

	public void editorContextMenuAboutToShow(MenuManager menu) {
		super.editorContextMenuAboutToShow(menu);
		addAction(menu, "ContentAssistProposal");
		addAction(menu, "ContentAssistTip");
	}

	public void dispose() {
		super.dispose();
		
		RubyEditorEnvironment.tearDown();
	}

}

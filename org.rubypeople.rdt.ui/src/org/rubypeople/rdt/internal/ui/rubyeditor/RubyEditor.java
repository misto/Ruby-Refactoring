package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.rubypeople.rdt.internal.ui.rubyeditor.ruby.RubyEditorMessages;

public class RubyEditor extends TextEditor {

	public RubyEditor() {
		super();
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

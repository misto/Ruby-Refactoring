package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.texteditor.WorkbenchChainedTextFontFieldEditor;
import org.rubypeople.rdt.internal.ui.RdtUiMessages;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.ui.actions.RubyActionGroup;
import org.rubypeople.rdt.ui.actions.RubyEditorActionDefinitionIds;

public class RubyEditor extends TextEditor {
	protected RubyActionGroup actionGroup;

	public RubyEditor() {
		super();
		this.setRulerContextMenuId("#rubyRulerContext") ;
	}

	protected void configurePreferenceStore() {
		IPreferenceStore prefs = RdtUiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefs);

		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_DEFAULT, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_KEYWORD, new RGB(164, 53, 122));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_STRING, new RGB(15, 120, 142));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_MULTI_LINE_COMMENT, new RGB(247, 32, 64));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_SINGLE_LINE_COMMENT, new RGB(227, 64, 227));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_CONTENT_ASSISTANT_BACKGROUND, new RGB(150, 150, 0));
		WorkbenchChainedTextFontFieldEditor.startPropagate(prefs, JFaceResources.TEXT_FONT);

		prefs.setDefault(RubyColorConstants.RUBY_KEYWORD + "_bold", true);
	}

	protected void initializeEditor() {
		configurePreferenceStore();

		RubyTextTools textTools = RdtUiPlugin.getDefault().getTextTools();
		setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools, this));
		setRangeIndicator(new DefaultRangeIndicator());
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

		actionGroup = new RubyActionGroup(this, ITextEditorActionConstants.GROUP_EDIT);
	}

	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		actionGroup.fillContextMenu(menu);
	}
}

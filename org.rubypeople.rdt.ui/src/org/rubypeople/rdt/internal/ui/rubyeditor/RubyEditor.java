package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.Arrays;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.rubypeople.rdt.internal.ui.RdtUiMessages;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.actions.FormatAction;
import org.rubypeople.rdt.ui.actions.RubyActionGroup;
import org.rubypeople.rdt.ui.actions.RubyEditorActionDefinitionIds;

public class RubyEditor extends RubyAbstractEditor {

	protected RubyActionGroup actionGroup;
	private String tabReplaceString;
	private boolean isTabReplacing=false;

	public RubyEditor() {
		super();
		this.setRulerContextMenuId("org.rubypeople.rdt.ui.rubyeditor.rulerContextMenu"); //$NON-NLS-1$
		this.setEditorContextMenuId("org.rubypeople.rdt.ui.rubyeditor.contextMenu"); //$NON-NLS-1$
		setKeyBindingScopes(new String[] { "org.rubypeople.rdt.ui.rubyEditorScope"}); //$NON-NLS-1$
	}

	protected void createActions() {
		super.createActions();

		Action action = new ContentAssistAction(RdtUiMessages.getResourceBundle(), "ContentAssistProposal.", this);
		action.setActionDefinitionId(RubyEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action);

		action = new TextOperationAction(RdtUiMessages.getResourceBundle(), "Comment.", this, ITextOperationTarget.PREFIX);
		action.setActionDefinitionId(RubyEditorActionDefinitionIds.COMMENT);
		setAction("Comment", action);

		action = new TextOperationAction(RdtUiMessages.getResourceBundle(), "Uncomment.", this, ITextOperationTarget.STRIP_PREFIX);
		action.setActionDefinitionId(RubyEditorActionDefinitionIds.UNCOMMENT);
		setAction("Uncomment", action);

		action = new FormatAction(RdtUiMessages.getResourceBundle(), "Format.", this);
		action.setActionDefinitionId(RubyEditorActionDefinitionIds.FORMAT);
		setAction("Format", action);

		actionGroup = new RubyActionGroup(this, ITextEditorActionConstants.GROUP_EDIT);
	}

	protected void editorContextMenuAboutToShow(IMenuManager menu) {
		super.editorContextMenuAboutToShow(menu);

		actionGroup.fillContextMenu(menu);
	}

	protected void convertTabs(DocumentCommand command) {
		if (!isTabReplacing) { return; }
		if (command.text.equals("\t")) {
			command.text = this.tabReplaceString;
		}
	}

	protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {
		super.handlePreferenceStoreChanged(event) ;
		String property = event.getProperty();

		if (PreferenceConstants.FORMAT_USE_TAB.equals(property) || PreferenceConstants.FORMAT_INDENTATION.equals(property)) {
			this.initializeTabReplace();
			// for rereading the indentPrefixes for shift left/right from the RubySourceViewerConfiguration
			this.getSourceViewer().configure(this.getSourceViewerConfiguration()) ;			
		}
	}

	private void initializeTabReplace() {
		this.isTabReplacing = !RdtUiPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.FORMAT_USE_TAB);
		if (this.isTabReplacing) {
			int length = RdtUiPlugin.getDefault().getPreferenceStore().getInt(PreferenceConstants.FORMAT_INDENTATION);
			char[] spaces = new char[length];
			Arrays.fill(spaces, ' ');
			tabReplaceString = new String(spaces);
		}
	}

	protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
		fAnnotationAccess = createAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());

		ISourceViewer viewer = new RubySourceViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		return viewer;
	}

	/* Copied from Ant StatusLineSourceViewer */
	class RubySourceViewer extends SourceViewer {

		private boolean fIgnoreTextConverters = false;

		public RubySourceViewer(Composite composite, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean overviewRulerVisible, int styles) {
			super(composite, verticalRuler, overviewRuler, overviewRulerVisible, styles);
			initializeTabReplace();
		}

		public void doOperation(int operation) {
			if (getTextWidget() == null || !redraws()) { return; }

			switch (operation) {
			case UNDO:
				fIgnoreTextConverters = true;
				break;
			case REDO:
				fIgnoreTextConverters = true;
				break;
			}

			super.doOperation(operation);
		}

		protected void customizeDocumentCommand(DocumentCommand command) {
			super.customizeDocumentCommand(command);
			if (!fIgnoreTextConverters) {
				convertTabs(command);
			}
			fIgnoreTextConverters = false;
		}
	}

	public boolean isTabReplacing() {
		return isTabReplacing;
	}
	
	/**
	 * 
	 * @return Returns the replacement string for tab if isTabReplacing()
	 */	
	public String getTabReplaceString() {
		return tabReplaceString;
	}
}
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.source.AnnotationRulerColumn;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;
import org.eclipse.ui.texteditor.TextOperationAction;
import org.eclipse.ui.texteditor.WorkbenchChainedTextFontFieldEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.rubypeople.rdt.internal.core.parser.RubyParsedComponent;
import org.rubypeople.rdt.internal.ui.RdtUiMessages;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.ui.actions.RubyActionGroup;
import org.rubypeople.rdt.ui.actions.RubyEditorActionDefinitionIds;

public class RubyEditor extends TextEditor {
	protected RubyActionGroup actionGroup;
	protected RubyContentOutlinePage outlinePage;
	protected RubyTextTools textTools ;
	public RubyEditor() {
		super();
		this.setRulerContextMenuId("#rubyRulerContext") ;
	}

	protected void configurePreferenceStore() {
		IPreferenceStore prefs = RdtUiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefs);

		WorkbenchChainedTextFontFieldEditor.startPropagate(prefs, JFaceResources.TEXT_FONT);

	}

	protected void initializeEditor() {
		configurePreferenceStore();

		textTools = RdtUiPlugin.getDefault().getTextTools();
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

	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter))
			return createRubyOutlinePage();
		
		return super.getAdapter(adapter);
	}
	
	protected Object createRubyOutlinePage() {
		outlinePage = new RubyContentOutlinePage(this.getEditorInput());
		outlinePage.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleOutlinePageSelection(event);
			}
		});
		return outlinePage;
	}

	protected void handleOutlinePageSelection(SelectionChangedEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		RubyParsedComponent rubySelection = (RubyParsedComponent) selection.getFirstElement();
		ISourceViewer viewer = getSourceViewer();
		viewer.setRangeIndication(rubySelection.offset(), rubySelection.length(), true);
		viewer.setSelectedRange(rubySelection.nameOffset(), rubySelection.nameLength());
	}

	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		if (outlinePage != null)
			outlinePage.setEditorInput(input);
	}
	
	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return textTools.affectsTextPresentation(event) ;
	}

}

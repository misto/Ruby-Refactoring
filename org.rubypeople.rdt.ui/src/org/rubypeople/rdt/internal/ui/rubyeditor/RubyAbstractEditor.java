package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.eclipse.ui.texteditor.WorkbenchChainedTextFontFieldEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RubyAbstractEditor extends TextEditor {

	protected RubyContentOutlinePage outlinePage;
	protected RubyTextTools textTools;

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

	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter))
			return createRubyOutlinePage();
		
		return super.getAdapter(adapter);
	}

	protected Object createRubyOutlinePage() {
		outlinePage = new RubyContentOutlinePage(getSourceViewer().getDocument());
		outlinePage.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				handleOutlinePageSelection(event);
			}
		});
		return outlinePage;
	}

	protected void handleOutlinePageSelection(SelectionChangedEvent event) {
		// todo handle outline selection
	}

	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return textTools.affectsTextPresentation(event) ;
	}

}

package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RubyEditor extends TextEditor {

	public RubyEditor() {
		super();
	}

	protected void initializeEditor() {
		RubyTextTools textTools = RdtUiPlugin.getDefault().getTextTools();
		setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools, this));
		setRangeIndicator(new DefaultRangeIndicator());
		setPreferenceStore(RdtUiPlugin.getDefault().getPreferenceStore());
	}
}

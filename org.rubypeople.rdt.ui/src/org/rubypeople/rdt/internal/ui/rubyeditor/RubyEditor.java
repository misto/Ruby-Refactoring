package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.DefaultRangeIndicator;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RubyEditor extends TextEditor {

	public RubyEditor() {
		super();
	}

	protected void configurePreferenceStore() {
		IPreferenceStore prefs = RdtUiPlugin.getDefault().getPreferenceStore();
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_DEFAULT, new RGB(150, 150, 150));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_KEYWORD, new RGB(200, 200, 150));
	}

	protected void initializeEditor() {
		configurePreferenceStore();

		RubyTextTools textTools = RdtUiPlugin.getDefault().getTextTools();
		setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools, this));
		setRangeIndicator(new DefaultRangeIndicator());
		setPreferenceStore(RdtUiPlugin.getDefault().getPreferenceStore());
	}
}

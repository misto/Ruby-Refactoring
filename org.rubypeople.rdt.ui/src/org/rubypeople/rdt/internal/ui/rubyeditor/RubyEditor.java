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
		setPreferenceStore(prefs);

		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_DEFAULT, new RGB(0, 0, 0));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_KEYWORD, new RGB(164, 53, 122));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_STRING, new RGB(15, 120, 142));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_MULTI_LINE_COMMENT, new RGB(247, 32, 64));
		PreferenceConverter.setDefault(prefs, RubyColorConstants.RUBY_SINGLE_LINE_COMMENT, new RGB(227, 64, 227));

		prefs.setDefault(RubyColorConstants.RUBY_KEYWORD + "_bold", true);
	}

	protected void initializeEditor() {
		configurePreferenceStore();

		RubyTextTools textTools = RdtUiPlugin.getDefault().getTextTools();
		setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools, this));
		setRangeIndicator(new DefaultRangeIndicator());
	}
}

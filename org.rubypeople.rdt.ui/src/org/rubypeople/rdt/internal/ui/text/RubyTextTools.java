package org.rubypeople.rdt.internal.ui.text;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditorPreferences;
import org.rubypeople.rdt.internal.ui.text.ruby.AbstractRubyScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCodeScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.SingleTokenRubyCodeScanner;

public class RubyTextTools {
	protected String[] keywords;
	protected RubyColorProvider colorProvider;
	protected RubyPartitionScanner partitionScanner;
	protected AbstractRubyScanner codeScanner;
	protected AbstractRubyScanner multilineCommentScanner, singlelineCommentScanner, stringScanner;

	public RubyTextTools() {
		super();

		colorProvider = new RubyColorProvider();
		partitionScanner = new RubyPartitionScanner();

		codeScanner = new RubyCodeScanner(this);
		multilineCommentScanner = new SingleTokenRubyCodeScanner(this, RubyColorConstants.RUBY_MULTI_LINE_COMMENT);
		singlelineCommentScanner = new RubyCodeScanner(this);
		stringScanner = new SingleTokenRubyCodeScanner(this, RubyColorConstants.RUBY_STRING);
	}

	public IDocumentPartitioner createDocumentPartitioner() {
		String[] types = new String[] { RubyPartitionScanner.MULTI_LINE_COMMENT, RubyPartitionScanner.STRING };

		return new DefaultPartitioner(getPartitionScanner(), types);
	}

	protected IPartitionTokenScanner getPartitionScanner() {
		return partitionScanner;
	}

	public AbstractRubyScanner getCodeScanner() {
		return codeScanner;
	}

	protected ITokenScanner getMultilineCommentScanner() {
		return multilineCommentScanner;
	}

	protected ITokenScanner getSinglelineCommentScanner() {
		return singlelineCommentScanner;
	}

	protected ITokenScanner getStringScanner() {
		return stringScanner;
	}

	public RubyColorProvider getColorProvider() {
		return colorProvider;
	}

	public IPreferenceStore getPreferenceStore() {
		return RdtUiPlugin.getDefault().getPreferenceStore();
	}

	public String[] getKeyWords() {
		if (keywords == null) {
			String csvKeywords = RubyEditorPreferences.getString("keywords");

			List keywordList = new ArrayList();
			StringTokenizer tokenizer = new StringTokenizer(csvKeywords, ",");
			while (tokenizer.hasMoreTokens())
				keywordList.add(tokenizer.nextToken());

			keywords = new String[keywordList.size()];
			keywordList.toArray(keywords);
		}

		return keywords;
	}
	
	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return this.getCodeScanner().affectsTextPresentation(event) ;
	}

}

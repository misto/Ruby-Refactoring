package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCodeScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.SingleTokenRubyCodeScanner;

public class RubyTextTools {
	protected RubyColorProvider colorProvider;
	protected RubyPartitionScanner partitionScanner;
	protected RubyCodeScanner codeScanner;
	protected SingleTokenRubyCodeScanner multilineCommentScanner, singlelineCommentScanner, stringScanner;

	public RubyTextTools() {
		super();

		IPreferenceStore prefs = RdtUiPlugin.getDefault().getPreferenceStore();
		colorProvider = new RubyColorProvider();
		codeScanner = new RubyCodeScanner(colorProvider, prefs);
		multilineCommentScanner = new SingleTokenRubyCodeScanner(colorProvider, prefs, RubyColorConstants.RUBY_MULTI_LINE_COMMENT);
		singlelineCommentScanner = new SingleTokenRubyCodeScanner(colorProvider, prefs, RubyColorConstants.RUBY_SINGLE_LINE_COMMENT);
		stringScanner = new SingleTokenRubyCodeScanner(colorProvider, prefs, RubyColorConstants.RUBY_STRING);
		partitionScanner = new RubyPartitionScanner();
	}

	public IDocumentPartitioner createDocumentPartitioner() {
		String[] types = new String[] { RubyPartitionScanner.MULTI_LINE_COMMENT, RubyPartitionScanner.STRING, RubyPartitionScanner.SINGLE_LINE_COMMENT };

		return new DefaultPartitioner(getPartitionScanner(), types);
	}

	protected IPartitionTokenScanner getPartitionScanner() {
		return partitionScanner;
	}

	protected ITokenScanner getCodeScanner() {
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
}

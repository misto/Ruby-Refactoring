package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.rubypeople.rdt.internal.ui.rubyeditor.ruby.RubyCodeScanner;
import org.rubypeople.rdt.internal.ui.rubyeditor.ruby.RubyPartitionScanner;
import org.rubypeople.rdt.internal.ui.rubyeditor.util.RubyColorProvider;

public class RubyEditorEnvironment {
	protected static RubyColorProvider colorProvider;
	protected static RubyCodeScanner codeScanner;
	protected static RubyPartitionScanner partitionScanner;

	protected static boolean isSetUp = false;

	public static void setUp() {
		if (!isSetUp) {
			colorProvider = new RubyColorProvider();
			codeScanner = new RubyCodeScanner(colorProvider);
			partitionScanner = new RubyPartitionScanner();
			isSetUp = true;
		}
	}

	public static void tearDown() {
		if (isSetUp) {
			codeScanner = null;
			colorProvider.dispose();
			colorProvider = null;
			isSetUp = false;
		}
	}

	public static RuleBasedScanner getRubyCodeScanner() {
		return codeScanner;
	}

	public static RubyColorProvider getRubyColorProvider() {
		return colorProvider;
	}
	
	public static RubyPartitionScanner getRubyPartitionScanner() {
		return partitionScanner;
	}
}
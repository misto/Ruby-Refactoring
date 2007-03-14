package org.rubypeople.rdt.internal.ui.text.ruby;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.rules.IToken;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubyColorManager;

public class TC_RubyTokenScanner extends TestCase {

	private RubyTokenScanner fScanner;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		RubyColorManager colorManager = new RubyColorManager(false);
		fScanner = new RubyTokenScanner(colorManager, RubyPlugin.getDefault().getPreferenceStore());
	}

	private void setUpScanner(String code) {
		setUpScanner(code, 0, code.length());
	}
	
	private void setUpScanner(String code, int offset, int length) {
		Document doc = new Document(code);
		fScanner.setRange(doc, offset, length);		
	}

	private void assertToken(String color, int offset, int length) {
		IToken token = fScanner.nextToken();
		assertEquals("Offsets don't match", offset, fScanner.getTokenOffset());
		assertEquals("Lengths don't match", length, fScanner.getTokenLength());	
		assertEquals("Colors don't match", fScanner.getToken(color), token);
	}
	
	public void testSimpleClassDefinition() {
		String code = "class Chris\nend\n";
		setUpScanner(code);
		assertToken(IRubyColorConstants.RUBY_KEYWORD, 0, 5);
		assertToken(IRubyColorConstants.RUBY_DEFAULT, 5, 6);
		assertToken(IRubyColorConstants.RUBY_DEFAULT, 11, 1);
		assertToken(IRubyColorConstants.RUBY_KEYWORD, 12, 3);
	}
	
	public void testMultipleCommentsInARow() {
		String code = "# comment one\n#comment two\nclass Chris\nend\n";
		setUpScanner(code);
		assertToken(IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT, 0, 27);
		assertToken(IRubyColorConstants.RUBY_KEYWORD, 27, 5);
		assertToken(IRubyColorConstants.RUBY_DEFAULT, 32, 6);
		assertToken(IRubyColorConstants.RUBY_DEFAULT, 38, 1);
		assertToken(IRubyColorConstants.RUBY_KEYWORD, 39, 3);		
	}
	
	public void testCommentAfterEnd() {
		String code = "class Chris\nend # comment\n";
		setUpScanner(code);
		assertToken(IRubyColorConstants.RUBY_KEYWORD, 0, 5);
		assertToken(IRubyColorConstants.RUBY_DEFAULT, 5, 6);
		assertToken(IRubyColorConstants.RUBY_DEFAULT, 11, 1);
		assertToken(IRubyColorConstants.RUBY_KEYWORD, 12, 3);
		assertToken(IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT, 15, 10);
	}
	
	public void testCommentAfterEndWhileEditing() {
		String code = "=begin\r\n" +
"c\r\n" +
"=end\r\n" +
"#hmm\r\n" +
"#comment here why is ths\r\n" +
"class Chris\r\n" +
"  def thing\r\n" +
"  end  #ocmm \r\n" +
"end";
		setUpScanner(code, 75, 14);
		assertToken(IRubyColorConstants.RUBY_KEYWORD, 75, 5);
		assertToken(IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT, 80, 7);
	}

}

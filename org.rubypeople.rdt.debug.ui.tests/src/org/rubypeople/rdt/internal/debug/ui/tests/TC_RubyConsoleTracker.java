package org.rubypeople.rdt.internal.debug.ui.tests;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamMonitor;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.internal.ui.views.console.ConsoleOutputTextStore;
import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleHyperlink;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.rubypeople.rdt.internal.debug.ui.console.RubyConsoleTracker;
import org.rubypeople.rdt.internal.debug.ui.console.RubyStackTraceHyperlink;

public class TC_RubyConsoleTracker extends TestCase {

	public TC_RubyConsoleTracker(String name) {
		super(name);
	}

	protected void setUp() {
	}

	public void testCorrect() throws Exception {
		//data/test/configFile.rb:1:in `require': No such file to load -- inifile (LoadError)
		// *	from /data/test/configFile.rb:1
		// *	from /data/test/fetchmail.rb:2:in `require'
		 
		TestConsole console = new TestConsole() ;
		console.lineAppend("/d/t.rb:1:in `require': No such file to load -- inifile (LoadError)") ;
		RubyStackTraceHyperlink link = console.getLink() ;
		Assert.assertNotNull(link) ;
		Assert.assertEquals(0, console.getOffset()) ;
		Assert.assertEquals(9, console.getLength()) ;
		Assert.assertEquals("/d/t.rb", link.getFilename()) ;
		Assert.assertEquals(1, link.getLineNumber()) ;
		console.lineAppend("  	from c:/d/abc.rb:99") ;
		link = console.getLink() ;
		Assert.assertNotNull(link) ;
		Assert.assertEquals(8, console.getOffset()) ;
		Assert.assertEquals(14, console.getLength()) ;
		Assert.assertEquals("c:/d/abc.rb", link.getFilename()) ;
		Assert.assertEquals(99, link.getLineNumber()) ;
		// Another one directly following
		console.lineAppend("/name with from in the middle/rb.rb:123") ;
		link = console.getLink() ;
		Assert.assertNotNull(link) ;
		Assert.assertEquals(0, console.getOffset()) ;
		Assert.assertEquals(39, console.getLength()) ;
		Assert.assertEquals("/name with from in the middle/rb.rb", link.getFilename()) ;
		Assert.assertEquals(123, link.getLineNumber()) ;		
		
		
	}

	public void testSecondWithoutFirst() throws Exception {
		TestConsole console = new TestConsole() ;
		console.lineAppend("  	from c:/d/abc.rb:99") ;
		RubyStackTraceHyperlink link = console.getLink() ;
		Assert.assertNotNull(link) ;
		Assert.assertEquals(8, console.getOffset()) ;
		Assert.assertEquals("c:/d/abc.rb", link.getFilename()) ;
	}
	
	public void testInCorrect() throws Exception {
		 
		TestConsole console = new TestConsole() ;
		console.lineAppend("/d/t.rb:a:in `require': No such file to load -- inifile (LoadError)") ;
		RubyStackTraceHyperlink link = console.getLink() ;
		Assert.assertNull(link) ;
		console.lineAppend("/d/t.rb:123 ") ;
		Assert.assertNull(link) ;
	}
	

	public class TestConsole implements IConsole {
		private RubyStackTraceHyperlink link;
		private int offset ;
		private int length ;
		SimpleDocument doc = new SimpleDocument() ;
		RubyConsoleTracker tracker ;
		
		public TestConsole() throws Exception {
			tracker = new RubyConsoleTracker();					
			tracker.init(this) ;			
		}
		
		public void addLink(IConsoleHyperlink pLink, int pOffset, int pLength) {
			this.link = (RubyStackTraceHyperlink) pLink ;
			this.offset = pOffset ;
			this.length = pLength ;
		}
		public void connect(IStreamMonitor streamMonitor, String streamIdentifer) {
			// TODO Auto-generated method stub
		}
		public void connect(IStreamsProxy streamsProxy) {
			// TODO Auto-generated method stub
		}
		public IDocument getDocument() {
			return doc;
		}
		public IProcess getProcess() {
			// TODO Auto-generated method stub
			return null;
		}
		public IRegion getRegion(IConsoleHyperlink link) {
			return new Region(this.getOffset(), this.getLength()) ;
		}

		public void lineAppend(String pLine) throws Exception {
			doc.set(pLine) ;
			tracker.lineAppended(doc.getLineInformationOfOffset(1)) ;
		}
		
		public RubyStackTraceHyperlink getLink() {
			return link;
		}

		public int getLength() {
			return length;
		}

		public int getOffset() {
			return offset;
		}
	}

	public class SimpleDocument extends AbstractDocument {
		public SimpleDocument() {
			this.setTextStore(new ConsoleOutputTextStore(1000)) ;
			setLineTracker(new DefaultLineTracker());
			completeInitialization();
		}
	}
}

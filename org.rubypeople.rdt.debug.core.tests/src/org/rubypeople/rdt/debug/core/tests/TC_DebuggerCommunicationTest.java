package org.rubypeople.rdt.debug.core.tests;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rubypeople.rdt.internal.debug.core.ExceptionSuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.StepSuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.core.model.ThreadInfo;
import org.rubypeople.rdt.internal.debug.core.parsing.MultiReaderStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.rubypeople.rdt.internal.debug.core.parsing.FramesReader;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.rubypeople.rdt.internal.debug.core.parsing.ThreadInfoReader;
import org.rubypeople.rdt.internal.debug.core.parsing.VariableReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class TC_DebuggerCommunicationTest extends TestCase {

	public class OutputRedirectorThread extends Thread {
		private InputStream inputStream;
		public OutputRedirectorThread(InputStream aInputStream) {
			inputStream = aInputStream;
		}

		public void run() {
			System.out.println("OutputRedirectorThread started.");
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

			String line;
			try {
				while ((line = br.readLine()) != null) {
					System.out.println("RUBY: " + line);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("OutputRedirectorThread stopped.");
		}
	}
/*
	public static TestSuite suite() {
		
		TestSuite suite = new TestSuite();
		suite.addTest(new TC_DebuggerCommunicationTest("testThreads"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariablesInFrames"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testBreakpoint"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testFramesWhenThreadSpawned"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreadIdsAndResume"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreadsAndFrames"));		
		//suite.addTest(new TC_DebuggerCommunicationTest("testStepOver"));		
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreadFramesAndVariables"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariableNil"));	
		//suite.addTest(new TC_DebuggerCommunicationTest("testException"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testNameError"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariablesInObject"));	
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariableString"));	
		
		return suite;

	}
*/

	private static final String TMP_DIR = System.getProperty("java.io.tmpdir");
	private Process process;
	private OutputRedirectorThread rubyStdoutRedirectorThread;
	private OutputRedirectorThread rubyStderrRedirectorThread;
	private Socket socket;
	private PrintWriter out;
	private MultiReaderStrategy multiReaderStrategy;

	public TC_DebuggerCommunicationTest(String arg0) {
		super(arg0);
	}

	public static void main(String[] args) {
		junit.textui.TestRunner.run(TC_DebuggerCommunicationTest.class);
	}

	private String getTestFilename() {
		return TC_DebuggerCommunicationTest.TMP_DIR + "test.rb";
	}

	private String getRubyTestFilename() {
		return this.getTestFilename().replace('\\', '/');
	}

	protected XmlPullParser getXpp(Socket socket) throws Exception {

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance("org.kxml2.io.KXmlParser,org.kxml2.io.KXmlSerializer", null);
		XmlPullParser xpp = factory.newPullParser();
		xpp.setInput(new BufferedReader(new InputStreamReader(socket.getInputStream())));
		return xpp;
	}

	protected SuspensionReader getSuspensionReader() throws Exception {
		return new SuspensionReader(multiReaderStrategy);
	}

	protected VariableReader getVariableReader() throws Exception {
		return new VariableReader(multiReaderStrategy);
	}

	protected FramesReader getFramesReader() throws Exception {
		return new FramesReader(multiReaderStrategy);
	}

	protected ThreadInfoReader getThreadInfoReader() throws Exception {
		return new ThreadInfoReader(multiReaderStrategy);
	}

	public void startRubyProcess() throws Exception {
		String srcDir = this.getClass().getResource("/").getFile().replaceFirst("/","") ;
		String cmd = "rubyw -I "+srcDir+"../../org.rubypeople.rdt.launching/ruby -I" + TMP_DIR.replace('\\', '/') + " -reclipseDebug.rb " + this.getRubyTestFilename();
		System.out.println("Starting: " + cmd);
		process = Runtime.getRuntime().exec(cmd);
		rubyStderrRedirectorThread = new OutputRedirectorThread(process.getErrorStream());
		rubyStderrRedirectorThread.start();
		rubyStdoutRedirectorThread = new OutputRedirectorThread(process.getInputStream());
		rubyStdoutRedirectorThread.start();

	}

	protected String getOSIndependent(String path) {
		return path.replace('\\', '/');
	}

	public void setUp() throws Exception {
		if (!new File(TMP_DIR).exists() || !new File(TMP_DIR).isDirectory()) {
			throw new RuntimeException("Temp directory does not exist: " + TMP_DIR);
		}
	}

	public void tearDown() throws Exception {
		Thread.sleep(500);
		try {
			if (process.exitValue() != 0) {
				System.out.println("Ruby finished with exit value: "+process.exitValue());
			}
		} catch (IllegalThreadStateException ex) {
			process.destroy();
			System.out.println("Ruby proces had to be destroyed.");
		}

		System.out.println("Waiting for stdout redirector thread..");
		rubyStdoutRedirectorThread.join();
		System.out.println("..done");
		System.out.println("Waiting for stdout redirector thread..");
		rubyStderrRedirectorThread.join();
		System.out.println("..done");
		socket.close();
	}

	private void writeFile(String name, String[] content) throws Exception {
		PrintWriter writer = new PrintWriter(new FileOutputStream(TC_DebuggerCommunicationTest.TMP_DIR + name));
		for (int i = 0; i < content.length; i++) {
			writer.println(content[i]);
		}
		writer.close();
	}

	private void createSocket(String[] lines) throws Exception {
		this.writeFile("test.rb", lines);
		this.startRubyProcess();
		socket = new Socket("localhost", 1098);
		multiReaderStrategy = new MultiReaderStrategy(this.getXpp(socket));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	public void testNameError() throws Exception {

		this.createSocket(new String[] { "puts 'x'" });
		out.println("cont");
		SuspensionPoint hit = this.getSuspensionReader().readSuspension();
		// TODO: assertion is wrong, I want the debugger to suspend here
		// but there must be several catchpoints available
		this.assertNull(hit);
	}

	public void testBreakpoint() throws Exception {
		// Breakpoint in line 1 does not work yet.
		this.createSocket(new String[] { "puts 'a'", "def add", "puts 'b'", "end", "add()", "add()" });
		out.println("b test.rb:1");
		out.println("b add test.rb:3");
		out.println("cont");
		System.out.println("Waiting for breakpoint..");
		SuspensionPoint hit = this.getSuspensionReader().readSuspension();
		this.assertNotNull(hit);
		this.assertTrue(hit.isBreakpoint());
		this.assertEquals(1, hit.getLine());
		this.assertEquals("test.rb", hit.getFile());
		out.println("cont");
		hit = this.getSuspensionReader().readSuspension();
		this.assertNotNull(hit);
		this.assertTrue(hit.isBreakpoint());
		this.assertEquals(3, hit.getLine());
		this.assertEquals("test.rb", hit.getFile());
		out.println("b remove test.rb:3") ;
		out.println("cont");
		hit = this.getSuspensionReader().readSuspension();
		this.assertNull(hit);
		
	}

	public void testException() throws Exception {
		this.createSocket(new String[] { "puts 'a'", "raise 'message'", "puts 'c'" });
		out.println("cont");
		System.out.println("Waiting for exception");
		SuspensionPoint hit = this.getSuspensionReader().readSuspension();
		this.assertNotNull(hit);
		this.assertEquals(2, hit.getLine());
		this.assertEquals(this.getOSIndependent(TMP_DIR + "test.rb"), hit.getFile());
		this.assertTrue(hit.isException());
		this.assertEquals("message", ((ExceptionSuspensionPoint) hit).getExceptionMessage());
		this.assertEquals("RuntimeError", ((ExceptionSuspensionPoint) hit).getExceptionType());
		out.println("cont");
	}

	public void testBreakpointNeverReached() throws Exception {
		this.createSocket(new String[] { "puts 'a'", "puts 'b'", "puts 'c'" });
		out.println("b test.rb:10");
		out.println("cont");
		System.out.println("Waiting for breakpoint..");
		SuspensionPoint hit = this.getSuspensionReader().readSuspension();
		this.assertNull(hit);
	}

	public void testStepOver() throws Exception {
		this.createSocket(new String[] { "puts 'a'", "puts 'b'", "puts 'c'" });
		out.println("b test.rb:2");
		out.println("cont");
		this.getSuspensionReader().readSuspension();
		out.println("next");
		SuspensionPoint info = this.getSuspensionReader().readSuspension();
		this.assertEquals(3, info.getLine());
		this.assertEquals(this.getOSIndependent(TMP_DIR + "test.rb"), info.getFile());
		this.assertTrue(info.isStep());
		this.assertEquals(1, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("next");
		info = this.getSuspensionReader().readSuspension();
		this.assertNull(info);
	}

	public void testStepOverFrames() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "puts 'a'", "Test2.new.print()" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "end", "end" });
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		out.println("b test.rb:3");
		out.println("cont");
		this.getSuspensionReader().readSuspension();
		out.println("next");
		SuspensionPoint info = this.getSuspensionReader().readSuspension();
		this.assertNull(info);
	}

	public void testStepOverFramesValue2() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "puts 'a'", "Test2.new.print()" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		this.runTo("test2.rb", 3);
		out.println("next");
		SuspensionPoint info = this.getSuspensionReader().readSuspension();
		this.assertEquals(4, info.getLine());
		this.assertEquals(this.getOSIndependent(TMP_DIR + "test2.rb"), info.getFile());
		this.assertTrue(info.isStep());
		this.assertEquals(2, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("next");
		info = this.getSuspensionReader().readSuspension();
		this.assertNull(info);
	}

	public void testStepOverInDifferentFrame() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "Test2.new.print()", "puts 'a'" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		this.runTo("test2.rb", 4);
		out.println("next");
		SuspensionPoint info = this.getSuspensionReader().readSuspension();
		this.assertEquals(3, info.getLine());
		this.assertEquals(this.getOSIndependent(TMP_DIR + "test.rb"), info.getFile());
		this.assertTrue(info.isStep());
		this.assertEquals(1, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("cont");
	}

	public void testStepReturn() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "Test2.new.print()", "puts 'a'" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		this.runTo("test2.rb", 4);
		out.println("next");
		SuspensionPoint info = this.getSuspensionReader().readSuspension();
		this.assertEquals(3, info.getLine());
		this.assertEquals(this.getOSIndependent(TMP_DIR + "test.rb"), info.getFile());
		this.assertTrue(info.isStep());
		this.assertEquals(1, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("cont");
	}

	public void testHitBreakpointWhileSteppingOver() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "Test2.new.print()", "puts 'a'" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		out.println("b test2.rb:4");
		this.runTo("test.rb", 2);
		out.println("next");
		SuspensionPoint info = this.getSuspensionReader().readSuspension();
		this.assertEquals(4, info.getLine());
		this.assertEquals("test2.rb", info.getFile());
		this.assertTrue(info.isBreakpoint());
		out.println("cont");
	}

	public void testStepInto() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "puts 'a'", "Test2.new.print()" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		this.runTo("test.rb", 3);
		out.println("step");
		SuspensionPoint info = this.getSuspensionReader().readSuspension();
		this.assertEquals(3, info.getLine());
		this.assertEquals(getOSIndependent(TMP_DIR + "test2.rb"), info.getFile());
		this.assertTrue(info.isStep());
		this.assertEquals(2, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("cont");
	}

	private void runToLine(int lineNumber) throws Exception {
		this.runTo("test.rb", lineNumber);
	}

	private void runTo(String filename, int lineNumber) throws Exception {
		out.println("b " + filename + ":" + lineNumber);
		out.println("cont");
		SuspensionPoint suspension = this.getSuspensionReader().readSuspension();
		if (suspension == null) {
			throw new RuntimeException("Expected suspension, but program exited.");
		}
	}

	protected RubyStackFrame createStackFrame() throws Exception {
		RubyStackFrame stackFrame = new RubyStackFrame(null, "", 5, 1); //RubyThread thread = new RubyThread(null) ;
		//thread.addStackFrame(stackFrame) ;
		return stackFrame;
	}

	public void testVariableNil() throws Exception {
		this.createSocket(new String[] { "puts 'a'", "puts 'b'", "stringA='XX'" });
		this.runToLine(2);
		out.println("v l");
		RubyVariable[] variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(1, variables.length);
		this.assertEquals("stringA", variables[0].getName());
		this.assertEquals("nil", variables[0].getValue().getValueString());
		this.assertEquals(null, variables[0].getValue().getReferenceTypeName());
		this.assertTrue(!variables[0].getValue().hasVariables());
	}

	public void testVariableWithXmlContent() throws Exception {
		this.createSocket(new String[] { "stringA='<start test=\"\"/>'", "puts 'b'" });
		this.runToLine(2);
		out.println("v l");
		RubyVariable[] variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(1, variables.length);
		this.assertEquals("stringA", variables[0].getName());
		this.assertEquals("<start test=\"\"/>", variables[0].getValue().getValueString());
	}

	public void testVariablesInObject() throws Exception {
		this.createSocket(new String[] { "class Test", "def initialize", "@y=5", "puts y", "end", "def to_s", "'test'", "end", "end", "Test.new()" });
		this.runTo("test.rb", 4);
		// Read numerical variable
		out.println("v l");
		RubyVariable[] variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(1, variables.length);
		this.assertEquals("self", variables[0].getName());
		this.assertEquals("test", variables[0].getValue().getValueString());
		this.assertEquals("Test", variables[0].getValue().getReferenceTypeName());
		this.assertTrue(variables[0].getValue().hasVariables());
		out.println("v i self");
		variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(1, variables.length);
		this.assertEquals("@y", variables[0].getName());
		this.assertEquals("5", variables[0].getValue().getValueString());
		this.assertEquals("Fixnum", variables[0].getValue().getReferenceTypeName());
		this.assertTrue(!variables[0].getValue().hasVariables());
		out.println("cont") ;
	}



	public void testVariableString() throws Exception {
		this.createSocket(new String[] { "stringA='XX'", "puts stringA" });
		this.runToLine(2);
		// Read numerical variable
		out.println("v l");
		RubyVariable[] variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(1, variables.length);
		this.assertEquals("stringA", variables[0].getName());
		this.assertEquals("XX", variables[0].getValue().getValueString());
		this.assertEquals("String", variables[0].getValue().getReferenceTypeName());
		this.assertTrue(!variables[0].getValue().hasVariables());
		out.println("cont") ;
	}

	public void testVariableInstance() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "customObject=Test2.new", "puts customObject" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def initialize", "@y=5", "end", "def to_s", "'test'", "end", "end" });
		this.runTo("test2.rb", 6);
		out.println("v i 2 customObject");
		RubyVariable[] variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(1, variables.length);
		this.assertEquals("@y", variables[0].getName());
		this.assertEquals("5", variables[0].getValue().getValueString());
		this.assertEquals("Fixnum", variables[0].getValue().getReferenceTypeName());
		this.assertTrue(!variables[0].getValue().hasVariables());
	}

	public void testVariableInstanceNested() throws Exception {
		this.createSocket(new String[] { "class Test", "def initialize(test)", "@privateTest = test", "end", "end", "test2 = Test.new(Test.new(nil))", "puts test" });
		this.runToLine(7);
		out.println("v l");
		RubyVariable[] variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(1, variables.length);
		RubyVariable test2Variable = variables[0];
		this.assertEquals("test2", test2Variable.getName());
		this.assertEquals("test2", test2Variable.getQualifiedName());
		out.println("v i " + test2Variable.getQualifiedName());
		variables = this.getVariableReader().readVariables(test2Variable);
		this.assertEquals(1, variables.length);
		RubyVariable privateTestVariable = variables[0];
		this.assertEquals("@privateTest", privateTestVariable.getName());
		this.assertEquals("test2.@privateTest", privateTestVariable.getQualifiedName());
		this.assertTrue(privateTestVariable.getValue().hasVariables());
		out.println("v i " + privateTestVariable.getQualifiedName());
		variables = this.getVariableReader().readVariables(privateTestVariable);
		this.assertEquals(1, variables.length);
		RubyVariable privateTestprivateTestVariable = variables[0];
		this.assertEquals("@privateTest", privateTestprivateTestVariable.getName());
		this.assertEquals("test2.@privateTest.@privateTest", privateTestprivateTestVariable.getQualifiedName());
		this.assertEquals("nil", privateTestprivateTestVariable.getValue().getValueString());
		this.assertTrue(!privateTestprivateTestVariable.getValue().hasVariables());
	}

	public void testVariablesInFrames() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "y=5", "Test2.new().test()" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def test", "y=6", "puts y", "end", "end" });
		this.runTo("test2.rb", 4);
		out.println("v l");
		RubyVariable[] variables = this.getVariableReader().readVariables(this.createStackFrame());
		// there are 2 variables self and y
		this.assertEquals(2, variables.length);
		// the variables are sorted: self = variables[0], y = variables[1]
		this.assertEquals("y", variables[1].getName());
		this.assertEquals("6", variables[1].getValue().getValueString());
		out.println("v l 1");
		variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(2, variables.length);
		this.assertEquals("y", variables[1].getName());
		this.assertEquals("6", variables[1].getValue().getValueString());
		out.println("v l 2");
		variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(1, variables.length);
		this.assertEquals("y", variables[0].getName());
		this.assertEquals("5", variables[0].getValue().getValueString());
		// 20 is out of range, then the default frame is used, which is 1
		out.println("v l 20");
		variables = this.getVariableReader().readVariables(this.createStackFrame());
		this.assertEquals(2, variables.length);
		this.assertEquals("y", variables[1].getName());
		this.assertEquals("6", variables[1].getValue().getValueString());
		out.println("cont");
	}

	public void testFrames() throws Exception {
		this.createSocket(new String[] { "require 'test2.rb'", "test = Test2.new()", "test.print()", "test.print()" });
		this.writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "end", "end" });
		this.runTo("test2.rb", 3);
		out.println("b test.rb:4");
		out.println("w");
		RubyThread thread = new RubyThread(null, 0);
		this.getFramesReader().readFrames(thread);
		this.assertEquals(2, thread.getStackFrames().length);
		RubyStackFrame frame1 = (RubyStackFrame) thread.getStackFrames()[0];
		this.assertEquals(this.getOSIndependent(TMP_DIR + "test2.rb"), frame1.getFileName());
		this.assertEquals(1, frame1.getIndex());
		this.assertEquals(3, frame1.getLineNumber());
		RubyStackFrame frame2 = (RubyStackFrame) thread.getStackFrames()[1];
		this.assertEquals(this.getOSIndependent(TMP_DIR + "test.rb"), frame2.getFileName());
		this.assertEquals(2, frame2.getIndex());
		this.assertEquals(3, frame2.getLineNumber());
		out.println("cont");		
		this.getSuspensionReader().readSuspension() ;	
		out.println("w");	
		this.getFramesReader().readFrames(thread);
		this.assertEquals(1, thread.getStackFramesSize()) ;
		
	}
	
	public void testFramesWhenThreadSpawned() throws Exception {		
		this.createSocket(new String[] { "def startThread", "Thread.new() { a = 5 }", "end", "def calc", "5 + 5", "end", "startThread()", "calc()" });
		this.fail("Probably bug in ruby 1.7, which does not trace return from startThread") ;
		this.runTo("test.rb", 5);
		RubyThread thread = new RubyThread(null, 0);
		out.println("w") ;
		this.getFramesReader().readFrames(thread) ;
		this.assertEquals(2, thread.getStackFramesSize()) ;
	}
	
	public void testThreads() throws Exception {
		this.createSocket(new String[] { "Thread.new {", "puts 'a'", "}", "Thread.pass", "puts 'b'" });
		out.println("b test.rb:2");
		out.println("b test.rb:5");
		out.println("cont");
		SuspensionPoint point = this.getSuspensionReader().readSuspension();
		if (point.getThreadId() == 2) {
			this.assertEquals(2, point.getLine());
		}
		else {
			this.assertEquals(5, point.getLine());
		}
		out.println("cont");
		point = this.getSuspensionReader().readSuspension();
		if (point.getThreadId() == 2) {
			this.assertEquals(2, point.getLine());
		}
		else {
			this.assertEquals(5, point.getLine());
		}
		out.println("cont");
	}

	public void testThreadIdsAndResume() throws Exception {
		this.createSocket(new String[] { "threads=[]", "threads << Thread.new {", "puts 'a'", "}", "threads << Thread.new{", "puts 'b'", "}", "puts 'c'", "threads.each{|t| t.join()}" });
		out.println("b test.rb:3");
		out.println("b test.rb:6");
		out.println("b test.rb:8");
		out.println("cont");
		this.getSuspensionReader().readSuspension();
		this.getSuspensionReader().readSuspension();
		this.getSuspensionReader().readSuspension();		
		
		out.println("th l");
		ThreadInfo[] threads = this.getThreadInfoReader().readThreads();
		this.assertEquals(3, threads.length);
		this.assertEquals(1, threads[0].getId());
		this.assertEquals(2, threads[1].getId());
		this.assertEquals(3, threads[2].getId());
		out.println("th 2;cont");

		out.println("th l");
		threads = this.getThreadInfoReader().readThreads();
		this.assertEquals(2, threads.length);
		this.assertEquals(1, threads[0].getId());
		this.assertEquals(3, threads[1].getId());
		out.println("th 3 ; cont");

		out.println("th l");
		threads = this.getThreadInfoReader().readThreads();
		this.assertEquals(1, threads.length);
		this.assertEquals(1, threads[0].getId());
		out.println("cont");
	}

	public void testThreadFramesAndVariables() throws Exception {
		this.createSocket(new String[] { "Thread.new {", "a=5", "x=6", "}", "b=10", "b=11" });
		out.println("b test.rb:3");
		out.println("b test.rb:6");
		out.println("cont");
		this.getSuspensionReader().readSuspension();
		this.getSuspensionReader().readSuspension();
		// the main thread and the "puts 'a'" - thread are active
		out.println("th l");
		ThreadInfo[] threads = this.getThreadInfoReader().readThreads();
		this.assertEquals(2, threads.length);
		this.assertEquals(1, threads[0].getId());
		this.assertEquals(2, threads[1].getId());
		out.println("th 1 ; f ");
		RubyStackFrame[] stackFrames = this.getFramesReader().readFrames(new RubyThread(null, 1));
		this.assertEquals(1, stackFrames.length);
		this.assertEquals(6, stackFrames[0].getLineNumber());
		out.println("th 1 ; v l");
		RubyVariable[] variables = this.getVariableReader().readVariables(stackFrames[0]) ;
		this.assertEquals(1, variables.length) ;
		this.assertEquals("b", variables[0].getName()) ;
		out.println("th 2 ; f");
		stackFrames = this.getFramesReader().readFrames(new RubyThread(null, 1));
		this.assertEquals(1, stackFrames.length);
		this.assertEquals(3, stackFrames[0].getLineNumber());
		out.println("th 2 ; v l");
		variables = this.getVariableReader().readVariables(stackFrames[0]) ;
		// TODO: whats about the local variables within blocks given to Thread.new?
		//exception: NULL pointer given
		//this.assertEquals(1, variables.length) ;
		//this.assertEquals("a", variables[0].getName()) ;
		
	} 

}

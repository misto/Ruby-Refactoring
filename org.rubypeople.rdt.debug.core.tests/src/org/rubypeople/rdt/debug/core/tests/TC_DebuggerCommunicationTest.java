package org.rubypeople.rdt.debug.core.tests;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.rubypeople.rdt.internal.debug.core.ExceptionSuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.StepSuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.core.model.ThreadInfo;
import org.rubypeople.rdt.internal.debug.core.parsing.FramesReader;
import org.rubypeople.rdt.internal.debug.core.parsing.MultiReaderStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.rubypeople.rdt.internal.debug.core.parsing.ThreadInfoReader;
import org.rubypeople.rdt.internal.debug.core.parsing.VariableReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

public class TC_DebuggerCommunicationTest extends TestCase {
/*
	public static TestSuite suite() {

		TestSuite suite = new TestSuite();
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreads"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariablesInFrames"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testBreakpoint"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testFramesWhenThreadSpawned"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreadIdsAndResume"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreadsAndFrames"));		
		//suite.addTest(new TC_DebuggerCommunicationTest("testStepOver"));		
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreadFramesAndVariables"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariableNil"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariableInstanceNested"));				
		//suite.addTest(new TC_DebuggerCommunicationTest("testStaticVariableInstanceNested"));			
		//suite.addTest(new TC_DebuggerCommunicationTest("testException"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testNameError"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariablesInObject"));	
		suite.addTest(new TC_DebuggerCommunicationTest("testStaticVariables"));		
		//suite.addTest(new TC_DebuggerCommunicationTest("testSingletonStaticVariables"));							
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariableString"));	

		return suite;

	}
*/
	private static String tmpDir;
	private static String getTmpDir() {
		if (tmpDir == null) {
			tmpDir = System.getProperty("java.io.tmpdir");
			if (tmpDir.charAt(tmpDir.length() - 1) != File.separatorChar) {
				tmpDir = tmpDir + File.separator;
			}
		}
		return tmpDir;
	}
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
		return getTmpDir() + "test.rb";
	}

	private String getRubyTestFilename() {
		return getTestFilename().replace('\\', '/');
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
		String binDir = getClass().getResource("/").getFile();
		if (binDir.startsWith("/") && File.separatorChar == '\\') {
			binDir = binDir.substring(1);
		}
		String cmd = "ruby -I " + binDir + "../../org.rubypeople.rdt.launching/ruby -I" + getTmpDir().replace('\\', '/') + " -reclipseDebug.rb " + getRubyTestFilename();
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
		if (!new File(getTmpDir()).exists() || !new File(getTmpDir()).isDirectory()) {
			throw new RuntimeException("Temp directory does not exist: " + getTmpDir());
		}
	}

	public void tearDown() throws Exception {
		Thread.sleep(500);
		socket.close();
		try {
			if (process.exitValue() != 0) {
				System.out.println("Ruby finished with exit value: " + process.exitValue());
			}
		} catch (IllegalThreadStateException ex) {
			process.destroy();
			System.out.println("Ruby process had to be destroyed.");
		}

		System.out.println("Waiting for stdout redirector thread..");
		rubyStdoutRedirectorThread.join();
		System.out.println("..done");
		System.out.println("Waiting for stdout redirector thread..");
		rubyStderrRedirectorThread.join();
		System.out.println("..done");
	}

	private void writeFile(String name, String[] content) throws Exception {
		String fileName;
		PrintWriter writer = new PrintWriter(new FileOutputStream(getTmpDir() + name));
		for (int i = 0; i < content.length; i++) {
			writer.println(content[i]);
		}
		writer.close();
	}

	private void createSocket(String[] lines) throws Exception {
		writeFile("test.rb", lines);
		startRubyProcess();
		socket = new Socket("localhost", 1098);
		multiReaderStrategy = new MultiReaderStrategy(getXpp(socket));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	public void testNameError() throws Exception {

		createSocket(new String[] { "puts 'x'" });
		out.println("cont");
		SuspensionPoint hit = getSuspensionReader().readSuspension();
		// TODO: assertion is wrong, I want the debugger to suspend here
		// but there must be several catchpoints available
		assertNull(hit);
	}

	public void testBreakpoint() throws Exception {
		// Breakpoint in line 1 does not work yet.
		createSocket(new String[] { "puts 'a'", "def add", "puts 'b'", "end", "add()", "add()" });
		out.println("b test.rb:1");
		out.println("b add test.rb:3");
		out.println("cont");
		System.out.println("Waiting for breakpoint..");
		SuspensionPoint hit = getSuspensionReader().readSuspension();
		assertNotNull(hit);
		assertTrue(hit.isBreakpoint());
		assertEquals(1, hit.getLine());
		assertEquals("test.rb", hit.getFile());
		out.println("cont");
		hit = getSuspensionReader().readSuspension();
		assertNotNull(hit);
		assertTrue(hit.isBreakpoint());
		assertEquals(3, hit.getLine());
		assertEquals("test.rb", hit.getFile());
		out.println("b remove test.rb:3");
		out.println("cont");
		hit = getSuspensionReader().readSuspension();
		assertNull(hit);

	}

	public void testException() throws Exception {
		createSocket(new String[] { "puts 'a'", "raise 'message'", "puts 'c'" });
		out.println("cont");
		System.out.println("Waiting for exception");
		SuspensionPoint hit = getSuspensionReader().readSuspension();
		assertNotNull(hit);
		assertEquals(2, hit.getLine());
		assertEquals(getOSIndependent(getTmpDir() + "test.rb"), hit.getFile());
		assertTrue(hit.isException());
		assertEquals("message", ((ExceptionSuspensionPoint) hit).getExceptionMessage());
		assertEquals("RuntimeError", ((ExceptionSuspensionPoint) hit).getExceptionType());
		out.println("cont");
	}

	public void testBreakpointNeverReached() throws Exception {
		createSocket(new String[] { "puts 'a'", "puts 'b'", "puts 'c'" });
		out.println("b test.rb:10");
		out.println("cont");
		System.out.println("Waiting for breakpoint..");
		SuspensionPoint hit = getSuspensionReader().readSuspension();
		assertNull(hit);
	}

	public void testStepOver() throws Exception {
		createSocket(new String[] { "puts 'a'", "puts 'b'", "puts 'c'" });
		out.println("b test.rb:2");
		out.println("cont");
		getSuspensionReader().readSuspension();
		out.println("next");
		SuspensionPoint info = getSuspensionReader().readSuspension();
		assertEquals(3, info.getLine());
		assertEquals(getOSIndependent(getTmpDir() + "test.rb"), info.getFile());
		assertTrue(info.isStep());
		assertEquals(1, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("next");
		info = getSuspensionReader().readSuspension();
		assertNull(info);
	}

	public void testStepOverFrames() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "puts 'a'", "Test2.new.print()" });
		writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "end", "end" });
		PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		out.println("b test.rb:3");
		out.println("cont");
		getSuspensionReader().readSuspension();
		out.println("next");
		SuspensionPoint info = getSuspensionReader().readSuspension();
		assertNull(info);
	}

	public void testStepOverFramesValue2() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "puts 'a'", "Test2.new.print()" });
		writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		runTo("test2.rb", 3);
		out.println("next");
		SuspensionPoint info = getSuspensionReader().readSuspension();
		assertEquals(4, info.getLine());
		assertEquals(getOSIndependent(getTmpDir() + "test2.rb"), info.getFile());
		assertTrue(info.isStep());
		assertEquals(2, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("next");
		info = getSuspensionReader().readSuspension();
		assertNull(info);
	}

	public void testStepOverInDifferentFrame() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "Test2.new.print()", "puts 'a'" });
		writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		runTo("test2.rb", 4);
		out.println("next");
		SuspensionPoint info = getSuspensionReader().readSuspension();
		assertEquals(3, info.getLine());
		assertEquals(getOSIndependent(getTmpDir() + "test.rb"), info.getFile());
		assertTrue(info.isStep());
		assertEquals(1, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("cont");
	}

	public void testStepReturn() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "Test2.new.print()", "puts 'a'" });
		writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		runTo("test2.rb", 4);
		out.println("next");
		SuspensionPoint info = getSuspensionReader().readSuspension();
		assertEquals(3, info.getLine());
		assertEquals(getOSIndependent(getTmpDir() + "test.rb"), info.getFile());
		assertTrue(info.isStep());
		assertEquals(1, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("cont");
	}

	public void testHitBreakpointWhileSteppingOver() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "Test2.new.print()", "puts 'a'" });
		writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		out.println("b test2.rb:4");
		runTo("test.rb", 2);
		out.println("next");
		SuspensionPoint info = getSuspensionReader().readSuspension();
		assertEquals(4, info.getLine());
		assertEquals("test2.rb", info.getFile());
		assertTrue(info.isBreakpoint());
		out.println("cont");
	}

	public void testStepInto() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "puts 'a'", "Test2.new.print()" });
		writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "puts 'XX'", "end", "end" });
		runTo("test.rb", 3);
		out.println("step");
		SuspensionPoint info = getSuspensionReader().readSuspension();
		assertEquals(3, info.getLine());
		assertEquals(getOSIndependent(getTmpDir() + "test2.rb"), info.getFile());
		assertTrue(info.isStep());
		assertEquals(2, ((StepSuspensionPoint) info).getFramesNumber());
		out.println("cont");
	}

	private void runToLine(int lineNumber) throws Exception {
		runTo("test.rb", lineNumber);
	}

	private void runTo(String filename, int lineNumber) throws Exception {
		out.println("b " + filename + ":" + lineNumber);
		out.println("cont");
		SuspensionPoint suspension = getSuspensionReader().readSuspension();
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
		createSocket(new String[] { "puts 'a'", "puts 'b'", "stringA='XX'" });
		runToLine(2);
		out.println("v l");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("stringA", variables[0].getName());
		assertEquals("nil", variables[0].getValue().getValueString());
		assertEquals(null, variables[0].getValue().getReferenceTypeName());
		assertTrue(!variables[0].getValue().hasVariables());
	}

	public void testVariableWithXmlContent() throws Exception {
		createSocket(new String[] { "stringA='<start test=\"\"/>'", "puts 'b'" });
		runToLine(2);
		out.println("v l");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("stringA", variables[0].getName());
		assertEquals("<start test=\"\"/>", variables[0].getValue().getValueString());
		assertTrue(!variables[0].isStatic()) ;
		assertTrue(variables[0].isLocal()) ;
		assertTrue(!variables[0].isInstance()) ;						
	}

	public void testVariablesInObject() throws Exception {
		createSocket(new String[] { "class Test", "def initialize", "@y=5", "puts @y", "end", "def to_s", "'test'", "end", "end", "Test.new()" });
		runTo("test.rb", 4);
		// Read numerical variable
		out.println("v l");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("self", variables[0].getName());
		assertEquals("test", variables[0].getValue().getValueString());
		assertEquals("Test", variables[0].getValue().getReferenceTypeName());
		assertTrue(variables[0].getValue().hasVariables());
		out.println("v i self");
		variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("@y", variables[0].getName());
		assertEquals("5", variables[0].getValue().getValueString());
		assertEquals("Fixnum", variables[0].getValue().getReferenceTypeName());
		assertTrue(!variables[0].isStatic()) ;
		assertTrue(!variables[0].isLocal()) ;
		assertTrue(variables[0].isInstance()) ;				
		assertTrue(!variables[0].getValue().hasVariables());
		out.println("cont");
	}

	public void testStaticVariables() throws Exception {
		createSocket(new String[] { "class Test", "@@staticVar=55", "def method", "puts 'a'", "end", "end", "test=Test.new()", "test.method()" });
		runTo("test.rb", 4);
		out.println("v l") ;
	    RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("self", variables[0].getName());	
		assertTrue(variables[0].getValue().hasVariables());			    
		out.println("v i self");
		variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("@@staticVar", variables[0].getName());
		assertEquals("55", variables[0].getValue().getValueString());
		assertEquals("Fixnum", variables[0].getValue().getReferenceTypeName());
		assertTrue(variables[0].isStatic()) ;
		assertTrue(!variables[0].isLocal()) ;
		assertTrue(!variables[0].isInstance()) ;						
		assertTrue(!variables[0].getValue().hasVariables());
		out.println("cont");
	}

	public void testSingletonStaticVariables() throws Exception {
		createSocket(new String[] { "class Test",  "def method", "puts 'a'", "end", "class << Test", "@@staticVar=55", "end", "end", "Test.new().method()" });
		runTo("test.rb", 3);
		out.println("v i self");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("@@staticVar", variables[0].getName());
		assertEquals("55", variables[0].getValue().getValueString());
		assertEquals("Fixnum", variables[0].getValue().getReferenceTypeName());
		assertTrue(variables[0].isStatic()) ;
		assertTrue(!variables[0].isLocal()) ;
		assertTrue(!variables[0].isInstance()) ;				
		assertTrue(!variables[0].getValue().hasVariables());
		out.println("cont");
	}


	public void testVariableString() throws Exception {
		createSocket(new String[] { "stringA='XX'", "puts stringA" });
		runToLine(2);
		// Read numerical variable
		out.println("v l");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("stringA", variables[0].getName());
		assertEquals("XX", variables[0].getValue().getValueString());
		assertEquals("String", variables[0].getValue().getReferenceTypeName());
		assertTrue(!variables[0].getValue().hasVariables());
		out.println("cont");
	}

	public void testVariableInstance() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "customObject=Test2.new", "puts customObject" });
		writeFile("test2.rb", new String[] { "class Test2", "def initialize", "@y=5", "end", "def to_s", "'test'", "end", "end" });
		runTo("test2.rb", 6);
		out.println("v i 2 customObject");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("@y", variables[0].getName());
		assertEquals("5", variables[0].getValue().getValueString());
		assertEquals("Fixnum", variables[0].getValue().getReferenceTypeName());
		assertTrue(!variables[0].getValue().hasVariables());
	}

	public void testVariableInstanceNested() throws Exception {
		createSocket(new String[] { "class Test", "def initialize(test)", "@privateTest = test", "end", "end", "test2 = Test.new(Test.new(nil))", "puts test2" });
		runToLine(7);
		out.println("v l");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		RubyVariable test2Variable = variables[0];
		assertEquals("test2", test2Variable.getName());
		assertEquals("test2", test2Variable.getQualifiedName());
		out.println("v i " + test2Variable.getQualifiedName());
		variables = getVariableReader().readVariables(test2Variable);
		assertEquals(1, variables.length);
		RubyVariable privateTestVariable = variables[0];
		assertEquals("@privateTest", privateTestVariable.getName());
		assertEquals("test2.@privateTest", privateTestVariable.getQualifiedName());
		assertTrue(privateTestVariable.getValue().hasVariables());
		out.println("v i " + privateTestVariable.getQualifiedName());
		variables = getVariableReader().readVariables(privateTestVariable);
		assertEquals(1, variables.length);
		RubyVariable privateTestprivateTestVariable = variables[0];
		assertEquals("@privateTest", privateTestprivateTestVariable.getName());
		assertEquals("test2.@privateTest.@privateTest", privateTestprivateTestVariable.getQualifiedName());
		assertEquals("nil", privateTestprivateTestVariable.getValue().getValueString());
		assertTrue(!privateTestprivateTestVariable.getValue().hasVariables());
		out.println("cont");
	}

	public void testStaticVariableInstanceNested() throws Exception {
		createSocket(new String[] { "class TestStatic", "def initialize(no)", "@no = no", "end", "@@staticVar=TestStatic.new(2)",  "end", "test = TestStatic.new(1)", "puts test" });
		runToLine(8);
		out.println("v i test.@@staticVar");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(2, variables.length) ; 
		assertEquals("@no", variables[0].getName()) ;
		assertEquals("2", variables[0].getValue().getValueString()) ;
		assertEquals("@@staticVar", variables[1].getName()) ;
		assertTrue("2", variables[1].getValue().hasVariables()) ;
		
		out.println("cont");
	}


	public void testVariablesInFrames() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "y=5", "Test2.new().test()" });
		writeFile("test2.rb", new String[] { "class Test2", "def test", "y=6", "puts y", "end", "end" });
		runTo("test2.rb", 4);
		out.println("v l");
		RubyVariable[] variables = getVariableReader().readVariables(createStackFrame());
		// there are 2 variables self and y
		assertEquals(2, variables.length);
		// the variables are sorted: self = variables[0], y = variables[1]
		assertEquals("y", variables[1].getName());
		assertEquals("6", variables[1].getValue().getValueString());
		out.println("v l 1");
		variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(2, variables.length);
		assertEquals("y", variables[1].getName());
		assertEquals("6", variables[1].getValue().getValueString());
		out.println("v l 2");
		variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(1, variables.length);
		assertEquals("y", variables[0].getName());
		assertEquals("5", variables[0].getValue().getValueString());
		// 20 is out of range, then the default frame is used, which is 1
		out.println("v l 20");
		variables = getVariableReader().readVariables(createStackFrame());
		assertEquals(2, variables.length);
		assertEquals("y", variables[1].getName());
		assertEquals("6", variables[1].getValue().getValueString());
		out.println("cont");
	}

	public void testFrames() throws Exception {
		createSocket(new String[] { "require 'test2.rb'", "test = Test2.new()", "test.print()", "test.print()" });
		writeFile("test2.rb", new String[] { "class Test2", "def print", "puts 'XX'", "end", "end" });
		runTo("test2.rb", 3);
		out.println("b test.rb:4");
		out.println("w");
		RubyThread thread = new RubyThread(null, 0);
		getFramesReader().readFrames(thread);
		assertEquals(2, thread.getStackFrames().length);
		RubyStackFrame frame1 = (RubyStackFrame) thread.getStackFrames()[0];
		assertEquals(getOSIndependent(getTmpDir() + "test2.rb"), frame1.getFileName());
		assertEquals(1, frame1.getIndex());
		assertEquals(3, frame1.getLineNumber());
		RubyStackFrame frame2 = (RubyStackFrame) thread.getStackFrames()[1];
		assertEquals(getOSIndependent(getTmpDir() + "test.rb"), frame2.getFileName());
		assertEquals(2, frame2.getIndex());
		assertEquals(3, frame2.getLineNumber());
		out.println("cont");
		getSuspensionReader().readSuspension();
		out.println("w");
		getFramesReader().readFrames(thread);
		assertEquals(1, thread.getStackFramesSize());

	}

	public void testFramesWhenThreadSpawned() throws Exception {
		createSocket(new String[] { "def startThread", "Thread.new() {  a = 5  }", "end", "def calc", "5 + 5", "end", "startThread()", "calc()" });
		runTo("test.rb", 5);
		RubyThread thread = new RubyThread(null, 0);
		out.println("w");
		getFramesReader().readFrames(thread);
		assertEquals(2, thread.getStackFramesSize());
	}

	public void testThreads() throws Exception {
		createSocket(new String[] { "Thread.new {", "puts 'a'", "}", "Thread.pass", "puts 'b'" });
		out.println("b test.rb:2");
		out.println("b test.rb:5");
		out.println("cont");
		SuspensionPoint point = getSuspensionReader().readSuspension();
		if (point.getThreadId() == 2) {
			assertEquals(2, point.getLine());
		} else {
			assertEquals(5, point.getLine());
		}
		out.println("cont");
		point = getSuspensionReader().readSuspension();
		if (point.getThreadId() == 2) {
			assertEquals(2, point.getLine());
		} else {
			assertEquals(5, point.getLine());
		}
		out.println("cont");
	}

	public void testThreadIdsAndResume() throws Exception {
		createSocket(new String[] { "threads=[]", "threads << Thread.new {", "puts 'a'", "}", "threads << Thread.new{", "puts 'b'", "}", "puts 'c'", "threads.each{|t| t.join()}" });
		out.println("b test.rb:3");
		out.println("b test.rb:6");
		out.println("b test.rb:8");
		out.println("cont");
		getSuspensionReader().readSuspension();
		getSuspensionReader().readSuspension();
		getSuspensionReader().readSuspension();

		out.println("th l");
		ThreadInfo[] threads = getThreadInfoReader().readThreads();
		assertEquals(3, threads.length);
		assertEquals(1, threads[0].getId());
		assertEquals(2, threads[1].getId());
		assertEquals(3, threads[2].getId());
		out.println("th 2;cont");

		out.println("th l");
		threads = getThreadInfoReader().readThreads();
		assertEquals(2, threads.length);
		assertEquals(1, threads[0].getId());
		assertEquals(3, threads[1].getId());
		out.println("th 3 ; cont");

		out.println("th l");
		threads = getThreadInfoReader().readThreads();
		assertEquals(1, threads.length);
		assertEquals(1, threads[0].getId());
		out.println("cont");
	}

	public void testThreadFramesAndVariables() throws Exception {
		createSocket(new String[] { "Thread.new {", "a=5", "x=6", "puts 'x'", "}", "b=10", "b=11" });
		out.println("b test.rb:3");
		out.println("b test.rb:7");
		out.println("cont");
		getSuspensionReader().readSuspension();
		getSuspensionReader().readSuspension();
		// the main thread and the "puts 'a'" - thread are active
		out.println("th l");
		ThreadInfo[] threads = getThreadInfoReader().readThreads();
		assertEquals(2, threads.length);
		assertEquals(1, threads[0].getId());
		assertEquals(2, threads[1].getId());
		out.println("th 1 ; f ");
		RubyStackFrame[] stackFrames = getFramesReader().readFrames(new RubyThread(null, 1));
		assertEquals(1, stackFrames.length);
		assertEquals(7, stackFrames[0].getLineNumber());
		out.println("th 1 ; v l");
		RubyVariable[] variables = getVariableReader().readVariables(stackFrames[0]);
		assertEquals(1, variables.length);
		assertEquals("b", variables[0].getName());
		out.println("th 2 ; f");
		stackFrames = getFramesReader().readFrames(new RubyThread(null, 1));
		assertEquals(1, stackFrames.length);
		assertEquals(3, stackFrames[0].getLineNumber());
		out.println("th 2 ; v l");
		variables = getVariableReader().readVariables(stackFrames[0]);
		assertEquals(2, variables.length) ;
		assertEquals("a", variables[0].getName()) ;
		assertEquals("b", variables[1].getName()) ;
		out.println("th 2 ; next");
		getSuspensionReader().readSuspension();
		out.println("th 2 ; v l");		
		variables = getVariableReader().readVariables(stackFrames[0]);
		assertEquals(3, variables.length) ;
		assertEquals("a", variables[0].getName()) ;
		assertEquals("b", variables[1].getName()) ;
		assertEquals("x", variables[2].getName()) ;		
		
	}

}

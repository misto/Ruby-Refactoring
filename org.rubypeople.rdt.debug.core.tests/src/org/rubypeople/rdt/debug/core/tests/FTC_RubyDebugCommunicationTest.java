package org.rubypeople.rdt.debug.core.tests;

import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;

public class FTC_RubyDebugCommunicationTest extends
		FTC_ClassicDebuggerCommunicationTest {
	
	public static junit.framework.TestSuite suite() {

		junit.framework.TestSuite suite = new junit.framework.TestSuite();
		
		suite.addTest(new FTC_RubyDebugCommunicationTest("testCommandList"));
		
		suite.addTest(new FTC_RubyDebugCommunicationTest("testInspect"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testFrames"));
		
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepOver"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepOverFrames"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepOverFramesValue2"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepOverInDifferentFrame"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepReturn"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testHitBreakpointWhileSteppingOver"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepInto"));
		
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableString"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableLocal"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableInstance"));
		//suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableInstanceNested"));
		
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableNil"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableWithXmlContent"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableInObject"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableArray"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableArrayEmpty"));
		
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableHashWithStringKeys"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableHashWithObjectKeys"));
		
		
		
		
        
		return suite;
	}
	
	public FTC_RubyDebugCommunicationTest(String arg0) {
		super(arg0);
	}
	
	@Override
	public void startRubyProcess() throws Exception {
		// TODO Auto-generated method stub
		String cmd = "rdebug -s -p 1097 --cport 1098 -w -f xml -I " + getTmpDir().replace('\\', '/') + " " +  getRubyTestFilename() ;
				//"FTC_DebuggerCommunicationTest.RUBY_INTERPRETER + " -I" + createIncludeDir() +  " -I" + getTmpDir().replace('\\', '/') + " -reclipseDebugVerbose.rb " + ;
		System.out.println("Starting: " + cmd);
		process = Runtime.getRuntime().exec(cmd);
		rubyStderrRedirectorThread = new OutputRedirectorThread(process.getErrorStream());
		rubyStderrRedirectorThread.start();
		rubyStdoutRedirectorThread = new OutputRedirectorThread(process.getInputStream());
		rubyStdoutRedirectorThread.start();

	}

	@Override
	protected void readSuspensionInFirstLine() throws Exception {
		System.out.println("Waiting for suspension in first line") ;
		SuspensionPoint hit = getSuspensionReader().readSuspension();
		assertEquals(1, hit.getLine()) ;
	}
	
}

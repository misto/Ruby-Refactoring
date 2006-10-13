package org.rubypeople.rdt.debug.core.tests;

public class FTC_RubyDebugCommunicationTest extends
		FTC_ClassicDebuggerCommunicationTest {
	
	public static junit.framework.TestSuite suite() {

		junit.framework.TestSuite suite = new junit.framework.TestSuite();
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableLocal"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariablesInFrames"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableArray"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableArrayEmpty"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableHash"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableHashWithObjectKeys"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableHashWithStringKeys"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableWithXmlContent"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testThreads"));		
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepOver"));		
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testThreadFramesAndVariables"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testFramesWhenThreadSpawned"));
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testThreadIdsAndResume"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testThreadFramesAndVariables"));
//		
//		suite.addTest(new FTC_RubyDebugCommunicationTest("testFrames"));
		
		//suite.addTest(new TC_DebuggerCommunicationTest("testConstants"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testConstantDefinedInBothClassAndSuperclass"));
		
		//suite.addTest(new FTC_RubyDebugCommunicationTest("testBreakpointOnFirstLine"));
		//suite.addTest(new FTC_RubyDebugCommunicationTest("testBreakpointAddAndRemove"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariableNil"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariableInstanceNested"));				
		//suite.addTest(new TC_DebuggerCommunicationTest("testStaticVariableInstanceNested"));			
		
		//suite.addTest(new TC_DebuggerCommunicationTest("testNameError"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariablesInObject"));	
		//suite.addTest(new TC_DebuggerCommunicationTest("testStaticVariables"));		
		//suite.addTest(new TC_DebuggerCommunicationTest("testSingletonStaticVariables"));							
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariableString"));	
		// suite.addTest(new TC_DebuggerCommunicationTest("testInspect"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testInspectError"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testReloadAndInspect")) ;
		//suite.addTest(new TC_DebuggerCommunicationTest("testReloadWithException")) ;
		//suite.addTest(new TC_DebuggerCommunicationTest("testReloadAndStep")) ;
		//suite.addTest(new TC_DebuggerCommunicationTest("testReloadInRequire")) ;
		//suite.addTest(new TC_DebuggerCommunicationTest("testReloadInStackFrame")) ;
		//suite.addTest(new TC_DebuggerCommunicationTest("testIgnoreException"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testExceptionHierarchy"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testException"));
        
		return suite;
	}
	
	public FTC_RubyDebugCommunicationTest(String arg0) {
		super(arg0);
	}
	
	@Override
	public void startRubyProcess() throws Exception {
		// TODO Auto-generated method stub
		String cmd = "C:\\Programme\\ruby-1.8.5\\bin\\rdebug.cmd -s -p 1098 -e -w -I " + getTmpDir().replace('\\', '/') + " " +  getRubyTestFilename() ;
				//"FTC_DebuggerCommunicationTest.RUBY_INTERPRETER + " -I" + createIncludeDir() +  " -I" + getTmpDir().replace('\\', '/') + " -reclipseDebugVerbose.rb " + ;
		System.out.println("Starting: " + cmd);
		process = Runtime.getRuntime().exec(cmd);
		rubyStderrRedirectorThread = new OutputRedirectorThread(process.getErrorStream());
		rubyStderrRedirectorThread.start();
		rubyStdoutRedirectorThread = new OutputRedirectorThread(process.getInputStream());
		rubyStdoutRedirectorThread.start();

	}

}

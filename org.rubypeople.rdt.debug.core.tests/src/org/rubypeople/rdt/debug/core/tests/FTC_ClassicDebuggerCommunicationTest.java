package org.rubypeople.rdt.debug.core.tests;

import java.io.File;

import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.launching.RdtLaunchingPlugin;

public class FTC_ClassicDebuggerCommunicationTest extends
		FTC_AbstractDebuggerCommunicationTest {
	public static junit.framework.TestSuite suite() {

		junit.framework.TestSuite suite = new junit.framework.TestSuite();
		//suite.addTest(new FTC_DebuggerCommunicationTest("testBreakpointOnFirstLine"));
		//suite.addTest(new FTC_DebuggerCommunicationTest("testBreakpointAddAndRemove"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testVariableLocal"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testVariableArray"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testVariableArrayEmpty"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testVariableHash"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testVariableHashWithObjectKeys"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testVariableHashWithStringKeys"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testVariableWithXmlContent"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testThreadIdsAndResume"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testThreadFramesAndVariables"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testFrames"));
//		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testThreads"));
		
		
		suite.addTest(new FTC_ClassicDebuggerCommunicationTest("testVariablesInFrames"));
		
		
		//suite.addTest(new TC_DebuggerCommunicationTest("testConstants"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testConstantDefinedInBothClassAndSuperclass"));
		
		//suite.addTest(new TC_DebuggerCommunicationTest("testVariablesInFrames"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testFramesWhenThreadSpawned"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreadIdsAndResume"));
		//suite.addTest(new TC_DebuggerCommunicationTest("testThreadsAndFrames"));		
		//suite.addTest(new TC_DebuggerCommunicationTest("testStepOver"));		
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
//		suite.addTest(new TC_DebuggerCommunicationTest("testIgnoreException"));
//		suite.addTest(new TC_DebuggerCommunicationTest("testExceptionHierarchy"));
//		suite.addTest(new TC_DebuggerCommunicationTest("testException"));
        
		return suite;
	}

	public FTC_ClassicDebuggerCommunicationTest(String arg0) {
		super(arg0);
	}

	public void startRubyProcess() throws Exception {
		String cmd = FTC_ClassicDebuggerCommunicationTest.RUBY_INTERPRETER + " -I" + createIncludeDir() +  " -I" + getTmpDir().replace('\\', '/') + " -reclipseDebugVerbose.rb " + getRubyTestFilename();
		System.out.println("Starting: " + cmd);
		process = Runtime.getRuntime().exec(cmd);
		rubyStderrRedirectorThread = new OutputRedirectorThread(process.getErrorStream());
		rubyStderrRedirectorThread.start();
		rubyStdoutRedirectorThread = new OutputRedirectorThread(process.getInputStream());
		rubyStdoutRedirectorThread.start();
	
	}

	private String createIncludeDir() {
		String includeDir;
		if (RdtLaunchingPlugin.getDefault() != null) {
			// being run as JUnit Plug-in Test, Eclipse is running			
			includeDir = RubyCore.getOSDirectory(RdtLaunchingPlugin.getDefault()) + "ruby" ;
		}
		else {
		    // being run as "pure" JUnit Test without Eclipse running 
			// getResource delivers a URL, so we get slashes as Fileseparator
			includeDir = getClass().getResource("/").getFile();
			includeDir += "../../org.rubypeople.rdt.launching/ruby" ;
			// if on windows, remove a leading slash
			if (includeDir.startsWith("/") && File.separatorChar == '\\') {
				includeDir = includeDir.substring(1);
			}
		}
		// the ruby interpreter on linux does not like quotes, so we use them only if really necessary
		if (includeDir.indexOf(" ") == -1) {
			return includeDir ;
		}
		else {
			return '"' + includeDir + '"';
		}
	}

}

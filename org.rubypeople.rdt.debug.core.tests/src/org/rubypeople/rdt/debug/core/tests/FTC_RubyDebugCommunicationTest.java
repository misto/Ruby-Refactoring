package org.rubypeople.rdt.debug.core.tests;

import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;

import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.commands.AbstractDebuggerConnection;
import org.rubypeople.rdt.internal.debug.core.commands.EvalCommand;
import org.rubypeople.rdt.internal.debug.core.commands.RubyDebugConnection;
import org.rubypeople.rdt.internal.debug.core.parsing.AbstractReadStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.EvalReader;
import org.rubypeople.rdt.internal.debug.core.parsing.MultiReaderStrategy;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

public class FTC_RubyDebugCommunicationTest extends FTC_ClassicDebuggerCommunicationTest {

	public static junit.framework.TestSuite suite() {

		junit.framework.TestSuite suite = new junit.framework.TestSuite();

		suite.addTest(new FTC_RubyDebugCommunicationTest("testCommandList"));

		suite.addTest(new FTC_RubyDebugCommunicationTest("testBreakpointOnFirstLine"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testBreakpointAddAndRemove"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testBreakpointNeverReached"));
		
		suite.addTest(new FTC_RubyDebugCommunicationTest("testException"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testExceptionsIgnoredByDefault"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testExceptionHierarchy"));
		
		suite.addTest(new FTC_RubyDebugCommunicationTest("testInspect"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testInspectTemporaryArray"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testInspectNil"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testInspectError"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testEvalError"));

		suite.addTest(new FTC_RubyDebugCommunicationTest("testFrames"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testFramesWhenThreadSpawned"));

		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepOver"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepOverFrames"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepOverInDifferentFrame"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepReturn"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testHitBreakpointWhileSteppingOver"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testStepInto"));

		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableString"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableLocal"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableInstance"));
		// is instance nested still needed? 
		// suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableInstanceNested"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableNil"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableWithXmlContent"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableInObject"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableArray"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableArrayEmpty"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableHashWithStringKeys"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testVariableHashWithObjectKeys"));

		suite.addTest(new FTC_RubyDebugCommunicationTest("testStaticVariables"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testSingletonStaticVariables"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testConstants"));
		suite.addTest(new FTC_RubyDebugCommunicationTest("testConstantDefinedInBothClassAndSuperclass"));
		
		return suite;
	}


	public FTC_RubyDebugCommunicationTest(String arg0) {
		super(arg0);
	}

	@Override
	public void startRubyProcess() throws Exception {
		// TODO Auto-generated method stub
		String cmd = "rdebug -s -w -n -p 1098 --cport 1099 -d -f xml -I " + getTmpDir().replace('\\', '/') + " " + getRubyTestFilename();
		// "FTC_DebuggerCommunicationTest.RUBY_INTERPRETER + " -I" +
		// createIncludeDir() + " -I" + getTmpDir().replace('\\', '/') + "
		// -reclipseDebugVerbose.rb " + ;
		System.out.println("Starting: " + cmd);
		process = Runtime.getRuntime().exec(cmd);
		rubyStderrRedirectorThread = new OutputRedirectorThread(process.getErrorStream());
		rubyStderrRedirectorThread.start();
		rubyStdoutRedirectorThread = new OutputRedirectorThread(process.getInputStream());
		rubyStdoutRedirectorThread.start();

	}

	protected String getDirectoryOfRubyDebuggerFile() {
		String result = null;
		if (RubyCore.getPlugin() != null) {
			result = RubyCore.getOSDirectory(LaunchingPlugin.getDefault()) + "ruby";
		} else {
			result = LaunchingPlugin.class.getResource(".").getPath() + "/../../../../../../ruby";
		}
		return result;
	}
	
//	@Override
//	protected void createControlSocket() throws Exception {
//			try {
//				controlSocket = new Socket("localhost", 1099);
//			} catch (ConnectException cex) {
//				throw new RuntimeException(
//						"Ruby process finished prematurely. Last line in stderr: "
//								+ rubyStderrRedirectorThread.getLastLine(), cex);
//			}
//			controlReaderStrategy = new MultiReaderStrategy(getXpp(controlSocket));
//
//			Runnable runnable = new Runnable() {
//				public void run() {
//					try {
//						while (true) {
//							new WasteReader(controlReaderStrategy).read();
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				};
//			};
//			new Thread(runnable).start();
//			Thread.sleep(500) ;
//			controlWriter = new PrintWriter(controlSocket.getOutputStream(), true);
//			registerRubyDebugExtensions() ;
//	}

	@Override
	protected AbstractDebuggerConnection createDebuggerConnection() {
		return new RubyDebugConnection(getDirectoryOfRubyDebuggerFile(), 1098);
	}
	


}

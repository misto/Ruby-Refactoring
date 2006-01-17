package org.rubypeople.rdt.debug.core.tests;

import java.io.ByteArrayInputStream;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.eclipse.testutils.ResourceTools;
import org.rubypeople.rdt.internal.debug.core.RubyLineBreakpoint;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import org.rubypeople.rdt.internal.launching.RubyRuntime;

/*
 * 
 */
public class FTC_DebuggerLaunch extends TestCase {


	public void setUp() {
		this.createInterpreter() ;
	}
	
	protected void createInterpreter() {
		// We rely on the RUBY_INTERPRETER to be a full path to a valid ruby executable, therefore the property rdt.rubyInterpreter has
		// to be set accordingly
		String rubyInterpreterPath = FTC_DebuggerCommunicationTest.RUBY_INTERPRETER ;
		System.out.println("Using interpreter: " + rubyInterpreterPath) ;
		RubyInterpreter rubyInterpreter = new RubyInterpreter("RubyInterpreter", new Path(rubyInterpreterPath));
		RubyRuntime.getDefault().addInstalledInterpreter(rubyInterpreter) ;
	
	}
	
	protected void log(String label, ILaunch launch) throws Exception {
		System.out.println("Infos about " + label + ":");
		IProcess process = launch.getProcesses()[0];
		if (process.isTerminated()) {
			System.out.println("Process has finished with exit-value: " + process.getExitValue());
		} else {
			System.out.println("Process still running.");
		}
		String error = process.getStreamsProxy().getErrorStreamMonitor().getContents();
		if (error != null && error.length() > 0) {
			System.out.println("Process stderr: " + error);
		}	
		String stdout = process.getStreamsProxy().getOutputStreamMonitor().getContents();
		if (stdout != null && stdout.length() > 0) {
			System.out.println("Process stdout: " + stdout);
		}		
	}

	
	public void testTwoSessions() throws Exception {
		ILaunchConfigurationType lcT = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(RubyLaunchConfigurationAttribute.RUBY_LAUNCH_CONFIGURATION_TYPE) ;
		
		ILaunchConfigurationWorkingCopy wc = lcT.newInstance(null, "TestLaunchConfiguration") ;
		IProject project = ResourceTools.createProject("FTCDebuggerLaunchMultipleSessions") ;
		IFile rubyFile = project.getFile("run.rb");
		
		rubyFile.create(new ByteArrayInputStream("puts 'a'\nputs 'b'".getBytes()), true, new NullProgressMonitor()) ;
		wc.setAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, rubyFile.getProject().getName());
		wc.setAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, rubyFile.getProjectRelativePath().toString());
		//wc.setAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, RubyApplicationShortcut.getDefaultWorkingDirectory(rubyFile.getProject()));
		wc.setAttribute(RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER, "RubyInterpreter");
		ILaunchConfiguration lc = wc.doSave() ;
		
		DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(new RubyLineBreakpoint(rubyFile, 1)) ;
		
		ILaunch launch = lc.launch("debug", new NullProgressMonitor()) ;
		Thread.sleep(5000)  ;
		this.log("1. launch", launch) ;
		// getDebugTarget returns null if connection between ruby debugger and RubyDebuggerProxy (RubyLoop) could not
		// be established
		assertNotNull("1. debug target not null", launch.getDebugTarget()) ;
        assertNotNull("1. debug target has threads", launch.getDebugTarget().getThreads());
        assertTrue("1. debug target has at least one thread", launch.getDebugTarget().getThreads().length > 0);
		assertTrue("1. debug target's first thread is suspended ", launch.getDebugTarget().getThreads()[0].isSuspended()) ;
		
		// the breakpoint we have set for the first launch has disappeard at this point through
		// a ResourceChanged Event
		DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(new RubyLineBreakpoint(rubyFile, 1)) ;
		ILaunch secondlaunch = lc.launch("debug", new NullProgressMonitor()) ;
		Thread.sleep(5000)  ;
		this.log("2. launch", secondlaunch) ;
		assertNotNull("2. debug target not null", secondlaunch.getDebugTarget()) ;
        assertNotNull("2. debug target has threads", secondlaunch.getDebugTarget().getThreads());
        assertTrue("2. debug target has at least one thread", secondlaunch.getDebugTarget().getThreads().length > 0);
		assertFalse("2. debug target's first prozess is not terminated", secondlaunch.getProcesses()[0].isTerminated()) ;
		assertTrue("2. debug target's first thread is suspended ", secondlaunch.getDebugTarget().getThreads()[0].isSuspended()) ;
	}
}

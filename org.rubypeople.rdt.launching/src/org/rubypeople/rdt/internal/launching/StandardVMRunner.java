/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.launching;


import java.io.File;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.launching.AbstractVMRunner;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class StandardVMRunner extends AbstractVMRunner {
	
	protected static final String END_OF_OPTIONS_DELIMITER = "--";
	
	protected IVMInstall fVMInstance;

	public StandardVMRunner(IVMInstall vmInstance) {
		fVMInstance= vmInstance;
	}
	
	protected String renderDebugTarget(String classToRun, int host) {
		String format= LaunchingMessages.StandardVMRunner__0__at_localhost__1__1; 
		return MessageFormat.format(format, classToRun, String.valueOf(host));
	}

	public static String renderProcessLabel(String[] commandLine) {
		String format= LaunchingMessages.StandardVMRunner__0____1___2; 
		String timestamp= DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(System.currentTimeMillis()));
		return MessageFormat.format(format, commandLine[0], timestamp);
	}
	
	protected static String renderCommandLine(String[] commandLine) {
		if (commandLine.length < 1)
			return ""; //$NON-NLS-1$
		StringBuffer buf= new StringBuffer();
		for (int i= 0; i < commandLine.length; i++) {
			buf.append(' ');
			char[] characters= commandLine[i].toCharArray();
			StringBuffer command= new StringBuffer();
			boolean containsSpace= false;
			for (int j = 0; j < characters.length; j++) {
				char character= characters[j];
				if (character == '\"') {
					command.append('\\');
				} else if (character == ' ') {
					containsSpace = true;
				}
				command.append(character);
			}
			if (containsSpace) {
				buf.append('\"');
				buf.append(command.toString());
				buf.append('\"');
			} else {
				buf.append(command.toString());
			}
		}	
		return buf.toString();
	}	
	
	protected void addArguments(String[] args, List<String> v) {
		if (args == null) {
			return;
		}
		for (int i= 0; i < args.length; i++) {
			v.add(args[i]);
		}
	}
	
	/**
	 * Returns the working directory to use for the launched VM,
	 * or <code>null</code> if the working directory is to be inherited
	 * from the current process.
	 * 
	 * @return the working directory to use
	 * @exception CoreException if the working directory specified by
	 *  the configuration does not exist or is not a directory
	 */	
	protected File getWorkingDir(VMRunnerConfiguration config) throws CoreException {
		String path = config.getWorkingDirectory();
		if (path == null) {
			return null;
		}
		File dir = new File(path);
		if (!dir.isDirectory()) {
			abort(MessageFormat.format(LaunchingMessages.StandardVMRunner_Specified_working_directory_does_not_exist_or_is_not_a_directory___0__3, path), null, IRubyLaunchConfigurationConstants.ERR_WORKING_DIRECTORY_DOES_NOT_EXIST); 
		}
		return dir;
	}
	
	/**
	 * @see VMRunner#getPluginIdentifier()
	 */
	protected String getPluginIdentifier() {
		return LaunchingPlugin.getUniqueIdentifier();
	}
	
	/**
	 * Construct and return a String containing the full path of a ruby executable
	 * command such as 'ruby' or 'rubyw.exe'.  If the configuration specifies an
	 * explicit executable, that is used.
	 * 
	 * @return full path to ruby executable
	 * @exception CoreException if unable to locate an executeable
	 */
	protected String constructProgramString(VMRunnerConfiguration config) throws CoreException {
		// Look for the user-specified ruby executable command
		String command= null;
		Map map= config.getVMSpecificAttributesMap();
		if (map != null) {
			command = (String)map.get(IRubyLaunchConfigurationConstants.ATTR_RUBY_COMMAND);
		}
		
		// If no ruby command was specified, use default executable
		if (command == null) {
			File exe = StandardVMType.findRubyExecutable(fVMInstance.getInstallLocation());
			if (exe == null) {
				abort(MessageFormat.format(LaunchingMessages.StandardVMRunner_Unable_to_locate_executable_for__0__1, fVMInstance.getName()), null, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
			}
			return exe.getAbsolutePath();
		}
				
		// Build the path to the ruby executable.  First try 'bin'
		String installLocation = fVMInstance.getInstallLocation().getAbsolutePath() + File.separatorChar;
		File exe = new File(installLocation + "bin" + File.separatorChar + command); //$NON-NLS-1$ 		
		if (fileExists(exe)){
			return exe.getAbsolutePath();
		}
		exe = new File(exe.getAbsolutePath() + ".exe"); //$NON-NLS-1$
		if (fileExists(exe)){
			return exe.getAbsolutePath();
		}
		
		// not found
		abort(MessageFormat.format(LaunchingMessages.StandardVMRunner_Specified_executable__0__does_not_exist_for__1__4, command, fVMInstance.getName()), null, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
		// NOTE: an exception will be thrown - null cannot be returned
		return null;		
	}	
	
	protected boolean fileExists(File file) {
		return file.exists() && file.isFile();
	}

	protected List<String> convertLoadPath(String[] lp) {
		List<String> strings = new ArrayList<String>();
		for (int i= 0; i < lp.length; i++) {
			strings.add("-I"); //$NON-NLS-1$
			strings.add(lp[i]);
		}
		return strings;
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jdt.launching.IVMRunner#run(org.eclipse.jdt.launching.VMRunnerConfiguration, org.eclipse.debug.core.ILaunch, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) throws CoreException {

		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}
		
		IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
		subMonitor.beginTask(LaunchingMessages.StandardVMRunner_Launching_VM____1, 2); 
		subMonitor.subTask(LaunchingMessages.StandardVMRunner_Constructing_command_line____2); 
		
		String program= constructProgramString(config);
		
		List<String> arguments= new ArrayList<String>();
		arguments.add(program);
				
		// VM args are the first thing after the ruby program so that users can specify
		// options like '-client' & '-server' which are required to be the first option
		String[] allVMArgs = combineVmArgs(config, fVMInstance);
		addArguments(allVMArgs, arguments);
		// FIXME Find a way to set stderr and stdout to sync/auto-flush without messing up value of __FILE__ (becomes absolute which messes up the 'if __FILE__ == $0' idiom)
//		arguments.add("-e");
//		arguments.add("STDOUT.sync=true");
//		arguments.add("-e");
//		arguments.add("STDERR.sync=true");
//		arguments.add("-e");
//		arguments.add("load($0=ARGV.shift)");
		
		String[] lp= config.getLoadPath();
		if (lp.length > 0) {
			arguments.addAll(convertLoadPath(lp));
		}
		arguments.add(END_OF_OPTIONS_DELIMITER);
		
		arguments.add(config.getFileToLaunch());
		
		String[] programArgs= config.getProgramArguments();
		addArguments(programArgs, arguments);
				
		String[] cmdLine= new String[arguments.size()];
		arguments.toArray(cmdLine);
		
		String[] envp= config.getEnvironment();
		
		subMonitor.worked(1);

		// check for cancellation
		if (monitor.isCanceled()) {
			return;
		}
		
		subMonitor.subTask(LaunchingMessages.StandardVMRunner_Starting_virtual_machine____3); 
		Process p= null;
		File workingDir = getWorkingDir(config);
		p= exec(cmdLine, workingDir, envp);
		if (p == null) {
			return;
		}
		
		// check for cancellation
		if (monitor.isCanceled()) {
			p.destroy();
			return;
		}		
		
		IProcess process= newProcess(launch, p, renderProcessLabel(cmdLine), getDefaultProcessMap());
		process.setAttribute(IProcess.ATTR_CMDLINE, renderCommandLine(cmdLine));
		subMonitor.worked(1);
		subMonitor.done();
	}

}

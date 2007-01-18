package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.launching.IInterpreter;
import org.rubypeople.rdt.launching.RubyRuntime;

public class InterpreterRunnerConfiguration {
	protected ILaunchConfiguration configuration;

	public InterpreterRunnerConfiguration(ILaunchConfiguration aConfiguration) {
		configuration = aConfiguration;
	}
	
	public String getAbsoluteFileName() {
		IProject project = getProject().getProject();

		return project.getLocation().toOSString() + "/" + getFileName();
	}
	
	
	public String getAbsoluteFileDirectory() {
		
		IPath path = new Path(this.getFileName()) ;
		path = path.removeLastSegments(1) ;
		IProject project = getProject().getProject();
		return 	project.getLocation().toOSString() + File.separator + path.toOSString();
	}
	
	public String getFileName() {
		String fileName = "";

		try {
			fileName = configuration.getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "No file specified in configuration");
		} catch(CoreException e) {}
		
		return fileName.replace('\\', '/');
	}
	
	public RubyProject getProject() {
		String projectName = "" ;
		
		try {
			projectName = configuration.getAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, "");
		} catch(CoreException e) {
			RdtLaunchingPlugin.log(e);
		}
		
		RubyProject rubyProject = new RubyProject();
		if (projectName.length() > 0 ) {
			IProject project = RdtLaunchingPlugin.getWorkspace().getRoot().getProject(projectName);
			rubyProject.setProject(project);
		}
		
		return rubyProject;
	}
	
	public File getAbsoluteWorkingDirectory() {
		String file = null;
		try {
			file = configuration.getAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, "");
		} catch(CoreException e) {
			RdtLaunchingPlugin.log(e);
		}
		// it is valid not to specify a working directroy by returning null
		// whereas new File("") would specify an invalid directory
		return file == "" ? null : new File(file);
	}
	
	public String getInterpreterArguments() {
		try {
			return configuration.getAttribute(RubyLaunchConfigurationAttribute.INTERPRETER_ARGUMENTS, "");
		} catch(CoreException e) {}
		
		return "";
	}
	
	public String getProgramArguments() {
		try {
			return configuration.getAttribute(RubyLaunchConfigurationAttribute.PROGRAM_ARGUMENTS, "");
		} catch (CoreException e) {}
		
		return "";
	}

	public IInterpreter getInterpreter() {
		String selectedInterpreter = null;
		try {
			selectedInterpreter = configuration.getAttribute(RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER, "");
		} catch(CoreException e) {}
		
		return RubyRuntime.getDefault().getInterpreter(selectedInterpreter);
	}
    
    protected void addToLoadPath(List<String> loadPath, IProject project) {
    	if (!project.isAccessible()) {
    		return ;
    	}
        addToLoadPath(loadPath, project.getLocation().toOSString());
    }

	private void addToLoadPath(List<String> loadPath, String pathDirectory) {
		loadPath.add("-I");
        loadPath.add(RdtLaunchingPlugin.osDependentPath(pathDirectory));
	}
    
    protected List<String> renderLoadPath() {
    	List<String> loadPath = new ArrayList<String>();
        RubyProject project = this.getProject();
        addToLoadPath(loadPath, project.getProject());

        try {
			ILoadpathEntry[] entries = project.getResolvedLoadpath(true, false);
			for (int i = 0; i < entries.length; i++) {
				addToLoadPath(loadPath, entries[i].getPath().toOSString());
			}
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (new Path(this.getFileName()).segmentCount() > 1) { ;
        	addToLoadPath(loadPath, this.getAbsoluteFileDirectory());
        }
        return loadPath;
    }
}

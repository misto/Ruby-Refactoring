/**
 * 
 */
package org.rubypeople.rdt.internal.core.builder;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.parser.MarkerUtility;
import org.rubypeople.rdt.internal.core.parser.RdtWarnings;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.TaskParser;

/**
 * @author Chris
 * 
 */
public class RubyBuilder extends IncrementalProjectBuilder {

	private static final boolean DEBUG = false;
	public static int MAX_AT_ONCE = 1000;
	private IProject currentProject;
	protected boolean compiledAllAtOnce;

	public RubyBuilder() {}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IncrementalProjectBuilder#build(int,
	 *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
		RubyCore.log("Told to build");

		this.currentProject = getProject();
		if (currentProject == null || !currentProject.isAccessible()) return new IProject[0];
		// FIXME Build incrementally by deltas if we can!
		build();

		// FIXME Get the required projects as in JavaBuilder
		if (DEBUG) System.out.println("Finished build of " + currentProject.getName() //$NON-NLS-1$
				+ " @ " + new Date(System.currentTimeMillis())); //$NON-NLS-1$
		return new IProject[0];
	}

	public void build() {
		if (DEBUG) System.out.println("FULL build"); //$NON-NLS-1$

		try {
			ArrayList sourceFiles = new ArrayList(33);
			addAllSourceFiles(sourceFiles);

			if (sourceFiles.size() > 0) {
				IFile[] allSourceFiles = new IFile[sourceFiles.size()];
				sourceFiles.toArray(allSourceFiles);
				compile(allSourceFiles);
			}

		} catch (CoreException e) {
			// throw internalException(e);
			throw new RuntimeException(e);
		}
	}

	protected void addAllSourceFiles(final ArrayList sourceFiles) throws CoreException {
		currentProject.accept(new IResourceProxyVisitor() {

			public boolean visit(IResourceProxy proxy) throws CoreException {
				IResource resource = null;
				switch (proxy.getType()) {
				case IResource.FILE:
					if (org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(proxy.getName())) {
						if (resource == null) resource = proxy.requestResource();
						sourceFiles.add(resource);
					}
					return false;
				}
				return true;
			}
		}, IResource.NONE);
	}

	/*
	 * Compile the given elements, adding more elements to the work queue if
	 * they are affected by the changes.
	 */
	protected void compile(IFile[] units) {
		int unitsLength = units.length;
		// do them all now
		for (int i = 0; i < unitsLength; i++) {
			try {
				IFile file = units[i];
				if (DEBUG) System.out.println("About to compile " + file); //$NON-NLS-1$
				RdtWarnings warnings = new RdtWarnings();
				RubyParser parser = new RubyParser(warnings);
				try {
					MarkerUtility.removeMarkers(file);
					parser.parse(units[i].getName(), new InputStreamReader(file.getContents()));
					MarkerUtility.createProblemMarkers(file, warnings.getWarnings());						
				} catch (SyntaxException e) {
					MarkerUtility.createSyntaxError(file, e);
				}
				createTasks(file);
			} catch (CoreException e) {
				RubyCore.log(e);
			}
		}

	}

	private void createTasks(IFile file) throws CoreException {
		IEclipsePreferences preferences = RubyCore.getInstancePreferences();
		TaskParser taskParser = new TaskParser(preferences);
		taskParser.parse(new InputStreamReader(file.getContents()));
		MarkerUtility.createTasks(file, taskParser.getTasks());
	}
}

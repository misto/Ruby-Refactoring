package org.rubypeople.rdt.internal.debug.core;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.LineBreakpoint;

public class RubyLineBreakpoint extends LineBreakpoint {
	private static final String RUBY_BREAKPOINT_MARKER = "org.rubypeople.rdt.debug.core.RubyBreakpointMarker"; //$NON-NLS-1$

	public RubyLineBreakpoint(final IResource resource, final int lineNumber) throws CoreException {
		IWorkspaceRunnable wr = new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException {
				setMarker(resource.createMarker(RUBY_BREAKPOINT_MARKER));
				getMarker().setAttribute(IMarker.LINE_NUMBER, lineNumber + 1);
				getMarker().setAttribute(REGISTERED, false);
				setRegistered(true);
				setEnabled(true);
			}
		};
		try {
			ResourcesPlugin.getWorkspace().run(wr, null);
		} catch (CoreException e) {
			throw new DebugException(e.getStatus());
		}

	}

	private void register() throws CoreException {
		DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(this);
	}

	public int getLineNumber() throws CoreException {
		return ensureMarker().getAttribute(IMarker.LINE_NUMBER, -1);
	}

	public int getCharStart() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_START, -1);
	}

	public int getCharEnd() throws CoreException {
		return ensureMarker().getAttribute(IMarker.CHAR_END, -1);
	}

	/**
	 * Returns the type of marker associated with this type of breakpoints
	 */
	public static String getMarkerType() {
		return RUBY_BREAKPOINT_MARKER;
	}

	public String getModelIdentifier() {
		return "org.rubypeople.rdt.debug";
	}

}

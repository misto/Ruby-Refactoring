package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.debug.ui.IValueDetailListener;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.internal.debug.core.RubyLineBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class DebugModelPresentation extends LabelProvider implements IDebugModelPresentation {

	public String getText(Object item) {
		if (item instanceof RubyLineBreakpoint) {
			RubyLineBreakpoint breakpoint = (RubyLineBreakpoint) item;
			try {
				return breakpoint.getMarker().getResource().getName() + ":" + breakpoint.getLineNumber();
			} catch (CoreException e) {
				e.printStackTrace();
				return "--";
			}
		}
		if (item instanceof RubyVariable) {
			return ((RubyVariable) item).toString() ;	
		}
		return DebugUIPlugin.getDefaultLabelProvider().getText(item) ;
	}

	protected IBreakpoint getBreakpoint(IMarker marker) {
		return DebugPlugin.getDefault().getBreakpointManager().getBreakpoint(marker);
	}

	public void computeDetail(IValue value, IValueDetailListener listener) {
		System.out.println("computeDetail.");
	}

	public void setAttribute(String attribute, Object value) {
		System.out.println("setAttribute.");
	}

	public String getEditorId(IEditorInput input, Object element) {
		System.out.println("getEditorId.");
		return "X";
	}

	public IEditorInput getEditorInput(Object element) {
		return null;
	}

	public Image getImage(Object item) {
		ImageDescriptor descriptor;
		if (item instanceof IMarker || item instanceof RubyLineBreakpoint) {
			descriptor = DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_BREAKPOINT);
		} else if (item instanceof RubyThread) {
			RubyThread thread = (RubyThread) item;
			if (thread.isSuspended()) {
				descriptor = DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_SUSPENDED);
			} else if (thread.isTerminated()) {
				descriptor = DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_TERMINATED);
			} else {
				descriptor = DebugUITools.getImageDescriptor(IDebugUIConstants.IMG_OBJS_THREAD_RUNNING);
			}
		} else {
			descriptor = DebugUITools.getDefaultImageDescriptor(item);
		}
		// TODO: save image instead of creating a new one every time.
		return descriptor.createImage();
	}

}
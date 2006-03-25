package org.rubypeople.rdt.internal.ui.browsing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.ui.StandardRubyElementContentProvider;

public class RubyBrowsingContentProvider extends
		StandardRubyElementContentProvider {

	private RubyBrowsingPart fBrowsingPart;
	private StructuredViewer fViewer;
	private int fReadsInDisplayThread;
	private Object fInput;

	public RubyBrowsingContentProvider(boolean provideMembers,
			RubyBrowsingPart browsingPart) {
		super(provideMembers);
		fBrowsingPart = browsingPart;
		fViewer = fBrowsingPart.getViewer();
		// TODO Add element change listener
		// RubyCore.addElementChangedListener(this);
	}

	public boolean hasChildren(Object element) {
		startReadInDisplayThread();
		try {
			return super.hasChildren(element);
		} finally {
			finishedReadInDisplayThread();
		}
	}

	public Object[] getChildren(Object element) {
		if (!exists(element))
			return NO_CHILDREN;

		startReadInDisplayThread();
		try {
			if (element instanceof Collection) {
				Collection elements = (Collection) element;
				if (elements.isEmpty())
					return NO_CHILDREN;
				Object[] result = new Object[0];
				Iterator iter = ((Collection) element).iterator();
				while (iter.hasNext()) {
					Object[] children = getChildren(iter.next());
					if (children != NO_CHILDREN)
						result = concatenate(result, children);
				}
				return result;
			}
			if (fProvideMembers && element instanceof IType)
				return getChildren((IType) element);
			if (fProvideMembers && element instanceof ISourceReference
					&& element instanceof IParent)
				return super.getChildren(element);
			if (element instanceof IRubyProject)
				return getRubyTypes((IRubyProject) element);
			return super.getChildren(element);
		} catch (RubyModelException e) {
			return NO_CHILDREN;
		} finally {
			finishedReadInDisplayThread();
		}
	}
	
	private Object[] getChildren(IType type) throws RubyModelException{
		IParent parent= type.getRubyScript();
		
		if (type.getDeclaringType() != null)
			return type.getChildren();

		// Add import declarations
		IRubyElement[] members= parent.getChildren();
		ArrayList tempResult= new ArrayList(members.length);
		for (int i= 0; i < members.length; i++)
			if ((members[i] instanceof IImportContainer))
				tempResult.add(members[i]);
		tempResult.addAll(Arrays.asList(type.getChildren()));
		return tempResult.toArray();
	}

	private Object[] getRubyTypes(IRubyProject project)
			throws RubyModelException {
		Object[] scripts = getRubyScripts(project);
		List list = new ArrayList();
		for (int i = 0; i < scripts.length; i++) {
			IRubyScript script = (IRubyScript) scripts[i];
			Object[] types = script.getTypes();
			for (int j = 0; j < types.length; j++) {
				list.add(types[j]);
			}
		}
		return concatenate(list.toArray(), new Object[] {});
	}

	protected Object[] getRubyScripts(IRubyProject project)
			throws RubyModelException {
		if (!project.getProject().isOpen())
			return NO_CHILDREN;

		return project.getRubyScripts();
	}

	private boolean isDisplayThread() {
		Control ctrl = fViewer.getControl();
		if (ctrl == null)
			return false;

		Display currentDisplay = Display.getCurrent();
		return currentDisplay != null
				&& currentDisplay.equals(ctrl.getDisplay());
	}

	protected void startReadInDisplayThread() {
		if (isDisplayThread())
			fReadsInDisplayThread++;
	}

	protected void finishedReadInDisplayThread() {
		if (isDisplayThread())
			fReadsInDisplayThread--;
	}

	/*
	 * (non-Javadoc) Method declared on IContentProvider.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		super.inputChanged(viewer, oldInput, newInput);

		if (newInput instanceof Collection) {
			// Get a template object from the collection
			Collection col = (Collection) newInput;
			if (!col.isEmpty())
				newInput = col.iterator().next();
			else
				newInput = null;
		}
		fInput = newInput;
	}

	/*
	 * (non-Javadoc) Method declared on IContentProvider.
	 */
	public void dispose() {
		super.dispose();
		// TODO Listen for element changes
		// RubyCore.removeElementChangedListener(this);
	}

	/**
	 * Returns the parent for the element.
	 * <p>
	 * Note: This method will return a working copy if the parent is a working
	 * copy. The super class implementation returns the original element
	 * instead.
	 * </p>
	 */
	protected Object internalGetParent(Object element) {
		if (element instanceof IRubyProject) {
			return ((IRubyProject) element).getRubyModel();
		}
		// try to map resources to the containing package fragment
		if (element instanceof IResource) {
			IResource parent = ((IResource) element).getParent();
			Object jParent = RubyCore.create(parent);
			if (jParent != null)
				return jParent;
			return parent;
		}

		if (element instanceof IRubyElement)
			return ((IRubyElement) element).getParent();

		return null;
	}

}

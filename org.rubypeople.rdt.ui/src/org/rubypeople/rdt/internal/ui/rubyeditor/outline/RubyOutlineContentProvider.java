package org.rubypeople.rdt.internal.ui.rubyeditor.outline;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchAdapter;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IRubyType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyOutlineContentProvider implements ITreeContentProvider {

	private Object[] NO_CLASS= new Object[] {new NoClassElement()};
	static Object[] NO_CHILDREN= new Object[0];
	protected Viewer viewer;
	private boolean fTopLevelTypeOnly = false;

	public Object[] getChildren(Object parent) {
		if (parent instanceof IParent) {
			IParent c= (IParent) parent;
			try {
				return c.getChildren(); // FIXME Do Filtering
			} catch (RubyModelException x) {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
				// don't log NotExist exceptions as this is a valid case
				// since we might have been posted and the element
				// removed in the meantime.
				if (/*RubyPlugin.isDebug() || */ !x.isDoesNotExist())
					RubyPlugin.log(x);
			}
		}
		return NO_CHILDREN;
	}

	public Object getParent(Object element) {
		if (element instanceof IRubyElement) { return ((IRubyElement) element).getParent(); }
		return null;
	}

	public boolean hasChildren(Object parent) {
		if (parent instanceof IParent) {
			IParent c= (IParent) parent;
			try {
				IRubyElement[] children= c.getChildren();
				return (children != null && children.length > 0);
			} catch (RubyModelException x) {
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=38341
				// don't log NotExist exceptions as this is a valid case
				// since we might have been posted and the element
				// removed in the meantime.
				if (/*RdtUiPlugin.isDebug() ||*/ !x.isDoesNotExist())
					RubyPlugin.log(x);
			}
		}
		return false;
	}

	public Object[] getElements(Object parent) {
		if (fTopLevelTypeOnly) {
			if (parent instanceof IRubyScript) {
				try {
					IRubyType type= getMainType((IRubyScript) parent);
					return type != null ? type.getChildren() : NO_CLASS;
				} catch (RubyModelException e) {
					RubyPlugin.log(e);
				}
			} 
		}
		return getChildren(parent);
	}
	
	/**
	 * Returns the primary type of a compilation unit (has the same
	 * name as the compilation unit).
	 * 
	 * @param compilationUnit the compilation unit
	 * @return returns the primary type of the compilation unit, or
	 * <code>null</code> if is does not have one
	 */
	protected IRubyType getMainType(IRubyScript compilationUnit) {
		
		if (compilationUnit == null)
			return null;
		
		String name= compilationUnit.getElementName();
		int index= name.indexOf('.');
		if (index != -1)
			name= name.substring(0, index);
		IRubyType type= compilationUnit.getType(name);
		return type.exists() ? type : null;
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	}
	
	static class NoClassElement extends WorkbenchAdapter implements IAdaptable {
		/*
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
			return "NoTopLevelType";
			// TODO Use the translation bundle string!
//			return RubyEditorMessages.getString("RubyOutlinePage.error.NoTopLevelType"); //$NON-NLS-1$
		}

		/*
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
		 */
		public Object getAdapter(Class clas) {
			if (clas == IWorkbenchAdapter.class)
				return this;
			return null;
		}
	}
}
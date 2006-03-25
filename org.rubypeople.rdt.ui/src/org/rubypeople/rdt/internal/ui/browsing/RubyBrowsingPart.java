package org.rubypeople.rdt.internal.ui.browsing;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.Assert;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.ui.ISearchResultView;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.Bundle;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.DecoratingRubyLabelProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyElementImageProvider;
import org.rubypeople.rdt.internal.ui.viewsupport.RubyUILabelProvider;
import org.rubypeople.rdt.ui.StandardRubyElementContentProvider;

public abstract class RubyBrowsingPart extends ViewPart implements ISelectionListener {

	private StructuredViewer fViewer;
	private RubyUILabelProvider fLabelProvider;
	
	protected IWorkbenchPart fPreviousSelectionProvider;
	protected Object fPreviousSelectedElement;
	
	/*
	 * Ensure selection changed events being processed only if
	 * initiated by user interaction with this part.
	 */
	private boolean fProcessSelectionEvents= true;
	private RubyElementTypeComparator fTypeComparator;
	
	private IPartListener2 fPartListener= new IPartListener2() {
		public void partActivated(IWorkbenchPartReference ref) {
		}
		public void partBroughtToTop(IWorkbenchPartReference ref) {
		}
	 	public void partInputChanged(IWorkbenchPartReference ref) {
	 	}
		public void partClosed(IWorkbenchPartReference ref) {
		}
		public void partDeactivated(IWorkbenchPartReference ref) {
		}
		public void partOpened(IWorkbenchPartReference ref) {
		}
		public void partVisible(IWorkbenchPartReference ref) {
			if (ref != null && ref.getId() == getSite().getId()){
				fProcessSelectionEvents= true;
				IWorkbenchPage page= getSite().getWorkbenchWindow().getActivePage();
				if (page != null)
					selectionChanged(page.getActivePart(), page.getSelection());
		}
		}
		public void partHidden(IWorkbenchPartReference ref) {
			if (ref != null && ref.getId() == getSite().getId())
				fProcessSelectionEvents= false;
		}
	};

	public void createPartControl(Composite parent) {
		Assert.isTrue(fViewer == null);
		
		fViewer = new TreeViewer(parent);
		
		fTypeComparator= new RubyElementTypeComparator();
		
		fLabelProvider= createLabelProvider();
		fViewer.setLabelProvider(new DecoratingRubyLabelProvider(fLabelProvider));
		fViewer.setUseHashlookup(true);

		getSite().setSelectionProvider(fViewer);
		fViewer.setContentProvider(createContentProvider());
		setInitialInput();

		// Initialize selection
		// TODO Use selection from editor, etc
		//setInitialSelection();
		
//		 Listen to page changes
		getViewSite().getPage().addPostSelectionListener(this);
		getViewSite().getPage().addPartListener(fPartListener);
	}
	
	/**
	 * Creates the the content provider of this part.
	 */
	protected IContentProvider createContentProvider() {
		return new RubyBrowsingContentProvider(true, this);
	}
	
	protected final StructuredViewer getViewer() {
		return fViewer;
	}
	
	protected void setInitialInput() {
		// Use the selection, if any
		ISelection selection = getSite().getPage().getSelection();
		Object input = getSingleElementFromSelection(selection);
		if (!(input instanceof IRubyElement)) {
			// Use the input of the page
			input = getSite().getPage().getInput();
			if (!(input instanceof IRubyElement) && input instanceof IAdaptable)
				input = ((IAdaptable) input).getAdapter(IRubyElement.class);
		}
		setInput(findInputForRubyElement((IRubyElement) input));
	}
	
	protected RubyUILabelProvider createLabelProvider() {
		return new AppearanceAwareLabelProvider(
						AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS,
						AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS | RubyElementImageProvider.SMALL_ICONS
						);
	}
	
	protected void setInput(Object input) {
		setViewerInput(input);
		// TODO Update the title
		//updateTitle();
	}
	
	private void setViewerInput(Object input) {
		fProcessSelectionEvents= false;
		fViewer.setInput(input);
		fProcessSelectionEvents= true;
	}

	private boolean isSearchResultView(IWorkbenchPart part) {
		return isSearchPlugInActivated() && (part instanceof ISearchResultView || part instanceof ISearchResultViewPart);
	}
	
	// FIXME Move this to a ruby SearchUtil class
	public static boolean isSearchPlugInActivated() {
		return Platform.getBundle("org.eclipse.search").getState() == Bundle.ACTIVE; //$NON-NLS-1$
	}
	
	protected boolean needsToProcessSelectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!fProcessSelectionEvents || part == this || isSearchResultView(part)){
			if (part == this)
				fPreviousSelectionProvider= part;
			return false;
		}
		return true;
	}
	
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (!needsToProcessSelectionChanged(part, selection))
			return;

		// TODO Handle editor selections
//		if (fToggleLinkingAction.isChecked() && (part instanceof ITextEditor)) {
//			setSelectionFromEditor(part, selection);
//			return;
//		}

		if (!(selection instanceof IStructuredSelection))
			return;

		// Set selection
		Object selectedElement= getSingleElementFromSelection(selection);

		if (selectedElement != null && (part == null || part.equals(fPreviousSelectionProvider)) && selectedElement.equals(fPreviousSelectedElement))
			return;

		fPreviousSelectedElement= selectedElement;

		Object currentInput= getViewer().getInput();
		if (selectedElement != null && selectedElement.equals(currentInput)) {
			IRubyElement elementToSelect= findElementToSelect(selectedElement);
			if (elementToSelect != null && getTypeComparator().compare(selectedElement, elementToSelect) < 0)
				setSelection(new StructuredSelection(elementToSelect), true);
			// TODO Uncomment when we have a MembersView
//			else if (elementToSelect == null && (this instanceof MembersView)) {
//				setSelection(StructuredSelection.EMPTY, true);
//				fPreviousSelectedElement= StructuredSelection.EMPTY;
//			}
			fPreviousSelectionProvider= part;
			return;
		}

		// Clear input if needed
		if (part != fPreviousSelectionProvider && selectedElement != null && !selectedElement.equals(currentInput) && isInputResetBy(selectedElement, currentInput, part)) {
			if (!isAncestorOf(selectedElement, currentInput))
				setInput(null);
			fPreviousSelectionProvider= part;
			return;
		} else	if (selection.isEmpty() && !isInputResetBy(part)) {
			fPreviousSelectionProvider= part;
			return;
		} else if (selectedElement == null && part == fPreviousSelectionProvider) {
			setInput(null);
			fPreviousSelectionProvider= part;
			return;
		}
		fPreviousSelectionProvider= part;

		// Adjust input and set selection and
		adjustInputAndSetSelection(selectedElement);
	}
	
	void setSelection(ISelection selection, boolean reveal) {
		if (selection != null && selection.equals(fViewer.getSelection()))
			return;
		fProcessSelectionEvents= false;
		fViewer.setSelection(selection, reveal);
		fProcessSelectionEvents= true;
	}
	
	protected Object getInput() {
		return fViewer.getInput();
	}
	
	public void setFocus() {
		fViewer.getControl().setFocus();
	}
	
	/**
	 * Answer the property defined by key.
	 */
	public Object getAdapter(Class key) {
		if (key == IShowInSource.class) {
			return getShowInSource();
		}
		// TODO Uncomment when we have RubyUIHelp
//		if (key == IContextProvider.class)
//			return RubyUIHelp.getHelpContextProvider(this, getHelpContextId());

		return super.getAdapter(key);
	}
	
	/**
	 * Returns the <code>IShowInSource</code> for this view.
	 */
	protected IShowInSource getShowInSource() {
		return new IShowInSource() {
			public ShowInContext getShowInContext() {
				return new ShowInContext(
					null,
				getSite().getSelectionProvider().getSelection());
			}
		};
	}
	
	void adjustInputAndSetSelection(Object o) {
		if (!(o instanceof IRubyElement)) {
			if (o == null)
				setInput(null);
			setSelection(StructuredSelection.EMPTY, true);
			return;
		}

		IRubyElement je= (IRubyElement)o;
		IRubyElement elementToSelect= getSuitableRubyElement(findElementToSelect(je));
		IRubyElement newInput= findInputForRubyElement(je);
		IRubyElement oldInput= null;
		if (getInput() instanceof IRubyElement)
			oldInput= (IRubyElement)getInput();

		if (elementToSelect == null && !isValidInput(newInput) && (newInput == null && !isAncestorOf(je, oldInput)))
			// Clear input
			setInput(null);
		else if (mustSetNewInput(elementToSelect, oldInput, newInput)) {
			// Adjust input to selection
			setInput(newInput);
			// Recompute suitable element since it depends on the viewer's input
			elementToSelect= getSuitableRubyElement(elementToSelect);
		}

		if (elementToSelect != null && elementToSelect.exists())
			setSelection(new StructuredSelection(elementToSelect), true);
		else
			setSelection(StructuredSelection.EMPTY, true);
	}
	
	/**
	 * Converts the given Java element to one which is suitable for this
	 * view. It takes into account whether the view shows working copies or not.
	 *
	 * @param	obj the Java element to be converted
	 * @return	an element suitable for this view
	 */
	IRubyElement getSuitableRubyElement(Object obj) {
		if (!(obj instanceof IRubyElement))
			return null;
		IRubyElement element= (IRubyElement)obj;
		if (fTypeComparator.compare(element, IRubyElement.SCRIPT) > 0)
			return element;
		if (isInputAWorkingCopy()) {
			IRubyElement wc= getWorkingCopy(element);
			if (wc != null)
				element= wc;
			return element;
		}
		else {
			return element.getPrimaryElement();
		}
	}
	
	boolean isInputAWorkingCopy() {
		return ((StandardRubyElementContentProvider)getViewer().getContentProvider()).getProvideWorkingCopy();
	}
	
	/**
	 * Tries to find the given element in a workingcopy.
	 */
	protected static IRubyElement getWorkingCopy(IRubyElement input) {
		// MA: with new working copy story original == working copy
		return input;
	}
	
	/**
	 * Compute if a new input must be set.
	 *
	 * @return	<code>true</code> if the input has to be set
	 * @since 3.0
	 */
	private boolean mustSetNewInput(IRubyElement elementToSelect, IRubyElement oldInput, IRubyElement newInput) {
		return (newInput == null || !newInput.equals(oldInput))
			&& (elementToSelect == null
				|| oldInput == null
				|| (!(false
					&& (elementToSelect.getParent().equals(oldInput.getParent()))
					&& (!isAncestorOf(getViewPartInput(), elementToSelect)))));
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.viewsupport.IViewPartInputProvider#getViewPartInput()
	 */
	public Object getViewPartInput() {
		if (fViewer != null) {
			return fViewer.getInput();
		}
		return null;
	}
	
	/**
	 * Gets the typeComparator.
	 * @return Returns a RubyElementTypeComparator
	 */
	protected Comparator getTypeComparator() {
		return fTypeComparator;
	}
	
	private boolean isInputResetBy(Object newInput, Object input, IWorkbenchPart part) {
		if (newInput == null)
			return part == fPreviousSelectionProvider;

		if (input instanceof IRubyElement && newInput instanceof IRubyElement)
			return getTypeComparator().compare(newInput, input)  > 0;

		else
			return false;
	}
	
	private boolean isInputResetBy(IWorkbenchPart part) {
		if (!(part instanceof RubyBrowsingPart))
			return true;
		Object thisInput= getViewer().getInput();
		Object partInput= ((RubyBrowsingPart)part).getViewer().getInput();

		if(thisInput instanceof Collection)
			thisInput= ((Collection)thisInput).iterator().next();

		if(partInput instanceof Collection)
			partInput= ((Collection)partInput).iterator().next();

		if (thisInput instanceof IRubyElement && partInput instanceof IRubyElement)
			return getTypeComparator().compare(partInput, thisInput) > 0;
		else
			return true;
	}
	
	protected boolean isAncestorOf(Object ancestor, Object element) {
		if (element instanceof IRubyElement && ancestor instanceof IRubyElement)
			return !element.equals(ancestor) && internalIsAncestorOf((IRubyElement)ancestor, (IRubyElement)element);
		return false;
	}

	private boolean internalIsAncestorOf(IRubyElement ancestor, IRubyElement element) {
		if (element != null)
			return element.equals(ancestor) || internalIsAncestorOf(ancestor, element.getParent());
		else
			return false;
	}
	
	protected final IRubyElement findElementToSelect(Object obj) {
		if (obj instanceof IRubyElement)
			return findElementToSelect((IRubyElement)obj);
		return null;
	}
	
	/**
	 * Finds the element which has to be selected in this part.
	 *
	 * @param je	the Ruby element which has the focus
	 */
	abstract protected IRubyElement findElementToSelect(IRubyElement je);
	
	protected final Object getSingleElementFromSelection(ISelection selection) {
		if (!(selection instanceof StructuredSelection) || selection.isEmpty())
			return null;

		Iterator iter= ((StructuredSelection)selection).iterator();
		Object firstElement= iter.next();
		if (!(firstElement instanceof IRubyElement)) {
			if (firstElement instanceof IMarker)
				firstElement= ((IMarker)firstElement).getResource();
			if (firstElement instanceof IAdaptable) {
				IRubyElement je= (IRubyElement)((IAdaptable)firstElement).getAdapter(IRubyElement.class);
				if (je == null && firstElement instanceof IFile) {
					IContainer parent= ((IFile)firstElement).getParent();
					if (parent != null)
						return (IRubyElement)parent.getAdapter(IRubyElement.class);
					else return null;
				} else
					return je;

			} else
				return firstElement;
		}
		Object currentInput= getViewer().getInput();
		if (currentInput == null || !currentInput.equals(findInputForRubyElement((IRubyElement)firstElement)))
			if (iter.hasNext())
				// multi-selection and view is empty
				return null;
			else
				// OK: single selection and view is empty
				return firstElement;

		// be nice to multi-selection
		while (iter.hasNext()) {
			Object element= iter.next();
			if (!(element instanceof IRubyElement))
				return null;
			if (!currentInput.equals(findInputForRubyElement((IRubyElement)element)))
				return null;
		}
		return firstElement;
	}
	
	/**
	 * Finds the closest Ruby element which can be used as input for
	 * this part and has the given Ruby element as child
	 *
	 * @param 	je 	the Ruby element for which to search the closest input
	 * @return	the closest Ruby element used as input for this part
	 */
	protected IRubyElement findInputForRubyElement(IRubyElement je) {
		if (je == null || !je.exists())
			return null;
		if (isValidInput(je))
			return je;
		return findInputForRubyElement(je.getParent());
	}
	
	/**
	 * Answers if the given <code>element</code> is a valid
	 * input for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<code>true</code> if the given element is a valid input
	 */
	abstract protected boolean isValidInput(Object element);
	
	/**
	 * Answers if the given <code>element</code> is a valid
	 * element for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<code>true</code> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		if (element == null)
			return false;
		element= getSuitableRubyElement(element);
		if (element == null)
			return false;
		Object input= getViewer().getInput();
		if (input == null)
			return false;
		if (input instanceof Collection)
			return ((Collection)input).contains(element);
		else
			return input.equals(element);

	}
	
	protected IType getTypeForRubyScript(IRubyScript script) {
		script = (IRubyScript) getSuitableRubyElement(script);

		// Use primary type if possible
		IType primaryType = script.findPrimaryType();
		if (primaryType != null)
			return primaryType;

		// Use first top-level type
		try {
			IType[] types = script.getTypes();
			if (types.length > 0)
				return types[0];
			else
				return null;
		} catch (RubyModelException ex) {
			return null;
		}
	}
	
	public void dispose() {
		if (fViewer != null) {
			getViewSite().getPage().removePostSelectionListener(this);
			getViewSite().getPage().removePartListener(fPartListener);
			fViewer= null;
		}

		super.dispose();
	}
}

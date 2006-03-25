package org.rubypeople.rdt.internal.ui.browsing;

import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;

public class MembersView extends RubyBrowsingPart {

	/**
	 * Answers if the given <code>element</code> is a valid
	 * input for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid input
	 */
	protected boolean isValidInput(Object element) {
		if (element instanceof IType) {
			IType type= (IType)element;
			return type.getDeclaringType() == null;
		}
		return false;
	}

	/**
	 * Answers if the given <code>element</code> is a valid
	 * element for this part.
	 *
	 * @param 	element	the object to test
	 * @return	<true> if the given element is a valid element
	 */
	protected boolean isValidElement(Object element) {
		if (element instanceof IMember)
			return super.isValidElement(((IMember)element).getDeclaringType());
		else if (element instanceof IImportDeclaration)
			return isValidElement(((IRubyElement)element).getParent());
		else if (element instanceof IImportContainer) {
			Object input= getViewer().getInput();
			if (input instanceof IRubyElement) {
				IRubyScript cu= (IRubyScript)((IRubyElement)input).getAncestor(IRubyElement.SCRIPT);
				if (cu != null) {
					IRubyScript importContainerCu= (IRubyScript)((IRubyElement)element).getAncestor(IRubyElement.SCRIPT);
					return cu.equals(importContainerCu);
				}
			}
		}
		return false;
	}

	/**
	 * Finds the element which has to be selected in this part.
	 *
	 * @param je	the Ruby element which has the focus
	 */
	protected IRubyElement findElementToSelect(IRubyElement je) {
		if (je == null)
			return null;

		switch (je.getElementType()) {
			case IRubyElement.TYPE:
				if (((IType)je).getDeclaringType() == null)
					return null;
				// fall through
			case IRubyElement.METHOD:
				// fall through
			case IRubyElement.FIELD:
				// fall through
			case IRubyElement.IMPORT_CONTAINER:
				return getSuitableRubyElement(je);
			case IRubyElement.IMPORT_DECLARATION:
				je= getSuitableRubyElement(je);
				if (je != null) {
					IRubyScript cu= (IRubyScript)je.getParent().getParent();
					try {
						if (cu.getImports()[0].equals(je)) {
							Object selectedElement= getSingleElementFromSelection(getViewer().getSelection());
							if (selectedElement instanceof IImportContainer)
								return (IImportContainer)selectedElement;
						}
					} catch (RubyModelException ex) {
						// return je;
					}
					return je;
				}
				break;
		}
		return null;
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

		switch (je.getElementType()) {
			case IRubyElement.TYPE:
				IType type= ((IType)je).getDeclaringType();
				if (type == null)
					return je;
				else
					return findInputForRubyElement(type);
			case IRubyElement.SCRIPT:
				return getTypeForRubyScript((IRubyScript)je);
			case IRubyElement.IMPORT_DECLARATION:
				return findInputForRubyElement(je.getParent());
			case IRubyElement.IMPORT_CONTAINER:
				IRubyElement parent= je.getParent();
				if (parent instanceof IRubyScript) {
					return getTypeForRubyScript((IRubyScript)parent);
				}
			default:
				if (je instanceof IMember)
					return findInputForRubyElement(((IMember)je).getDeclaringType());
		}
		return null;
	}
	
	boolean isInputAWorkingCopy() {
		Object input= getViewer().getInput();
		if (input instanceof IRubyElement) {
			IRubyScript cu= (IRubyScript)((IRubyElement)input).getAncestor(IRubyElement.SCRIPT);
			if (cu != null)
				return cu.isWorkingCopy();
		}
		return false;
	}

}

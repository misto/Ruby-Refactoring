package org.rubypeople.rdt.ui.wizards;

import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.formatter.CodeFormatter;
import org.rubypeople.rdt.internal.corext.codemanipulation.StubUtility;
import org.rubypeople.rdt.internal.corext.util.CodeFormatterUtil;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.Separator;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringDialogField;

public abstract class NewTypeWizardPage extends NewContainerWizardPage {
	
	private static class InterfaceWrapper {
		public String interfaceName;

		public InterfaceWrapper(String interfaceName) {
			this.interfaceName= interfaceName;
		}

		public int hashCode() {
			return interfaceName.hashCode();
		}

		public boolean equals(Object obj) {
			return obj != null && getClass().equals(obj.getClass()) && ((InterfaceWrapper) obj).interfaceName.equals(interfaceName);
		}
	}
	
	private static class InterfacesListLabelProvider extends LabelProvider {
		private Image fInterfaceImage;
		
		public InterfacesListLabelProvider() {
			fInterfaceImage= RubyPluginImages.get(RubyPluginImages.IMG_OBJS_MODULE);
		}
		
		public String getText(Object element) {
			return ((InterfaceWrapper) element).interfaceName;
		}
		
		public Image getImage(Object element) {
			return fInterfaceImage;
		}
	}	

	private final static String PAGE_NAME= "NewTypeWizardPage"; //$NON-NLS-1$
	
	/** Field ID of the enclosing type input field. */
	protected final static String ENCLOSING= PAGE_NAME + ".enclosing"; //$NON-NLS-1$
	/** Field ID of the enclosing type checkbox. */
	protected final static String ENCLOSINGSELECTION= ENCLOSING + ".selection"; //$NON-NLS-1$
	/** Field ID of the type name input field. */	
	protected final static String TYPENAME= PAGE_NAME + ".typename"; //$NON-NLS-1$
	/** Field ID of the super type input field. */
	protected final static String SUPER= PAGE_NAME + ".superclass"; //$NON-NLS-1$
	/** Field ID of the super interfaces input field. */
	protected final static String INTERFACES= PAGE_NAME + ".interfaces"; //$NON-NLS-1$
	/** Field ID of the method stubs check boxes. */
	protected final static String METHODS= PAGE_NAME + ".methods"; //$NON-NLS-1$
	
		
	/**
	 * a handle to the type to be created (does usually not exist, can be null)
	 */
	private IType fCurrType;
	private StringDialogField fTypeNameDialogField;
	
	private StringButtonDialogField fSuperClassDialogField;
	private ListDialogField fSuperInterfacesDialogField;
	
	private IType fCreatedType;
		
	protected IStatus fTypeNameStatus;
	protected IStatus fSuperClassStatus;
	protected IStatus fSuperInterfacesStatus;	

	private int fTypeKind;
	
	/**
	 * Constant to signal that the created type is a class.
	 * @since 0.9.0
	 */
	public static final int CLASS_TYPE = 1;
	
	/**
	 * Constant to signal that the created type is a interface.
	 * @since 0.9.0
	 */
	public static final int INTERFACE_TYPE = 2;
	
	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 * 
	 * @param isClass <code>true</code> if a new class is to be created; otherwise
	 * an interface is to be created
	 * @param pageName the wizard page's name
	 */
	public NewTypeWizardPage(boolean isClass, String pageName) {
		this(isClass ? CLASS_TYPE : INTERFACE_TYPE, pageName);
	}
	
	/**
	 * Creates a new <code>NewTypeWizardPage</code>.
	 * 
	 * @param typeKind Signals the kind of the type to be created. Valid kinds are
	 * {@link #CLASS_TYPE}, {@link #INTERFACE_TYPE}
	 * @param pageName the wizard page's name
	 * @since 3.1
	 */
	public NewTypeWizardPage(int typeKind, String pageName) {
	    super(pageName);
	    fTypeKind= typeKind;

	    fCreatedType= null;
		
		TypeFieldsAdapter adapter= new TypeFieldsAdapter();
		
		fTypeNameDialogField= new StringDialogField();
		fTypeNameDialogField.setDialogFieldListener(adapter);
		fTypeNameDialogField.setLabelText(getTypeNameLabel()); 
		
		fSuperClassDialogField= new StringButtonDialogField(adapter);
		fSuperClassDialogField.setDialogFieldListener(adapter);
		fSuperClassDialogField.setLabelText(getSuperClassLabel()); 
		fSuperClassDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_superclass_button); 
		
		String[] addButtons= new String[] {
			NewWizardMessages.NewTypeWizardPage_interfaces_add, 
			/* 1 */ null,
			NewWizardMessages.NewTypeWizardPage_interfaces_remove
		}; 
		fSuperInterfacesDialogField= new ListDialogField(adapter, addButtons, new InterfacesListLabelProvider());
		fSuperInterfacesDialogField.setDialogFieldListener(adapter);
		fSuperInterfacesDialogField.setTableColumns(new ListDialogField.ColumnsDescription(1, false));
		fSuperInterfacesDialogField.setLabelText(getSuperInterfacesLabel());
		fSuperInterfacesDialogField.setRemoveButtonIndex(2);
							
		fTypeNameStatus= new StatusInfo();
		fSuperClassStatus= new StatusInfo();
		fSuperInterfacesStatus= new StatusInfo();
	}
	
	/**
	 * Creates the controls for the type name field. Expects a <code>GridLayout</code> with at 
	 * least 2 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createTypeNameControls(Composite composite, int nColumns) {
		fTypeNameDialogField.doFillIntoGrid(composite, nColumns - 1);
		DialogField.createEmptySpace(composite);
		
		Text text= fTypeNameDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		// FIXME Uncomment
//		TextFieldNavigationHandler.install(text);
	}
	
	/**
	 * Sets the focus on the type name input field.
	 */		
	protected void setFocus() {
		fTypeNameDialogField.setFocus();
	}
	
	/**
	 * Sets the type name input field's text to the given value. Method doesn't update
	 * the model.
	 * 
	 * @param name the new type name
	 * @param canBeModified if <code>true</code> the type name field is
	 * editable; otherwise it is read-only.
	 */	
	public void setTypeName(String name, boolean canBeModified) {
		fTypeNameDialogField.setText(name);
		fTypeNameDialogField.setEnabled(canBeModified);
	}	
	
	/**
	 * Creates a separator line. Expects a <code>GridLayout</code> with at least 1 column.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */
	protected void createSeparator(Composite composite, int nColumns) {
		(new Separator(SWT.SEPARATOR | SWT.HORIZONTAL)).doFillIntoGrid(composite, nColumns, convertHeightInCharsToPixels(1));		
	}
		
	/**
	 * Returns the label that is used for the super interfaces input field.
	 * 
	 * @return the label that is used for the super interfaces input field.
	 * @since 3.2
	 */
	protected String getSuperInterfacesLabel() {
	    if (fTypeKind != INTERFACE_TYPE)
	        return NewWizardMessages.NewTypeWizardPage_interfaces_class_label; 
	    return NewWizardMessages.NewTypeWizardPage_interfaces_ifc_label; 
	}
	
	/**
	 * Returns the label that is used for the type name input field.
	 * 
	 * @return the label that is used for the type name input field.
	 * @since 3.2
	 */
	protected String getTypeNameLabel() {
		return NewWizardMessages.NewTypeWizardPage_typename_label;
	}
	
	/**
	 * Returns the label that is used for the super class input field.
	 * 
	 * @return the label that is used for the super class input field.
	 * @since 3.2
	 */
	protected String getSuperClassLabel() {
		return NewWizardMessages.NewTypeWizardPage_superclass_label;
	}
	
	/**
	 * Creates the controls for the superclass name field. Expects a <code>GridLayout</code> 
	 * with at least 3 columns.
	 * 
	 * @param composite the parent composite
	 * @param nColumns number of columns to span
	 */		
	protected void createSuperClassControls(Composite composite, int nColumns) {
		fSuperClassDialogField.doFillIntoGrid(composite, nColumns);
		Text text= fSuperClassDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		
//		RubyTypeCompletionProcessor superClassCompletionProcessor= new RubyTypeCompletionProcessor(false, false);
//		superClassCompletionProcessor.setCompletionContextRequestor(new CompletionContextRequestor() {
//			public StubTypeContext getStubTypeContext() {
//				return getSuperClassStubTypeContext();
//			}
//		});
//
//		ControlContentAssistHelper.createTextContentAssistant(text, superClassCompletionProcessor);
//		TextFieldNavigationHandler.install(text);
	}
	
//	 -------- TypeFieldsAdapter --------

	private class TypeFieldsAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter, SelectionListener {
		
		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typePageChangeControlPressed(field);
		}
		
		// -------- IListAdapter
		public void customButtonPressed(ListDialogField field, int index) {
			typePageCustomButtonPressed(field, index);
		}
		
		public void selectionChanged(ListDialogField field) {}
		
		// -------- IDialogFieldListener
		public void dialogFieldChanged(DialogField field) {
			typePageDialogFieldChanged(field);
		}
		
		public void doubleClicked(ListDialogField field) {
		}


		public void widgetSelected(SelectionEvent e) {
			typePageLinkActivated(e);
		}

		public void widgetDefaultSelected(SelectionEvent e) {
			typePageLinkActivated(e);
		}
	}
	
	private void typePageLinkActivated(SelectionEvent e) {
		IContainer root= getIContainer();
		if (root != null) {
			// TODO Uncomment!
//			PreferenceDialog dialog= PreferencesUtil.createPropertyDialogOn(getShell(), root.getProject(), CodeTemplatePreferencePage.PROP_ID, null, null);
//			dialog.open();
		} else {
			String title= NewWizardMessages.NewTypeWizardPage_configure_templates_title; 
			String message= NewWizardMessages.NewTypeWizardPage_configure_templates_message; 
			MessageDialog.openInformation(getShell(), title, message);
		}
	}
	
	private void typePageChangeControlPressed(DialogField field) {
		if (field == fSuperClassDialogField) {
			IType type= chooseSuperClass();
			if (type != null) {
				// TODO Spit out fully qualified name?!
				fSuperClassDialogField.setText(type.getElementName());
			}
		}
	}
	
	private void typePageCustomButtonPressed(DialogField field, int index) {		
		if (field == fSuperInterfacesDialogField) {
			chooseSuperInterfaces();
			List interfaces= fSuperInterfacesDialogField.getElements();
			if (!interfaces.isEmpty()) {
				Object element= interfaces.get(interfaces.size() - 1);
				fSuperInterfacesDialogField.editElement(element);
			}
		}
	}
	
	/**
	 * Sets the super class name.
	 * 
	 * @param name the new superclass name
	 * @param canBeModified  if <code>true</code> the superclass name field is
	 * editable; otherwise it is read-only.
	 */		
	public void setSuperClass(String name, boolean canBeModified) {
		fSuperClassDialogField.setText(name);
		fSuperClassDialogField.setEnabled(canBeModified);
	}	
	
	/**
	 * Hook method that gets called from <code>createType</code> to support adding of 
	 * unanticipated methods, fields, and inner types to the created type.
	 * <p>
	 * Implementers can use any methods defined on <code>IType</code> to manipulate the
	 * new type.
	 * </p>
	 * <p>
	 * The source code of the new type will be formatted using the platform's formatter. Needed 
	 * imports are added by the wizard at the end of the type creation process using the given 
	 * import manager.
	 * </p>
	 * 
	 * @param newType the new type created via <code>createType</code>
	 * @param imports an import manager which can be used to add new imports
	 * @param monitor a progress monitor to report progress. Must not be <code>null</code>
	 * 
	 * @see #createType(IProgressMonitor)
	 */		
	protected void createTypeMembers(IType newType, IProgressMonitor monitor) throws CoreException {
		// default implementation does nothing
		// example would be
		// String mainMathod= "public void foo(Vector vec) {}"
		// createdType.createMethod(main, null, false, null);
		// imports.addImport("java.lang.Vector");
	}
	
	/**
	 * Hook method that is called when evaluating the name of the compilation unit to create. By default, a file extension
	 * <code>java</code> is added to the given type name, but implementors can override this behavior.
	 * 
	 * @param typeName the name of the type to create the compilation unit for.
	 * @return the name of the compilation unit to be created for the given name
	 * 
	 * @since 3.2
	 */
	protected String getRubyScriptName(String typeName) {
		return typeName + RubyModelUtil.DEFAULT_SCRIPT_SUFFIX;
	}
	
	/**
	 * Creates the new type using the entered field values.
	 * 
	 * @param monitor a progress monitor to report progress.
	 * @throws CoreException Thrown when the creation failed.
	 * @throws InterruptedException Thrown when the operation was canceled.
	 */
	public void createType(IProgressMonitor monitor) throws CoreException, InterruptedException {		
		if (monitor == null) {
			monitor= new NullProgressMonitor();
		}

		monitor.beginTask(NewWizardMessages.NewTypeWizardPage_operationdesc, 8); 
		
		IRubyProject root= getProject();
		ISourceFolder pack= getSourceFolder();
		if (pack == null) {
			pack= root.getSourceFolder(new String[] { "" }); //$NON-NLS-1$
		}
		
		if (!pack.exists()) {
			String packName= pack.getElementName();
			pack= root.createSourceFolder(packName, true, new SubProgressMonitor(monitor, 1));
		} else {
			monitor.worked(1);
		}
		
		boolean needsSave;
		IRubyScript connectedCU= null;
		
		try {	
			String typeName= getTypeName();
					
			IType createdType;
			int indent= 0;
			
			String lineDelimiter= StubUtility.getLineDelimiterUsed(pack.getRubyProject());
				
			String cuName= getRubyScriptName(typeName);
			IRubyScript parentCU= pack.createRubyScript(cuName, "", false, new SubProgressMonitor(monitor, 2)); //$NON-NLS-1$
			// create a working copy with a new owner
			
			needsSave= true;
			parentCU.becomeWorkingCopy(null, new SubProgressMonitor(monitor, 1)); // cu is now a (primary) working copy
			connectedCU= parentCU;
				
			IBuffer buffer= parentCU.getBuffer();
				
			String cuContent= constructSimpleTypeStub(lineDelimiter);
			buffer.setContents(cuContent);
							
			createdType= parentCU.getType(typeName);
			
			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}
						
			IRubyScript cu= createdType.getRubyScript();	
							
			RubyModelUtil.reconcile(cu);

			if (monitor.isCanceled()) {
				throw new InterruptedException();
			}			
			
			createTypeMembers(createdType, new SubProgressMonitor(monitor, 1));
				
			RubyModelUtil.reconcile(cu);
			
			ISourceRange range= createdType.getSourceRange();
			
			IBuffer buf= cu.getBuffer();
			String originalContent= buf.getText(range.getOffset(), range.getLength());

			String formattedContent= CodeFormatterUtil.format(CodeFormatter.K_CLASS_BODY_DECLARATIONS, originalContent, indent, null, lineDelimiter, pack.getRubyProject());
//			formattedContent= Strings.trimLeadingTabsAndSpaces(formattedContent);
			buf.replace(range.getOffset(), range.getLength(), formattedContent);
		
			fCreatedType= createdType;

			if (needsSave) {
				cu.commitWorkingCopy(true, new SubProgressMonitor(monitor, 1));
			} else {
				monitor.worked(1);
			}
			
		} finally {
			if (connectedCU != null) {
				connectedCU.discardWorkingCopy();
			}
			monitor.done();
		}
	}	
	
	private String constructSimpleTypeStub(String lineDelimiter) {
		StringBuffer buf= new StringBuffer("class "); //$NON-NLS-1$
		buf.append(getTypeName());
		buf.append(lineDelimiter);
		buf.append("end"); //$NON-NLS-1$
		return buf.toString();
	}
	
	/**
	 * Opens a selection dialog that allows to select the super interfaces. The selected interfaces are
	 * directly added to the wizard page using {@link #addSuperInterface(String)}.
	 * 
	 * 	<p>
	 * Clients can override this method if they want to offer a different dialog.
	 * </p>
	 * 
	 * @since 3.2
	 */
	protected void chooseSuperInterfaces() {
		IContainer root= getIContainer();
		if (root == null) {
			return;
		}	

		RubyModuleSelectionDialog dialog = new RubyModuleSelectionDialog(getShell());
		dialog.setTitle(getInterfaceDialogTitle());
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_InterfacesDialog_message); 
		dialog.open();	
	}
	
	private String getInterfaceDialogTitle() {
	    if (fTypeKind == INTERFACE_TYPE)
	        return NewWizardMessages.NewTypeWizardPage_InterfacesDialog_interface_title; 
	    return NewWizardMessages.NewTypeWizardPage_InterfacesDialog_class_title; 
	}
	
	/*
	 * A field on the type has changed. The fields' status and all dependent
	 * status are updated.
	 */
	private void typePageDialogFieldChanged(DialogField field) {
		String fieldName= null;
		if (field == fTypeNameDialogField) {
			fTypeNameStatus= typeNameChanged();
			fieldName= TYPENAME;
		} else if (field == fSuperClassDialogField) {
			fSuperClassStatus= superClassChanged();
			fieldName= SUPER;
		} else if (field == fSuperInterfacesDialogField) {
			fSuperInterfacesStatus= superInterfacesChanged();
			fieldName= INTERFACES;
		} else {
			fieldName= METHODS;
		}
		// tell all others
		handleFieldChanged(fieldName);
	}		
	
	/**
	 * Returns the content of the superclass input field.
	 * 
	 * @return the superclass name
	 */
	public String getSuperClass() {
		return fSuperClassDialogField.getText();
	}
	
	/**
	 * Hook method that gets called when the superclass name has changed. The method 
	 * validates the superclass name and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus superClassChanged() {
		StatusInfo status= new StatusInfo();
		IContainer root= getIContainer();
		fSuperClassDialogField.enableButton(root != null);
				
		String sclassName= getSuperClass();
		if (sclassName.length() == 0) {
			// accept the empty field (stands for Object)
			return status;
		}
		
		if (root != null) {
			// TODO Check to make sure super class exists and is valid
		} else {
			status.setError(""); //$NON-NLS-1$
		}
		return status;
	}
	
	/**
	 * Returns the type name entered into the type input field.
	 * 
	 * @return the type name
	 */
	public String getTypeName() {
		return fTypeNameDialogField.getText();
	}
	
	private IStatus typeNameChanged() {
		StatusInfo status= new StatusInfo();
		fCurrType= null;
		String typeName = getTypeName();
		if (typeName.length() == 0) {
			status.setError(NewWizardMessages.NewTypeWizardPage_error_EnterTypeName);
			return status;
		}
		if (!isConstant(typeName)) {
			status.setError("Class name must be a constant. It must begin with a capital letter, and contain only letters, digits, or underscores.");
			return status;
		}
		return status;
	}
	
	private boolean isConstant(String className) {
        if (className == null || className.length() == 0) return false;
        if (!Character.isLowerCase(className.charAt(0)) && !Character.isLetter(className.charAt(0)))
            return false;
        int namespaceDelimeterIndex = className.indexOf("::");
        if (namespaceDelimeterIndex != -1) {
        	return isConstant(className.substring(0, namespaceDelimeterIndex)) && isConstant(className.substring(namespaceDelimeterIndex+2));
        }
        for (int i = 0; i < className.length(); i++) {
            char c = className.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') return false;
        }
        return true;
    }

	/**
	 * Hook method that gets called when the list of super interface has changed. The method 
	 * validates the super interfaces and returns the status of the validation.
	 * <p>
	 * Subclasses may extend this method to perform their own validation.
	 * </p>
	 * 
	 * @return the status of the validation
	 */
	protected IStatus superInterfacesChanged() {
		StatusInfo status= new StatusInfo();
		
		IContainer root= getIContainer();
		fSuperInterfacesDialogField.enableButton(0, root != null);
						
		if (root != null) {
			List elements= fSuperInterfacesDialogField.getElements();
			int nElements= elements.size();
			for (int i= 0; i < nElements; i++) {
				// TODO Check to make sure each interface exists and is valid
//				String intfname= ((InterfaceWrapper) elements.get(i)).interfaceName;
//				Type type= TypeContextChecker.parseSuperInterface(intfname);
//				if (type == null) {
//					status.setError(Messages.format(NewWizardMessages.NewTypeWizardPage_error_InvalidSuperInterfaceName, intfname)); 
//					return status;
//				}
			}				
		}
		return status;
	}
	
	/**
	 * Opens a selection dialog that allows to select a super class.
	 * 
	 * @return returns the selected type or <code>null</code> if the dialog
	 *         has been canceled. The caller typically sets the result to the
	 *         super class input field.
	 *         <p>
	 *         Clients can override this method if they want to offer a
	 *         different dialog.
	 *         </p>
	 * 
	 * @since 3.2
	 */
	protected IType chooseSuperClass() {
		IContainer root= getIContainer();
		if (root == null) {
			return null;
		}	
		
		RubyClassSelectionDialog dialog = new RubyClassSelectionDialog(getShell());
		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}
}
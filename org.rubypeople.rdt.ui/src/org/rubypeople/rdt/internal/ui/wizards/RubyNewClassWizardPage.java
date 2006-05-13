package org.rubypeople.rdt.internal.ui.wizards;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.ui.wizards.RubyClassSelectionDialog;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class RubyNewClassWizardPage extends WizardPage {

    private Text containerText;

    private Text classText;

    private Text superclassText;

    private ISelection selection;

    /**
     * Constructor for SampleNewWizardPage.
     * 
     * @param pageName
     */
    public RubyNewClassWizardPage(ISelection selection) {
        super("wizardPage");
        setTitle("New Ruby Class");
        setDescription("This wizard creates a new Ruby file with *.rb extension.");
        this.selection = selection;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText("&Container:");

        containerText = new Text(container, SWT.BORDER | SWT.SINGLE);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        containerText.setLayoutData(gd);
        containerText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                handleBrowse();
            }
        });
        
        label = new Label(container, SWT.NULL);
        label.setText("&Superclass:");

        superclassText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        superclassText.setLayoutData(gd);
        superclassText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });
        button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                handleSuperClassBrowse();
            }
        });
        
        label = new Label(container, SWT.NULL);
        label.setText("Class &name:");

        classText = new Text(container, SWT.BORDER | SWT.SINGLE);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        classText.setLayoutData(gd);
        classText.addModifyListener(new ModifyListener() {

            public void modifyText(ModifyEvent e) {
                dialogChanged();
            }
        });

        initialize();
        dialogChanged();
        setControl(container);
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */
    private void initialize() {
        if (selection != null && selection.isEmpty() == false
                && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1) return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IContainer container;
                if (obj instanceof IContainer)
                    container = (IContainer) obj;
                else
                    container = ((IResource) obj).getParent();
                containerText.setText(container.getFullPath().toString());
            }
        }
        classText.setText("MyNewClass");
        superclassText.setText("Object");
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */

    private void handleSuperClassBrowse() {
        ElementListSelectionDialog dialog = new RubyClassSelectionDialog(getShell());
        if (dialog.open() == Window.OK) {
            superclassText.setText(((IRubyElement) dialog.getFirstResult()).getElementName());
        }
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */
    private void handleBrowse() {
        ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), ResourcesPlugin
                .getWorkspace().getRoot(), false, "Select new file container");
        if (dialog.open() == ContainerSelectionDialog.OK) {
            Object[] result = dialog.getResult();
            if (result.length == 1) {
                containerText.setText(((Path) result[0]).toString());
            }
        }
    }

    /**
     * Ensures that both text fields are set.
     */

    private void dialogChanged() {
        IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(
                new Path(getContainerName()));
        String className = getClassName();
        String superclassName = getSuperclassName();

        if (getContainerName().length() == 0) {
            updateStatus("File container must be specified");
            return;
        }
        if (container == null
                || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
            updateStatus("File container must exist");
            return;
        }
        if (!container.isAccessible()) {
            updateStatus("Project must be writable");
            return;
        }
        if (className.length() == 0) {
            updateStatus("Class name must be specified");
            return;
        }
        if (!isConstant(className)) {
            updateStatus("Class name must be a constant. It must begin with a capital letter, and contain only letters, digits, or underscores.");
            return;
        }
        // TODO Verify superclass exists in workspace?
        if (superclassName != null && superclassName.length() > 0 && !isConstant(superclassName)) {
            updateStatus("Superclass name must be a constant. It must begin with a capital letter, and contain only letters, digits, or underscores.");
            return;
        }
        updateStatus(null);
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

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getContainerName() {
        return containerText.getText();
    }

    public String getClassName() {
        return classText.getText();
    }

    public String getSuperclassName() {
        return superclassText.getText();
    }
}
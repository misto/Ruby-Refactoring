package org.rubypeople.rdt.internal.testunit.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.formatter.Indents;
import org.rubypeople.rdt.internal.testunit.ui.TestunitPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.actions.WorkbenchRunnableAdapter;
import org.rubypeople.rdt.internal.ui.util.ExceptionHandler;
import org.rubypeople.rdt.internal.ui.wizards.NewWizardMessages;
import org.rubypeople.rdt.testunit.wizards.RubyNewTestCaseWizardPage;

public class NewTestCaseCreationWizard extends Wizard implements INewWizard {

    private static final String RUBY_FILE_EXTENSION = ".rb";
    private RubyNewTestCaseWizardPage page;
    private IStructuredSelection selection;

    /**
     * Constructor for SampleNewWizard.
     */
    public NewTestCaseCreationWizard() {
        super();
        setWindowTitle(WizardMessages.Wizard_title_new_testcase);
        setDefaultPageImageDescriptor(RubyPluginImages.DESC_WIZBAN_NEWCLASS);
        setNeedsProgressMonitor(true);
    }

    /**
     * Adding the page to the wizard.
     */

    public void addPages() {
        page = new RubyNewTestCaseWizardPage(selection);
        page.init(selection);
        addPage(page);
    }

    /**
     * This method is called when 'Finish' button is pressed in the wizard. We
     * will create an operation and run it using wizard as execution context.
     */
    public boolean performFinish() {
    	IWorkspaceRunnable op= new IWorkspaceRunnable() {
			public void run(IProgressMonitor monitor) throws CoreException, OperationCanceledException {
				try {
					finishPage(monitor);
				} catch (InterruptedException e) {
					throw new OperationCanceledException(e.getMessage());
				}
			}
		};
		try {
			ISchedulingRule rule= null;
			Job job= Platform.getJobManager().currentJob();
			if (job != null)
				rule= job.getRule();
			IRunnableWithProgress runnable= null;
			if (rule != null)
				runnable= new WorkbenchRunnableAdapter(op, rule, true);
			else
				runnable= new WorkbenchRunnableAdapter(op, getSchedulingRule());
			getContainer().run(canRunForked(), true, runnable);
		} catch (InvocationTargetException e) {
			handleFinishException(getShell(), e);
			return false;
		} catch  (InterruptedException e) {
			return false;
		}
		return true;
    }
    
	protected boolean canRunForked() {
		return true;
	}
	
	/**
	 * Returns the scheduling rule for creating the element.
	 */
	protected ISchedulingRule getSchedulingRule() {
		return ResourcesPlugin.getWorkspace().getRoot(); // look all by default
	}
    
	protected void handleFinishException(Shell shell, InvocationTargetException e) {
		String title= NewWizardMessages.NewElementWizard_op_error_title; 
		String message= NewWizardMessages.NewElementWizard_op_error_message; 
		ExceptionHandler.handle(e, shell, title, message);
	}
    
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor) throws InterruptedException, CoreException {
		page.createType(monitor); // use the full progress monitor
	}

    /**
     * The worker method. It will find the container, create the file if missing
     * or just replace its contents, and open the editor on the newly created
     * file.
     * @param superclassName 
     */

    private void doFinish(String containerName, String className, String superclassName, IProgressMonitor monitor)
            throws CoreException {
        // create a sample file
        monitor.beginTask("Creating " + className, 2);
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource resource = root.findMember(new Path(containerName));
        if (!resource.exists() || !(resource instanceof IContainer)) {
            throwCoreException("Container \"" + containerName + "\" does not exist.");
        }
        IContainer container = (IContainer) resource;
        String fileName = classNameToFileName(className) + RUBY_FILE_EXTENSION;

        final IFile file = container.getFile(new Path(fileName));
        try {
            InputStream stream = openContentStream(className, superclassName);
            if (file.exists()) {
                file.setContents(stream, true, true, monitor);
            } else {
                file.create(stream, true, monitor);
            }
            stream.close();
        } catch (IOException e) {
        }
        monitor.worked(1);
        monitor.setTaskName("Opening file for editing...");
        getShell().getDisplay().asyncExec(new Runnable() {

            public void run() {
                IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getActivePage();
                try {
                    IDE.openEditor(page, file, true);
                } catch (PartInitException e) {
                }
            }
        });
        monitor.worked(1);
    }

    /**
     * Convert a Constant Class Name (in camels) to a file name (all lowercase,
     * uppercase characters get downcased and have underscores put in front,
     * except first character.)
     * 
     * @param className
     * @return
     */
    private String classNameToFileName(String className) {
    	  className = stripNamespace(className);
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < className.length(); i++) {
            char c = className.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i != 0) buffer.append('_');
                buffer.append(Character.toLowerCase(c));
            } else {
                buffer.append(c);
            }
        }
        return buffer.toString();
    }

    private String stripNamespace(String className) {
		if (className == null || className.length() == 0) return className;
		if (className.lastIndexOf("::") != -1) {
			return className.substring(className.lastIndexOf("::") + 2);
		}
		return className;
	}

	/**
     * We will initialize file contents with a sample text.
     * 
     * @param className
     *            The Filename chosen by the user
     * @param superclassName 
     */

    private InputStream openContentStream(String className, String superclassName) {
        StringBuffer contents = new StringBuffer();
        String endLine = System.getProperty("line.separator");
        if (endLine == null) endLine = "\n";
        contents.append("require 'test/unit'");
        contents.append(endLine);        
        
        contents.append("class ");
        contents.append(className);        
        contents.append(" < ");
        contents.append(superclassName);
        contents.append(endLine);
        
        contents.append(Indents.createIndentString(1, RubyCore.getOptions()));
        contents.append(endLine);
        
        contents.append(Indents.createIndentString(1, RubyCore.getOptions()));
        contents.append(endLine);
        
        contents.append("end");  
        contents.append(endLine);
        return new ByteArrayInputStream(contents.toString().getBytes());
    }

    private void throwCoreException(String message) throws CoreException {
        IStatus status = new Status(IStatus.ERROR, TestunitPlugin.PLUGIN_ID, IStatus.OK, message, null);
        throw new CoreException(status);
    }

    /**
     * We will accept the selection in the workbench to see if we can initialize
     * from it.
     * 
     * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
     */
    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
    }
}
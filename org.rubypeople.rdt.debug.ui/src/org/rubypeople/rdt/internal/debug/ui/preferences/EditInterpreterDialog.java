package org.rubypeople.rdt.internal.debug.ui.preferences;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.rubyvms.IAddVMDialogRequestor;
import org.rubypeople.rdt.internal.debug.ui.rubyvms.RubyVMMessages;
import org.rubypeople.rdt.internal.launching.VMStandin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusDialog;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.ComboDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.DialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.rubypeople.rdt.internal.ui.wizards.dialogfields.StringDialogField;
import org.rubypeople.rdt.launching.IInterpreter;
import org.rubypeople.rdt.launching.IInterpreter2;
import org.rubypeople.rdt.launching.IInterpreterInstallType;

public class EditInterpreterDialog extends StatusDialog {
	
	protected IStatus[] allStatus = new IStatus[2];
	
	protected IInterpreter fEditedVM;
	private StringButtonDialogField fJRERoot;
	private StringDialogField fVMName;
	
	private StringDialogField fVMArgs;
	
	private IInterpreterInstallType fSelectedVMType;
	private IInterpreterInstallType[] fVMTypes;
	private ComboDialogField fVMTypeCombo;
	
	private IStatus[] fStati;
	
	private int fPrevIndex = -1;

	private IAddVMDialogRequestor fRequestor;

	public EditInterpreterDialog(IAddVMDialogRequestor requestor, Shell shell, IInterpreterInstallType[] vmInstallTypes, IInterpreter editedVM) {
		super(shell);
		setShellStyle(getShellStyle() | SWT.RESIZE);
		fRequestor= requestor;
		fStati= new IStatus[5];
		for (int i= 0; i < fStati.length; i++) {
			fStati[i]= new StatusInfo();
		}
		
		fVMTypes= vmInstallTypes;
		fSelectedVMType= editedVM != null ? editedVM.getInterpreterInstallType() : vmInstallTypes[0];
		
		fEditedVM= editedVM;
	}
	
	protected IStatus validateInterpreterLocationText() {
		String locationName = fJRERoot.getText();
		if (locationName.length() == 0) {
			return new StatusInfo(IStatus.INFO, RubyVMMessages.addVMDialog_enterLocation); 
		}
		File file = new File(locationName);
		if (!file.exists()) {
			return new StatusInfo(IStatus.ERROR, RubyVMMessages.addVMDialog_locationNotExists); 
		} 
		// FIXME We want the user to select a directory as the home of the ruby install!
	 	if(file.isFile()){
	 		return new Status(IStatus.OK, RdtDebugUiPlugin.PLUGIN_ID, 0, "", null);
	 	}
		return new Status(IStatus.ERROR, RdtDebugUiPlugin.PLUGIN_ID, 1, RdtDebugUiMessages.getString("EditInterpreterDialog.rubyInterpreter.path.error"), null);
	}

	protected void browseForInstallLocation() {
		FileDialog dialog = new FileDialog(getShell());
		dialog.setFilterPath(fJRERoot.getText());
		dialog.setText(RdtDebugUiMessages.getString("EditInterpreterDialog.rubyInterpreter.path.browse.message")); //$NON-NLS-1$
		String newPath = dialog.open();
		if (newPath != null)
			fJRERoot.setText(newPath);
	}

	protected void okPressed() {
		doOkPressed();
		super.okPressed();
	}
	
	private void doOkPressed() {
		if (fEditedVM == null) {
			IInterpreter vm= new VMStandin(fSelectedVMType, createUniqueId(fSelectedVMType));
			setFieldValuesToVM(vm);
			fRequestor.vmAdded(vm);
		} else {
			setFieldValuesToVM(fEditedVM);
		}
	}
	
	public void create() {
		super.create();
		fVMName.setFocus();
		selectVMType();  
	}
	
	private String createUniqueId(IInterpreterInstallType vmType) {
		String id= null;
		do {
			id= String.valueOf(System.currentTimeMillis());
		} while (vmType.findInterpreterInstall(id) != null);
		return id;
	}
	
	private void selectVMType() {
		for (int i= 0; i < fVMTypes.length; i++) {
			if (fSelectedVMType == fVMTypes[i]) {
				fVMTypeCombo.selectItem(i);
				return;
			}
		}
	}
	
	private void updateVMType() {
		int selIndex= fVMTypeCombo.getSelectionIndex();
		if (selIndex == fPrevIndex) {
			return;
		}
		fPrevIndex = selIndex;
		if (selIndex >= 0 && selIndex < fVMTypes.length) {
			fSelectedVMType= fVMTypes[selIndex];
		}
		setJRELocationStatus(validateInterpreterLocationText());
//		fLibraryBlock.initializeFrom(fEditedVM, fSelectedVMType);
		updateStatusLine();
	}	
	
	private void setJRELocationStatus(IStatus status) {
		fStati[1]= status;
	}
		
	protected void updateStatusLine() {
		IStatus max= null;
		for (int i= 0; i < fStati.length; i++) {
			IStatus curr= fStati[i];
			if (curr.matches(IStatus.ERROR)) {
				updateStatus(curr);
				return;
			}
			if (max == null || curr.getSeverity() > max.getSeverity()) {
				max= curr;
			}
		}
		updateStatus(max);
	}
	
	protected Control createDialogArea(Composite ancestor) {	
		createDialogFields();
		Composite parent = (Composite)super.createDialogArea(ancestor);
		((GridLayout)parent.getLayout()).numColumns= 3;
		
		fVMTypeCombo.doFillIntoGrid(parent, 3);
		((GridData)fVMTypeCombo.getComboControl(null).getLayoutData()).widthHint= convertWidthInCharsToPixels(50);

		fVMName.doFillIntoGrid(parent, 3);
	
		fJRERoot.doFillIntoGrid(parent, 3);
		
		fVMArgs.doFillIntoGrid(parent, 3);
		((GridData)fVMArgs.getTextControl(null).getLayoutData()).widthHint= convertWidthInCharsToPixels(50);
		
		Label l = new Label(parent, SWT.NONE);
		l.setText(RubyVMMessages.AddVMDialog_JRE_system_libraries__1); 
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		l.setLayoutData(gd);	
		
//		fLibraryBlock = new VMLibraryBlock(this);
//		Control block = fLibraryBlock.createControl(parent);
//		gd = new GridData(GridData.FILL_BOTH);
//		gd.horizontalSpan = 3;
//		block.setLayoutData(gd);
		
		Text t= fJRERoot.getTextControl(parent);
		gd= (GridData)t.getLayoutData();
		gd.grabExcessHorizontalSpace=true;
		gd.widthHint= convertWidthInCharsToPixels(50);
		
		initializeFields();
		createFieldListeners();
		applyDialogFont(parent);
		return parent;
	}
	
	private void initializeFields() {
		fVMTypeCombo.setItems(getVMTypeNames());
		if (fEditedVM == null) {
			fVMName.setText(""); //$NON-NLS-1$
			fJRERoot.setText(""); //$NON-NLS-1$
//			fLibraryBlock.initializeFrom(null, fSelectedVMType);
			fVMArgs.setText(""); //$NON-NLS-1$
		} else {
			fVMTypeCombo.setEnabled(false);
			fVMName.setText(fEditedVM.getName());
			fJRERoot.setText(fEditedVM.getInstallLocation().getAbsolutePath());
//			fLibraryBlock.initializeFrom(fEditedVM, fSelectedVMType);
			if (fEditedVM instanceof IInterpreter2) {
				IInterpreter2 vm2 = (IInterpreter2) fEditedVM;
				String vmArgs = vm2.getVMArgs();
				if (vmArgs != null) {
					fVMArgs.setText(vmArgs);
				}
			} else {
				String[] vmArgs = fEditedVM.getInterpreterArguments();
				if (vmArgs != null) {
					StringBuffer buffer = new StringBuffer();
					int length= vmArgs.length;
					if (length > 0) {
						buffer.append(vmArgs[0]);
						for (int i = 1; i < length; i++) {
							buffer.append(' ').append(vmArgs[i]);
						}
					}
					fVMArgs.setText(buffer.toString());
				}				
			}
		}
		setVMNameStatus(validateVMName());
		updateStatusLine();
	}
	
	private void setVMNameStatus(IStatus status) {
		fStati[0]= status;
	}
	
	protected void createFieldListeners() {
		fVMTypeCombo.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				updateVMType();
			}
		});
		
		fVMName.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				setVMNameStatus(validateVMName());
				updateStatusLine();
			}
		});
		
		fJRERoot.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				setJRELocationStatus(validateInterpreterLocationText());
				updateStatusLine();
			}
		});
	}
	
	private IStatus validateVMName() {
		StatusInfo status= new StatusInfo();
		String name= fVMName.getText();
		if (name == null || name.trim().length() == 0) {
			status.setInfo(RubyVMMessages.addVMDialog_enterName); 
		} else {
			if (fRequestor.isDuplicateName(name) && (fEditedVM == null || !name.equals(fEditedVM.getName()))) {
				status.setError(RubyVMMessages.addVMDialog_duplicateName); 
			} else {
				IStatus s = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
				if (!s.isOK()) {
					status.setError(MessageFormat.format(RubyVMMessages.AddVMDialog_JRE_name_must_be_a_valid_file_name___0__1, new String[]{s.getMessage()})); 
				}
			}
		}
		return status;
	}
	
	protected void createDialogFields() {
		fVMTypeCombo= new ComboDialogField(SWT.READ_ONLY);
		fVMTypeCombo.setLabelText(RubyVMMessages.addVMDialog_jreType); 
		fVMTypeCombo.setItems(getVMTypeNames());
		
		fVMName= new StringDialogField();
		fVMName.setLabelText(RubyVMMessages.addVMDialog_jreName); 
		
		fJRERoot= new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				browseForInstallLocation();
			}
		});
		fJRERoot.setLabelText(RubyVMMessages.addVMDialog_jreHome); 
		fJRERoot.setButtonLabel(RubyVMMessages.addVMDialog_browse1); 
			
		fVMArgs= new StringDialogField();
		fVMArgs.setLabelText(RubyVMMessages.AddVMDialog_23); 
	}
	
	private String[] getVMTypeNames() {
		String[] names=  new String[fVMTypes.length];
		for (int i= 0; i < fVMTypes.length; i++) {
			names[i]= fVMTypes[i].getName();
		}
		return names;
	}
	
	protected void setFieldValuesToVM(IInterpreter vm) {
		File dir = new File(fJRERoot.getText());
		try {
			vm.setInstallLocation(dir.getCanonicalFile());
		} catch (IOException e) {
			vm.setInstallLocation(dir.getAbsoluteFile());
		}
		vm.setName(fVMName.getText());
		
		String argString = fVMArgs.getText().trim();
		if (vm instanceof IInterpreter2) {
			IInterpreter2 vm2 = (IInterpreter2) vm;
			if (argString != null && argString.length() >0) {
				vm2.setInterpreterArgs(argString);			
			} else {
				vm2.setInterpreterArgs(null);
			}
		} else {
			if (argString != null && argString.length() >0) {
				vm.setInterpreterArguments(DebugPlugin.parseArguments(argString));			
			} else {
				vm.setInterpreterArguments(null);
			}			
		}
		

//		fLibraryBlock.performApply(vm);
	}

}
package org.rubypeople.rdt.internal.debug.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.rubyvms.IAddVMDialogRequestor;
import org.rubypeople.rdt.internal.debug.ui.rubyvms.RubyVMMessages;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyInterpreterPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IAddVMDialogRequestor {
	
	/**
	 * VMs being displayed
	 */
	private List fVMs = new ArrayList(); 
	
	protected CheckboxTableViewer fVMList;
	protected Button addButton, editButton, removeButton;

	public RubyInterpreterPreferencePage() {
		super();
	}

	public void init(IWorkbench workbench) {}

	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();

		Composite composite = createPageRoot(parent);
		Table table = createInstalledInterpretersTable(composite);
		createInstalledInterpretersTableViewer(table);
		createButtonGroup(composite);

		fVMList.setInput(RubyRuntime.getDefault().getInstalledInterpreters());
		IVMInstall selectedInterpreter = RubyRuntime.getDefault().getSelectedInterpreter();
		if (selectedInterpreter != null)
			fVMList.setChecked(selectedInterpreter, true);

		enableButtons();

		return composite;
	}

	protected void createButtonGroup(Composite composite) {
		Composite buttons = new Composite(composite, SWT.NULL);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);

		addButton = new Button(buttons, SWT.PUSH);
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.setText(RdtDebugUiMessages.getString("RubyInterpreterPreferencePage.addButton.label")); //$NON-NLS-1$
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				addInterpreter();
			}
		});

		editButton = new Button(buttons, SWT.PUSH);
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editButton.setText(RdtDebugUiMessages.getString("RubyInterpreterPreferencePage.editButton.label")); //$NON-NLS-1$
		editButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				editInterpreter();
			}
		});

		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setText(RdtDebugUiMessages.getString("RubyInterpreterPreferencePage.removeButton.label")); //$NON-NLS-1$
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				removeInterpreter();
			}
		});
	}

	protected void createInstalledInterpretersTableViewer(Table table) {
		fVMList = new CheckboxTableViewer(table);

		fVMList.setLabelProvider(new RubyInterpreterLabelProvider());
		fVMList.setContentProvider(new RubyInterpreterContentProvider());

		fVMList.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}
		});

		fVMList.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateSelectedInterpreter(event.getElement());
			}
		});
		
		fVMList.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				editInterpreter();
			}
		});
	}

	protected Table createInstalledInterpretersTable(Composite composite) {
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);

		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText(RdtDebugUiMessages.getString("RubyInterpreterPreferencePage.rubyInterpreterTable.interpreterName")); //$NON-NLS-1$
		column.setWidth(125);

		column = new TableColumn(table, SWT.NULL);
		column.setText(RdtDebugUiMessages.getString("RubyInterpreterPreferencePage.rubyInterpreterTable.interpreterPath")); //$NON-NLS-1$
		column.setWidth(350);

		return table;
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		return composite;
	}

	protected void addInterpreter() {
		EditInterpreterDialog dialog = new EditInterpreterDialog(this, getShell(), RubyRuntime.getInterpreterInstallTypes(), null);
		dialog.setTitle(RubyVMMessages.InstalledJREsBlock_7); 
		if (dialog.open() != Window.OK) {
			return;
		}
	}

	protected void removeInterpreter() {
		fVMList.remove(getSelectedInterpreter());
	}

	protected void enableButtons() {
		if (getSelectedInterpreter() != null) {
			editButton.setEnabled(true);
			removeButton.setEnabled(true);
		} else {
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
	}

	protected void updateSelectedInterpreter(Object interpreter) {
		Object[] checkedElements = fVMList.getCheckedElements();
		for (int i = 0; i < checkedElements.length; i++) {
			fVMList.setChecked(checkedElements[i], false);
		}

		fVMList.setChecked(interpreter, true);
	}

	protected void editInterpreter() {
		IStructuredSelection selection= (IStructuredSelection)fVMList.getSelection();
		IVMInstall vm= (IVMInstall)selection.getFirstElement();
		if (vm == null) {
			return;
		}
//		if (isContributed(vm)) {
//			VMDetailsDialog dialog= new VMDetailsDialog(getShell(), vm);
//			dialog.open();
//		} else {
			EditInterpreterDialog dialog= new EditInterpreterDialog(this, getShell(), RubyRuntime.getInterpreterInstallTypes(), vm);
			dialog.setTitle(RubyVMMessages.InstalledJREsBlock_8); 
			if (dialog.open() != Window.OK) {
				return;
			}
			fVMList.refresh(vm);
//		}
	}
	
	protected IVMInstall getSelectedInterpreter() {
		IStructuredSelection selection = (IStructuredSelection) fVMList.getSelection();
		return (IVMInstall) selection.getFirstElement();
	}
	
	public boolean performOk() {
		TableItem[] tableItems = fVMList.getTable().getItems();
		List installedInterpreters = new ArrayList(tableItems.length);
		for (int i = 0; i < tableItems.length; i++)
			installedInterpreters.add(tableItems[i].getData());
		RubyRuntime.getDefault().setInstalledInterpreters(installedInterpreters);

		Object[] checkedElements = fVMList.getCheckedElements();
		if (checkedElements.length > 0)
			RubyRuntime.getDefault().setSelectedInterpreter((IVMInstall) checkedElements[0]);

		return super.performOk();
	}

	public boolean isDuplicateName(String name) {
		for (int i= 0; i < fVMs.size(); i++) {
			IVMInstall vm = (IVMInstall)fVMs.get(i);
			if (vm.getName().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void vmAdded(IVMInstall vm) {
		fVMs.add(vm);
		fVMList.refresh();
	}

}
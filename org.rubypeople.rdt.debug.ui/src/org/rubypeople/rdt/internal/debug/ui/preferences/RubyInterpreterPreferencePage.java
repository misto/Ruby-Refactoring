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
import org.rubypeople.rdt.launching.RubyInterpreter;
import org.rubypeople.rdt.launching.RubyRuntime;
import sun.security.krb5.internal.i;

public class RubyInterpreterPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected CheckboxTableViewer tableViewer;
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

		tableViewer.setInput(RubyRuntime.getDefault().getInstalledInterpreters());
		RubyInterpreter selectedInterpreter = RubyRuntime.getDefault().getSelectedInterpreter();
		if (selectedInterpreter != null)
			tableViewer.setChecked(selectedInterpreter, true);

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
		addButton.setText("Add");
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				addInterpreter();
			}
		});

		editButton = new Button(buttons, SWT.PUSH);
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editButton.setText("Edit");
		editButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				editInterpreter();
			}
		});

		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setText("Remove");
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				removeInterpreter();
			}
		});
	}

	protected void createInstalledInterpretersTableViewer(Table table) {
		tableViewer = new CheckboxTableViewer(table);

		tableViewer.setLabelProvider(new RubyInterpreterLabelProvider());
		tableViewer.setContentProvider(new RubyInterpreterContentProvider());

		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent evt) {
				enableButtons();
			}
		});

		tableViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				updateSelectedInterpreter(event.getElement());
			}
		});
		
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent e) {
				editInterpreter();
			}
		});
	}

	protected Table createInstalledInterpretersTable(Composite composite) {
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER | SWT.FULL_SELECTION);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.widthHint = convertWidthInCharsToPixels(80);
		data.heightHint = convertHeightInCharsToPixels(10);
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("Name");
		column.setWidth(125);

		column = new TableColumn(table, SWT.NULL);
		column.setText("Location");
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
		RubyInterpreter newInterpreter = new RubyInterpreter(null, null);
		EditInterpreterDialog editor = new EditInterpreterDialog(getShell(), "Add Interpreter");
		editor.create();
		editor.setInterpreterToEdit(newInterpreter);
		if (EditInterpreterDialog.OK == editor.open())
			tableViewer.add(newInterpreter);
	}

	protected void removeInterpreter() {
		tableViewer.remove(getSelectedInterpreter());
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
		Object[] checkedElements = tableViewer.getCheckedElements();
		for (int i = 0; i < checkedElements.length; i++) {
			tableViewer.setChecked(checkedElements[i], false);
		}

		tableViewer.setChecked(interpreter, true);
	}

	protected void editInterpreter() {
		EditInterpreterDialog editor = new EditInterpreterDialog(getShell(), "Edit Interpreter");
		editor.create();
		
		RubyInterpreter anInterpreter = getSelectedInterpreter();
		editor.setInterpreterToEdit(anInterpreter);
		if (EditInterpreterDialog.OK == editor.open())
			tableViewer.update(anInterpreter, null);
	}
	
	protected RubyInterpreter getSelectedInterpreter() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
		return (RubyInterpreter) selection.getFirstElement();
	}
	
	public boolean performOk() {
		TableItem[] tableItems = tableViewer.getTable().getItems();
		List installedInterpreters = new ArrayList(tableItems.length);
		for (int i = 0; i < tableItems.length; i++)
			installedInterpreters.add(tableItems[i].getData());
		RubyRuntime.getDefault().setInstalledInterpreters(installedInterpreters);

		Object[] checkedElements = tableViewer.getCheckedElements();
		if (checkedElements.length > 0)
			RubyRuntime.getDefault().setSelectedInterpreter((RubyInterpreter) checkedElements[0]);

		return super.performOk();
	}

}
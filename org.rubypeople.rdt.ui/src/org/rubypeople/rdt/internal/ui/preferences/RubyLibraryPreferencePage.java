package org.rubypeople.rdt.internal.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.RubyLibrary;

public class RubyLibraryPreferencePage extends PreferencePage
		implements
			IWorkbenchPreferencePage {
	protected CheckboxTableViewer tableViewer;
	protected Button addButton, editButton, removeButton;

	public RubyLibraryPreferencePage() {
		super();
	}

	public void init(IWorkbench workbench) {}

	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();

		Composite composite = createPageRoot(parent);
		Table table = createInstalledInterpretersTable(composite);
		createInstalledInterpretersTableViewer(table);
		createButtonGroup(composite);

		tableViewer.setInput(RubyPlugin.getDefault()
				.getInstalledLibraries());
		RubyLibrary selectedInterpreter = RubyPlugin.getDefault()
				.getSelectedLibrary();
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
		addButton.setText("Add"); //$NON-NLS-1$
		addButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				addInterpreter();
			}
		});

		editButton = new Button(buttons, SWT.PUSH);
		editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		editButton.setText("Edit"); //$NON-NLS-1$
		editButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				editInterpreter();
			}
		});

		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.setText("Remove"); //$NON-NLS-1$
		removeButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event evt) {
				removeInterpreter();
			}
		});
	}

	protected void createInstalledInterpretersTableViewer(Table table) {
		tableViewer = new CheckboxTableViewer(table);

		tableViewer.setLabelProvider(new RubyLibraryLabelProvider());
		tableViewer.setContentProvider(new RubyLibraryContentProvider());

		tableViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
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
		Table table = new Table(composite, SWT.CHECK | SWT.BORDER
				| SWT.FULL_SELECTION);

		GridData data = new GridData(GridData.FILL_BOTH);
		table.setLayoutData(data);
		table.setHeaderVisible(true);
		table.setLinesVisible(false);

		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText("Library Name"); //$NON-NLS-1$
		column.setWidth(125);

		column = new TableColumn(table, SWT.NULL);
		column.setText("Library Path"); //$NON-NLS-1$
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
		RubyLibrary newInterpreter = new RubyLibrary(null, null);
		EditLibraryDialog editor = new EditLibraryDialog(getShell(),
				"Add Library"); //$NON-NLS-1$
		editor.create();
		editor.setInterpreterToEdit(newInterpreter);
		if (EditLibraryDialog.OK == editor.open())
			tableViewer.add(newInterpreter);
	}

	protected void removeInterpreter() {
		tableViewer.remove(getSelectedInterpreter());
	}

	protected void enableButtons() {
		if (getSelectedInterpreter() != null) {
			editButton.setEnabled(true);
			removeButton.setEnabled(true);
		}
		else {
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
		EditLibraryDialog editor = new EditLibraryDialog(getShell(),
				"Edit Library"); //$NON-NLS-1$
		editor.create();

		RubyLibrary anInterpreter = getSelectedInterpreter();
		editor.setInterpreterToEdit(anInterpreter);
		if (EditLibraryDialog.OK == editor.open())
			tableViewer.update(anInterpreter, null);
	}

	protected RubyLibrary getSelectedInterpreter() {
		IStructuredSelection selection = (IStructuredSelection) tableViewer
				.getSelection();
		return (RubyLibrary) selection.getFirstElement();
	}

	public boolean performOk() {
		TableItem[] tableItems = tableViewer.getTable().getItems();
		List installedInterpreters = new ArrayList(tableItems.length);
		for (int i = 0; i < tableItems.length; i++)
			installedInterpreters.add(tableItems[i].getData());
		RubyPlugin.getDefault().setInstalledLibraries(installedInterpreters);

		Object[] checkedElements = tableViewer.getCheckedElements();
		if (checkedElements.length > 0)
			RubyPlugin.getDefault()
					.setSelectedLibrary((RubyLibrary) checkedElements[0]);

		return super.performOk();
	}

}

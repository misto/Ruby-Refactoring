package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.rubypeople.rdt.internal.debug.ui.preferences.EditInterpreterDialog;
import org.rubypeople.rdt.launching.RubyInterpreter;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyEnvironmentTab extends AbstractLaunchConfigurationTab {
	protected List loadPathList;
	protected java.util.List installedInterpretersWorkingCopy;

	public RubyEnvironmentTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);

		TabFolder tabFolder = new TabFolder(composite, SWT.NONE);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		tabFolder.setLayoutData(gridData);

		addLoadPathTab(tabFolder);
		addInterpreterTab(tabFolder);
	}

	protected void addLoadPathTab(TabFolder tabFolder) {
		Composite loadPathComposite = new Composite(tabFolder, SWT.NONE);
		loadPathComposite.setLayout(new GridLayout());

		loadPathList = new List(loadPathComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
		loadPathList.setLayoutData(new GridData(GridData.FILL_BOTH));
		loadPathList.addSelectionListener(getLoadPathSelectionListener());

		TabItem loadPathTab = new TabItem(tabFolder, SWT.NONE, 0);
		loadPathTab.setText("Load&path");
		loadPathTab.setControl(loadPathComposite);
		loadPathTab.setData(loadPathList);

		Button loadPathDefaultButton = new Button(loadPathComposite, SWT.CHECK);
		loadPathDefaultButton.setText("&Use default loadpath");
		loadPathDefaultButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		loadPathDefaultButton.addSelectionListener(getLoadPathDefaultButtonSelectionListener());
	}

	protected SelectionListener getLoadPathSelectionListener() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Loadpath list selection occurred: " + e.getSource());
			}
		};
	}

	protected SelectionListener getLoadPathDefaultButtonSelectionListener() {
		return new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				System.out.println("LoadpathDefault button selection occurred: " + e.getSource());
			}
		};
	}

	protected void addInterpreterTab(TabFolder tabFolder) {
		Composite interpreterComposite = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		interpreterComposite.setLayout(layout);
		interpreterComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		createVerticalSpacer(interpreterComposite, 2);

		final Combo interpreterCombo = new Combo(interpreterComposite, SWT.READ_ONLY);
		interpreterCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		initializeInterpreterCombo(interpreterCombo);
		interpreterCombo.addModifyListener(getInterpreterComboModifyListener());

		Button interpreterAddButton = new Button(interpreterComposite, SWT.PUSH);
		interpreterAddButton.setText("N&ew...");
		interpreterAddButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent evt) {
				RubyInterpreter newInterpreter = new RubyInterpreter(null, null);
				EditInterpreterDialog editor = new EditInterpreterDialog(getShell(), "Add Interpreter");
				editor.create();
				editor.setInterpreterToEdit(newInterpreter);
				if (EditInterpreterDialog.OK == editor.open()) {
					RubyRuntime.getDefault().addInstalledInterpreter(newInterpreter);
					interpreterCombo.add(newInterpreter.getName());
					interpreterCombo.select(interpreterCombo.indexOf(newInterpreter.getName()));
				}
			}
		});

		TabItem interpreterTab = new TabItem(tabFolder, SWT.NONE);
		interpreterTab.setText("&JRE");
		interpreterTab.setControl(interpreterComposite);
	}

	protected ModifyListener getInterpreterComboModifyListener() {
		return new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		};
	}

	protected void createVerticalSpacer(Composite comp, int colSpan) {
		Label label = new Label(comp, SWT.NONE);
		GridData gd = new GridData();
		gd.horizontalSpan = colSpan;
		label.setLayoutData(gd);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("RubyEnvironmentTab#setDefaults()");
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		setLoadPath(configuration);
	}

	protected void setLoadPath(ILaunchConfiguration configuration) {
		java.util.List loadPath = null;
		try {
			loadPath = configuration.getAttribute(RubyLaunchConfigurationAttribute.LOAD_PATH_LIST, new ArrayList());
		} catch (CoreException e) {
			System.out.println("RubyEnvironmentTab#setLoadPath(): " + e);
		}
		String[] loadPathStrings = new String[loadPath.size()];
		loadPath.toArray(loadPathStrings);
		loadPathList.setItems(loadPathStrings);
	}

	protected void initializeInterpreterCombo(Combo interpreterCombo) {
		installedInterpretersWorkingCopy = new ArrayList();
		installedInterpretersWorkingCopy.addAll(RubyRuntime.getDefault().getInstalledInterpreters());

		String[] interpreterNames = new String[installedInterpretersWorkingCopy.size()];
		for (int interpreterIndex = 0; interpreterIndex < installedInterpretersWorkingCopy.size(); interpreterIndex++) {
			RubyInterpreter interpreter = (RubyInterpreter) installedInterpretersWorkingCopy.get(interpreterIndex);
			interpreterNames[interpreterIndex] = interpreter.getName();
		}
		interpreterCombo.setItems(interpreterNames);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {}

	public boolean isValid() {
		return true;
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		createVerticalSpacer(composite, 2);
		setControl(composite);

		return composite;
	}
}
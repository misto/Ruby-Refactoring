package org.rubypeople.rdt.internal.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.progress.IProgressConstants;
import org.rubypeople.rdt.ui.actions.IRubyActionSetIds;

public class RdtPerspectiveFactory implements IPerspectiveFactory {

	public RdtPerspectiveFactory() {
		super();
	}

	public void createInitialLayout(IPageLayout layout) {
		String existingEditorArea = layout.getEditorArea();

		IFolderLayout rubyResourcesArea = layout.createFolder("rubyResourcesArea", IPageLayout.LEFT, (float) 0.26, existingEditorArea);
		rubyResourcesArea.addView(RubyPlugin.RUBY_RESOURCES_VIEW_ID);
		rubyResourcesArea.addPlaceholder(IPageLayout.ID_RES_NAV);

		IFolderLayout consoleArea = layout.createFolder("consoleArea", IPageLayout.BOTTOM, (float) 0.75, existingEditorArea);
		consoleArea.addView(IPageLayout.ID_PROBLEM_VIEW);
		consoleArea.addPlaceholder(IConsoleConstants.ID_CONSOLE_VIEW);
		consoleArea.addView(IPageLayout.ID_TASK_LIST);
		consoleArea.addPlaceholder(IProgressConstants.PROGRESS_VIEW_ID);

		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", IPageLayout.BOTTOM, (float) 0.5, "rubyResourcesArea");
		bottomLeft.addView(IPageLayout.ID_OUTLINE);
		
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(IRubyActionSetIds.RUBY_ACTION_SET_ID);
		layout.addActionSet(IRubyActionSetIds.ID_ELEMENT_CREATION_ACTION_SET);
		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
		
		// views - debugging
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);

		// views - standard workbench
		layout.addShowViewShortcut(IPageLayout.ID_OUTLINE);
		layout.addShowViewShortcut(IPageLayout.ID_PROBLEM_VIEW);
		layout.addShowViewShortcut(IPageLayout.ID_RES_NAV);
				
		// new actions - Java project creation wizard
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.EnumCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard");	 //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
	}

}
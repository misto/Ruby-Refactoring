
package org.rubypeople.rdt.internal.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.rubypeople.rdt.ui.actions.RubyActionSetIds;

public class RdtPerspectiveFactory implements IPerspectiveFactory {

	public RdtPerspectiveFactory() {
		super();
	}

	public void createInitialLayout(IPageLayout layout) {
		layout.addNewWizardShortcut("org.rubypeople.rdt.ui.wizards.NewWizardProjectCreation");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");
		
 		String existingEditorArea = layout.getEditorArea();
		
		IFolderLayout rubyResourcesArea = layout.createFolder("rubyResourcesArea", IPageLayout.LEFT, (float)0.26, existingEditorArea);
		rubyResourcesArea.addView(RdtUiPlugin.RUBY_RESOURCES_VIEW_ID);
		
		IFolderLayout consoleArea = layout.createFolder("consoleArea", IPageLayout.BOTTOM, (float)0.75, existingEditorArea);
		consoleArea.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(RubyActionSetIds.RUBY_ACTION_SET_ID);
		
		layout.addShowViewShortcut(IConsoleConstants.ID_CONSOLE_VIEW);
		layout.addShowViewShortcut("org.eclipse.ui.views.ContentOutline");
		layout.addShowViewShortcut(RdtUiPlugin.RUBY_RESOURCES_VIEW_ID);
		
		IFolderLayout bottomLeft = layout.createFolder("bottomLeft", 4, 0.5F, "rubyResourcesArea");
		bottomLeft.addView("org.eclipse.ui.views.ContentOutline");
	}
	
	

}

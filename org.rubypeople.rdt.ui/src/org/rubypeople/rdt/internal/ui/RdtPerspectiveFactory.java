
package org.rubypeople.rdt.internal.ui;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class RdtPerspectiveFactory implements IPerspectiveFactory {

	public RdtPerspectiveFactory() {
		super();
	}

	public void createInitialLayout(IPageLayout layout) {
 		String existingEditorArea = layout.getEditorArea();
		
		IFolderLayout rubyResourcesArea = layout.createFolder("rubyResourcesArea", IPageLayout.LEFT, (float)0.25, existingEditorArea);
		rubyResourcesArea.addView(RdtUiPlugin.RUBY_RESOURCES_VIEW_ID);
		
		IFolderLayout consoleArea = layout.createFolder("consoleArea", IPageLayout.BOTTOM, (float)0.75, existingEditorArea);
		consoleArea.addView(IDebugUIConstants.ID_CONSOLE_VIEW);
		
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		
		layout.addShowViewShortcut(IDebugUIConstants.ID_CONSOLE_VIEW);

		layout.addShowViewShortcut(RdtUiPlugin.RUBY_RESOURCES_VIEW_ID);
	}

}

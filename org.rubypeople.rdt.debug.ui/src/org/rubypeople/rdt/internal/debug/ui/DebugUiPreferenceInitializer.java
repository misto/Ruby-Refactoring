/*
 * Created on Mar 1, 2005
 *
 */
package org.rubypeople.rdt.internal.debug.ui;

import org.osgi.service.prefs.Preferences;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.rubypeople.rdt.debug.ui.RdtDebugUiConstants;


/**
 * @author Chris
 *
 */
public class DebugUiPreferenceInitializer extends AbstractPreferenceInitializer {

	public DebugUiPreferenceInitializer() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
	 */
	public void initializeDefaultPreferences() {
		Preferences node = new DefaultScope().getNode(RdtDebugUiPlugin.PLUGIN_ID);
		node.put(RdtDebugUiConstants.PREFERENCE_KEYWORDS, getDefaultKeywords());
	}

	private String getDefaultKeywords() {
		return "class,def,end,if,module,new,puts,require,rescue,throw,while";
	}
	
}

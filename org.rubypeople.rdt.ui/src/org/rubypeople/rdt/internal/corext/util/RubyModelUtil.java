/**
 * 
 */
package org.rubypeople.rdt.internal.corext.util;

import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * @author Chris
 * 
 */
public final class RubyModelUtil {

	private static boolean PRIMARY_ONLY = false;

	/**
	 * Returns the original cu if the given cu is a working copy. If the cu is
	 * already an original the input cu is returned. The returned cu might not
	 * exist
	 */
	public static IRubyScript toOriginal(IRubyScript cu) {
		if (PRIMARY_ONLY) {
			testRubyScriptOwner("toOriginal", cu); //$NON-NLS-1$
		}
		// To stay compatible with old version returned null
		// if cu is null
		if (cu == null) return cu;
		return cu.getPrimary();
	}

	private static void testRubyScriptOwner(String methodName, IRubyScript cu) {
		if (cu == null) { return; }
		if (!isPrimary(cu)) {
			RubyPlugin.logErrorMessage(methodName + ": operating with non-primary cu"); //$NON-NLS-1$
		}
	}

	/**
	 * Returns true if a cu is a primary cu (original or shared working copy)
	 */
	public static boolean isPrimary(IRubyScript cu) {
		return cu.getOwner() == null;
	}
}

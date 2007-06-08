package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.jface.action.Action;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.compare.RubyMergeViewer;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * @author Emanuel Graf
 *
 */
public class ShowWhiteSpaceAction extends Action {

	private RubyMergeViewer viewer;
	
	public ShowWhiteSpaceAction(RubyMergeViewer compareViewer) {
		super("Show Whitespace Characters", AS_CHECK_BOX); 
		viewer = compareViewer;
		setImageDescriptor(RubyPluginImages.DESC_ELCL_SHOW_WHITESPACE); //$NON-NLS-1$
		setToolTipText("Show Whitespace Characters");
		setChecked(RubyPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SHOW_WHITESPACES));
	}

	@Override
	public void run() {
		boolean show = ! RubyPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.SHOW_WHITESPACES);
		RubyPlugin.getDefault().getPreferenceStore().setValue(PreferenceConstants.SHOW_WHITESPACES, show);
		viewer.showWhitespaces(show);
	}
}

package org.rubypeople.rdt.internal.ui.search;

import org.eclipse.jface.action.Action;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;


public class GroupByAction extends Action {

	public GroupByAction(String label, String image) {
		super(label) ;
		this.setToolTipText(label) ;
		RubyPluginImages.setLocalImageDescriptors(this, image) ;
	}
}

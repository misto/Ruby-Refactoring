package org.rubypeople.rdt.ui.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.actions.ActionGroup;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

public class RubyActionGroup extends ActionGroup {
	protected RubyEditor editor;
	protected String menuGroupId;

	public RubyActionGroup(RubyEditor editor, String menuGroupId) {
		this.editor = editor;
		this.menuGroupId = menuGroupId;
	}

	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);

		menu.add(editor.getAction("ToggleComment"));
		menu.add(editor.getAction("Comment"));
		menu.add(editor.getAction("Uncomment"));
		menu.add(editor.getAction("Format"));
	}
}

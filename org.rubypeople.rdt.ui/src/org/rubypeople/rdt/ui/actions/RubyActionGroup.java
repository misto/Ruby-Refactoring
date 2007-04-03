package org.rubypeople.rdt.ui.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.actions.ActionGroup;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

public class RubyActionGroup extends ActionGroup {
	public static final String RUBY_SOURCE_SEPARATOR = "ruby.source.separator";
	protected RubyEditor editor;
	protected String menuGroupId;

	public RubyActionGroup(RubyEditor editor, String menuGroupId) {
		this.editor = editor;
		this.menuGroupId = menuGroupId;
	}

	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
		IMenuManager rubySourceMenu = getRubySourceMenu(menu);
		rubySourceMenu.insertBefore(RUBY_SOURCE_SEPARATOR, editor.getAction("SurroundWithBeginRescue"));
		rubySourceMenu.insertBefore(RUBY_SOURCE_SEPARATOR, editor.getAction("ToggleComment"));
		rubySourceMenu.insertBefore(RUBY_SOURCE_SEPARATOR, editor.getAction("Comment"));
		rubySourceMenu.insertBefore(RUBY_SOURCE_SEPARATOR, editor.getAction("Uncomment"));
		rubySourceMenu.insertBefore(RUBY_SOURCE_SEPARATOR, editor.getAction("Format"));
	}

	public static IMenuManager getRubySourceMenu(IMenuManager menu) {

		IMenuManager sourceMenu = menu.findMenuUsingPath("ruby.source");
		if (sourceMenu == null) {
			sourceMenu = new MenuManager("Source", "ruby.source");
			sourceMenu.add(new Separator(RUBY_SOURCE_SEPARATOR));
			menu.insertAfter("group.edit", sourceMenu);
		}
		return sourceMenu;
	}
}

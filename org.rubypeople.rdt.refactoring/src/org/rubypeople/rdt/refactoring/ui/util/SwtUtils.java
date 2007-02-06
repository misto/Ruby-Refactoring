package org.rubypeople.rdt.refactoring.ui.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

public class SwtUtils {
	
	public static void initExplanation(Composite control, String explTitle, String explText) {
		Group group = new Group(control, SWT.NONE);
		Label label = new Label(group, SWT.WRAP);

		group.setLayout(new FillLayout(SWT.VERTICAL));
		group.setText(explTitle);
		label.setText(explText);
	}
	
	public static Label initLabel(Group group, String text) {
		Label label = new Label(group, SWT.None);
		label.setText(text);
		return label;
	}

	public static Group initGroup(Composite c, String text) {
		Group group = new Group(c, SWT.NONE);
		group.setLayout(new FillLayout(SWT.HORIZONTAL));
		group.setText(text);
		return group;
	}
}

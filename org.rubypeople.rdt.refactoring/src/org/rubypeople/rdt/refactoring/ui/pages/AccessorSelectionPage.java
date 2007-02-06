/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.refactoring.ui.pages;

import org.eclipse.ltk.ui.refactoring.UserInputWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Widget;
import org.rubypeople.rdt.refactoring.core.generateaccessors.AccessorsGenerator;
import org.rubypeople.rdt.refactoring.core.generateaccessors.GeneratedAccessor;
import org.rubypeople.rdt.refactoring.ui.NotifiedContainerCheckedTree;
import org.rubypeople.rdt.refactoring.ui.util.SwtUtils;

public class AccessorSelectionPage extends UserInputWizardPage {
	private static final String title = "Select attributes";

	private NotifiedContainerCheckedTree tree;

	private AccessorsGenerator accessorsGenerator;

	private static final int DEFAULT_TYPE = GeneratedAccessor.DEFAULT_TYPE;

	public AccessorSelectionPage(AccessorsGenerator accessorsGenerator) {
		super(title);
		setTitle(title);
		this.accessorsGenerator = accessorsGenerator;
	}

	public void createControl(Composite parent) {

		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new FillLayout(SWT.HORIZONTAL));

		initTree(c);
		Composite c2 = new Composite(c, SWT.NONE);
		c2.setLayout(new FillLayout(SWT.VERTICAL));
		initAccessorTypeSelection(c2);
		initTypeExamples(c2);
		setControl(c);
	}

	private Widget[] initTypeExamples(Composite c) {
		Group simpleGroup = SwtUtils.initGroup(c, "Example simple accessor:");
		Label simpleLable = SwtUtils.initLabel(simpleGroup, "attr_accessor :a\nattr_reader :b\nattr_writer :c");
		Group methodGroup = SwtUtils.initGroup(c, "Example accessor method:");
		Label methodLabel = SwtUtils.initLabel(methodGroup, "def a= a\n  @a = a\nend\n\ndef a\n  @a\nend");
		return new Widget[] { simpleGroup, simpleLable, methodGroup, methodLabel };
	}

	private NotifiedContainerCheckedTree initTree(Composite c) {
		tree = new NotifiedContainerCheckedTree(c, accessorsGenerator, accessorsGenerator);
		return tree;
	}

	private Widget[] initAccessorTypeSelection(Composite parent) {
		Group g = new Group(parent, SWT.NONE | SWT.VERTICAL);
		g.setText("Select accessor type:");
		g.setBounds(200, 10, 300, 300);
		Button simpleButton = initTypeButton("Generate simple accessors", GeneratedAccessor.TYPE_SIMPLE_ACCESSOR, g, 20);
		Button methodButton = initTypeButton("Generate accessor methods", GeneratedAccessor.TYPE_METHOD_ACCESSOR, g, 40);
		return new Widget[] { g, simpleButton, methodButton };
	}

	private Button initTypeButton(String name, final int type, Group g, int y) {

		Button button = new Button(g, SWT.RADIO);
		button.setText(name);
		button.setBounds(15, y, 200, 20);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setType(type);
			}
		});
		if (type == DEFAULT_TYPE) {
			setType(type);
			button.setSelection(true);
		}
		return button;
	}

	protected void setType(int type) {
		accessorsGenerator.setType(type);
		tree.setInput("");
	}
}

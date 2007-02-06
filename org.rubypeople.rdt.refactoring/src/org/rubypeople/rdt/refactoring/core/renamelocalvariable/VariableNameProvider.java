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
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
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

package org.rubypeople.rdt.refactoring.core.renamelocalvariable;

import java.util.Observable;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.refactoring.ui.IErrorMessageGenerator;
import org.rubypeople.rdt.refactoring.ui.IErrorMessageReceiver;
import org.rubypeople.rdt.refactoring.util.NameValidator;

public class VariableNameProvider extends Observable implements IErrorMessageGenerator {

	private String selected = "";

	private String name = "";

	private IErrorMessageReceiver errorReceiver;

	public VariableNameProvider(String selected) {
		this.selected = selected;
		this.name = selected;
	}

	public String getSelected() {
		return selected;
	}

	public String getName() {
		return name;
	}

	public void handleEvent(Event event) {
		if (event.widget instanceof List) {
			selected = ((List) event.widget).getSelection()[0];
		} else if (event.widget instanceof Text) {
			String newName = ((Text) event.widget).getText();
			if(NameValidator.isValidLocalVariableName(newName)) {
				name = newName;
				errorReceiver.setError(null);
			} else {
				errorReceiver.setError(newName + " isn't a valid name.");
			}
		} else {
			return;
		}
		setChanged();
		notifyObservers();
	}

	public void setErrorReceiver(IErrorMessageReceiver errorReceiver) {
		this.errorReceiver = errorReceiver;
	}
}
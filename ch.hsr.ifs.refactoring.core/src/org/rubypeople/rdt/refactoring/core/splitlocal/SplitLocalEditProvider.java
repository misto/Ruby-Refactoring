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

package org.rubypeople.rdt.refactoring.core.splitlocal;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.Node;
import org.rubypeople.rdt.refactoring.editprovider.EditProvider;
import org.rubypeople.rdt.refactoring.editprovider.MultiEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.SimpleNodeEditProvider;

public class SplitLocalEditProvider extends MultiEditProvider implements ISplittedNamesReceiver {

	private final SplitLocalConfig config;

	public SplitLocalEditProvider(SplitLocalConfig config) {
		this.config = config;
	}

	@Override
	protected Collection<EditProvider> getEditProviders() {
		ArrayList<EditProvider> edits = new ArrayList<EditProvider>();
		for (Node node : new SplittedVariableRenamer(config.getLocalVariablesFinder().getScopeNode()).rename(config.getLocalUsages())) {
			edits.add(new SimpleNodeEditProvider(node));
		}
		return edits;
	}

	public void setNewNames(String[] names) {
		assert names.length == config.getLocalUsages().size();

		for (int i = 0; i < names.length; i++) {
			config.getLocalUsages().toArray(new LocalVarUsage[config.getLocalUsages().size()])[i].setNewName(names[i]);
		}
	}

	public Collection<LocalVarUsage> getLocalUsages() {
		return config.getLocalUsages();
	}

}

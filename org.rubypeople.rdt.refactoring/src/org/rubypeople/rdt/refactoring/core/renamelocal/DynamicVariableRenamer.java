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

package org.rubypeople.rdt.refactoring.core.renamelocal;

import java.util.ArrayList;

import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.Node;
import org.jruby.ast.types.INameNode;

public class DynamicVariableRenamer extends VariableRenamer {
	public DynamicVariableRenamer(String oldName, String newName, IAbortCondition abort) {
		super(oldName, newName, abort);
	}

	public ArrayList<Node> replaceVariableNamesInNode(Node n) {
		ArrayList<Node> renamed = new ArrayList<Node>();
		
		if (n instanceof MethodDefNode) {
			n = ((MethodDefNode) n).getBodyNode();
		} else if (n instanceof IterNode) {
			renamed.addAll(replaceVariableNames(((IterNode) n).getVarNode()));
			renamed.addAll(replaceVariableNames(((IterNode) n).getBodyNode()));
		}
		
		renamed.addAll(replaceVariableNames(n));
		return renamed;
	}

	public ArrayList<Node> replaceVariableNames(Node n) {

		ArrayList<Node> renamedNodes = new ArrayList<Node>();

		if (abort.abort(n)) {
			return renamedNodes;
		}
		
		if(n instanceof INameNode && ((INameNode) n).getName().equals(oldName)) {
			
			if (n instanceof DVarNode) {
				((DVarNode) n).setName(newName);
			} else if (n instanceof DAsgnNode) {
				((DAsgnNode) n).setName(newName);
				replaceVariableNames(n);
			}
			renamedNodes.add(n);
			return renamedNodes;
		}

		for (Object node : n.childNodes()) {
			renamedNodes.addAll(replaceVariableNames((Node) node));
		}

		return renamedNodes;
	}
}

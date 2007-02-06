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

package org.rubypeople.rdt.refactoring.core.renameclass;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.ClassNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.Node;
import org.rubypeople.rdt.refactoring.editprovider.FileEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.ReplaceEditProvider;

public class ChildClassesRenameEditProvider {

	private static class ChildClassEditProvider extends ReplaceEditProvider {
		
		private final Node node;

		public ChildClassEditProvider(Node node) {
			this.node = node;
		}

		@Override
		protected int getOffsetLength() {
			return node.getPosition().getEndOffset() - node.getPosition().getStartOffset();
		}

		@Override
		protected Node getEditNode(int offset, String document) {
			return node;
		}

		@Override
		protected int getOffset(String document) {
			return node.getPosition().getStartOffset();
		}
		
	}
	
	private final Collection<ClassNode> classes;
	private final String newName;

	public ChildClassesRenameEditProvider(Collection<ClassNode> childClasses, String newName) {
		this.classes = childClasses;
		this.newName = newName;
	}

	protected Collection<FileEditProvider> getEditProviders() {
		Collection<FileEditProvider> edits = new ArrayList<FileEditProvider>();
		
		for(ClassNode klass : classes) {
			((ConstNode) klass.getSuperNode()).setName(newName);
			edits.add(new FileEditProvider(klass.getPosition().getFile(), new ChildClassEditProvider(klass.getSuperNode())));
		}
		
		return edits;
	}

}
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

package org.rubypeople.rdt.refactoring.core.generateconstructor;

import java.util.Collection;
import java.util.Iterator;

import org.jruby.ast.BlockNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SourcePosition;
import org.rubypeople.rdt.refactoring.core.NodeFactory;
import org.rubypeople.rdt.refactoring.editprovider.InsertEditProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.offsetprovider.ConstructorOffsetProvider;
import org.rubypeople.rdt.refactoring.util.Constants;

public class GeneratedConstructor extends InsertEditProvider {
	private Collection<String> arguments;

	private ClassNodeWrapper classNode;

	public GeneratedConstructor(ClassNodeWrapper classNode, Collection<String> arguments) {
		super(true);
		this.classNode = classNode;
		this.arguments = arguments;
	}

	protected BlockNode getInsertNode(int offset, String document) {
		return NodeFactory.createBlockNode(!isNextLineEmpty(offset, document), NodeFactory.createNewLineNode(getConstructorNode()));
	}

	private DefnNode getConstructorNode() {
		return NodeFactory.createMethodNode(Constants.CONSTRUCTOR_NAME, arguments.toArray(new String[arguments.size()]), arguments.size() > 0 ? getBody(arguments) : null);
	}

	private Node getBody(Collection<String> args) {
		Iterator<String> argsIter = args.iterator();
		BlockNode blockNode = new BlockNode(new SourcePosition());
		
		for (int i = 0; i < args.size(); i++) {
			String name = argsIter.next();
			InstAsgnNode assignment = NodeFactory.createInstAsgnNode('@' + name, NodeFactory.createLocalVarNode(name));
			if(i > 0) {
				blockNode.add(NodeFactory.createNewLineNode(assignment));
			} else {
				blockNode.add(assignment);
			}
		}
		return blockNode;
	}

	protected int getOffset(String document) {
		return new ConstructorOffsetProvider(classNode, document).getOffset();
	}
}
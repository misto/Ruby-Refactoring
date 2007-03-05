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
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
 * Copyright (C) 2006 Thomas Corbat <tcorbat@hsr.ch>
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

package org.rubypeople.rdt.refactoring.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;

import org.jruby.ast.FCallNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.Node;
import org.jruby.ast.SymbolNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.refactoring.nodewrapper.AttrAccessorNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;

public class FieldProvider {

	private ClassNodeWrapper classNode;
	
	private LinkedHashMap<String, ArrayList<FCallNode>> attrs;
	
	private LinkedHashMap<String, ArrayList<AttrAccessorNodeWrapper>> accessors;
	
	private LinkedHashMap<String, ArrayList<InstVarNode>> instVars;
	
	private LinkedHashMap<String, ArrayList<InstAsgnNode>> instAsgns;
	
	public FieldProvider(ClassNodeWrapper classNode){
		this.classNode = classNode;
		initAttrs();
		initAccessors();
	}

	private void initAccessors() {

		accessors = new LinkedHashMap<String, ArrayList<AttrAccessorNodeWrapper>>();
		
		for(AttrAccessorNodeWrapper currentAccessor : classNode.getAccessorNodes()){
			
			String name = fieldName(currentAccessor.getAttrName());
			 
			 if(!accessors.containsKey(name)){
				 accessors.put(name, new ArrayList<AttrAccessorNodeWrapper>());
			 }
			 ArrayList<AttrAccessorNodeWrapper> accessorList = accessors.get(name);
			 accessorList.add(currentAccessor);
		}
	}

	private void initAttrs() {
		
		attrs = new LinkedHashMap<String, ArrayList<FCallNode>>();
		instVars = new LinkedHashMap<String, ArrayList<InstVarNode>>();
		instAsgns = new LinkedHashMap<String, ArrayList<InstAsgnNode>>();
		
		Collection<Node> allAttrs = classNode.getAttrNodes();
		
		for(Node currentAttr : allAttrs){
			if(currentAttr instanceof FCallNode){
				addAttr((FCallNode)currentAttr);
			}
			else if (currentAttr instanceof InstVarNode){
				addInstVar((InstVarNode)currentAttr );
			}			
			else if (currentAttr instanceof InstAsgnNode){
				addInstAsgn((InstAsgnNode)currentAttr);
			}
		}
	}

	private void addInstAsgn(InstAsgnNode currentAttr) {

		 String name = fieldName(currentAttr.getName());
		 
		 if(!attrs.containsKey(name)){
			 instAsgns.put(name, new ArrayList<InstAsgnNode>());
		 }
		 ArrayList<InstAsgnNode> asgnList = instAsgns.get(name);
		 asgnList.add(currentAttr);
	}


	public static String fieldName(String name) {
		return name.replaceAll("@|:", ""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private void addInstVar(InstVarNode currentAttr) {
		 String name = fieldName(currentAttr.getName());
		 
		 if(!attrs.containsKey(name)){
			 instVars.put(name, new ArrayList<InstVarNode>());
		 }
		 ArrayList<InstVarNode> varList = instVars.get(name);
		 varList.add(currentAttr);
	}

	private void addAttr(FCallNode currentAttr) {
		SymbolNode symbol = (SymbolNode)currentAttr.getArgsNode();
		String name = fieldName(symbol.getName());
		 
		 if(!attrs.containsKey(name)){
			 attrs.put(name, new ArrayList<FCallNode>());
		 }
		 ArrayList<FCallNode> attrList = attrs.get(name);
		 attrList.add(currentAttr);
	}

	public ArrayList<AttrAccessorNodeWrapper> getAccessors(String fieldName) {
		return accessors.get(fieldName);
	}

	public ArrayList<FCallNode> getAttrs(String fieldName) {
		return attrs.get(fieldName);
	}

	public ArrayList<InstAsgnNode> getInstAsgns(String fieldName) {
		return instAsgns.get(fieldName);
	}

	public ArrayList<InstVarNode> getInstVars(String fieldName) {
		return instVars.get(fieldName);
	}

	public Collection<String> getFieldNames() {
		HashSet<String> names = new HashSet<String>();
		names.addAll(attrs.keySet());
		names.addAll(accessors.keySet());
		names.addAll(instVars.keySet());
		names.addAll(instAsgns.keySet());
		return names;
	}

	public String getNameAtPosition(int caretPosition) {
		
		for(String currentName : attrs.keySet()){
			for(FCallNode currentFCall : attrs.get(currentName)){
				if(containsPosition(caretPosition, currentFCall.getArgsNode().getPosition())){
					return currentName;
				}
			}
		}
		
		for(String currentName : accessors.keySet()){
			for(AttrAccessorNodeWrapper currentAccessorWrapper : accessors.get(currentName)){
				for(FCallNode currentAccessor : currentAccessorWrapper.getAccessorNodes()){
					if(containsPosition(caretPosition, currentAccessor.getArgsNode().getPosition())){
						return currentName;
					}
				}
			}
		}
		
		for(String currentName : instVars.keySet()){
			for(InstVarNode currentInstVar : instVars.get(currentName)){
				if(containsPosition(caretPosition, currentInstVar.getPosition())){
					return currentName;
				}	
			}
		}
		
		for(String currentName : instAsgns.keySet()){
			for(InstAsgnNode currentInstAsgn : instAsgns.get(currentName)){
				if(containsPosition(caretPosition, currentInstAsgn.getPosition())){
					return currentName;
				}	
			}
		}

		return null;
	}

	private boolean containsPosition(int offset, ISourcePosition pos){
		return (pos.getStartOffset() <= offset) && (pos.getEndOffset() >= offset);
	}
}

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

package org.rubypeople.rdt.refactoring.core.renamemethod;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.Node;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SymbolNode;
import org.jruby.ast.VCallNode;
import org.rubypeople.rdt.refactoring.classnodeprovider.ClassNodeProvider;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.core.renamemethod.methoditems.CallCandidateItem;
import org.rubypeople.rdt.refactoring.core.renamemethod.methoditems.MethodNameArgumentItem;
import org.rubypeople.rdt.refactoring.core.renamemethod.methoditems.SymbolItem;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;
import org.rubypeople.rdt.refactoring.editprovider.FileEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.FileMultiEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.IMultiFileEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.MultiFileEditProvider;
import org.rubypeople.rdt.refactoring.exception.NoClassNodeException;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.INodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.MethodCallNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.MethodNodeWrapper;

public class MethodRenamer implements IMultiFileEditProvider {
	
	private RenameMethodConfig config;
	private DocumentProvider docProvider;
	private MultiFileEditProvider fileEdits;
	
	public Collection<String> getAllMethodsFromClass() {
		Collection<String> names = new ArrayList<String>();
		try {
			for(MethodNodeWrapper method : config.getAllMethodsInClass()) {
				names.add(method.getName());
			}
		} catch (NoClassNodeException e) {
			/*we don't care*/
		}
		return names;
	}
	
	public MethodRenamer(RenameMethodConfig config){
		this.config = config;
		this.docProvider = config.getDocProvider();
		fileEdits = new MultiFileEditProvider();		
		
		Collection<MethodCallNodeWrapper> probableClass = getCallCandidatesInClass();
		probableClass.addAll(getSubsequentCalls());
		config.setSelectedCalls(probableClass);
	}



	public Collection<FileMultiEditProvider> getFileEditProviders(){
			
		addDefinitionRenamer();
		
		addCallRenamers();
		
		if(!config.getTargetMethod().isClassMethod()){
			addSymbolRenamers();
		}
		
		return fileEdits.getFileEditProviders();
	}

	private void addSymbolRenamers() {
		if(config.getTargetMethod().isClassMethod()){
			return;
		}
		String file = docProvider.getActiveFileName();
		for(SymbolNode currentNode : getSymbolCandidatesInClass()){
			SymbolItem currentItem = new SymbolItem(currentNode);
			fileEdits.addEditProvider(new FileEditProvider(file, new MethodRenameEditProvider(currentItem, config.getNewName())));
		}
	}

	private void addCallRenamers() {
		
		for(INodeWrapper currentCandidate : config.getSelectedCalls()){
			String file = currentCandidate.getWrappedNode().getPosition().getFile();
			//Were expecting only MethodCallNodeWrappers in this refactoring
			CallCandidateItem candidateItem = new CallCandidateItem((MethodCallNodeWrapper)currentCandidate);
			fileEdits.addEditProvider(new FileEditProvider(file, new MethodRenameEditProvider(candidateItem, config.getNewName())));
		}
	}

	private void addDefinitionRenamer() {
		if(config.getSelectedClass()==null || config.getTargetMethod().isClassMethod()){
			String file = docProvider.getActiveFileName();
			MethodNameArgumentItem argumentItem = new MethodNameArgumentItem(config.getTargetMethod().getWrappedNode().getNameNode());
			fileEdits.addEditProvider(new FileEditProvider(file, new MethodRenameEditProvider(argumentItem, config.getNewName())));	
		}
		else {
			for(ClassNodeWrapper currentClassNode : findRelatedClasses()){
				MethodNodeWrapper currentMethodDef = currentClassNode.getMethod(config.getTargetMethod().getName());
				if(currentMethodDef == null){
					continue;
				}
				String currentFile = currentMethodDef.getPosition().getFile();
				MethodNameArgumentItem argumentItem = new MethodNameArgumentItem(currentMethodDef.getWrappedNode().getNameNode());
				fileEdits.addEditProvider(new FileEditProvider(currentFile, new MethodRenameEditProvider(argumentItem, config.getNewName())));
			}
		}
	}

	private ArrayList<ClassNodeWrapper> findRelatedClasses() {
		ClassNodeProvider projectClassProvider = docProvider.getProjectClassNodeProvider();
		
		ArrayList<ClassNodeWrapper> relatedClasses = new ArrayList<ClassNodeWrapper>();
		relatedClasses.addAll(projectClassProvider.getClassAndAllSuperClassesFor(config.getSelectedClass().getName()));
		relatedClasses.addAll(projectClassProvider.getSubClassesOf(config.getSelectedClass().getName()));
		return relatedClasses;
	}

	public NodeSelector getConfig() {
		return config;
	}
	
	public Collection<MethodCallNodeWrapper> getCallCandidatesInClass(){
		
		ArrayList<MethodCallNodeWrapper> callCandidates = new ArrayList<MethodCallNodeWrapper>();

		if(config.getSelectedClass() != null){
			for(ClassNodeWrapper currentClass : findRelatedClasses()){
				callCandidates.addAll(config.getTargetMethod().getCallCandidatesInClass(currentClass));
			}
		}
		return callCandidates;
	}
	
	public Collection<SymbolNode> getSymbolCandidatesInClass(){
		return config.getTargetMethod().getSymbolCandidatesInClass(config.getSelectedClass());
	}
	
	public Collection<MethodCallNodeWrapper> getSubsequentCalls(){
		Node fileRoot = docProvider.getRootNode(); 
		int methodEndPos = config.getTargetMethod().getWrappedNode().getPosition().getEndOffset();
		ArrayList<MethodCallNodeWrapper> subsequentCalls = new ArrayList<MethodCallNodeWrapper>();
		
		if(SelectionNodeProvider.getSelectedNodeOfType(fileRoot, methodEndPos, ClassNode.class, SClassNode.class) != null){
			return subsequentCalls;
		}
		Collection<Node> callNodes = NodeProvider.getSubNodes(fileRoot, CallNode.class, VCallNode.class, FCallNode.class);
		for(Node currentNode : callNodes){
			if(currentNode.getPosition().getStartOffset() >= methodEndPos){
				MethodCallNodeWrapper currentCall = new MethodCallNodeWrapper(currentNode);
				if(currentCall.getName().equals(config.getTargetMethod().getName()) && !hasDefsInEnclosingClass(currentCall, fileRoot)){
					subsequentCalls.add(currentCall);
				}
			}
		}
		return subsequentCalls;
	}

	private boolean hasDefsInEnclosingClass(MethodCallNodeWrapper currentCall, Node rootNode) {
		int position = currentCall.getWrappedNode().getPosition().getStartOffset();
		try {
			ClassNodeWrapper classNode = SelectionNodeProvider.getSelectedClassNode(rootNode, position);
			for(MethodNodeWrapper currentMethod : classNode.getMethods()){
				if(currentCall.getName().equals(currentMethod.getName())){
					return true;
				}
			}
			return false;
		} catch (NoClassNodeException e) {
			return false;
		}
	}
}

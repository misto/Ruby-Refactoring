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

package org.rubypeople.rdt.refactoring.core.inlineclass;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.AssignableNode;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.refactoring.classnodeprovider.ClassNodeProvider;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;
import org.rubypeople.rdt.refactoring.exception.NoClassNodeException;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.MethodNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.PartialClassNodeWrapper;
import org.rubypeople.rdt.refactoring.util.Constants;

public class InlineClassConditionChecker extends RefactoringConditionChecker{

	
	private InlineClassConfig config;
	private DocumentProvider docProvider;
	private ClassNodeWrapper selectedClass;

	public InlineClassConditionChecker(InlineClassConfig config) {
		super(config.getDocProvider(), config);
	}
	
	public void init(Object configObj) {
		this.config = (InlineClassConfig) configObj;
		docProvider = config.getDocProvider();
		intiSourceClass();
		initPossibleTargetClasses();
	}

	private void intiSourceClass() {
		int caretPosition = config.getCaretPosition();
		Node rootNode = docProvider.getActiveFileRootNode();
		try {
			selectedClass = SelectionNodeProvider.getSelectedClassNode(rootNode, caretPosition);
			config.setSourceClass(selectedClass);
		} catch (NoClassNodeException e) {

		}
	}

	@Override
	protected void checkFinalConditions() {
		if(equalClassPartPosition(selectedClass.getFirstPartialClassNode(), config.getTargetClassPart())){
			addError("Cannot inline the selected class part into itself.");
		}
	}

	@Override
	protected void checkInitialConditions() {

		if(selectedClass == null){
			addError("There is no class selected to inline.");
			return;
		}

		ClassNodeProvider classesProvider = docProvider.getProjectClassNodeProvider();
		Collection<PartialClassNodeWrapper> inlinedClass = classesProvider.getClassNode(selectedClass.getName()).getPartialClassNodes();
		if(inlinedClass.size() > 1){
			addError("The selected class cannot be inlined as its is spread over several class parts.");
		}
		
		if(classesProvider.getSubClassesOf(selectedClass.getName()).size() > 0){
			addError("The inlne target is subclassed and thus cannot be inlined.");
		}
		
		if(!selectedClass.getSuperClassName().equals(Constants.OBJECT_NAME)) {
			addError("The selected class is derived from another class and thus cannot be inlined.");
		}
		
		if(config.getPossibleTargetClasses().isEmpty()){
			addError("There must be at least one class that creates an instance of the selected class in the constructor and keeps it in a field.");
		}

	}
	
	public void initPossibleTargetClasses(){
		ClassNodeProvider classesProvider = config.getDocProvider().getIncludedClassNodeProvider();
		Collection<ClassNodeWrapper> classNodes = classesProvider.getAllClassNodes();
		
		ArrayList<ClassNodeWrapper> possibleClassNodes = new ArrayList<ClassNodeWrapper>();
		
		Node rootNode = config.getDocProvider().getActiveFileRootNode();
		try {
			ClassNodeWrapper selectedClass = SelectionNodeProvider.getSelectedClassNode(rootNode, config.getCaretPosition());
			for(ClassNodeWrapper currentClass : classNodes){
				if(isPossibleTarget(selectedClass, currentClass)){
					possibleClassNodes.add(currentClass);
				}
			}
		} catch (NoClassNodeException e) {
			/*don't care*/
		}
		config.setPossibleTargetClasses(possibleClassNodes);
	}

	private boolean isPossibleTarget(ClassNodeWrapper selectedClass, ClassNodeWrapper targetClass) {
		String inlinedClassName = selectedClass.getName();
		String targetClassName = targetClass.getName();
		
		if(targetClassName.equals(inlinedClassName)){
			return false;
		}
		
		MethodNodeWrapper constructor = targetClass.getConstructorNode();
		Collection<AssignableNode> matchingAssignmentsFound = config.findFieldAsgnsOfSource(constructor);
		
		if(matchingAssignmentsFound.isEmpty()){
			return false;
		}
		
		return true;
	}
	
	private boolean equalClassPartPosition(PartialClassNodeWrapper part1, PartialClassNodeWrapper part2){
		ISourcePosition first = part1.getWrappedNode().getPosition();
		ISourcePosition second = part2.getWrappedNode().getPosition();
		return (first.getFile().equals(second.getFile()) && first.getStartOffset() == second.getStartOffset() && first.getEndOffset() == second.getEndOffset());
	}
}

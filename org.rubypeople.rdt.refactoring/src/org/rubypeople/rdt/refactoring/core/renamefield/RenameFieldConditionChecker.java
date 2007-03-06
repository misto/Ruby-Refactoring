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

package org.rubypeople.rdt.refactoring.core.renamefield;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.CallNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.rubypeople.rdt.refactoring.classnodeprovider.ClassNodeProvider;
import org.rubypeople.rdt.refactoring.classnodeprovider.IncludedClassesProvider;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.core.renamefield.fielditems.FieldCallItem;
import org.rubypeople.rdt.refactoring.core.renamefield.fielditems.FieldItem;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentWithIncluding;
import org.rubypeople.rdt.refactoring.exception.NoClassNodeException;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.FieldNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.PartialClassNodeWrapper;

public class RenameFieldConditionChecker extends RefactoringConditionChecker {

	public static final String DEFAULT_ERROR = Messages.RenameFieldConditionChecker_NoFieldAtCaretPosition;

	private RenameFieldConfig config;

	private RootNode rootNode;

	public RenameFieldConditionChecker(RenameFieldConfig config) {
		super(config.getDocProvider(), config);
	}

	public void init(Object configObj) {
		this.config = (RenameFieldConfig) configObj;

		config.setDocProvider(new DocumentWithIncluding(config.getDocProvider()));
		rootNode = config.getDocProvider().getActiveFileRootNode();

		try {
			ClassNodeWrapper enclosingClassNode = SelectionNodeProvider.getSelectedClassNode(rootNode, config.getCaretPosition());
			ClassNodeProvider classNodeProvider = new IncludedClassesProvider(config.getDocProvider());
			config.setWholeClassNode(classNodeProvider.getClassNode(enclosingClassNode.getName()));
			config.setFieldProvider(new FieldProvider(config.getWholeClassNode(), config.getDocProvider()));
			config.setSelectedItem(config.getFieldProvider()
					.getNameAtPosition(config.getCaretPosition(), config.getDocProvider().getActiveFileName()));
			if (config.hasSelectedItem()) {
				config.setSelectedName(config.getSelectedItem().getFieldName());
			}

		} catch (NoClassNodeException e) {
			/* don't care */
		}

		if (config.hasSelectedName()) {
			setSelection();
		}
	}

	private void setSelection() {
		String fieldName = config.getSelectedName();
		boolean concernsClassField = config.concernsClassField();
		Collection<FieldItem> selectedItems = config.getFieldProvider().getFieldItems(fieldName, concernsClassField);
		config.setSelectedCalls(selectedItems);

		Collection<FieldItem> possibleItems = new ArrayList<FieldItem>();
		possibleItems.addAll(selectedItems);

		if (!concernsClassField) {
			possibleItems.addAll(getInstVarAccesses());
		}

		config.setPossibleCalls(possibleItems);
	}

	private Collection<FieldItem> getInstVarAccesses() {
		ArrayList<FieldItem> fieldCallNodes = new ArrayList<FieldItem>();

		Collection<Node> allNodes = NodeProvider.getAllNodes(rootNode);
		for (Node currentNode : allNodes) {
			if (isPossibleCall(currentNode)) {
				fieldCallNodes.add(new FieldCallItem((CallNode) currentNode));
			}
		}

		return fieldCallNodes;
	}

	private boolean isPossibleCall(Node candidateNode) {
		if ((candidateNode instanceof CallNode)) {

			CallNode callNode = (CallNode) candidateNode;
			if (callNode.getName().replaceAll("=", "").equals(config.getSelectedName())) { //$NON-NLS-1$ //$NON-NLS-2$
				String fileName = callNode.getPosition().getFile();
				Node rootNode = config.getDocProvider().getRootNode(fileName);
				try {
					SelectionNodeProvider.getSelectedClassNode(rootNode, callNode.getPosition().getStartOffset());
				} catch (NoClassNodeException e) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	protected void checkFinalConditions() {
		String newName = config.getNewName();
		String selectedName = config.getSelectedName();

		if (newName == null || selectedName.equals(newName)) {
			addError(Messages.RenameFieldConditionChecker_NoNewName);
			return;
		}

		for (String currentName : config.getFieldNames()) {
			if (currentName.equals(newName)) {
				addError(Messages.RenameFieldConditionChecker_AlreadyExists);
				return;
			}
		}
	}

	@Override
	protected void checkInitialConditions() {
		Collection<FieldNodeWrapper> fields = PartialClassNodeWrapper.getFieldsFromNode(config.getDocProvider().getActiveFileRootNode());
		FieldNodeWrapper selectedFieldNode = SelectionNodeProvider.getSelectedWrappedNode(fields, config.getCaretPosition());
		if (config.getWholeClassNode() == null) {
			if (selectedFieldNode != null) {
				addError(Messages.RenameFieldConditionChecker_CannotNoSurroundingClass);
				return;
			}
		}
		if (!config.hasSelectedName() || !isSelectionInFieldName(selectedFieldNode)) {
			addError(DEFAULT_ERROR);
		}
	}

	private boolean isSelectionInFieldName(FieldNodeWrapper node) {
		return config.getCaretPosition() <= node.getPosition().getStartOffset() + node.getName().length();
	}
}

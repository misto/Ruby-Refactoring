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

import org.jruby.ast.SymbolNode;
import org.rubypeople.rdt.refactoring.core.rename.RenameConfig;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.INodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.MethodNodeWrapper;

public class RenameMethodConfig extends RenameConfig implements NodeSelector {

	private Collection<? extends INodeWrapper> renamedCalls;
	
	private Collection<? extends INodeWrapper> possibleCalls;

	private Collection<SymbolNode> symbolCandidate;
	
	private ClassNodeWrapper classNode;

	private MethodNodeWrapper targetMethod;

	private boolean renameFields = true;

	public RenameMethodConfig(IDocumentProvider docProvider, int caretPosition) {
		super(docProvider, caretPosition);
		this.renamedCalls = new ArrayList<INodeWrapper>();
		this.possibleCalls = new ArrayList<INodeWrapper>();
	}

	public void setSelectedCalls(Collection<? extends INodeWrapper> callCandidates){
		this.renamedCalls = callCandidates;
	}
	
	public Collection<? extends INodeWrapper> getSelectedCalls(){
		return renamedCalls;
	}
	
	public void setRenamedSymbols(Collection<SymbolNode> symbolCandidate){
		this.symbolCandidate = symbolCandidate;
	}
	
	public Collection<SymbolNode> getRenamedSymbols(){
		return symbolCandidate;
	}

	public void setClassNode(ClassNodeWrapper selectedClassNode) {
		classNode = selectedClassNode;
	}

	public void setTargetMethod(MethodNodeWrapper selectedMethod) {
		this.targetMethod = selectedMethod;
	}

	public MethodNodeWrapper getTargetMethod() {
		return targetMethod;
	}

	public ClassNodeWrapper getSelectedClass() {
		return classNode;
	}

	public Collection<? extends INodeWrapper> getPossibleCalls() {
		return possibleCalls;
	}
	
	public void setPossibleCalls(Collection<? extends INodeWrapper> possibleCalls){
		this.possibleCalls = possibleCalls;
	}

	public boolean renameFields() {
		return renameFields;
	}

	public void setRenameFields(boolean renameFields) {
		this.renameFields = renameFields;
	}
}

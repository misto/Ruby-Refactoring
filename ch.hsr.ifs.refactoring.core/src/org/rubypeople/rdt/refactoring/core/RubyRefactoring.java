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

package org.rubypeople.rdt.refactoring.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.Refactoring;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.rubypeople.rdt.refactoring.RefactoringPlugin;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;
import org.rubypeople.rdt.refactoring.documentprovider.WorkspaceDocumentProvider;
import org.rubypeople.rdt.refactoring.editprovider.FileMultiEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.FileNameChangeProvider;
import org.rubypeople.rdt.refactoring.editprovider.IEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.IMultiFileEditProvider;

public abstract class RubyRefactoring extends Refactoring {

	protected RefactoringStatus initialStatus;

	protected RefactoringStatus finalStatus;

	private String name;

	protected Collection<IWizardPage> pages;

	private IEditProvider editProvider;
	
	private IMultiFileEditProvider multiFileEditProvider;

	private IRefactoringConditionChecker conditionChecker;

	private FileNameChangeProvider fileNameChangeProvider;

	private IFile activeFile;

	public RubyRefactoring(String name) {
		this.name = name;
		initialStatus = new RefactoringStatus();
		finalStatus = new RefactoringStatus();
		pages = new ArrayList<IWizardPage>();
		activeFile = RefactoringPlugin.getRefactoringObjectFactory().getActiveFile();
	}

	protected IFile getActiveFile() {
		return activeFile;
	}

	protected void setEditProvider(IEditProvider editProvider) {
		this.editProvider = editProvider;
	}

	protected void setEditProvider(IMultiFileEditProvider multiFileEditProvider) {
		this.multiFileEditProvider = multiFileEditProvider;
	}
	
	protected void setFileNameChangeProvider(FileNameChangeProvider fileNameChangeProvider) {
		this.fileNameChangeProvider = fileNameChangeProvider;
	}

	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public RefactoringStatus checkInitialConditions(IProgressMonitor pm) {
		if (conditionChecker != null) {
			Map<String, Collection<String>> messages = conditionChecker.getInitialMessages();
			for (String errMessage : messages.get(IRefactoringConditionChecker.ERRORS)) {
				initialStatus.addFatalError(errMessage);
			}
			Collection<String> warnings = messages.get(IRefactoringConditionChecker.ERRORS);
			for (String warningMessage : warnings) {
				initialStatus.addWarning(warningMessage);
			}
		}
		return initialStatus;
	}

	@Override
	public RefactoringStatus checkFinalConditions(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		finalStatus = new RefactoringStatus();
		if (conditionChecker != null) {
			Map<String, Collection<String>> messages = conditionChecker.getFinalMessages();
			Collection<String> errors = messages.get(IRefactoringConditionChecker.ERRORS);
			for (String errMessage : errors) {
				finalStatus.addFatalError(errMessage);
			}
			Collection<String> warnings = messages.get(IRefactoringConditionChecker.WARNING);
			for (String warningMessage : warnings) {
				finalStatus.addWarning(warningMessage);
			}
		}
		return finalStatus;
	}

	@Override
	public Change createChange(IProgressMonitor pm) throws CoreException, OperationCanceledException {
		Change change = createEditChanges();

		Map<String, String> filesToRename = getFileNameChangeProvider().getFilesToRename(getAllAffectedFiles(change));
		if(filesToRename.isEmpty()) {
			return change;
		} 
		
		CompositeChange compositeChange;
		
		if (change.getAffectedObjects().length <= 0) {
			compositeChange = new CompositeChange(getName(), new Change[]{null});
		} else {
			compositeChange = new CompositeChange(getName(), new Change[]{change});
		}
		
		compositeChange.markAsSynthetic();
		for (Map.Entry<String, String> entry : filesToRename.entrySet()) {
			compositeChange.add(createDynamicValidationChange(entry.getKey(), entry.getValue()));
		}
		return compositeChange;
	}

	private Change createDynamicValidationChange(String key, String value) {
		return RefactoringPlugin.getRefactoringObjectFactory().createDynamicValidationChange(key, value);
	}

	private Collection<String> getAllAffectedFiles(Change change) {
		Collection<String> affectedFiles = new ArrayList<String>();
		for (Object object : change.getAffectedObjects()) {
			if (object instanceof File) {
				affectedFiles.add(((File) object).getFullPath().toString());
			}
		}
		return affectedFiles;
	}

	private Change createEditChanges() {
		DocumentProvider docProvider = new WorkspaceDocumentProvider(getActiveFile());

		if(multiFileEditProvider != null) {
			return createMultiFileChange(docProvider);
		}
		return createActiveFileChange(docProvider);
	}

	private Change createActiveFileChange(DocumentProvider docProvider) {
		return getChange(getActiveFile(), editProvider, docProvider);
	}

	private Change createMultiFileChange(DocumentProvider docProvider) {
		CompositeChange change = new CompositeChange(name);
		for (FileMultiEditProvider currentProvider : multiFileEditProvider.getFileEditProviders()) {
			IFile currentIFile = getDocumentProvider().getIFile(currentProvider.getFileName());
			change.add(getChange(currentIFile, currentProvider, docProvider));
		}
		return change;
	}
	
	private Change getChange(IFile file, IEditProvider editProvider, DocumentProvider docProvider) {
		String fileName = file.getFullPath().toOSString();
		RubyTextFileChange change = new RubyTextFileChange(fileName, file);
		String document = docProvider.getFileContent(fileName);
		change.setEdit(editProvider.getEdit(document));
		return change;
	}

	public Collection<IWizardPage> getPages() {
		return pages;
	}

	public WorkspaceDocumentProvider getDocumentProvider() {
		return new WorkspaceDocumentProvider(getActiveFile());
	}

	protected void setRefactoringConditionChecker(IRefactoringConditionChecker conditionChecker) {
		this.conditionChecker = conditionChecker;
	}

	public IRefactoringConditionChecker getConditionChecker() {
		return conditionChecker;
	}

	public IEditProvider getEditProvider() {
		return editProvider;
	}

	public IMultiFileEditProvider getMultiFileEditProvider() {
		return multiFileEditProvider;
	}

	public FileNameChangeProvider getFileNameChangeProvider() {
		return fileNameChangeProvider != null ? fileNameChangeProvider : new FileNameChangeProvider();
	}
}

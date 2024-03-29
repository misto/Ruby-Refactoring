/*******************************************************************************
 * Copyright (c) 2006 Institute for Software, HSR Hochschule fr Technik  
 * Rapperswil, University of applied sciences
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 * Emanuel Graf & Leo Buettiker - initial API and implementation 
 ******************************************************************************/
package org.rubypeople.rdt.refactoring.core;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.ChangeDescriptor;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.TextEditBasedChangeGroup;
import org.eclipse.ltk.core.refactoring.TextEditChangeGroup;
import org.eclipse.ltk.core.refactoring.TextFileChange;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.TextEditGroup;

/**
 * @author Emanuel Graf
 *
 */
public class RubyTextFileChange extends Change {
	
	private TextFileChange change;

	/**
	 * @param name
	 * @param file
	 */
	public RubyTextFileChange(String name, IFile file) {
		change = new TextFileChange(name, file);
	}

	@Override
	public Object getModifiedElement() {
		return change.getModifiedElement();
	}

	@Override
	public String getName() {
		return change.getName();
	}

	@Override
	public void initializeValidationData(IProgressMonitor pm) {
		change.initializeValidationData(pm);		
	}

	@Override
	public Change perform(IProgressMonitor pm) throws CoreException {
		return change.perform(pm);
	}
	
	public void setEdit(TextEdit edit) {
		change.setEdit(edit);
	}

	public TextEdit getEdit() {
		return change.getEdit();
	}
	
	public void addTextEditGroup(TextEditBasedChangeGroup group) {
		change.addChangeGroup(group);
	}
	
	public  String getCurrentContent(IProgressMonitor pm) throws CoreException{
		return change.getCurrentContent(pm);
	}
	
	public  String getPreviewContent(IProgressMonitor pm) throws CoreException{
		return change.getPreviewContent(pm);
	}
	public String getTextType() {
		return change.getTextType();
	}
	
	public IFile getFile() {
		return change.getFile();
	}

	public void addChangeGroup(TextEditBasedChangeGroup group) {
		change.addChangeGroup(group);
	}

	public void addEdit(TextEdit edit) throws MalformedTreeException {
		change.addEdit(edit);
	}

	public void addTextEditChangeGroup(TextEditChangeGroup group) {
		change.addTextEditChangeGroup(group);
	}

	public void addTextEditGroup(TextEditGroup group) {
		change.addTextEditGroup(group);
	}

	public void dispose() {
		change.dispose();
	}

	public boolean equals(Object obj) {
		return change.equals(obj);
	}

	@SuppressWarnings("unchecked") // Can't change the interface
	public Object getAdapter(Class adapter) {
		return change.getAdapter(adapter);
	}

	public Object[] getAffectedObjects() {
		return change.getAffectedObjects();
	}

	public String getCurrentContent(IRegion region, boolean expandRegionToFullLine, int surroundingLines, IProgressMonitor pm) throws CoreException {
		return change.getCurrentContent(region, expandRegionToFullLine, surroundingLines, pm);
	}

	public IDocument getCurrentDocument(IProgressMonitor pm) throws CoreException {
		return change.getCurrentDocument(pm);
	}

	public ChangeDescriptor getDescriptor() {
		return change.getDescriptor();
	}

	public boolean getKeepPreviewEdits() {
		return change.getKeepPreviewEdits();
	}

	public Change getParent() {
		return change.getParent();
	}

	public String getPreviewContent(TextEditBasedChangeGroup[] changeGroups, IRegion region, boolean expandRegionToFullLine, int surroundingLines, IProgressMonitor pm) throws CoreException {
		return change.getPreviewContent(changeGroups, region, expandRegionToFullLine, surroundingLines, pm);
	}

	public String getPreviewContent(TextEditChangeGroup[] changeGroups, IRegion region, boolean expandRegionToFullLine, int surroundingLines, IProgressMonitor pm) throws CoreException {
		return change.getPreviewContent(changeGroups, region, expandRegionToFullLine, surroundingLines, pm);
	}

	public IDocument getPreviewDocument(IProgressMonitor pm) throws CoreException {
		return change.getPreviewDocument(pm);
	}

	public TextEdit getPreviewEdit(TextEdit original) {
		return change.getPreviewEdit(original);
	}

	public TextEdit[] getPreviewEdits(TextEdit[] originals) {
		return change.getPreviewEdits(originals);
	}

	public int getSaveMode() {
		return change.getSaveMode();
	}

	public TextEditChangeGroup[] getTextEditChangeGroups() {
		return change.getTextEditChangeGroups();
	}

	public int hashCode() {
		return change.hashCode();
	}

	@SuppressWarnings("unchecked") // Can't change the interface
	public boolean hasOneGroupCategory(List groupCategories) {
		return change.hasOneGroupCategory(groupCategories);
	}

	public boolean isEnabled() {
		return change.isEnabled();
	}

	public RefactoringStatus isValid(IProgressMonitor pm) throws CoreException {
		return change.isValid(pm);
	}

	public void setEnabled(boolean enabled) {
		change.setEnabled(enabled);
	}

	public void setKeepPreviewEdits(boolean keep) {
		change.setKeepPreviewEdits(keep);
	}

	public void setSaveMode(int saveMode) {
		change.setSaveMode(saveMode);
	}

	public void setTextType(String type) {
		change.setTextType(type);
	}

	public String toString() {
		return change.toString();
	}	

}

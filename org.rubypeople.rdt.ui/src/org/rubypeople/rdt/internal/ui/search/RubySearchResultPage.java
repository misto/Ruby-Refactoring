/*
 * Author: Markus
 * 
 * Copyright (c) 2005 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT is
 * subject to the "Common Public License (CPL) v 1.0". You may not use RDT except in 
 * compliance with the License. For further information see org.rubypeople.rdt/rdt.license.
 * 
 */

package org.rubypeople.rdt.internal.ui.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.search.internal.ui.text.FileSearchPage.DecoratorIgnoringViewerSorter;
import org.eclipse.search.ui.text.AbstractTextSearchViewPage;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.internal.core.symbols.SearchResult;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubySearchResultPage extends AbstractTextSearchViewPage {

	RubySearchTreeContentProvider fContentProvider;
	GroupByAction fGroupByPackage;
	GroupByAction fGroupByFile;

	public RubySearchResultPage() {

	}

	protected void elementsChanged(Object[] objects) {
		if (fContentProvider != null) {
			fContentProvider.elementsChanged(objects);
		}
	}

	protected void clear() {
	// TODO Auto-generated method stub
	}

	protected void configureTreeViewer(TreeViewer viewer) {
		viewer.setUseHashlookup(true);
		RubySearchLabelProvider innerLabelProvider = new RubySearchLabelProvider();
		viewer.setLabelProvider(new DecoratingLabelProvider(innerLabelProvider, PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator()));
		fContentProvider = new RubySearchTreeContentProvider(viewer);
		viewer.setContentProvider(fContentProvider);
		viewer.setSorter(new DecoratorIgnoringViewerSorter(innerLabelProvider));
		// TODO: addDragAdapters(viewer);
	}

	protected void configureTableViewer(TableViewer viewer) {
	// TODO: When is a table viewer needed ?
	}

	protected IEditorPart openEditor(Match match, boolean activate) {
		// TODO: like OpenEditor: consider NewSearchUI.reuseEditor() ?

		IWorkbenchPage page = RubyPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
		try {
			SearchResult result = (SearchResult) match.getElement();
			IPath sourcePath = result.getLocation().getSourceFile().getFullPath();
			IResource resource = RubyPlugin.getWorkspace().getRoot().findMember(sourcePath);

			IFile file = (IFile) resource.getAdapter(org.eclipse.core.resources.IFile.class);
			if (file == null) {
				RubyPlugin.log("Couldn't convert search result to file: " + resource.getFullPath());
			} else {
				return IDE.openEditor(page, file, activate);
			}
		} catch (PartInitException e) {
			RubyPlugin.log(e);
		}
		return null;
	}

	protected void showMatch(Match match, int offset, int length, boolean activate) throws PartInitException {
		// TODO: show search marker
		IEditorPart editor = openEditor(match, activate);
		if (offset != 0 && length != 0) {
			if (editor instanceof ITextEditor) {
				ITextEditor textEditor = (ITextEditor) editor;
				textEditor.selectAndReveal(offset, length);
			}
		}
	}

	protected void fillToolbar(IToolBarManager tbm) {
		super.fillToolbar(tbm);
		fGroupByFile = new GroupByAction("groupByFile", "file_mode.gif") {

			public void run() {
				fContentProvider.setGroupByPath();
				getViewer().refresh();
				fGroupByPackage.setChecked(false);
				fGroupByFile.setChecked(true);
			}
		};
		fGroupByFile.setChecked(true);

		fGroupByPackage = new GroupByAction("groupByPackage", "package_mode.gif") {

			public void run() {
				fContentProvider.setGroupByScope();
				getViewer().refresh();
				fGroupByPackage.setChecked(true);
				fGroupByFile.setChecked(false);
			}
		};
		fGroupByPackage.setChecked(false);

		tbm.add(fGroupByFile);
		tbm.add(fGroupByPackage);

	}
}

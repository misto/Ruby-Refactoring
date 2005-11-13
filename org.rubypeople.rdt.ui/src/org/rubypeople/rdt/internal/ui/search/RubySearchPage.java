/* Copyright (c) 2005 RubyPeople.
 * 
 * Author: Markus
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT
 * is subject to the "Common Public License (CPL) v 1.0". You may not use RDT
 * except in compliance with the License. For further information see
 * org.rubypeople.rdt/rdt.license.
 * 
 * This file is based on
 * org.eclipse.jdt.internal.ui.search.JavaSearchPage 
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 */

package org.rubypeople.rdt.internal.ui.search;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.Assert;
import org.eclipse.search.internal.core.SearchScope;
import org.eclipse.search.internal.ui.Messages;
import org.eclipse.search.internal.ui.ScopePart;
import org.eclipse.search.internal.ui.SearchMessages;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.core.symbols.ISymbolTypes;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;

public class RubySearchPage extends DialogPage implements ISearchPage, ISymbolTypes {

	// Shouldn't SearchPatternData be a public class in org.eclipse.search ?
	private static class SearchPatternData {

		private int searchFor;
		private int limitTo;
		private String pattern;
		private boolean isCaseSensitive;

		private int scope;
		private IWorkingSet[] workingSets;

		public SearchPatternData(int searchFor, int limitTo, boolean isCaseSensitive, String pattern) {
			this(searchFor, limitTo, pattern, isCaseSensitive, ISearchPageContainer.WORKSPACE_SCOPE, null);
		}

		public SearchPatternData(int searchFor, int limitTo, String pattern, boolean isCaseSensitive, int scope, IWorkingSet[] workingSets) {
			this.searchFor = searchFor;
			this.limitTo = limitTo;
			this.pattern = pattern;
			this.isCaseSensitive = isCaseSensitive;
			this.scope = scope;
			this.workingSets = workingSets;

		}

		public boolean isCaseSensitive() {
			return isCaseSensitive;
		}

		public int getLimitTo() {
			return limitTo;
		}

		public String getPattern() {
			return pattern;
		}

		public int getScope() {
			return scope;
		}

		public int getSearchFor() {
			return searchFor;
		}

		public IWorkingSet[] getWorkingSets() {
			return workingSets;
		}

		public void store(IDialogSettings settings) {
			settings.put("searchFor", searchFor); //$NON-NLS-1$
			settings.put("scope", scope); //$NON-NLS-1$
			settings.put("pattern", pattern); //$NON-NLS-1$
			settings.put("limitTo", limitTo); //$NON-NLS-1$
			// TODO
			// settings.put("rubyElement", rubyElement != null ?
			// rubyElement.getHandleIdentifier() : ""); //$NON-NLS-1$
			// //$NON-NLS-2$
			settings.put("isCaseSensitive", isCaseSensitive); //$NON-NLS-1$
			if (workingSets != null) {
				String[] wsIds = new String[workingSets.length];
				for (int i = 0; i < workingSets.length; i++) {
					wsIds[i] = workingSets[i].getId();
				}
				settings.put("workingSets", wsIds); //$NON-NLS-1$
			} else {
				settings.put("workingSets", new String[0]); //$NON-NLS-1$
			}

		}

		public static SearchPatternData create(IDialogSettings settings) {
			String pattern = settings.get("pattern"); //$NON-NLS-1$
			if (pattern.length() == 0) { return null; }

			// handle the RubyElement

			// this is copied from JavaSearchPage
			String[] wsIds = settings.getArray("workingSets"); //$NON-NLS-1$
			IWorkingSet[] workingSets = null;
			if (wsIds != null && wsIds.length > 0) {
				IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
				workingSets = new IWorkingSet[wsIds.length];
				for (int i = 0; workingSets != null && i < wsIds.length; i++) {
					workingSets[i] = workingSetManager.getWorkingSet(wsIds[i]);
					if (workingSets[i] == null) {
						workingSets = null;
					}
				}
			}

			try {
				int searchFor = settings.getInt("searchFor"); //$NON-NLS-1$
				int scope = settings.getInt("scope"); //$NON-NLS-1$
				int limitTo = settings.getInt("limitTo"); //$NON-NLS-1$
				boolean isCaseSensitive = settings.getBoolean("isCaseSensitive"); //$NON-NLS-1$
				// TODO
				IRubyElement elem = null; // settings.get("rubyElement") ;
				return new SearchPatternData(searchFor, limitTo, pattern, isCaseSensitive, scope, workingSets);
			} catch (NumberFormatException e) {
				return null;
			}
		}

	}

	private static final int HISTORY_SIZE = 12;

	// Dialog store id constants
	private final static String PAGE_NAME = "JavaSearchPage"; //$NON-NLS-1$
	private final static String STORE_CASE_SENSITIVE = "CASE_SENSITIVE"; //$NON-NLS-1$
	private final static String STORE_HISTORY = "HISTORY"; //$NON-NLS-1$
	private final static String STORE_HISTORY_SIZE = "HISTORY_SIZE"; //$NON-NLS-1$

	private final List fPreviousSearchPatterns;

	private SearchPatternData fInitialData;
	private boolean fFirstTime = true;
	private IDialogSettings fDialogSettings;
	private boolean fIsCaseSensitive;

	private Combo fPattern;
	private ISearchPageContainer fContainer;
	private Button fCaseSensitive;

	private Button[] fLimitTo;
	private String[] fLimitToText = { "declarations" //$NON-NLS-1$
	/*
	 * SearchMessages.SearchPage_limitTo_declarations,
	 * SearchMessages.SearchPage_limitTo_implementors,
	 * SearchMessages.SearchPage_limitTo_references,
	 * SearchMessages.SearchPage_limitTo_allOccurrences
	 */};

	private Button fSearchJRE;
	private static final int INDEX_REFERENCES = 2;
	private static final int INDEX_ALL = 3;
	private SearchForManager searchForManager = new SearchForManager();

	/**
	 * 
	 */
	public RubySearchPage() {
		fPreviousSearchPatterns = new ArrayList();
	}

	// ---- Action Handling ------------------------------------------------

	public boolean performAction() {
		NewSearchUI.runQueryInBackground(getSearchQuery());
		return true;
	}

	private ISearchQuery getSearchQuery() {

		SearchPatternData patternData = getPatternData();

		// Setup search scope
		SearchScope scope = null;
		switch (getContainer().getSelectedScope()) {
		case ISearchPageContainer.WORKSPACE_SCOPE:
			scope = SearchScope.newWorkspaceScope();
			break;
		/*
		 * case ISearchPageContainer.SELECTION_SCOPE: scope=
		 * getSelectedResourcesScope(false); break; case
		 * ISearchPageContainer.SELECTED_PROJECTS_SCOPE: scope=
		 * getSelectedResourcesScope(true); break;
		 */
		case ISearchPageContainer.WORKING_SET_SCOPE:
			IWorkingSet[] workingSets = getContainer().getSelectedWorkingSets();
			String desc = Messages.format(SearchMessages.WorkingSetScope, ScopePart.toString(workingSets));
			scope = SearchScope.newSearchScope(desc, workingSets);
		}
		NewSearchUI.activateSearchResultView();

		return new RubySearchQuery(scope, patternData.getPattern(), patternData.getSearchFor());
	}

	private int getLimitTo() {
		// for (int i = 0; i < fLimitTo.length; i++) {
		// if (fLimitTo[i].getSelection()) return i;
		// }
		return -1;
	}

	private void setLimitTo(int searchFor, int limitTo) {
	/*
	 * if (!(searchFor == TYPE || searchFor == INTERFACE) && limitTo ==
	 * IMPLEMENTORS) { limitTo= REFERENCES; }
	 * 
	 * if (!(searchFor == FIELD) && (limitTo == READ_ACCESSES || limitTo ==
	 * WRITE_ACCESSES)) { limitTo= REFERENCES; }
	 * 
	 * for (int i= 0; i < fLimitTo.length; i++) {
	 * fLimitTo[i].setSelection(limitTo == i); }
	 * 
	 * fLimitTo[DECLARATIONS].setEnabled(true);
	 * fLimitTo[IMPLEMENTORS].setEnabled(searchFor == INTERFACE || searchFor ==
	 * TYPE); fLimitTo[REFERENCES].setEnabled(true);
	 * fLimitTo[ALL_OCCURRENCES].setEnabled(true);
	 * fLimitTo[READ_ACCESSES].setEnabled(searchFor == FIELD);
	 * fLimitTo[WRITE_ACCESSES].setEnabled(searchFor == FIELD);
	 */
	}

	private String[] getPreviousSearchPatterns() {
		// Search results are not persistent
		int patternCount = fPreviousSearchPatterns.size();
		String[] patterns = new String[patternCount];
		for (int i = 0; i < patternCount; i++)
			patterns[i] = ((SearchPatternData) fPreviousSearchPatterns.get(i)).getPattern();
		return patterns;
	}

	private String getPattern() {
		return fPattern.getText();
	}

	private SearchPatternData findInPrevious(String pattern) {
		for (Iterator iter = fPreviousSearchPatterns.iterator(); iter.hasNext();) {
			SearchPatternData element = (SearchPatternData) iter.next();
			if (pattern.equals(element.getPattern())) { return element; }
		}
		return null;
	}

	/**
	 * Return search pattern data and update previous searches. An existing
	 * entry will be updated.
	 */
	private SearchPatternData getPatternData() {
		String pattern = getPattern();
		SearchPatternData match = findInPrevious(pattern);
		if (match != null) {
			fPreviousSearchPatterns.remove(match);
		}
		match = new SearchPatternData(searchForManager.getSelectedSymbolType(), getLimitTo(), pattern, fCaseSensitive.getSelection(), getContainer().getSelectedScope(), getContainer().getSelectedWorkingSets());

		fPreviousSearchPatterns.add(0, match); // insert on top
		return match;
	}

	/*
	 * Implements method from IDialogPage
	 */
	public void setVisible(boolean visible) {
		if (visible && fPattern != null) {
			if (fFirstTime) {
				fFirstTime = false;
				// Set item and text here to prevent page from resizing
				fPattern.setItems(getPreviousSearchPatterns());
				// initSelections();
			}
			fPattern.setFocus();
		}
		// updateOKStatus();
		super.setVisible(visible);
	}

	public boolean isValid() {
		return true;
	}

	// ---- Widget creation ------------------------------------------------

	/**
	 * Creates the page's content.
	 */
	public void createControl(Composite parent) {
		initializeDialogUnits(parent);
		// readConfiguration();

		Composite result = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		result.setLayout(layout);

		Control expressionComposite = createExpression(result);
		expressionComposite.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false, 2, 1));

		Label separator = new Label(result, SWT.NONE);
		separator.setVisible(false);
		GridData data = new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1);
		data.heightHint = convertHeightInCharsToPixels(1) / 3;
		separator.setLayoutData(data);

		Control searchFor = searchForManager.createSearchFor(result);
		searchFor.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		Control limitTo = createLimitTo(result);
		limitTo.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1));

		// createParticipants(result);

		setControl(result);

		Dialog.applyDialogFont(result);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(result,
		// IJavaHelpContextIds.JAVA_SEARCH_PAGE);
	}

	/*
	 * private Control createParticipants(Composite result) { if
	 * (!SearchParticipantsExtensionPoint.hasAnyParticipants()) return new
	 * Composite(result, SWT.NULL); Button selectParticipants= new
	 * Button(result, SWT.PUSH);
	 * selectParticipants.setText(SearchMessages.getString("SearchPage.select_participants.label"));
	 * //$NON-NLS-1$ GridData gd= new GridData(); gd.verticalAlignment=
	 * GridData.VERTICAL_ALIGN_BEGINNING; gd.horizontalAlignment=
	 * GridData.HORIZONTAL_ALIGN_END; gd.grabExcessHorizontalSpace= false;
	 * gd.horizontalAlignment= GridData.END; gd.horizontalSpan= 2;
	 * selectParticipants.setLayoutData(gd);
	 * selectParticipants.addSelectionListener(new SelectionAdapter() { public
	 * void widgetSelected(SelectionEvent e) {
	 * PreferencePageSupport.showPreferencePage(getShell(),
	 * "org.eclipse.jdt.ui.preferences.SearchParticipantsExtensionPoint", new
	 * SearchParticipantsExtensionPoint()); //$NON-NLS-1$ }
	 * 
	 * }); return selectParticipants; }
	 */

	private Control createExpression(Composite parent) {
		Composite result = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		result.setLayout(layout);

		// Pattern text + info
		Label label = new Label(result, SWT.LEFT);
		// TODO
		label.setText("Expression"); //$NON-NLS-1$
		// label.setText(SearchMessages.SearchPage_expression_label);
		label.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));

		// Pattern combo
		fPattern = new Combo(result, SWT.SINGLE | SWT.BORDER);
		fPattern.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
			// handlePatternSelected();
			// updateOKStatus();
			}
		});
		fPattern.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				doPatternModified();
				// updateOKStatus();

			}
		});
		GridData data = new GridData(GridData.FILL, GridData.FILL, true, false, 1, 1);
		data.widthHint = convertWidthInCharsToPixels(50);
		fPattern.setLayoutData(data);

		// Ignore case checkbox
		fCaseSensitive = new Button(result, SWT.CHECK);
		// TODO
		fCaseSensitive.setText("CaseSensitive"); //$NON-NLS-1$
		fCaseSensitive.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				fIsCaseSensitive = fCaseSensitive.getSelection();
			}
		});
		fCaseSensitive.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 1, 1));

		return result;
	}

	private boolean isValidSearchPattern() {
		if (getPattern().length() == 0) { return false; }
		// TODO
		return true;
		// return SearchPattern.createPattern(getPattern(), getSearchFor(),
		// getLimitTo(), SearchPattern.R_EXACT_MATCH) != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.DialogPage#dispose()
	 */
	public void dispose() {
		// writeConfiguration();
		super.dispose();
	}

	private void doPatternModified() {
	/*
	 * if (fInitialData != null &&
	 * getPattern().equals(fInitialData.getPattern()) &&
	 * fInitialData.getJavaElement() != null && fInitialData.getSearchFor() ==
	 * getSearchFor()) { fCaseSensitive.setEnabled(false);
	 * fCaseSensitive.setSelection(true); fJavaElement=
	 * fInitialData.getJavaElement(); } else { fCaseSensitive.setEnabled(true);
	 * fCaseSensitive.setSelection(fIsCaseSensitive); fJavaElement= null; }
	 */
	}

	private Control createLimitTo(Composite parent) {
		Group result = new Group(parent, SWT.NONE);
		/*
		 * result.setText("Limit to")
		 * ;//SearchMessages.SearchPage_limitTo_label); result.setLayout(new
		 * GridLayout(2, true));
		 * 
		 * SelectionAdapter listener= new SelectionAdapter() { public void
		 * widgetSelected(SelectionEvent e) { //updateUseJRE(); } };
		 * 
		 * fLimitTo= new Button[fLimitToText.length]; for (int i= 0; i <
		 * fLimitToText.length; i++) { Button button= new Button(result,
		 * SWT.RADIO); button.setText(fLimitToText[i]); fLimitTo[i]= button;
		 * button.setSelection(i == REFERENCES);
		 * button.addSelectionListener(listener); button.setLayoutData(new
		 * GridData()); }
		 */
		return result;
	}

	/*
	 * Implements method from ISearchPage
	 */
	public void setContainer(ISearchPageContainer container) {
		fContainer = container;
	}

	/**
	 * Returns the search page's container.
	 */
	private ISearchPageContainer getContainer() {
		return fContainer;
	}

	class SearchForManager {

		private Button[] fSearchFor;

		private Button createButton(Group parent, String text, int symbolType) {

			Button button = new Button(parent, SWT.RADIO);
			button.setText(text);
			button.setData(new Integer(symbolType));
			button.setLayoutData(new GridData());
			return button;
		}

		private Control createSearchFor(Composite parent) {
			Group result = new Group(parent, SWT.NONE);
			result.setText(RubyUIMessages.getString("RubySearchPage.SearchForGroupLabel")); //$NON-NLS-1$
			result.setLayout(new GridLayout(2, true));

			fSearchFor = new Button[2];
			fSearchFor[0] = createButton(result, RubyUIMessages.getString("RubySearch.SearchForClassSymbol"), CLASS_SYMBOL); //$NON-NLS-1$
			fSearchFor[0].setSelection(true);
			fSearchFor[1] = createButton(result, RubyUIMessages.getString("RubySearch.SearchForMethodSymbol"), METHOD_SYMBOL); //$NON-NLS-1$

			return result;
		}


		public int getSelectedSymbolType() {
			for (int i = 0; i < fSearchFor.length; i++) {
				if (fSearchFor[i].getSelection()) { return ((Integer) fSearchFor[i].getData()).intValue(); }
			}
			Assert.isTrue(false, "Error in RubySearchPage: There should always be a selected symbol type to search for"); //$NON-NLS-1$
			return CLASS_SYMBOL;
		}


	}
}

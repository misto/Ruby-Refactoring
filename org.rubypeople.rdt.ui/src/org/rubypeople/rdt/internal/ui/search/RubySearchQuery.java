/* Copyright (c) 2005 RubyPeople.
* 
* Author: Markus
* 
* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT
* is subject to the "Common Public License (CPL) v 1.0". You may not use RDT
* except in compliance with the License. For further information see
* org.rubypeople.rdt/rdt.license.
* 
*/

package org.rubypeople.rdt.internal.ui.search;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.search.internal.core.SearchScope;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.symbols.ISymbolTypes;
import org.rubypeople.rdt.internal.core.symbols.SearchResult;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;

public class RubySearchQuery implements ISearchQuery, ISymbolTypes {

	private String fSearchString;
	private SearchScope fScope;
	private RubySearchResult fResult;
	private int fSymbolType ;

	public RubySearchQuery(SearchScope scope, String searchString, int symbolType) {
		fScope = scope;
		fSearchString = searchString;
		fSymbolType = symbolType ;
	}

	public boolean canRerun() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean canRunInBackground() {
		return true;
	}

	public String getLabel() {
		return toString() ;
	}

	public ISearchResult getSearchResult() {
		if (fResult == null) {
			fResult = new RubySearchResult(this);
			// new SearchResultUpdater(fResult);
		}
		return fResult;
	}

	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {

		Set entries = RubyCore.getPlugin().getSymbolFinder().find(fSearchString, fSymbolType );

		for (Iterator iter = entries.iterator(); iter.hasNext();) {
			SearchResult searchResult = (SearchResult) iter.next();
			int startOffset = searchResult.getLocation().getPosition().getStartOffset();
			int length = searchResult.getLocation().getPosition().getEndOffset() - startOffset;
			fResult.addMatch(new Match(searchResult, Match.UNIT_CHARACTER, startOffset, length));
		}
		MultiStatus status = new MultiStatus(NewSearchUI.PLUGIN_ID, IStatus.OK, "Alright", null); //$NON-NLS-1$
		return status;
	}

	public String toString() {
		String args[] = new String[2] ;
		switch (fSymbolType) {
		case METHOD_SYMBOL:
			args[0] = RubyUIMessages.getString("RubySearch.SearchForMethodSymbol") ; //$NON-NLS-1$
			break;
		case CLASS_SYMBOL:
			args[0] = RubyUIMessages.getString("RubySearch.SearchForClassSymbol") ; //$NON-NLS-1$
			break;
		default:
			break;
		}
		args[1] =  fSearchString ;
		return RubyUIMessages.getFormattedString("RubySearch.ResultLabel", args) ;  //$NON-NLS-1$ 
	}

}

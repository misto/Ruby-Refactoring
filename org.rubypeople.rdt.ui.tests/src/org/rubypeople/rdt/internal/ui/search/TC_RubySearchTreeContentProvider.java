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


import org.eclipse.core.runtime.Path;
import org.eclipse.search.ui.text.Match;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;
import org.rubypeople.rdt.internal.core.symbols.Location;
import org.rubypeople.rdt.internal.core.symbols.MethodSymbol;
import org.rubypeople.rdt.internal.core.symbols.SearchResult;

import junit.framework.Assert;
import junit.framework.TestCase;


public class TC_RubySearchTreeContentProvider extends TestCase {

	MockTreeViewer mockTreeViewer  ;
	RubySearchTreeContentProvider rubySearchTreeContentProvider ;
	
	protected void setUp() throws Exception {
		mockTreeViewer = new MockTreeViewer() ;
		rubySearchTreeContentProvider = new RubySearchTreeContentProvider(mockTreeViewer) ;
		
	}
	
	public void testGroupByPath() {
		// could be a MockSearchResult instead
		RubySearchResult rubyUISearchResult = new RubySearchResult(null);
		// call initialize before the search starts
		rubySearchTreeContentProvider.initialize(rubyUISearchResult) ;
		// initialize does not refresh the viewer, therefore elementsChanges is called here
		MethodSymbol methodSymbol = new MethodSymbol("myMethod") ;
		Path path = new Path("test") ;
		Location location = new Location(path,new RdtPosition(0,0,0,0)) ;
		SearchResult searchResult = new SearchResult(methodSymbol, location) ;
		rubyUISearchResult.addMatch(new Match(searchResult, Match.UNIT_CHARACTER, 0, 0));

		rubySearchTreeContentProvider.elementsChanged(new Object[]{searchResult}) ;
		Assert.assertTrue(mockTreeViewer.isParentAdded(path)) ;
		
	}

}

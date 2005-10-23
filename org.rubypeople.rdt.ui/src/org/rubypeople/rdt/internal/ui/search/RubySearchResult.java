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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.IEditorMatchAdapter;
import org.eclipse.search.ui.text.IFileMatchAdapter;


public class RubySearchResult extends AbstractTextSearchResult {

	private RubySearchQuery fRubySearchQuery ;
	
	public RubySearchResult(RubySearchQuery rubySearchQuery) {
		fRubySearchQuery = rubySearchQuery ;
	}
	
	public IEditorMatchAdapter getEditorMatchAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	public IFileMatchAdapter getFileMatchAdapter() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		return "Ruby search result for " + fRubySearchQuery.toString();
	}

	public String getTooltip() {
		// TODO Auto-generated method stub
		return null;
	}

	public ImageDescriptor getImageDescriptor() {
		// TODO Auto-generated method stub
		return null;
	}

	public ISearchQuery getQuery() {
		return fRubySearchQuery;
	}

}

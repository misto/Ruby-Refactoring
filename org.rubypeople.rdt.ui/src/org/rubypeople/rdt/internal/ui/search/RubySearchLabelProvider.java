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



import org.eclipse.jface.viewers.LabelProvider;
import org.rubypeople.rdt.internal.core.symbols.SearchResult;


public class RubySearchLabelProvider extends LabelProvider {

	public String getText(Object element) {
		if (element instanceof SearchResult) {
			SearchResult searchResult = (SearchResult) element ;
			return searchResult.getSymbol().toString() ;
		}
		if (element instanceof Scope) {
			Scope scope = (Scope) element ;
			return scope.getName() ;
		}
		return super.getText(element);
	}

}

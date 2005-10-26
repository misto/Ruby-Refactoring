package org.rubypeople.rdt.internal.ui.search;

import org.rubypeople.rdt.internal.core.symbols.SearchResult;



public class GroupByScopeStrategy implements IGroupByStrategy {

	private Scope createScope(String qualifiedName) {
		int index = qualifiedName.lastIndexOf("::") ;
		if (index == -1) {
			return null ;
		}
		String packageName = qualifiedName.substring(0, index) ;
		return new Scope(packageName);
	}
	
	public Object getParent(Object element) {
		if (element instanceof SearchResult) {
			SearchResult result = (SearchResult) element ;
			return this.createScope(result.getSymbol().getName()) ;
		}
		if (element instanceof Scope) {
			Scope scope = (Scope) element ;
			return this.createScope(scope.getQualifiedName()) ;
		}
		return null ;
	}

}

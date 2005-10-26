package org.rubypeople.rdt.internal.ui.search;

import org.rubypeople.rdt.internal.core.symbols.SearchResult;

public class GroupByPathStrategy implements IGroupByStrategy {

	public Object getParent(Object element) {
		if (!(element instanceof SearchResult)) { return null; }
		SearchResult result = (SearchResult) element;
		return result.getLocation().getSourcePath();
	}
}

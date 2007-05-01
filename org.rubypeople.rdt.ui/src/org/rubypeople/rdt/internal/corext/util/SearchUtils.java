package org.rubypeople.rdt.internal.corext.util;

import org.rubypeople.rdt.core.search.SearchPattern;

public class SearchUtils {

    /**
     * Returns whether the given pattern is a camel case pattern or not.
     * 
     * @param pattern the pattern to inspect
     * @return whether it is a camel case pattern or not
     */
	public static boolean isCamelCasePattern(String pattern) {
		return SearchPattern.validateMatchRule(
			pattern, 
			SearchPattern.R_CAMELCASE_MATCH) == SearchPattern.R_CAMELCASE_MATCH;
	}

}

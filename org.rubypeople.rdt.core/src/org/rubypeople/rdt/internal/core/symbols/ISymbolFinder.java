package org.rubypeople.rdt.internal.core.symbols;

import java.util.Set;
import java.util.regex.PatternSyntaxException;

public interface ISymbolFinder {

    public abstract Set find(Symbol symbol);

    /**
     * returns a set of SearchResult instances as opposed to find(symbol), which returns locations
     */
    public abstract Set find(String regExp, int symbolType)
            throws PatternSyntaxException;

}
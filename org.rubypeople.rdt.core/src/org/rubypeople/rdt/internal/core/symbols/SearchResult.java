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

package org.rubypeople.rdt.internal.core.symbols;


public class SearchResult {
	private static final int ODD_PRIME_NUMBER = 37;
	private Symbol symbol ;
	private Location location ;
	
	public SearchResult(Symbol symbol, Location location) {
		this.location = location ;
		this.symbol = symbol ;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public Symbol getSymbol() {
		return symbol;
	}

	public boolean equals(Object obj) {
	    if (!(obj instanceof SearchResult))
	        return false;
	    
	    SearchResult that = (SearchResult) obj;
	    return that.symbol.equals(this.symbol) && that.location.equals(this.location);
	}

	public int hashCode() {				
		return ODD_PRIME_NUMBER * symbol.hashCode() + location.hashCode() ;
	}

	public String toString() {
	    return "Symbol [" + symbol.toString() + "] at Location ["+location.toString()+"]";
	}
}

package org.rubypeople.rdt.internal.core.symbols;


public abstract class Symbol implements ISymbolTypes {
	private static int ODD_PRIME_NUMBER = 23 ;
	private int fType ;
	protected final String name;
	
	public Symbol(String name, int symbolType) {
		fType = symbolType ;
		this.name = name ;
	}

	
	public int getType() {
		return fType;
	}


	public String getName() {
		return this.name ;
	}


	public int hashCode() {						
		return ODD_PRIME_NUMBER * name.hashCode() + fType ;
	}
	
    public boolean equals(Object obj) {
        if (!(obj instanceof Symbol))
            return false;
        
        Symbol that = (Symbol) obj;
        return that.fType == this.fType && that.name.equals(this.name);
    }

}

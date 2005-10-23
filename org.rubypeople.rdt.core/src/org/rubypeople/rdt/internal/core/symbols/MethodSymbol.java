package org.rubypeople.rdt.internal.core.symbols;


public class MethodSymbol extends Symbol {

	public MethodSymbol(String name) {
		super(name, METHOD_SYMBOL);
	}

	public String toString() {
	    return "Method [" + this.getName() + "]";
	}

}

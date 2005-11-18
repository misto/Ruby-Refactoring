package org.rubypeople.rdt.internal.core.symbols;


public class MethodSymbol extends Symbol {

	public MethodSymbol(String name) {
		super(name, METHOD_SYMBOL);
	}

	public MethodSymbol(String className, String methodName) {
        this(className + "::" + methodName);
	}

    public String toString() {
	    return "Method [" + this.getName() + "]";
	}

}

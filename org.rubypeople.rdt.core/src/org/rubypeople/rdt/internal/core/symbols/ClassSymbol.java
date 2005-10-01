package org.rubypeople.rdt.internal.core.symbols;

public class ClassSymbol {

    private final String className;

    public ClassSymbol(String className) {
        this.className = className;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ClassSymbol))
            return false;
        
        ClassSymbol that = (ClassSymbol) obj;
        return that.className.equals(this.className);
    }
    
    public int hashCode() {
        return className.hashCode();
    }
}

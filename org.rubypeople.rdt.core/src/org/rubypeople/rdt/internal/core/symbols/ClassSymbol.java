/*
?* Author: David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/

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
    
    public String toString() {
        return "Class [" + className + "]";
    }
}

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

import org.eclipse.core.resources.IFile;
import org.jruby.lexer.yacc.ISourcePosition;

public class Location {

    private final ISourcePosition position;
    private final IFile sourceFile;

    public Location(IFile sourceFile, ISourcePosition position) {
        this.sourceFile = sourceFile;
        this.position = position;
    }

    public String toString() {
        return sourceFile.getFullPath() + ": " + position;
    }

    public boolean forSource(IFile file) {
        return sourceFile.equals(file);
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) 
            return false;
        Location that = (Location) obj;
        
        return this.sourceFile.equals(that.sourceFile)
            && this.position.equals(that.position);
    }
    
    public int hashCode() {
        return sourceFile.hashCode() * position.hashCode();
    }

    public String getFilename() {
        return sourceFile.getLocation().toOSString();
    }

    public ISourcePosition getPosition() {
        return position;
    }
    
    public IFile getSourceFile() 
    {
    	return sourceFile;
    }
}

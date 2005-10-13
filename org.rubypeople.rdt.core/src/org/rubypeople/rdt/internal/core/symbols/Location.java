package org.rubypeople.rdt.internal.core.symbols;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jruby.lexer.yacc.ISourcePosition;

public class Location {

    private final IPath sourcePath;
    private final ISourcePosition position;

    public Location(IPath sourcePath, ISourcePosition position) {
        this.sourcePath = sourcePath;
        this.position = position;
    }

    public String toString() {
        return sourcePath+": " + position;
    }

    public boolean forSource(Path path) {
        return sourcePath.equals(path);
    }
    
    public boolean equals(Object obj) {
        if (!(obj instanceof Location)) 
            return false;
        Location that = (Location) obj;
        
        return this.sourcePath.equals(that.sourcePath)
            && this.position.equals(that.position);
    }
    
    public int hashCode() {
        return sourcePath.hashCode() * position.hashCode();
    }

    public String getFilename() {
        return sourcePath.toOSString();
    }

    public ISourcePosition getPosition() {
        return position;
    }
}

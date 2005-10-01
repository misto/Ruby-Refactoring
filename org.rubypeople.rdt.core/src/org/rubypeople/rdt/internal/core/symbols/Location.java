package org.rubypeople.rdt.internal.core.symbols;

import org.eclipse.core.runtime.Path;

public class Location {

    private final Path sourcePath;
    private final int line;
    private final int column;

    public Location(Path sourcePath, int line, int column) {
        this.sourcePath = sourcePath;
        this.line = line;
        this.column = column;
    }

    public String toString() {
        return sourcePath+": " + line + "("+column+")";
    }

    public boolean forSource(Path path) {
        return sourcePath.equals(path);
    }
}

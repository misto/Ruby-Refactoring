/*
 * Author: David Corbin
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */

package org.rubypeople.rdt.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;

public class ShamSymbolIndex extends SymbolIndex {

    private IFile fileArg;
    private ClassSymbol symbolArg;
    private ISourcePosition positionArg;
    private IPath flushedPathArg;

    public void flush(IPath path) {
        flushedPathArg = path;
    }
    public void assertFlushed(IPath expectedPath) {
        TC_IndexUpdater.assertEquals("Flushed path", expectedPath, flushedPathArg);
    }

    public void assertAddNotCalled() {
        TC_IndexUpdater.assertNull("Unexpected call to assertAddNotCalled()", fileArg);
    }

    public void assertAdded(ClassSymbol expectedSymbol, IFile expectedFile, ISourcePosition expectedPosition) {
        TC_IndexUpdater.assertEquals("Symbol", expectedSymbol, symbolArg);
        TC_IndexUpdater.assertEquals("File", expectedFile, fileArg);
        TC_IndexUpdater.assertEquals("Position", expectedPosition, positionArg);
        
    }
    public void add(ClassSymbol symbol, IFile file, ISourcePosition position) {
        symbolArg = symbol;
        fileArg = file;
        positionArg = position;
    }

}
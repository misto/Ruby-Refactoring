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

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.rdt.internal.core.symbols.Symbol;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;
import org.rubypeople.rdt.internal.core.util.ListUtil;

public class ShamSymbolIndex extends SymbolIndex {

    private IFile fileArg;
    private Symbol symbolArg;
    private ISourcePosition positionArg;
    private List flushedFiles = new ArrayList();
    private IProject flushedProjectArg;

    public void flush(IFile file) {
        flushedFiles.add(file);
    }
    
    public void assertFlushed(IFile expectedFile) {
        Assert.assertEquals("Flushed file", ListUtil.create(expectedFile), flushedFiles);
    }

    public void assertAddNotCalled() {
        Assert.assertNull("Unexpected call to assertAddNotCalled()", fileArg);
    }

    public void assertAdded(Symbol expectedSymbol, IFile expectedFile, ISourcePosition expectedPosition) {
        Assert.assertEquals("Symbol", expectedSymbol, symbolArg);
        Assert.assertEquals("File", expectedFile, fileArg);
        Assert.assertEquals("Position", expectedPosition, positionArg);
        
    }
    public void add(Symbol symbol, IFile file, ISourcePosition position) {
        symbolArg = symbol;
        fileArg = file;
        positionArg = position;
    }
    
    public void assertFlushed(ShamProject expectedProject) {
        Assert.assertEquals("Flushed project", flushedProjectArg, expectedProject);
        
    }
    
    public void flush(IProject project) {
        flushedProjectArg = project;
    }
    
    public void assertFlushed(List expectedFiles) {
        Assert.assertEquals(expectedFiles, flushedFiles);
    }

}
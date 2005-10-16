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

import java.io.InputStreamReader;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.jruby.ast.ClassNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.Node;
import org.jruby.ast.TrueNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;

public class TC_IndexUpdater extends TestCase {
    private static final RdtPosition POSITION_1 = new RdtPosition(1,2,3);

    private static final String TEST_CLASS_NAME = "TestClassName";
    
    private IndexUpdater updater;
    private ShamFile file;
    private MockSymbolIndex symbolIndex;
    
    public void setUp() {
        file = new ShamFile("TestFile.rb");
        symbolIndex = new MockSymbolIndex();
        updater = new IndexUpdater(symbolIndex);
    }
    
    public void testIrrelevantNodes() {
        Node node = new TrueNode(POSITION_1);
        updater.update(file, node);
        
        symbolIndex.assertFlushed(file.getFullPath());
        symbolIndex.assertAddNotCalled();
    }

    public void testSimple() {
        Colon2Node nameNode = new Colon2Node(POSITION_1, null, TEST_CLASS_NAME);
        Node node = new ClassNode(POSITION_1, nameNode, null, null);
        updater.update(file, node);
        
        symbolIndex.assertFlushed(file.getFullPath());
        symbolIndex.assertAdded(new ClassSymbol(TEST_CLASS_NAME), file, POSITION_1);
    }
    
    public void testWithTree() throws Exception {
        Node node = parseCode("if x\nclass Foo\nend\n end\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file.getFullPath());
        symbolIndex.assertAdded(new ClassSymbol("Foo"), file, new RdtPosition(1, 2, 14, 15));
    }

    public void testTreeWithScopedClass() throws Exception {
        Node node = parseCode("if x\nclass Foo::Bar\nend\n end\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file.getFullPath());
        symbolIndex.assertAdded(new ClassSymbol("Foo::Bar"), file, new RdtPosition(1, 2, 16, 20));
    }

    public void testTreeWithDeeplyScopedClass() throws Exception {
        Node node = parseCode("if x\nclass X::Foo::Bar\nend\n end\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file.getFullPath());
        symbolIndex.assertAdded(new ClassSymbol("X::Foo::Bar"), file, new RdtPosition(1, 2, 19, 23));
    }

    public void testTreeWithNesting() throws Exception {
        Node node = parseCode("if x\nmodule Foo\nclass Bar\nend\n end\nend\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file.getFullPath());
        symbolIndex.assertAdded(new ClassSymbol("Foo::Bar"), file, new RdtPosition(2, 3, 25, 26));
    }
    

    private Node parseCode(String code) throws CoreException {
        file.setContents(code);
        InputStreamReader reader = new InputStreamReader(file.getContents());
        return new RubyParser().parse(file, reader);
    }

    private static class MockSymbolIndex extends SymbolIndex {

        private IFile fileArg;
        private ClassSymbol symbolArg;
        private ISourcePosition positionArg;
        private IPath flushedPathArg;

        public void flush(IPath path) {
            flushedPathArg = path;
        }
        public void assertFlushed(IPath expectedPath) {
            assertEquals("Flushed path", expectedPath, flushedPathArg);
        }

        public void assertAddNotCalled() {
            assertNull("Unexpected call to assertAddNotCalled()", fileArg);
        }

        public void assertAdded(ClassSymbol expectedSymbol, IFile expectedFile, ISourcePosition expectedPosition) {
            assertEquals("Symbol", expectedSymbol, symbolArg);
            assertEquals("File", expectedFile, fileArg);
            assertEquals("Position", expectedPosition, positionArg);
            
        }
        public void add(ClassSymbol symbol, IFile file, ISourcePosition position) {
            symbolArg = symbol;
            fileArg = file;
            positionArg = position;
        }

    }
}
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

import org.eclipse.core.runtime.CoreException;
import org.jruby.ast.ClassNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.Node;
import org.jruby.ast.TrueNode;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.MethodSymbol;

public class TC_IndexUpdater extends TestCase {
    private static final RdtPosition POSITION_1 = new RdtPosition(1,2,3);

    private static final String TEST_CLASS_NAME = "TestClassName";
    
    private IndexUpdater updater;
    private ShamFile file;
    private ShamSymbolIndex symbolIndex;
    
    public void setUp() {
        file = new ShamFile("TestFile.rb");
        symbolIndex = new ShamSymbolIndex();
        updater = new IndexUpdater(symbolIndex);
    }
    
    public void testIrrelevantNodes() {
        Node node = new TrueNode(POSITION_1);
        updater.update(file, node);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAddNotCalled();
    }

    public void testSimple() {
        Colon2Node nameNode = new Colon2Node(POSITION_1, null, TEST_CLASS_NAME);
        Node node = new ClassNode(POSITION_1, nameNode, null, null);
        updater.update(file, node);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol(TEST_CLASS_NAME), file, POSITION_1);
    }
    
    public void testWithTree() throws Exception {
        Node node = parseCode("if x\nclass Foo\nend\n end\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("Foo"), file, new RdtPosition(1, 2, 14, 15));
    }

    public void testTreeWithScopedClass() throws Exception {
        Node node = parseCode("if x\nclass Foo::Bar\nend\n end\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("Foo::Bar"), file, new RdtPosition(1, 2, 16, 20));
    }

    public void testTreeWithDeeplyScopedClass() throws Exception {
        Node node = parseCode("if x\nclass X::Foo::Bar\nend\n end\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("X::Foo::Bar"), file, new RdtPosition(1, 2, 19, 23));
    }

    public void testTreeWithNesting() throws Exception {
        Node node = parseCode("if x\nmodule Foo\nclass Bar\nend\n end\nend\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("Foo::Bar"), file, new RdtPosition(2, 3, 25, 26));
    }
    
    public void testTreeWithMoreNesting() throws Exception {
        Node node = parseCode("module Foo\nclass Bar\nclass InnerBar\nend\nend\nend\n");
        
        updater.update(file,node);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("Foo::Bar::InnerBar"), file, new RdtPosition(2, 3, 35, 36));
    }
    
    public void testMethod() throws Exception {
    	Node node = parseCode("def method\nend") ;
        
        updater.update(file,node);
                
        symbolIndex.assertAdded(new MethodSymbol("method"), file, new RdtPosition(0, 1, 3, 12));
    }

    public void testMethodWithNesting() throws Exception {
    	Node node = parseCode("class Foo\ndef method\nend\nend") ;
        
        updater.update(file,node);
                
        symbolIndex.assertAdded(new MethodSymbol("Foo::method"), file, new RdtPosition(1, 2, 13, 24));
    }
    
    
    private Node parseCode(String code) throws CoreException {
        file.setContents(code);
        InputStreamReader reader = new InputStreamReader(file.getContents());
        return new RubyParser().parse(file, reader);
    }
}
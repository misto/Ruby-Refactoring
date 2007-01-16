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
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.SourcePosition;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.MethodSymbol;

public class TC_IndexUpdater extends TestCase {
    private static final ISourcePosition POSITION_1 = createPosition(1,1,2,3);

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
        updater.update(file, node, false);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAddNotCalled();
    }

    public void testSimple() {
        Colon2Node nameNode = new Colon2Node(POSITION_1, null, TEST_CLASS_NAME);
        Node node = new ClassNode(POSITION_1, nameNode, null, null, null);
        updater.update(file, node, false);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol(TEST_CLASS_NAME), file, POSITION_1);
    }
    
    public void testSkippingFlush() {
        Colon2Node nameNode = new Colon2Node(POSITION_1, null, TEST_CLASS_NAME);
        Node node = new ClassNode(POSITION_1, nameNode, null, null, null);
        updater.update(file, node, true);
        
        symbolIndex.assertNotFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol(TEST_CLASS_NAME), file, POSITION_1);
    }

    public void testWithTree() throws Exception {
        Node node = parseCode("if x\nclass Foo\nend\n end\n");
        
        updater.update(file,node, false);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("Foo"), file, createPosition(1, 1, 11, 14));
    }

    public void testTreeWithScopedClass() throws Exception {
        Node node = parseCode("if x\nclass Foo::Bar\nend\n end\n");
        
        updater.update(file,node, false);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("Foo::Bar"), file, createPosition(1, 1, 11, 19));
    }

    public void testTreeWithDeeplyScopedClass() throws Exception {
        Node node = parseCode("if x\nclass X::Foo::Bar\nend\n end\n");
        
        updater.update(file,node, false);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("X::Foo::Bar"), file, createPosition(1, 1, 11, 21));
    }

    public void testTreeWithNesting() throws Exception {
        Node node = parseCode("if x\nmodule Foo\nclass Bar\nend\n end\nend\n");
        
        updater.update(file,node, false);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("Foo::Bar"), file, createPosition(2, 2, 22, 25));
    }
    
    public void testTreeWithMoreNesting() throws Exception {
        Node node = parseCode("module Foo\nclass Bar\nclass InnerBar\nend\nend\nend\n");
        
        updater.update(file,node, false);
        
        symbolIndex.assertFlushed(file);
        symbolIndex.assertAdded(new ClassSymbol("Foo::Bar::InnerBar"), file, createPosition(2, 2, 27, 35));
    }
    
    public void testMethod() throws Exception {
    	Node node = parseCode("def method\nend") ;
        
        updater.update(file,node, false);
                
        symbolIndex.assertAdded(new MethodSymbol("method"), file, createPosition(0, 0, 4, 10));
    }

    public void testMethodWithNesting() throws Exception {
    	Node node = parseCode("class Foo\ndef method\nend\nend") ;
        
        updater.update(file,node, false);
                
        symbolIndex.assertAdded(new MethodSymbol("Foo::method"), file, createPosition(1, 1, 14, 20));
    }
    
    
    private Node parseCode(String code) throws CoreException {
        file.setContents(code);
        InputStreamReader reader = new InputStreamReader(file.getContents());
        return new RubyParser().parse(file, reader);
    }
    
    private static ISourcePosition createPosition(int startLine, int endLine, int startOffset, int endOffset) {
        return new SourcePosition("TestFile.rb", startLine, endLine, startOffset, endOffset);
    }


}
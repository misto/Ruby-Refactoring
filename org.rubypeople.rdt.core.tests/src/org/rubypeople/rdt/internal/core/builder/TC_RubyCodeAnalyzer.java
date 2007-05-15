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

import junit.framework.TestCase;

import org.jruby.ast.Node;
import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;

public class TC_RubyCodeAnalyzer extends TestCase {

    private static final String FILE_CONTENTS = "file Contents";
    private static final String FILENAME = "testFile.rb";
    private ShamFile file;
    private ShamMarkerManager markerManager;
    private ShamRubyParser parser;
    private RubyCodeAnalyzer compiler;
    private Node rootNode;

    public void setUp() {
        file = new ShamFile(FILENAME);
        file.setContents(FILE_CONTENTS);
        
        rootNode = new Node(null, 0) {
			private static final long serialVersionUID = 9089679642764992140L;

			public Instruction accept(NodeVisitor visitor) {
                return null;
            }

            public List childNodes() {
                return new ArrayList();
            }
        };
        markerManager = new ShamMarkerManager();
        parser = new ShamRubyParser();
        parser.addParseResult(file, rootNode);
        compiler = new RubyCodeAnalyzer(markerManager, parser);
    }
    
    public void testParserInvocation() throws Exception {
        compiler.compileFile(file);
        
        parser.assertParsed(file, FILE_CONTENTS);
        file.assertContentStreamClosed();
    }
    

    public void testSyntaxException() throws Exception {
        SyntaxException syntaxException = new SyntaxException(new RdtPosition(1, 0, 10), "");
        parser.setExceptionToThrow(syntaxException);
        
        compiler.compileFile(file);
        
        file.assertContentStreamClosed();
        markerManager.assertWarningAdded(file, "", 1, 0, 10);
    }
}

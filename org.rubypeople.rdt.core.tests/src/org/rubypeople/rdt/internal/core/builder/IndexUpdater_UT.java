package org.rubypeople.rdt.internal.core.builder;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.jruby.ast.ClassNode;
import org.jruby.ast.Node;
import org.jruby.ast.TrueNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;

public class IndexUpdater_UT extends TestCase {
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
        Node node = new ClassNode(POSITION_1, TEST_CLASS_NAME, null, null);
        updater.update(file, node);
        
        symbolIndex.assertFlushed(file.getFullPath());
        symbolIndex.assertAdded(new ClassSymbol(TEST_CLASS_NAME), file, POSITION_1);
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
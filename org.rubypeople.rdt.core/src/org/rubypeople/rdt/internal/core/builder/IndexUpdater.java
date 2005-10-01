package org.rubypeople.rdt.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.ClassNode;
import org.jruby.ast.Node;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;

public class IndexUpdater {

    private final SymbolIndex index;


    public IndexUpdater(SymbolIndex index) {
        this.index = index;
    }


    public void update(IFile file, Node rootNode) {
        index.flush(file.getFullPath());
        if (rootNode instanceof ClassNode) {
            ClassNode classNode = (ClassNode) rootNode;
            index.add(new ClassSymbol(classNode.getClassName()), file, classNode.getPosition());
        }
    }

}

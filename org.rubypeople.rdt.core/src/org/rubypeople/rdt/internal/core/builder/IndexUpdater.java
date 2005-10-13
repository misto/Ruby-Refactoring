/*
?* Author: David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/

package org.rubypeople.rdt.internal.core.builder;

import java.util.Iterator;
import java.util.Stack;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.ClassNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.IScopingNode;
import org.jruby.ast.Node;
import org.jruby.ast.types.INameNode;
import org.rubypeople.rdt.internal.core.symbols.ClassSymbol;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;

public class IndexUpdater {
    private final SymbolIndex index;
    private Stack scopeStack = new Stack();
    
    public IndexUpdater(SymbolIndex index) {
        this.index = index;
    }


    public void update(IFile file, Node rootNode) {
        index.flush(file.getFullPath());
        processNode(file, rootNode);
    }


    private void processNode(IFile file, Node node) {
        if (isScopingNode(node)) {
            IScopingNode classNode = (IScopingNode) node;
            String name = getFullyQualifiedName(classNode);
            
            scopeStack.push(name);
            if (node instanceof ClassNode)
                index.add(new ClassSymbol(name), file, classNode.getCPath().getPosition());
        }
        
        for (Iterator iter = node.childNodes().iterator(); iter.hasNext();) {
            Node childNode = (Node) iter.next();
            processNode(file, childNode);
        }
        
        if (isScopingNode(node)) {
            scopeStack.pop();
        }
    }


    private boolean isScopingNode(Node node) {
        return node instanceof IScopingNode;
    }


    private String getFullyQualifiedName(IScopingNode classNode) {
        Colon2Node path = classNode.getCPath();
        return getContext() + assembleQualifiedName(path);
    }


    private String getContext() {
        StringBuffer buffer = new StringBuffer();
        for (Iterator iter = scopeStack.iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            buffer.append(name);
            buffer.append("::");
        }
        return buffer.toString();
    }


    private String assembleQualifiedName(INameNode path) {
        String name = "";
        if (path instanceof Colon2Node) {
            Colon2Node colon2Node = ((Colon2Node)path);
            INameNode leftNode = (INameNode) colon2Node.getLeftNode();
            if (leftNode != null)
                name += assembleQualifiedName(leftNode) + "::";
        }
        
        return name + path.getName();
    }

}

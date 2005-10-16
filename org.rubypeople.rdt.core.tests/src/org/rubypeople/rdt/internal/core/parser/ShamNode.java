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
package org.rubypeople.rdt.internal.core.parser;

import java.util.List;

import org.jruby.ast.Node;
import org.jruby.ast.visitor.NodeVisitor;

public class ShamNode extends Node {

    public ShamNode() {
        super(null);
    }

    public void accept(NodeVisitor visitor) {
    }

    public List childNodes() {
        return null;
    }

    public String toString() {
        return "ShamNode[]";
    }
}
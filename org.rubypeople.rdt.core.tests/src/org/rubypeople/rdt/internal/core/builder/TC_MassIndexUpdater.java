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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.Node;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.rdt.internal.core.parser.ShamNode;
import org.rubypeople.rdt.internal.core.util.ListUtil;

public class TC_MassIndexUpdater extends TestCase {

    public void testUpdateProjects() throws Exception {
        ShamRubyParser parser = new ShamRubyParser();
        ShamIndexUpdater updater = new ShamIndexUpdater();
        MassIndexUpdater massUpdater = new MassIndexUpdater(updater, parser);
        ShamProject project1 = new ShamProject("test1");
        ShamProject project2 = new ShamProject("test2");
        
        ShamFile file1 = new ShamFile("file1.rb");
        ShamFile file2 = new ShamFile("file2.rb");
        ShamFile file3 = new ShamFile("file3.rb");
        
        ShamNode rootNode1 = new ShamNode();
        ShamNode rootNode2 = new ShamNode();
        ShamNode rootNode3 = new ShamNode();
        
        parser.addParseResult(file1, rootNode1);
        parser.addParseResult(file2, rootNode2);
        parser.addParseResult(file3, rootNode3);
        
        project1.setResourcesToVisit(ListUtil.create(file1, file2));
        project2.setResourcesToVisit(ListUtil.create(file3));
        
        List projects = new ArrayList();
        projects.add(project1);
        projects.add(project2);
        
        massUpdater.updateProjects(projects);
        RubySourceFileCollectingVisitor expectedVisitor = 
            new RubySourceFileCollectingVisitor(new ArrayList());
        project1.assertAcceptCalled(expectedVisitor, 0);
        project2.assertAcceptCalled(expectedVisitor, 0);
        
        parser.assertParsed(file1);
        parser.assertParsed(file2);
        parser.assertParsed(file3);
        
        updater.assertUpdated(file1, rootNode1);
        updater.assertUpdated(file2, rootNode2);
        updater.assertUpdated(file3, rootNode3);
    }

    private static class ShamIndexUpdater extends IndexUpdater {

        private Map updates = new HashMap();

        public ShamIndexUpdater() {
            super(null);
        }

        public void assertUpdated(ShamFile expectedFile, ShamNode expectedNode) {
            assertTrue("Should have updated " + expectedFile,
                    updates.containsKey(expectedFile));
            assertEquals(expectedNode, updates.get(expectedFile));
        }

        public void update(IFile file, Node rootNode) {
            updates.put(file, rootNode);
        }
    }
}

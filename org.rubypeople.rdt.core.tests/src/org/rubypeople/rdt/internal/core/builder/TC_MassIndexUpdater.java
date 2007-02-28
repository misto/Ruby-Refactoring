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

import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.eclipse.shams.runtime.ShamMonitor;
import org.rubypeople.rdt.internal.core.parser.ShamNode;
import org.rubypeople.rdt.internal.core.util.ListUtil;

public class TC_MassIndexUpdater extends TestCase {

    public void testUpdateProjects() throws Exception {
    	// What exactly does this test?
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
        
        project1.addResource(file1);
        project1.addResource(file2);
        project2.addResource(file3);
        
        List projects = new ArrayList();
        projects.add(project1);
        projects.add(project2);
        
        ShamMonitor monitor = new ShamMonitor();
        massUpdater.updateProjects(projects, monitor);

        parser.assertParsed(file1);
        parser.assertParsed(file2);
        parser.assertParsed(file3);
        
        updater.assertUpdated(file1, rootNode1, false);
        updater.assertUpdated(file2, rootNode2, false);
        updater.assertUpdated(file3, rootNode3, false);
        
        monitor.assertTaskBegun("Update symbol index", 3);
        monitor.assertSubTasks(ListUtil.create(
                "file1.rb", "file2.rb", "file3.rb"));
    }
}

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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.parser.TaskParser;

public final class TaskCompiler implements SingleFileCompiler {
    private final TaskParser taskParser;
    private final IMarkerManager markerManager;
    
    public TaskCompiler(IMarkerManager markerManager) {
        this(markerManager, new TaskParser(RubyCore.getOptions()));
    }

    public TaskCompiler(IMarkerManager markerManager, TaskParser taskParser) {
        this.taskParser = taskParser;
        this.markerManager = markerManager;
    }

    public void compileFile(IFile file) throws CoreException {
        InputStream contents = null;
        try {
            contents = file.getContents();
            taskParser.clear();
            taskParser.parse(new InputStreamReader(contents));
            markerManager.createTasks(file, taskParser.getTasks());
            taskParser.clear();
        } catch (IOException e) {
            RubyCore.log(e);
        } finally {
            IoUtils.closeQuietly(contents);
        }
        
    }

}
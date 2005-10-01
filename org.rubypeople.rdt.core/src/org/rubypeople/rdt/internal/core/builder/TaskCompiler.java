/**
 * 
 */
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
        this(markerManager, new TaskParser(RubyCore.getInstancePreferences()));
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
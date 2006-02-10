package org.rubypeople.rdt.internal.core.builder;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.TaskParser;
import org.rubypeople.rdt.internal.core.parser.TaskTag;

public class TC_TaskCompiler extends TestCase {

    private static final TaskTag TASK_TAG = new TaskTag("",0,0,0,0);
    private ShamMarkerManager markerManager;
    private ShamTaskParser taskParser;
    private TaskCompiler taskCompiler;
    private ShamFile file;
    private static class ShamTaskParser extends TaskParser {

        private String contents;
        private List tasks = new ArrayList();

        public ShamTaskParser() {
            super(new HashMap());
        }
        
        public void parse(Reader reader) throws IOException {
            contents = IoUtils.readAll(reader);
        }
            
        public List getTasks() {
            return tasks;
        }
            
        public void assertFileContents(String expectedContents) {
            assertEquals(expectedContents, contents);
            
        }

        public void addTaskToReturn(TaskTag taskTag) {
            tasks.add(taskTag);
        }
    }

    public void setUp() {
        markerManager = new ShamMarkerManager();
        taskParser = new ShamTaskParser();
        taskCompiler = new TaskCompiler(markerManager, taskParser);

        file = new ShamFile("");
        file.setContents("fileContents");
    }

    public void testCompile() throws Exception {
        taskParser.addTaskToReturn(TASK_TAG);
        taskCompiler.compileFile(file);
        taskParser.assertFileContents("fileContents");
        markerManager.assertTasksCreated(file, Collections.singletonList(TASK_TAG));
    }
}

package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.internal.launching.CommandExecutor;
import org.rubypeople.rdt.internal.launching.IllegalCommandException;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;

public class TC_RubyInterpreter extends TestCase {

    private static final String TEST_RUBY_CMD = "/foo bar ruby";
    private static final File WORKING_DIR = new File("/testDir");

    public void testEquals() {
		RubyInterpreter interpreterOne = new RubyInterpreter("InterpreterOne", new Path("/InterpreterOnePath"));
		RubyInterpreter similarInterpreterOne = new RubyInterpreter("InterpreterOne", new Path("/InterpreterOnePath"));
		assertTrue("Interpreters should be equal.", interpreterOne.equals(similarInterpreterOne));
		
		RubyInterpreter interpreterTwo = new RubyInterpreter("InterpreterTwo", new Path("/InterpreterTwoPath"));
		assertTrue("Interpreters should not be equal.", !interpreterOne.equals(interpreterTwo));
	}
    
    public void testExecList() throws Exception {
        ShamCommandExecutor executor = new ShamCommandExecutor();
        RubyInterpreter interpreter = new NonValidatingInterpreter("Test", new Path("/path to ruby"), executor);
        ShamProcess process = new ShamProcess();
        executor.setProcessToReturn(process);
        
        Process result = interpreter.exec(Arrays.asList(new String[] {"a","b b", "c"}), WORKING_DIR);

        executor.assertExecute(new String[] {TEST_RUBY_CMD, "a", "b b", "c"}, WORKING_DIR);
        assertEquals(process, result);
    }

    public void testExecutorThrows() throws Exception {
        ShamCommandExecutor executor = new ShamCommandExecutor();
        RubyInterpreter interpreter = new NonValidatingInterpreter("Test", new Path("/path to ruby"), executor);
        IOException testException = new IOException("test");
        executor.setExceptionToThrow(testException);
        
        try {
            interpreter.exec(new ArrayList(), WORKING_DIR);
            fail("Expected CoreException");
        } catch (CoreException expected) {
            assertEquals(IStatus.ERROR, expected.getStatus().getSeverity());
            assertEquals(testException, expected.getStatus().getException());
        }
    }
    
    public void testUnknownInterperterThrows() throws Exception {
        RubyInterpreter interpreter = new RubyInterpreter("Test", new Path("unknown ruby interpreter"), null);
        
        try {
            interpreter.exec(new ArrayList(), WORKING_DIR);
            fail("Expected CoreException");
        } catch (CoreException expected) {
            assertEquals(IStatus.ERROR, expected.getStatus().getSeverity());
            assertEquals(IllegalCommandException.class, expected.getStatus().getException().getClass());
        }
    }

    private static final class NonValidatingInterpreter extends RubyInterpreter {
        private NonValidatingInterpreter(String name, IPath location, CommandExecutor executor) {
            super(name, location, executor);
        }


        public String getCommand() throws IllegalCommandException {
            return TEST_RUBY_CMD;
        }
    }

    private static class ShamCommandExecutor implements CommandExecutor {
        private String[] commandArg;
        private File workingDirectoryArg;
        private ShamProcess processToReturn;
        private IOException exceptionToThrow;
        
        public void assertExecute(String[] expectedCommand, File expectedWorkingDir) {
            assertEquals("Command ", Arrays.asList(expectedCommand), Arrays.asList(commandArg));
            assertEquals("Working dir", expectedWorkingDir, workingDirectoryArg);
        }

        public void setExceptionToThrow(IOException exception) {
            this.exceptionToThrow = exception;
            
        }

        public void setProcessToReturn(ShamProcess process) {
            this.processToReturn = process;
            
        }

        public Process exec(String[] command, File workingDirectory) throws IOException {
            commandArg = command;
            workingDirectoryArg = workingDirectory;
            if (exceptionToThrow != null)
                throw exceptionToThrow;
            return processToReturn;
        }

    }

}

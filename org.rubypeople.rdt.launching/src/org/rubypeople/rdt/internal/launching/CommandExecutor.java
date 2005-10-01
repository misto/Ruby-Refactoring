package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;

public interface CommandExecutor {
    public Process exec(String[] command, File workingDirectory) throws IOException;
}

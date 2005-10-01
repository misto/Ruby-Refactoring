/**
 * 
 */
package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;

class StandardCommandExecutor implements CommandExecutor {
    public Process exec(String[] command, File workingDirectory) throws IOException {
        return Runtime.getRuntime().exec(command, null, workingDirectory);
    }
}
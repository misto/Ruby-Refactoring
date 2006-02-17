package org.rubypeople.rdt.internal.corext.util;

import java.util.Map;

import org.eclipse.text.edits.TextEdit;
import org.rubypeople.rdt.core.ToolFactory;

public class CodeFormatterUtil {

    /**
     * Creates edits that describe how to format the given string. Returns
     * <code>null</code> if the code could not be formatted for the given
     * kind.
     * 
     * @throws IllegalArgumentException
     *             If the offset and length are not inside the string, a
     *             IllegalArgumentException is thrown.
     */
    public static TextEdit format2(int kind, String string, int offset, int length,
            int indentationLevel, String lineSeparator, Map options) {
        if (offset < 0 || length < 0 || offset + length > string.length()) { throw new IllegalArgumentException(
                "offset or length outside of string. offset: " + offset + ", length: " + length + ", string size: " + string.length()); //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
        }
        return ToolFactory.createCodeFormatter(options).format(kind, string, offset, length,
                indentationLevel, lineSeparator);
    }
}

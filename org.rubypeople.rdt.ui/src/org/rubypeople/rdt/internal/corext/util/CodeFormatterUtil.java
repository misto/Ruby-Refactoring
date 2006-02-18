package org.rubypeople.rdt.internal.corext.util;

import java.util.Map;

import org.eclipse.text.edits.TextEdit;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.ToolFactory;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.internal.corext.Assert;

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

    /**
     * Returns the current indent width.
     * 
     * @param project
     *            the project where the source is used or <code>null</code> if
     *            the project is unknown and the workspace default should be
     *            used
     * @return the indent width
     * @since 0.8.0
     */
    public static int getIndentWidth(IRubyProject project) {
        String key;
        if (DefaultCodeFormatterConstants.MIXED.equals(getCoreOption(project,
                DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)))
            key = DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
        else
            key = DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;

        return getCoreOption(project, key, 4);
    }

    /**
     * Gets the current tab width.
     * 
     * @param project
     *            The project where the source is used, used for project
     *            specific options or <code>null</code> if the project is
     *            unknown and the workspace default should be used
     * @return The tab width
     */
    public static int getTabWidth(IRubyProject project) {
        /*
         * If the tab-char is SPACE, FORMATTER_INDENTATION_SIZE is not used by
         * the core formatter. We piggy back the visual tab length setting in
         * that preference in that case.
         */
        String key;
        if (RubyCore.SPACE.equals(getCoreOption(project,
                DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR)))
            key = DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE;
        else
            key = DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE;

        return getCoreOption(project, key, 4);
    }

    /**
     * Returns the possibly <code>project</code>-specific core preference
     * defined under <code>key</code>.
     * 
     * @param project
     *            the project to get the preference from, or <code>null</code>
     *            to get the global preference
     * @param key
     *            the key of the preference
     * @return the value of the preference
     * @since 0.8.0
     */
    private static String getCoreOption(IRubyProject project, String key) {
        if (project == null) return RubyCore.getOption(key);
        return project.getOption(key, true);
    }

    /**
     * Returns the possibly <code>project</code>-specific core preference
     * defined under <code>key</code>, or <code>def</code> if the value is
     * not a integer.
     * 
     * @param project
     *            the project to get the preference from, or <code>null</code>
     *            to get the global preference
     * @param key
     *            the key of the preference
     * @param def
     *            the default value
     * @return the value of the preference
     * @since 0.8.0
     */
    private static int getCoreOption(IRubyProject project, String key, int def) {
        try {
            return Integer.parseInt(getCoreOption(project, key));
        } catch (NumberFormatException e) {
            return def;
        }
    }

    /**
     * Creates a string that represents the given number of indentation units.
     * The returned string can contain tabs and/or spaces depending on the core
     * formatter preferences.
     * 
     * @param indentationUnits
     *            the number of indentation units to generate
     * @param project
     *            the project from which to get the formatter settings,
     *            <code>null</code> if the workspace default should be used
     * @return the indent string
     */
    public static String createIndentString(int indentationUnits, IRubyProject project) {
        final String tabChar = getCoreOption(project,
                DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR);
        final int tabs, spaces;
        if (RubyCore.SPACE.equals(tabChar)) {
            tabs = 0;
            spaces = indentationUnits * getIndentWidth(project);
        } else if (RubyCore.TAB.equals(tabChar)) {
            // indentWidth == tabWidth
            tabs = indentationUnits;
            spaces = 0;
        } else if (DefaultCodeFormatterConstants.MIXED.equals(tabChar)) {
            int tabWidth = getTabWidth(project);
            int spaceEquivalents = indentationUnits * getIndentWidth(project);
            if (tabWidth > 0) {
                tabs = spaceEquivalents / tabWidth;
                spaces = spaceEquivalents % tabWidth;
            } else {
                tabs = 0;
                spaces = spaceEquivalents;
            }
        } else {
            // new indent type not yet handled
            Assert.isTrue(false);
            return null;
        }

        StringBuffer buffer = new StringBuffer(tabs + spaces);
        for (int i = 0; i < tabs; i++)
            buffer.append('\t');
        for (int i = 0; i < spaces; i++)
            buffer.append(' ');
        return buffer.toString();
    }

    public static TextEdit format2(int kind, String string, int indentationLevel,
            String lineSeparator, Map options) {
        return format2(kind, string, 0, string.length(), indentationLevel, lineSeparator, options);
    }
}

package org.rubypeople.rdt.internal.corext.util;

import org.rubypeople.rdt.core.IRubyProject;

public class Strings {

    /**
     * Returns the indent of the given string in indentation units. Odd spaces
     * are not counted.
     * 
     * @param line
     *            the text line
     * @param project
     *            the ruby project from which to get the formatter preferences,
     *            or <code>null</code> for global preferences
     * @since 3.1
     */
    public static int computeIndentUnits(String line, IRubyProject project) {
        return computeIndentUnits(line, CodeFormatterUtil.getTabWidth(project), CodeFormatterUtil
                .getIndentWidth(project));
    }

    /**
     * Returns the indent of the given string in indentation units. Odd spaces
     * are not counted.
     * 
     * @param line
     *            the text line
     * @param tabWidth
     *            the width of the '\t' character in space equivalents
     * @param indentWidth
     *            the width of one indentation unit in space equivalents
     * @since 3.1
     */
    public static int computeIndentUnits(String line, int tabWidth, int indentWidth) {
        if (indentWidth == 0) return -1;
        int visualLength = measureIndentLength(line, tabWidth);
        return visualLength / indentWidth;
    }

    /**
     * Computes the visual length of the indentation of a
     * <code>CharSequence</code>, counting a tab character as the size until
     * the next tab stop and every other whitespace character as one.
     * 
     * @param line
     *            the string to measure the indent of
     * @param tabSize
     *            the visual size of a tab in space equivalents
     * @return the visual length of the indentation of <code>line</code>
     * @since 3.1
     */
    public static int measureIndentLength(CharSequence line, int tabSize) {
        int length = 0;
        int max = line.length();
        for (int i = 0; i < max; i++) {
            char ch = line.charAt(i);
            if (ch == '\t') {
                int reminder = length % tabSize;
                length += tabSize - reminder;
            } else if (isIndentChar(ch)) {
                length++;
            } else {
                return length;
            }
        }
        return length;
    }
    
    /**
     * Indent char is a space char but not a line delimiters.
     * <code>== Character.isWhitespace(ch) && ch != '\n' && ch != '\r'</code>
     */
    public static boolean isIndentChar(char ch) {
        return Character.isWhitespace(ch) && !isLineDelimiterChar(ch);
    }
    
    /**
     * Line delimiter chars are  '\n' and '\r'.
     */
    public static boolean isLineDelimiterChar(char ch) {
        return ch == '\n' || ch == '\r';
    }

	/**
	 * Returns <code>true</code> if the given string only consists of
	 * white spaces according to Ruby. If the string is empty, <code>true
	 * </code> is returned.
	 * 
	 * @return <code>true</code> if the string only consists of white
	 * 	spaces; otherwise <code>false</code> is returned
	 * 
	 * @see java.lang.Character#isWhitespace(char)
	 */
	public static boolean containsOnlyWhitespaces(String s) {
		int size= s.length();
		for (int i= 0; i < size; i++) {
			if (!Character.isWhitespace(s.charAt(i)))
				return false;
		}
		return true;
	}

}

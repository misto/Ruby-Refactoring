package org.rubypeople.rdt.internal.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public class RubyScript {
	protected String content;

	public RubyScript(String content) {
		this.content = content;
	}
	public Object[] getElements() {
		List rootComponent = new ArrayList();
		BufferedReader reader = new BufferedReader(new StringReader(content));
		RE classMatcher = createClassMatcher();

		int lineStartOffset = 0;
		String previousLine = null;
		String currentLine = null;
		try {
			while ((currentLine = reader.readLine()) != null) {
				if (classMatcher.match(currentLine)) {
					String className = getClassName(currentLine);
//					int nameOffset = lineStartOffset + currentLine.indexOf("class ");
//					int nameLength = 6 + className.length();
//					rootComponent.add(new RubyClass(className, new SourceLocation(0,0,nameOffset,nameLength)));
					rootComponent.add(className);
				}

//				if (previousLine != null)
//					lineStartOffset += previousLine.length() + NEW_LINE_CHARACTER_COUNT;

				previousLine = currentLine;
			}
		} catch (IOException e) {
		}
		return rootComponent.toArray();
	}

	protected RE createClassMatcher() {
		try {
			return new RE("[a-zA-Z ]*class [a-zA-Z]");
		} catch (RESyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getClassName(String line) {
		String classWord = "class ";
		int endOfClass = line.indexOf(classWord) + classWord.length();
		int spaceAfterClassName = line.indexOf(" ", endOfClass);
		if (spaceAfterClassName < endOfClass)
			spaceAfterClassName = line.length();

		return line.substring(endOfClass, spaceAfterClassName);
	}

}

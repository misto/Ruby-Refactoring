package org.rubypeople.rdt.internal.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public class RubyScript extends RubyElement {
	protected RubyClass currentElement;

	public RubyScript(String source) {
		super("A Ruby Script");
		parse(source);
	}
	
	protected void parse(String source) {
		currentElement = new RubyClass("cannot start in a null state");
		BufferedReader reader = new BufferedReader(new StringReader(source));
		RE classMatcher = createClassMatcher();

		String currentLine = null;
		try {
			while ((currentLine = reader.readLine()) != null) {
				if (classMatcher.match(currentLine)) {
					String className = getClassName(currentLine);
					currentElement = new RubyClass(className); 
					addElement(currentElement);
				}
				currentElement.parse(currentLine);
			}
		} catch (IOException e) {
			throw new RuntimeException("Unexpected exception occurred parsing script: "+ e.toString());
		}
	}

	protected RE createClassMatcher() {
		try {
			return new RE("[a-zA-Z ]*class [a-zA-Z]");
		} catch (RESyntaxException e) {
			throw new RuntimeException(e.toString());
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

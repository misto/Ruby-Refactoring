package org.rubypeople.rdt.internal.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class RubyParser {

	/*
	 * class Simple
	 * 		def blah
	 *			begin
	 *				
	 *			end
	 *		end
	 * end
	 */
	public RubyParser() {
		super();
	}

	public RubyParsedComponent getComponentHierarchy(IFile rubyFile) {
		String content = getFileContent(rubyFile);
		RubyParsingStringTokenizer tokenizer = new RubyParsingStringTokenizer(content);

		RubyParsedComponent parsedComponent = new RubyParsedComponent(rubyFile.getName());
		parsedComponent.nameOffset(0);
		parsedComponent.nameLength(0);
		parsedComponent.offset(0);
		parsedComponent.length(content.length());
		
		RubyParsedComponent currentClassComponent = null;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (tokenizer.hasMoreTokens())
				tokenizer.nextToken(); // consume the delimeter
			if (token.equals("class")) {
				int offset = tokenizer.currentPosition();
				String classToken = tokenizer.nextToken();
				currentClassComponent = new RubyParsedComponent(classToken);
				currentClassComponent.nameOffset(offset);
				currentClassComponent.nameLength(classToken.length());
				currentClassComponent.offset(offset - "class ".length());
				currentClassComponent.length(content.length());
				
				parsedComponent.addChild(currentClassComponent);
			}
			else
				if (token.equals("def")) {
					int offset = tokenizer.currentPosition();
					String defToken = tokenizer.nextToken();
					RubyParsedComponent defComponent = new RubyParsedComponent(defToken); 
					defComponent.nameOffset(offset);
					defComponent.nameLength(defToken.length());
					defComponent.offset(offset - "def ".length());
					defComponent.length(40);
					
					currentClassComponent.addChild(defComponent);
				}
		}

		return parsedComponent;
	}
	
	protected String getFileContent(IFile rubyFile) {
		StringBuffer fileContents = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(rubyFile.getContents()));
			String line = reader.readLine();
			while (line != null) {
				fileContents.append(line);
				if ((line = reader.readLine()) != null)
					fileContents.append("\n");
			}
		} catch (CoreException e) {
		} catch (IOException e) {
		}
		
		return fileContents.toString();
	}

}

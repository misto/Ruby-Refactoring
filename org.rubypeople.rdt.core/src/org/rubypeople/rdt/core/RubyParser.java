package org.rubypeople.rdt.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

public class RubyParser {

	public RubyParser() {
		super();
	}

	public RubyParsedComponent getComponentHierarchy(IFile rubyFile) {
		
		RubyParsingStringTokenizer tokenizer = new RubyParsingStringTokenizer(getFileContent(rubyFile));

		RubyParsedComponent parsedComponent = new RubyParsedComponent(rubyFile.getName());
		parsedComponent.nameOffset(0);
		parsedComponent.nameLength(0);
		parsedComponent.offset(0);
		parsedComponent.length(0);
		
		RubyParsedComponent currentClassComponent = null;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.equals("class")) {
				int offset = tokenizer.currentPosition();
				String classToken = tokenizer.nextToken();
				currentClassComponent = new RubyParsedComponent(classToken);
				currentClassComponent.nameOffset(offset);
				currentClassComponent.nameLength(classToken.length());
				currentClassComponent.offset(offset);
				currentClassComponent.length(0);
				
				parsedComponent.addChild(currentClassComponent);
			}
			else
				if (token.equals("def")) {
					int offset = tokenizer.currentPosition();
					String defToken = tokenizer.nextToken();
					RubyParsedComponent defComponent = new RubyParsedComponent(defToken); 
					defComponent.nameOffset(offset);
					defComponent.nameLength(defToken.length());
					defComponent.offset(offset);
					defComponent.length(0);
					
					currentClassComponent.addChild(defComponent);
				}
		}

		return parsedComponent;
	}
	
	protected String getFileContent(IFile rubyFile) {
		StringBuffer fileContents = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(rubyFile.getContents()));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fileContents.append(line);
				fileContents.append("\n");
			}
		} catch (CoreException e) {
		} catch (IOException e) {
		}
		
		return fileContents.toString();
	}

}

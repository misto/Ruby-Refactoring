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
		
		StringTokenizer tokenizer = new StringTokenizer(getFileContent(rubyFile));

		RubyParsedComponent parsedComponent = new RubyParsedComponent(rubyFile.getName());
		RubyParsedComponent currentClassComponent = null;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			if (token.equals("class")) {
				currentClassComponent = new RubyParsedComponent(tokenizer.nextToken());
				parsedComponent.addChild(currentClassComponent);
			}
			else
				if (token.equals("def"))
					currentClassComponent.addChild(new RubyParsedComponent(tokenizer.nextToken()));
				
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

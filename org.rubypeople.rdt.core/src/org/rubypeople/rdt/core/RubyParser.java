package org.rubypeople.rdt.core;

import java.util.StringTokenizer;

public class RubyParser {

	public RubyParser() {
		super();
	}

	public RubyParsedComponent getComponentHierarchy(String filename, String rubySource) {
		StringTokenizer tokenizer = new StringTokenizer(rubySource);

		RubyParsedComponent parsedComponent = new RubyParsedComponent(filename);
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

}

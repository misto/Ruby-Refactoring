package org.rubypeople.rdt.internal.core.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

import org.ablaf.ast.INode;
import org.jruby.Ruby;
import org.jruby.ast.visitor.DefaultIteratorVisitor;

public class JRubyParser {
	public static RubyFile parse(String fileName) {
		try {
			Ruby runtime = Ruby.getDefaultInstance();
			INode parsedScript = runtime.parse(getReader(fileName), fileName);
			RubyOutlineVisitor rubyOutlineVisitor = new RubyOutlineVisitor(fileName);
			parsedScript.accept(new DefaultIteratorVisitor(rubyOutlineVisitor));

			return rubyOutlineVisitor.result();
		} catch (RuntimeException e) {
			return new RubyFile("Sorry, JRuby was unable to parse " + fileName);
		}
	}

	protected static Reader getReader(String fileName) {
		try {
			return new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
}

/*
 * Created on Oct 6, 2004
 */
package org.rubypeople.rdt.internal.core.parser;

import java.io.Reader;
import java.io.StringReader;

import org.jruby.ast.Node;
import org.jruby.common.IRubyWarnings;
import org.jruby.common.NullWarnings;
import org.jruby.lexer.yacc.LexerSource;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.parser.DefaultRubyParser;
import org.jruby.parser.RubyParserConfiguration;
import org.jruby.parser.RubyParserPool;
import org.jruby.parser.RubyParserResult;

/**
 * @author cawilliams
 */
public class RubyParser {

	private final RubyParserPool pool;
	private IRubyWarnings warnings;

	public RubyParser() {
		this(new NullWarnings());
	}

	public RubyParser(IRubyWarnings warnings) {
		this.warnings = warnings;
		this.pool = RubyParserPool.getInstance();
	}

	public Node parse(String file, String content) {
		return parse(file, new StringReader(content));
	}

	public Node parse(String file, Reader content) {
		return parse(file, content, new RubyParserConfiguration());
	}

	private Node parse(String file, Reader content, RubyParserConfiguration config) {
		DefaultRubyParser parser = null;
		RubyParserResult result = null;
		try {
			parser = pool.borrowParser();
			parser.setWarnings(warnings);
			parser.init(config);
			LexerSource lexerSource = new LexerSource(file, content, new RdtPositionFactory());
			result = parser.parse(lexerSource);
		} catch (SyntaxException e) {
			throw e;
		} catch (Exception e) {
			System.err.println(e);
		} finally {
			pool.returnParser(parser);
		}
		return result.getAST();
	}

	/**
	 * @param b
	 */
	public static void setDebugging(boolean b) {
	// FIXME Enable/Disable debug logging!
	}

}
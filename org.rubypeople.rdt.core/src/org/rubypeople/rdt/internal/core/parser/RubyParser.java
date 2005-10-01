/*
 * Created on Oct 6, 2004
 */
package org.rubypeople.rdt.internal.core.parser;

import java.io.Reader;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.Node;
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
	private IRdtWarnings warnings;
	private static boolean isDebug;

	public RubyParser() {
		this(new NullRdtWarnings());
	}

	public RubyParser(IRdtWarnings warnings) {
		this.warnings = warnings;
		this.pool = RubyParserPool.getInstance();
	}

	public Node parse(IFile file, Reader content) {
		DefaultRubyParser parser = null;
        RubyParserResult result = null;
        try {
            warnings.setFile(file);
        	parser = pool.borrowParser();
        	parser.setWarnings(warnings);
        	parser.init(new RubyParserConfiguration());
        	LexerSource lexerSource = new LexerSource(file.getName(), content, new RdtPositionFactory());
        	result = parser.parse(lexerSource);
        } catch (SyntaxException e) {
        	throw e;
        } finally {
        	pool.returnParser(parser);
        }
        return result.getAST();
	}

	public static void setDebugging(boolean b) {
		isDebug = b;
	}

	public static boolean isDebugging() {
		return isDebug;
	}

    private static class NullRdtWarnings extends NullWarnings implements IRdtWarnings {
        public void setFile(IFile file) {
        }
    }


}
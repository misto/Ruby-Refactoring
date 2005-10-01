package org.rubypeople.rdt.internal.core.builder;

import java.io.Reader;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class ShamRubyParser extends RubyParser {

    private IFile fileArg;
    private String contentArg;
    private SyntaxException syntaxException;
    private Node parseResult;

    public Node parse(IFile file, Reader reader) {
        fileArg = file;
        contentArg = IoUtils.readAllQuietly(reader);
        if (syntaxException != null)
            throw syntaxException;
        return parseResult;
    }

    public void assertParsed(IFile expectedFile, String expectedContent) {
        Assert.assertEquals("File", expectedFile, fileArg);
        Assert.assertEquals("Content", expectedContent, contentArg);
    }

    public void setExceptionToThrow(SyntaxException syntaxException) {
        this.syntaxException = syntaxException;
    }

    public void setParseResult(Node parseResult) {
        this.parseResult = parseResult;
        
    }
}

/*
 * Author: David Corbin
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */


package org.rubypeople.rdt.internal.core.builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.parser.ImmediateWarnings;
import org.rubypeople.rdt.internal.core.parser.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public final class RubyCodeAnalyzer implements SingleFileCompiler {
    private final IMarkerManager markerManager;
    private RubyParser parser;
    private final IndexUpdater indexUpdater;

    public RubyCodeAnalyzer(IMarkerManager markerManager) {
        this(markerManager, new RubyParser(new ImmediateWarnings(markerManager)), new IndexUpdater(((RubyCore) RubyCore.getPlugin()).getSymbolIndex()));
    }
    
    public RubyCodeAnalyzer(IMarkerManager markerManager, RubyParser parser, IndexUpdater indexUpdater) {
        this.markerManager = markerManager;
        this.parser = parser;
        this.indexUpdater = indexUpdater;
    }

    public void compileFile(IFile file) throws CoreException {
        Reader reader = new InputStreamReader(file.getContents());
        // XXX Make sure readContents isn't dropping end of line characters
        // XXX Use a StringReader for the parser since we've already read it all in once before? 
        String contents = readContents(reader);
        markerManager.removeProblemsAndTasksFor(file);
        try {
            Node rootNode = parser.parse(file, new StringReader(contents));
            RubyLintVisitor visitor = new RubyLintVisitor(contents, new ProblemRequestorMarkerManager(file, markerManager));
            rootNode.accept(visitor);
            indexUpdater.update(file, rootNode, true);
        } catch (SyntaxException e) {
            markerManager.createSyntaxError(file, e);
        } finally {
            IoUtils.closeQuietly(reader);
        }
    }

	private String readContents(Reader reader) {
		try {
			BufferedReader buff = new BufferedReader(reader);
			StringBuffer str = new StringBuffer();
			String line;
			while((line = buff.readLine()) != null) {
				str.append(line);
				str.append("\n");
			}
			return str.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
	}

}
/**
 * 
 */
package org.rubypeople.rdt.internal.core;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.ast.Node;
import org.jruby.ast.visitor.NodeVisitor;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RdtWarnings;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.TaskParser;
import org.rubypeople.rdt.internal.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.internal.core.parser.warnings.RubyLintVisitor;

/**
 * @author Chris
 * 
 */
public class RubyScriptProblemFinder {

    // DSC convert to ImmediateWarnings
    public static void process(RubyScript script, char[] charContents, HashMap problems, IProgressMonitor pm) {
        RdtWarnings warnings = new RdtWarnings();
        RubyParser parser = new RubyParser(warnings);
        String contents = new String(charContents);

//        runLint(script, problemRequestor, parser, contents); FIXME Make all these compilationParticipants

        TaskParser taskParser = new TaskParser(script.getRubyProject().getOptions(true));
        taskParser.parse(contents);

        problems.put(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, warnings.getWarnings());
        problems.put(IRubyModelMarker.TASK_MARKER, taskParser.getTasks());
    }

	private static void runLint(RubyScript script, IProblemRequestor problemRequestor, RubyParser parser, String contents) {
		try {
            Node node = parser.parse((IFile) script.getUnderlyingResource(), new StringReader(contents));
            if (node == null) return;
            List<RubyLintVisitor> visitors = DelegatingVisitor.createVisitors(contents, problemRequestor);
			NodeVisitor visitor = new DelegatingVisitor(visitors);
            node.accept(visitor);
        } catch (SyntaxException e) {
        	// Eat the exception
//            problemRequestor.acceptProblem(new Error(e.getPosition(), e.getMessage()));
        } catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

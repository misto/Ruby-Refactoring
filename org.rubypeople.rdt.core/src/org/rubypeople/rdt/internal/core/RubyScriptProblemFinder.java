/**
 * 
 */
package org.rubypeople.rdt.internal.core;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.parser.IProblem;
import org.rubypeople.rdt.internal.core.parser.Error;
import org.rubypeople.rdt.internal.core.parser.RdtWarnings;
import org.rubypeople.rdt.internal.core.parser.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.TaskParser;

/**
 * @author Chris
 * 
 */
public class RubyScriptProblemFinder {

    // DSC convert to ImmediateWarnings
    public static void process(RubyScript script, char[] charContents,
            IProblemRequestor problemRequestor, IProgressMonitor pm) {
        RdtWarnings warnings = new RdtWarnings();
        RubyParser parser = new RubyParser(warnings);
        String contents = new String(charContents);
        try {
            Node node = parser.parse((IFile) script.getUnderlyingResource(), new StringReader(contents));
            // FIXME We're double marking problems here. We create markers for them when we build, and then create temporary annotations when we reconcile. We need to "toss" out any duplicates generated here.
            RubyLintVisitor visitor = new RubyLintVisitor(contents, problemRequestor);
            node.accept(visitor);
        } catch (SyntaxException e) {
            problemRequestor.acceptProblem(new Error(e.getPosition(), "Syntax Error"));
        }

        TaskParser taskParser = new TaskParser(script.getRubyProject().getOptions(true));
        taskParser.parse(contents);

        List problems = new ArrayList();
        problems.addAll(warnings.getWarnings());
        problems.addAll(taskParser.getTasks());
        for (Iterator iter = problems.iterator(); iter.hasNext();) {
            IProblem problem = (IProblem) iter.next();
            problemRequestor.acceptProblem(problem);
        }
    }

}

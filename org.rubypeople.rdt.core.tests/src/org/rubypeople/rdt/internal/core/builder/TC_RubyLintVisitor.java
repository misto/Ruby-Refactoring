package org.rubypeople.rdt.internal.core.builder;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jruby.ast.Node;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.parser.IProblem;
import org.rubypeople.rdt.internal.core.parser.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class TC_RubyLintVisitor extends TestCase {

	class MockProblemRequestor implements IProblemRequestor {
		List problems;

		public MockProblemRequestor() {
			problems = new ArrayList();
		}
		
		public void acceptProblem(IProblem problem) {
			problems.add(problem);			
		}

		public void beginReporting() {
		}

		public void endReporting() {
		}

		public boolean isActive() {
			return false;
		}
	}
	
	private MockProblemRequestor problemRequestor;
	
	public void testUnlessModififerDoesntCreateEmptyConditionalWarning() throws Exception {
		runLint("@var = 3 unless @blah");
		assertEquals(0, problemRequestor.problems.size());
	}
	

	public void testUnlessConditionalDoesntCreateEmptyConditionalWarning() throws Exception {
		runLint("unless @blah\n  @var = 3\nend");
		System.out.println(problemRequestor.problems.get(0));
		assertEquals(0, problemRequestor.problems.size());
	}

	private void runLint(String contents) {
		RubyParser parser = new RubyParser();
		Node rootNode = parser.parse(new ShamFile("fake/path.rb"), new StringReader(contents));
		problemRequestor = new MockProblemRequestor();
		RubyLintVisitor visitor = new RubyLintVisitor(contents,
				problemRequestor);
		rootNode.accept(visitor);
	}
}

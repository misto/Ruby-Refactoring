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
	
	public void testBlah() throws Exception {
		String contents = "@var = 3 unless @blah";
		RubyParser parser = new RubyParser();
		Node rootNode = parser.parse(new ShamFile("fake/path.rb"), new StringReader(contents));
		MockProblemRequestor problemRequestor = new MockProblemRequestor();
		RubyLintVisitor visitor = new RubyLintVisitor(contents,
				problemRequestor);
		rootNode.accept(visitor);
		assertEquals(1, problemRequestor.problems.size());
	}
}

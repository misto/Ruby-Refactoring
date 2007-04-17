package org.rubypeople.rdt.internal.core.search.matching;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyScript;

public class MethodLocator extends PatternLocator {

	private MethodPattern pattern;

	public MethodLocator(MethodPattern pattern) {
		super(pattern);
		this.pattern = pattern;
	}

	@Override
	public void reportMatches(RubyScript script, MatchLocator locator) {
		reportMatches((IParent) script, locator);
	}

	private void reportMatches(IParent parent, MatchLocator locator) {
		try {
			IRubyElement[] children = parent.getChildren();
			for (int i = 0; i < children.length; i++) {
				IRubyElement child = children[i];
				if (child.isType(IRubyElement.METHOD)) {
					int accuracy = getAccuracy((IMethod) child);
					if (accuracy != IMPOSSIBLE_MATCH) {
						IMember member = (IMember) child;
						ISourceRange range = member.getSourceRange();
						try {
							locator.report(locator.newDeclarationMatch(child, accuracy, range.getOffset(), range.getLength()));
						} catch (CoreException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				if (child instanceof IParent) {
					IParent parentTwo = (IParent) child;
					reportMatches(parentTwo, locator);
				}
			}
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private int getAccuracy(IMethod method) {
		if (!this.pattern.findDeclarations)
			return IMPOSSIBLE_MATCH;

		// Verify method name
		if (!matchesName(this.pattern.selector, method.getElementName().toCharArray()))
			return IMPOSSIBLE_MATCH;

		// Verify parameter count
		if (this.pattern.parameterNames != null) {
			int length = this.pattern.parameterNames.length;
			String[] args = null;
			try {
				args = method.getParameterNames();
			} catch (RubyModelException e) {
				// ignore
			}
			int argsLength = args == null ? 0 : args.length;
			if (length != argsLength)
				return IMPOSSIBLE_MATCH;
		}

		// Method declaration may match pattern
		return ACCURATE_MATCH;
	}

}

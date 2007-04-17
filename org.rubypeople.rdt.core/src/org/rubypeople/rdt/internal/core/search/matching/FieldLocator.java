package org.rubypeople.rdt.internal.core.search.matching;

import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyScript;

public class FieldLocator extends PatternLocator {

	private FieldPattern pattern;

	public FieldLocator(FieldPattern pattern) {
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
				if (child.isType(IRubyElement.FIELD) || 
						child.isType(IRubyElement.GLOBAL) ||
						child.isType(IRubyElement.CONSTANT) ||
						child.isType(IRubyElement.CLASS_VAR) ||
						child.isType(IRubyElement.INSTANCE_VAR)) {
					int accuracy = getAccuracy((IField) child);
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

	private int getAccuracy(IField field) {
		if (this.pattern.findReferences)
			// must be a write only access with an initializer
			if (this.pattern.writeAccess)
				if (matchesName(this.pattern.name, field.getElementName().toCharArray()))
					return ACCURATE_MATCH;

		if (this.pattern.findDeclarations) {
			if (matchesName(this.pattern.name, field.getElementName().toCharArray()))
				return ACCURATE_MATCH;

		}
		return IMPOSSIBLE_MATCH;
	}

}

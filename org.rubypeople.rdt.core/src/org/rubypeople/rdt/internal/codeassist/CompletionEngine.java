package org.rubypeople.rdt.internal.codeassist;

import java.util.Iterator;
import java.util.List;

import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.core.CompletionRequestor;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ti.DefaultTypeInferrer;
import org.rubypeople.rdt.internal.ti.ITypeGuess;
import org.rubypeople.rdt.internal.ti.ITypeInferrer;

public class CompletionEngine {
	private CompletionRequestor requestor;

	public CompletionEngine(CompletionRequestor requestor) {
		this.requestor = requestor;
	}

	public void complete(IRubyScript script, int offset) throws RubyModelException {
		this.requestor.beginReporting();
		if (offset < 0)
			offset = 0;
		ITypeInferrer inferrer = new DefaultTypeInferrer();

		StringBuffer source = new StringBuffer(script.getSource());
		int replaceStart = offset + 1;
		// Read from offset back until we hit a: space, period
		// if we hit a period, use character before period as offset for
		// inferrer
		// if we hit a space, use character after space?
		for (int i = offset; i >= 0; i--) {
			char curChar = (char) source.charAt(i);
			if (curChar == '.') {
				if (offset == i) { // if it's the first character we looked at,
					// fix syntax
					source.deleteCharAt(i);
					offset--;
					break;
				}
				// TODO Grab the prefix we just ate up and use it to filter
				// responses?
				offset = i - 1;
				break;
			}
			if (Character.isWhitespace(curChar)) {
				offset = i + 1;
				break;
			}
		}
		System.out.println((char) source.charAt(offset));

		List<ITypeGuess> guesses = inferrer.infer(source.toString(), offset);
		// TODO Grab the project and all referred projects!
		IRubyProject[] projects = new IRubyProject[1];
		projects[0] = script.getRubyProject();
		RubyElementRequestor completer = new RubyElementRequestor(projects);
		for (Iterator iter = guesses.iterator(); iter.hasNext();) {
			ITypeGuess guess = (ITypeGuess) iter.next();
			IType type = completer.findType(guess.getType());
			suggestMethods(requestor, replaceStart, completer, guess, type);
		}
	}

	private void suggestMethods(CompletionRequestor requestor,
			int replaceStart, RubyElementRequestor completer, ITypeGuess guess,
			IType type) throws RubyModelException {
		if (type == null)
			return;

		suggestMethods(requestor, replaceStart, guess.getConfidence(), type);
		// Now grab methods from all the included modules
		String[] modules = type.getIncludedModuleNames();
		for (int x = 0; x < modules.length; x++) {
			IType tmpType = completer.findType(modules[x]);
			suggestMethods(requestor, replaceStart, guess.getConfidence(),
					tmpType);
		}
		String superClass = type.getSuperclassName();
		// FIXME This shouldn't happen! Object shouldn't be a parent of itself!
		if (type.getElementName().equals("Object")
				&& superClass.equals("Object"))
			return;
		IType parentClass = completer.findType(superClass);
		suggestMethods(requestor, replaceStart, completer, guess, parentClass);
	}

	private void suggestMethods(CompletionRequestor requestor,
			int replaceStart, int confidence, IType type)
			throws RubyModelException {
		if (type == null)
			return;
		IMethod[] methods = type.getMethods();
		for (int k = 0; k < methods.length; k++) {
			IMethod method = methods[k];
			String name = method.getElementName();
			CompletionProposal proposal = new CompletionProposal(
					CompletionProposal.METHOD_REF, name, confidence);
			// TODO Handle replacement start index correctly
			proposal
					.setReplaceRange(replaceStart, replaceStart + name.length());
			int flags = Flags.AccDefault;
			if (method.isSingleton()) {
				flags |= Flags.AccStatic;
			}
			switch (method.getVisibility()) {
			case IMethod.PRIVATE:
				flags |= Flags.AccPrivate;
				break;
			case IMethod.PUBLIC:
				flags |= Flags.AccPublic;
				break;
			case IMethod.PROTECTED:
				flags |= Flags.AccProtected;
				break;
			default:
				break;
			}
			proposal.setFlags(flags);
			requestor.accept(proposal);
		}
	}

}

/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.refactoring.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jruby.RubyClass;
import org.jruby.ast.Node;
import org.jruby.exceptions.RaiseException;
import org.jruby.runtime.Arity;
import org.rubypeople.rdt.refactoring.RefactoringPlugin;
import org.rubypeople.rdt.refactoring.core.NodeProvider;

public class ArityEvaluator {

	private String className;

	private String methodName;

	public ArityEvaluator(String className, String methodName) {
		this.className = className;
		this.methodName = methodName;
	}

	public int evaluateRequiredArity(Arity arity, boolean isModule) {
		int value = arity.getValue();
		if (value >= 0)
			return value;
		if (value < -1)
			return -(1 + value);
		String script;
		if (methodName.equals("initialize")) {
			script = className + ".new";
			return evaluateArity(script, true);
		}
		if (isModule) {
			script = className + '.' + methodName;
			return evaluateArity(script, true);
		} 
		int constructorArity = evaluateArity(className + ".new", false);
		String constructorArgs = getDummyArgs(constructorArity);
		script = "x = " + className + ".new" + constructorArgs + "\nx." + methodName;
		return evaluateArity(script, true);
	}

	private int evaluateArity(String scriptPart, boolean ignoreExceptions) {
		int arityGuess = 0;
		boolean success = false;
		while (!success) {
			String args = getDummyArgs(arityGuess);
			String script = scriptPart + args;
			String error = getScriptEvaluationError(script, ignoreExceptions);
			if (error.equals(""))
				success = true;
			else {
				int errorGuess = getArityFromError(error);
				if (errorGuess > arityGuess)
					arityGuess = errorGuess;
				else
					arityGuess++;
			}
		}
		return arityGuess;
	}

	private String getScriptEvaluationError(String script, boolean ignoreExceptions) {
		Node node = NodeProvider.getRootNode("test", script);
		try {
			RefactoringPlugin.getRuby().eval(node);
		} catch (RaiseException e) {
			RubyClass metaClass = e.getException().getMetaClass();
			if (metaClass.getName().equalsIgnoreCase(Constants.ARGUMENT_ERROR))
				return e.getMessage();
			else if (!ignoreExceptions)
				throw e;
		}
		return "";
	}

	private int getArityFromError(String error) {
		Pattern pattern = Pattern.compile("\\(([0-9])+ for ([0-9]+)\\)");
		Matcher matcher = pattern.matcher(error);
		if (!matcher.find())
			return 0;
		return Integer.parseInt(matcher.group(2));
	}

	private String getDummyArgs(int argGuess) {
		if (argGuess == 0)
			return "";
		StringBuilder args = new StringBuilder(" ");
		for (int i = 0; i < argGuess; i++)
			args.append("\"\", ");
		return args.substring(0, args.length() - 2);
	}

}

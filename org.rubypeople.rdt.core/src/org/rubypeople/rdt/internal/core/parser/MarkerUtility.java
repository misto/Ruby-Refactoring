/*
 * Created on Feb 20, 2005
 *
 */
package org.rubypeople.rdt.internal.core.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.core.RubyCore;

/**
 * @author Chris
 * 
 */
public class MarkerUtility {

	/**
	 * @param underlyingResource
	 * @param syntaxException
	 * @param contentLength
	 */
	public static void createSyntaxError(IResource underlyingResource, SyntaxException syntaxException) {
		try {
			ISourcePosition pos = syntaxException.getPosition();
			IMarker marker = underlyingResource.createMarker(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER);
			Map map = new HashMap();
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
			map.put(IMarker.MESSAGE, syntaxException.getMessage());
			map.put(IMarker.USER_EDITABLE, Boolean.FALSE);
			map.put(IMarker.LINE_NUMBER, new Integer(pos.getLine()));
			map.put(IMarker.CHAR_START, new Integer(pos.getStartOffset()));
			map.put(IMarker.CHAR_END, new Integer(pos.getEndOffset()));
			marker.setAttributes(map);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

	/**
	 * @param underlyingResource
	 */
	public static void removeMarkers(IResource underlyingResource) {
		try {
			underlyingResource.deleteMarkers(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

	/**
	 * @param underlyingResource
	 * @param warnings
	 */
	public static void createWarnings(IResource underlyingResource, List warnings) {
		for (Iterator iter = warnings.iterator(); iter.hasNext();) {
			createWarning(underlyingResource, (Warning) iter.next());
		}
	}

	/**
	 * @param underlyingResource
	 * @param warning
	 */
	private static void createWarning(IResource underlyingResource, Warning warning) {
		try {
			// TODO Combine with createSyntaxError code!
			ISourcePosition pos = warning.getPosition();
			IMarker marker = underlyingResource.createMarker(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER);
			Map map = new HashMap();
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING));
			map.put(IMarker.MESSAGE, warning.getMessage());
			map.put(IMarker.USER_EDITABLE, Boolean.FALSE);
			map.put(IMarker.LINE_NUMBER, new Integer(pos.getLine()));
			map.put(IMarker.CHAR_START, new Integer(pos.getStartOffset()));
			map.put(IMarker.CHAR_END, new Integer(pos.getEndOffset()));
			marker.setAttributes(map);
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

}

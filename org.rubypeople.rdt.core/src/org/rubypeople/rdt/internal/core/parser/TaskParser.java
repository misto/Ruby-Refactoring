/*
 * Created on Feb 18, 2005
 *
 */
package org.rubypeople.rdt.internal.core.parser;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.rubypeople.rdt.core.RubyCore;

/**
 * @author Chris
 * 
 */
public class TaskParser {

	private boolean fCaseSensitive = false;
	private String[] fTags;
	private int[] fPriorities;

	/**
	 * @param preferences
	 */
	public TaskParser(IEclipsePreferences preferences) {
		String caseSensitive = preferences.get(RubyCore.COMPILER_TASK_CASE_SENSITIVE, RubyCore.ENABLED);
		if (caseSensitive == RubyCore.ENABLED) fCaseSensitive = true;
		String tags = preferences.get(RubyCore.COMPILER_TASK_TAGS, RubyCore.DEFAULT_TASK_TAGS);
		String priorities = preferences.get(RubyCore.COMPILER_TASK_PRIORITIES, RubyCore.DEFAULT_TASK_PRIORITIES);
		fTags = tokenize(tags, ",");
		fPriorities = convertPriorities(tokenize(priorities, ","));
	}

	/**
	 * @param stringPriorities
	 * @return
	 */
	private int[] convertPriorities(String[] stringPriorities) {
		int priorities[] = new int[stringPriorities.length];
		for (int i = 0; i < stringPriorities.length; i++) {
			String priority = stringPriorities[i];
			if (priority.equals(RubyCore.COMPILER_TASK_PRIORITY_LOW)) {
				priorities[i] = IMarker.PRIORITY_LOW;
			} else if (priority.equals(RubyCore.COMPILER_TASK_PRIORITY_HIGH)) {
				priorities[i] = IMarker.PRIORITY_HIGH;
			} else {
				priorities[i] = IMarker.PRIORITY_NORMAL;
			}
		}
		return priorities;
	}

	/**
	 * @param tags
	 * @param delim
	 * @return
	 */
	private String[] tokenize(String tags, String delim) {
		String[] tokens;
		StringTokenizer tokenizer = new StringTokenizer(tags, delim);
		tokens = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens[i++] = tokenizer.nextToken();
		}
		return tokens;
	}

	/**
	 * @param underlyingResource
	 * @param contents
	 */
	public void parse(IResource resource, String contents) {
		if (fTags.length <= 0) return;
		try {
			resource.deleteMarkers(IMarker.TASK, false, 0);
			StringTokenizer tokenizer = new StringTokenizer(contents, "\n", true);
			int line = 0;
			int offset = 0;
			boolean lastWasNewLine = false;
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (token.equals("\n")) {
					if (lastWasNewLine) {
						line++;
					}
					lastWasNewLine = true;
					offset += token.length();
					continue; // skip newline characters
				}
				lastWasNewLine = false;
				String original = token;
				if (!fCaseSensitive) token = token.toLowerCase();
				for (int i = 0; i < fTags.length; i++) {
					String tag = fTags[i];
					int priority = fPriorities[i];
					if (!fCaseSensitive) tag = tag.toLowerCase();
					int index = token.indexOf(tag);
					if (index == -1) continue;
					String message = original.substring(index).trim();
					createTaskMarker(resource, priority, message, line + 1, offset + index, offset + index + message.length());
				}
				offset += token.length();
				line++;
			}
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

	/**
	 * @param resource
	 * @param message
	 * @param lineNumber
	 * @throws CoreException
	 */
	private static void createTaskMarker(IResource resource, int priority, String message, int lineNumber, int start, int end) throws CoreException {
		createMarker(resource, priority, message, lineNumber, start, end, IMarker.TASK, 1, false, false);
	}

	private static void createMarker(IResource resource, int priority, String message, int lineNumber, int start, int end, String markerType, int severity, boolean userEditable, boolean istransient) throws CoreException {
		if (lineNumber <= 0) lineNumber = 1;
		IMarker marker = markerExists(resource, message, lineNumber, markerType);
		if (marker == null) {
			HashMap map = new HashMap();
			map.put(IMarker.PRIORITY, new Integer(priority));
			map.put(IMarker.MESSAGE, message);
			map.put(IMarker.LINE_NUMBER, new Integer(lineNumber));
			map.put(IMarker.SEVERITY, new Integer(severity));
			map.put(IMarker.USER_EDITABLE, new Boolean(userEditable));
			map.put(IMarker.TRANSIENT, new Boolean(istransient));
			map.put(IMarker.CHAR_START, new Integer(start));
			map.put(IMarker.CHAR_END, new Integer(end));
			marker = resource.createMarker(markerType);
			marker.setAttributes(map);
		}
	}

	public static IMarker markerExists(IResource resource, String message, int lineNumber, String type) throws CoreException {
		IMarker tasks[] = resource.findMarkers(type, true, 0);
		for (int i = 0; i < tasks.length; i++) {
			if (tasks[i].getAttribute(IMarker.LINE_NUMBER).toString().equals(String.valueOf(lineNumber)) && tasks[i].getAttribute(IMarker.MESSAGE).equals(message)) return tasks[i];
		}
		return null;
	}
}

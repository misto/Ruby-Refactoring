/*
 * Created on Feb 18, 2005
 *
 */
package org.rubypeople.rdt.internal.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
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
	private List tasks;

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
		tasks = new ArrayList();
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
	 * @param contents
	 */
	public void parse(String contents) {
		if (fTags.length <= 0) return;
		try {
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
					createTaskTag(priority, message, line + 1, offset + index, offset + index + message.length());
				}
				offset += token.length();
				line++;
			}
		} catch (CoreException e) {
			RubyCore.log(e);
		}
	}

	/**
	 * @param priority
	 * @param message
	 * @param lineNumber
	 * @param start
	 * @param end
	 * @throws CoreException
	 */
	private void createTaskTag(int priority, String message, int lineNumber, int start, int end) throws CoreException {
		TaskTag task = new TaskTag(message, priority, lineNumber, start, end);
		tasks.add(task);
	}

	/**
	 * 
	 * @param reader
	 */
	public void parse(InputStreamReader reader) {
		StringBuffer buffer = new StringBuffer();
		BufferedReader buffered = null;
		try {			
			buffered = new BufferedReader(reader);
			String line;
			while ((line = buffered.readLine()) != null) {
				buffer.append(line);
				buffer.append('\n');
			}
			parse(buffer.toString());
		} catch (IOException e) {
			RubyCore.log(e);
		} finally {
			try {
				if (buffered != null) buffered.close();
			} catch (IOException e) {
				// ignore
			}
		}		
	}

	public List getTasks() {
		return Collections.unmodifiableList(tasks);
	}
}

/*
 * Created on Jan 29, 2005
 *
 */
package org.rubypeople.rdt.internal.core.util;

import java.io.PrintStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * @author Chris
 * 
 */
public class Util {

	/* Bundle containing messages */
	protected static ResourceBundle bundle;
	private final static String bundleName = "org.rubypeople.rdt.internal.core.util.messages"; //$NON-NLS-1$

	private final static char[] DOUBLE_QUOTES = "''".toCharArray(); //$NON-NLS-1$
	private final static char[] SINGLE_QUOTE = "'".toCharArray(); //$NON-NLS-1$

	static {
		relocalize();
	}

	private Util() {
	// cannot be instantiated
	}

	/**
	 * Creates a NLS catalog for the given locale.
	 */
	public static void relocalize() {
		try {
			bundle = ResourceBundle.getBundle(bundleName, Locale.getDefault());
		} catch (MissingResourceException e) {
			System.out.println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + Locale.getDefault()); //$NON-NLS-1$//$NON-NLS-2$
			throw e;
		}
	}

	/**
	 * Lookup the message with the given ID in this catalog
	 */
	public static String bind(String id) {
		return bind(id, (String[]) null);
	}

	/**
	 * Lookup the message with the given ID in this catalog and bind its
	 * substitution locations with the given string values.
	 */
	public static String bind(String id, String[] arguments) {
		if (id == null) return "No message available"; //$NON-NLS-1$
		String message = null;
		try {
			message = bundle.getString(id);
		} catch (MissingResourceException e) {
			// If we got an exception looking for the message, fail gracefully
			// by just returning
			// the id we were looking for. In most cases this is
			// semi-informative so is not too bad.
			return "Missing message: " + id + " in: " + bundleName; //$NON-NLS-2$ //$NON-NLS-1$
		}
		// for compatibility with MessageFormat which eliminates double quotes
		// in original message
		char[] messageWithNoDoubleQuotes = CharOperation.replace(message.toCharArray(), DOUBLE_QUOTES, SINGLE_QUOTE);

		if (arguments == null) return new String(messageWithNoDoubleQuotes);

		int length = messageWithNoDoubleQuotes.length;
		int start = 0;
		int end = length;
		StringBuffer output = null;
		while (true) {
			if ((end = CharOperation.indexOf('{', messageWithNoDoubleQuotes, start)) > -1) {
				if (output == null) output = new StringBuffer(length + arguments.length * 20);
				output.append(messageWithNoDoubleQuotes, start, end - start);
				if ((start = CharOperation.indexOf('}', messageWithNoDoubleQuotes, end + 1)) > -1) {
					int index = -1;
					String argId = new String(messageWithNoDoubleQuotes, end + 1, start - end - 1);
					try {
						index = Integer.parseInt(argId);
						if (arguments[index] == null) {
							output.append('{').append(argId).append('}'); // leave
																			// parameter
																			// in
																			// since
																			// no
																			// better
																			// arg
																			// '{0}'
						} else {
							output.append(arguments[index]);
						}
					} catch (NumberFormatException nfe) { // could be nested
															// message ID
															// {compiler.name}
						boolean done = false;
						if (!id.equals(argId)) {
							String argMessage = null;
							try {
								argMessage = bundle.getString(argId);
								output.append(argMessage);
								done = true;
							} catch (MissingResourceException e) {
								// unable to bind argument, ignore (will leave
								// argument in)
							}
						}
						if (!done) output.append(messageWithNoDoubleQuotes, end + 1, start - end);
					} catch (ArrayIndexOutOfBoundsException e) {
						output.append("{missing " + Integer.toString(index) + "}"); //$NON-NLS-2$ //$NON-NLS-1$
					}
					start++;
				} else {
					output.append(messageWithNoDoubleQuotes, end, length);
					break;
				}
			} else {
				if (output == null) return new String(messageWithNoDoubleQuotes);
				output.append(messageWithNoDoubleQuotes, start, length - start);
				break;
			}
		}
		return output.toString();
	}
	
	/**
	 * Lookup the message with the given ID in this catalog and bind its
	 * substitution locations with the given string.
	 */
	public static String bind(String id, String binding) {
		return bind(id, new String[] {binding});
	}
	
	/**
	 * Lookup the message with the given ID in this catalog and bind its
	 * substitution locations with the given strings.
	 */
	public static String bind(String id, String binding1, String binding2) {
		return bind(id, new String[] {binding1, binding2});
	}
	
	/*
	 * Returns whether the given resource path matches one of the inclusion/exclusion
	 * patterns.
	 * NOTE: should not be asked directly using pkg root pathes
	 * @see IClasspathEntry#getInclusionPatterns
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IPath resourcePath, char[][] inclusionPatterns, char[][] exclusionPatterns, boolean isFolderPath) {
		if (inclusionPatterns == null && exclusionPatterns == null) return false;
		return isExcluded(resourcePath.toString().toCharArray(), inclusionPatterns, exclusionPatterns, isFolderPath);
	}	

	/*
	 * Returns whether the given resource matches one of the exclusion patterns.
	 * NOTE: should not be asked directly using pkg root pathes
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(IResource resource, char[][] inclusionPatterns, char[][] exclusionPatterns) {
		IPath path = resource.getFullPath();
		// ensure that folders are only excluded if all of their children are excluded
		return isExcluded(path, inclusionPatterns, exclusionPatterns, resource.getType() == IResource.FOLDER);
	}
	
	/* TODO (philippe) should consider promoting it to CharOperation
	 * Returns whether the given resource path matches one of the inclusion/exclusion
	 * patterns.
	 * NOTE: should not be asked directly using pkg root pathes
	 * @see IClasspathEntry#getInclusionPatterns
	 * @see IClasspathEntry#getExclusionPatterns
	 */
	public final static boolean isExcluded(char[] path, char[][] inclusionPatterns, char[][] exclusionPatterns, boolean isFolderPath) {
		if (inclusionPatterns == null && exclusionPatterns == null) return false;

		inclusionCheck: if (inclusionPatterns != null) {
			for (int i = 0, length = inclusionPatterns.length; i < length; i++) {
				char[] pattern = inclusionPatterns[i];
				char[] folderPattern = pattern;
				if (isFolderPath) {
					int lastSlash = CharOperation.lastIndexOf('/', pattern);
					if (lastSlash != -1 && lastSlash != pattern.length-1){ // trailing slash -> adds '**' for free (see http://ant.apache.org/manual/dirtasks.html)
						int star = CharOperation.indexOf('*', pattern, lastSlash);
						if ((star == -1
								|| star >= pattern.length-1 
								|| pattern[star+1] != '*')) {
							folderPattern = CharOperation.subarray(pattern, 0, lastSlash);
						}
					}
				}
				if (CharOperation.pathMatch(folderPattern, path, true, '/')) {
					break inclusionCheck;
				}
			}
			return true; // never included
		}
		if (isFolderPath) {
			path = CharOperation.concat(path, new char[] {'*'}, '/');
		}
		exclusionCheck: if (exclusionPatterns != null) {
			for (int i = 0, length = exclusionPatterns.length; i < length; i++) {
				if (CharOperation.pathMatch(exclusionPatterns[i], path, true, '/')) {
					return true;
				}
			}
		}
		return false;
	}			
	
	public static void verbose(String log) {
		verbose(log, System.out);
	}
	public static synchronized void verbose(String log, PrintStream printStream) {
		int start = 0;
		do {
			int end = log.indexOf('\n', start);
			printStream.print(Thread.currentThread());
			printStream.print(" "); //$NON-NLS-1$
			printStream.print(log.substring(start, end == -1 ? log.length() : end+1));
			start = end+1;
		} while (start != 0);
		printStream.println();
	}

}

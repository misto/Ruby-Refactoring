/*
 * Created on Jan 29, 2005
 *
 */
package org.rubypeople.rdt.internal.core.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * @author Chris
 * 
 */
public class Util {

    /* Bundle containing messages */
    protected static ResourceBundle bundle;
	private static boolean ENABLE_RUBY_LIKE_EXTENSIONS = true;
	private static char[][] RUBY_LIKE_EXTENSIONS;
	private static char[][] RUBY_LIKE_FILENAMES;
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
            System.out
                    .println("Missing resource : " + bundleName.replace('.', '/') + ".properties for locale " + Locale.getDefault()); //$NON-NLS-1$//$NON-NLS-2$
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
        char[] messageWithNoDoubleQuotes = CharOperation.replace(message.toCharArray(),
                DOUBLE_QUOTES, SINGLE_QUOTE);

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
        return bind(id, new String[] { binding});
    }

    /**
     * Lookup the message with the given ID in this catalog and bind its
     * substitution locations with the given strings.
     */
    public static String bind(String id, String binding1, String binding2) {
        return bind(id, new String[] { binding1, binding2});
    }

    /*
     * Returns whether the given resource path matches one of the
     * inclusion/exclusion patterns. NOTE: should not be asked directly using
     * pkg root pathes
     * 
     * @see IClasspathEntry#getInclusionPatterns
     * @see IClasspathEntry#getExclusionPatterns
     */
    public final static boolean isExcluded(IPath resourcePath, char[][] inclusionPatterns,
            char[][] exclusionPatterns, boolean isFolderPath) {
        if (inclusionPatterns == null && exclusionPatterns == null) return false;
        return isExcluded(resourcePath.toString().toCharArray(), inclusionPatterns,
                exclusionPatterns, isFolderPath);
    }

    /*
     * Returns whether the given resource matches one of the exclusion patterns.
     * NOTE: should not be asked directly using pkg root pathes
     * 
     * @see IClasspathEntry#getExclusionPatterns
     */
    public final static boolean isExcluded(IResource resource, char[][] inclusionPatterns,
            char[][] exclusionPatterns) {
        IPath path = resource.getFullPath();
        // ensure that folders are only excluded if all of their children are
        // excluded
        return isExcluded(path, inclusionPatterns, exclusionPatterns,
                resource.getType() == IResource.FOLDER);
    }

    /*
     * TODO (philippe) should consider promoting it to CharOperation Returns
     * whether the given resource path matches one of the inclusion/exclusion
     * patterns. NOTE: should not be asked directly using pkg root pathes
     * 
     * @see IClasspathEntry#getInclusionPatterns
     * @see IClasspathEntry#getExclusionPatterns
     */
    public final static boolean isExcluded(char[] path, char[][] inclusionPatterns,
            char[][] exclusionPatterns, boolean isFolderPath) {
        if (inclusionPatterns == null && exclusionPatterns == null) return false;

        inclusionCheck: if (inclusionPatterns != null) {
            for (int i = 0, length = inclusionPatterns.length; i < length; i++) {
                char[] pattern = inclusionPatterns[i];
                char[] folderPattern = pattern;
                if (isFolderPath) {
                    int lastSlash = CharOperation.lastIndexOf('/', pattern);
                    if (lastSlash != -1 && lastSlash != pattern.length - 1) { // trailing
                        // slash
                        // ->
                        // adds
                        // '**'
                        // for
                        // free
                        // (see
                        // http://ant.apache.org/manual/dirtasks.html)
                        int star = CharOperation.indexOf('*', pattern, lastSlash);
                        if ((star == -1 || star >= pattern.length - 1 || pattern[star + 1] != '*')) {
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
            path = CharOperation.concat(path, new char[] { '*'}, '/');
        }
        exclusionCheck: if (exclusionPatterns != null) {
            for (int i = 0, length = exclusionPatterns.length; i < length; i++) {
                if (CharOperation.pathMatch(exclusionPatterns[i], path, true, '/')) { return true; }
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
            printStream.print(log.substring(start, end == -1 ? log.length() : end + 1));
            start = end + 1;
        } while (start != 0);
        printStream.println();
    }

    /**
	 * Returns true if the given name ends with one of the known ruby like extension.
	 * (implementation is not creating extra strings)
	 */
	public final static boolean isRubyLikeFileName(String name) {
		if (name == null) return false;
		char[][] rubyFileNames = getRubyLikeFilenames();
		for (int i = 0; i < rubyFileNames.length; i++) {
			char[] filename = rubyFileNames[i];
			if(name.equals(new String(filename))) return true;
		}
		return indexOfRubyLikeExtension(name) != -1;
	}

    /**
     * Validate the given compilation unit name. A compilation unit name must
     * obey the following rules:
     * <ul>
     * <li> it must not be null
     * <li> it must include the <code>".rb"</code> or <code>".rbw"</code>
     * suffix
     * <li> its prefix must be a valid identifier
     * </ul>
     * </p>
     * 
     * @param name
     *            the name of a compilation unit
     * @return a status object with code <code>IStatus.OK</code> if the given
     *         name is valid as a compilation unit name, otherwise a status
     *         object indicating what is wrong with the name
     */
    public static boolean isValidRubyScriptName(String name) {
        return RubyConventions.validateRubyScriptName(name).getSeverity() != IStatus.ERROR;
    }

    /**
     * Compares two arrays using equals() on the elements. Either or both arrays
     * may be null. Returns true if both are null. Returns false if only one is
     * null. If both are arrays, returns true iff they have the same length and
     * all elements compare true with equals.
     */
    public static boolean equalArraysOrNull(Object[] a, Object[] b) {
        if (a == b) return true;
        if (a == null || b == null) return false;

        int len = a.length;
        if (len != b.length) return false;
        for (int i = 0; i < len; ++i) {
            if (a[i] == null) {
                if (b[i] != null) return false;
            } else {
                if (!a[i].equals(b[i])) return false;
            }
        }
        return true;
    }

    /*
     * Add a log entry
     */
    public static void log(Throwable e, String message) {
        Throwable nestedException;
        if (e instanceof RubyModelException
                && (nestedException = ((RubyModelException) e).getException()) != null) {
            e = nestedException;
        }
        IStatus status = new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, IStatus.ERROR, message, e);
        RubyCore.getPlugin().getLog().log(status);
    }

    /**
     * Combines two hash codes to make a new one.
     */
    public static int combineHashCodes(int hashCode1, int hashCode2) {
        return hashCode1 * 17 + hashCode2;
    }

    /*
     * Returns whether the given ruby element is exluded from its root's classpath.
     * It doesn't check whether the root itself is on the classpath or not
     */
    public static final boolean isExcluded(IRubyElement element) {
        int elementType = element.getElementType();
        switch (elementType) {
            case IRubyElement.RUBY_MODEL:
            case IRubyElement.RUBY_PROJECT:
                return false;
            case IRubyElement.SCRIPT:                
                IResource resource = element.getResource();
                if (resource == null) 
                    return false;
//                if (isExcluded(resource, root.fullInclusionPatternChars(), root.fullExclusionPatternChars()))
//                    return true;
                return isExcluded(element.getParent());
                
            default:
                IRubyElement cu = element.getAncestor(IRubyElement.SCRIPT);
                return cu != null && isExcluded(cu);
        }
    }
    
	/**
	 * Returns the substring of the given file name, ending at the start of a
	 * Ruby like extension. The entire file name is returned if it doesn't end
	 * with a Ruby like extension.
	 */
	public static String getNameWithoutRubyLikeExtension(String fileName) {
		int index = indexOfRubyLikeExtension(fileName);
		if (index == -1)
			return fileName;
		return fileName.substring(0, index);
	}
	
	/*
	 * Returns the index of the Java like extension of the given file name
	 * or -1 if it doesn't end with a known Java like extension. 
	 * Note this is the index of the '.' even if it is not considered part of the extension.
	 */
	public static int indexOfRubyLikeExtension(String fileName) {
		int fileNameLength = fileName.length();
		char[][] rubyLikeExtensions = getRubyLikeExtensions();
		extensions: for (int i = 0, length = rubyLikeExtensions.length; i < length; i++) {
			char[] extension = rubyLikeExtensions[i];
			int extensionLength = extension.length;
			int extensionStart = fileNameLength - extensionLength;
			int dotIndex = extensionStart - 1;
			if (dotIndex < 0) continue;
			if (fileName.charAt(dotIndex) != '.') continue;
			for (int j = 0; j < extensionLength; j++) {
				if (fileName.charAt(extensionStart + j) != extension[j])
					continue extensions;
			}
			return dotIndex;
		}
		return -1;
	}
	
	/**
	 * Returns the registered Ruby like extensions.
	 */
	public static char[][] getRubyLikeExtensions() {
		if (RUBY_LIKE_EXTENSIONS == null) {
			// TODO (jerome) reenable once RDT UI supports other file extensions (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=71460)
			if (!ENABLE_RUBY_LIKE_EXTENSIONS)
				RUBY_LIKE_EXTENSIONS = new char[][] {"rb".toCharArray(), "rbw".toCharArray(), "rjs".toCharArray(), "rxml".toCharArray()};
			else {
				IContentType rubyContentType = Platform.getContentTypeManager().getContentType(RubyCore.RUBY_SOURCE_CONTENT_TYPE);
				String[] fileExtensions = rubyContentType == null ? null : rubyContentType.getFileSpecs(IContentType.FILE_EXTENSION_SPEC);
				// note that file extensions contains "ruby" as it is defined in RDT Core's plugin.xml
				int length = fileExtensions == null ? 0 : fileExtensions.length;
				char[][] extensions = new char[length][];
				SimpleWordSet knownExtensions = new SimpleWordSet(length); // used to ensure no duplicate extensions
				extensions[0] = "rb".toCharArray(); // ensure that "rb" is first
				knownExtensions.add(extensions[0]);
				int index = 1;
				for (int i = 0; i < length; i++) {
					String fileExtension = fileExtensions[i];
					char[] extension = fileExtension.toCharArray();
					if (!knownExtensions.includes(extension)) {
						extensions[index++] = extension;
						knownExtensions.add(extension);
					}
				}
				if (index != length)
					System.arraycopy(extensions, 0, extensions = new char[index][], 0, index);
				RUBY_LIKE_EXTENSIONS = extensions;
			}
		}
		return RUBY_LIKE_EXTENSIONS;
	}
	
	/**
	 * Returns the registered Ruby like filenames.
	 */
	public static char[][] getRubyLikeFilenames() {
		if (RUBY_LIKE_FILENAMES == null) {
			IContentType rubyContentType = Platform.getContentTypeManager()
					.getContentType(RubyCore.RUBY_SOURCE_CONTENT_TYPE);
			String[] filenames = rubyContentType == null ? null
					: rubyContentType
							.getFileSpecs(IContentType.FILE_NAME_SPEC);
			int length = filenames == null ? 0 : filenames.length;
			names = new char[length][];
			SimpleWordSet knownExtensions = new SimpleWordSet(length); // used
																		// to
																		// ensure
																		// no
																		// duplicate
																		// names
			names[0] = "Rakefile".toCharArray(); // ensure that "Rakefile" is first
			knownExtensions.add(names[0]);
			int index = 1;
			for (int i = 0; i < length; i++) {
				String fileExtension = filenames[i];
				char[] extension = fileExtension.toCharArray();
				if (!knownExtensions.includes(extension)) {
					names[index++] = extension;
					knownExtensions.add(extension);
				}
			}
			if (index != length)
				System.arraycopy(names, 0, names = new char[index][],
						0, index);
			RUBY_LIKE_FILENAMES = names;
		}
		return RUBY_LIKE_FILENAMES;
	}

	private static final int DEFAULT_READING_SIZE = 8192;
	private static char[][] names;

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws CoreException
	 */
	public static char[] getResourceContentsAsCharArray(IFile file) throws RubyModelException {
		// Get encoding from file
		String encoding = null;
		try {
			encoding = file.getCharset();
		} catch (CoreException ce) {
			// do not use any encoding
		}
		return getResourceContentsAsCharArray(file, encoding);
	}

	public static char[] getResourceContentsAsCharArray(IFile file, String encoding) throws RubyModelException {
		// Get resource contents
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(file.getContents(true));
		} catch (CoreException e) {
			throw new RubyModelException(e, IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST);
		}
		try {
			return Util.getInputStreamAsCharArray(stream, -1, encoding);
		} catch (IOException e) {
			throw new RubyModelException(e, IRubyModelStatusConstants.IO_EXCEPTION);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}

	/**
	 * Returns the given input stream's contents as a character array. If a
	 * length is specified (ie. if length != -1), only length chars are
	 * returned. Otherwise all chars in the stream are returned. Note this
	 * doesn't close the stream.
	 * 
	 * @throws IOException
	 *             if a problem occured reading the stream.
	 */
	public static char[] getInputStreamAsCharArray(InputStream stream, int length, String encoding) throws IOException {
		InputStreamReader reader = null;
		reader = encoding == null ? new InputStreamReader(stream) : new InputStreamReader(stream, encoding);
		char[] contents;
		if (length == -1) {
			contents = new char[0];
			int contentsLength = 0;
			int amountRead = -1;
			do {
				int amountRequested = Math.max(stream.available(), DEFAULT_READING_SIZE); // read
				// at
				// least
				// 8K

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(contents, 0, contents = new char[contentsLength + amountRequested], 0, contentsLength);
				}

				// read as many chars as possible
				amountRead = reader.read(contents, contentsLength, amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1);

			// Do not keep first character for UTF-8 BOM encoding
			int start = 0;
			if (contentsLength > 0 && "UTF-8".equals(encoding)) { //$NON-NLS-1$
				if (contents[0] == 0xFEFF) { // if BOM char then skip
					contentsLength--;
					start = 1;
				}
			}
			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(contents, start, contents = new char[contentsLength], 0, contentsLength);
			}
		} else {
			contents = new char[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length)) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual
				// read size.
				len += readSize;
				readSize = reader.read(contents, len, length - len);
			}
			// Do not keep first character for UTF-8 BOM encoding
			int start = 0;
			if (length > 0 && "UTF-8".equals(encoding)) { //$NON-NLS-1$
				if (contents[0] == 0xFEFF) { // if BOM char then skip
					len--;
					start = 1;
				}
			}
			// See PR 1FMS89U
			// Now we need to resize in case the default encoding used more than
			// one byte for each
			// character
			if (len != length) System.arraycopy(contents, start, (contents = new char[len]), 0, len);
		}

		return contents;
	}

	public static void resetRubyLikeExtensions() {
		RUBY_LIKE_EXTENSIONS = null;
		RUBY_LIKE_FILENAMES = null;		
	}

}

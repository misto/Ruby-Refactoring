/*
 * Created on Jan 13, 2005
 *
 */
package org.rubypeople.rdt.internal.core.parser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * @author cawilliams
 * 
 */
public class Util {

	private static final int DEFAULT_READING_SIZE = 8192;

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

}

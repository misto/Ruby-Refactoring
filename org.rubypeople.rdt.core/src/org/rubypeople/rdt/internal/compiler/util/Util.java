package org.rubypeople.rdt.internal.compiler.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Util {

	public interface Displayable {
		public String displayString(Object o);
	}

	private static final int DEFAULT_READING_SIZE = 8192;
	public final static String UTF_8 = "UTF-8"; //$NON-NLS-1$
	public static String LINE_SEPARATOR = System.getProperty("line.separator"); //$NON-NLS-1$

	public static byte[] getFileByteContent(File file) throws IOException {
		InputStream stream = null;
		try {
			stream = new FileInputStream(file);
			return getInputStreamAsByteArray(stream, (int) file.length());
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					// ignore
				}
			}
		}
	}

	/**
	 * Converts an array of Objects into String.
	 */
	public static String toString(Object[] objects, Displayable renderer) {
		if (objects == null) return ""; //$NON-NLS-1$
		StringBuffer buffer = new StringBuffer(10);
		for (int i = 0; i < objects.length; i++){
			if (i > 0) buffer.append(", "); //$NON-NLS-1$
			buffer.append(renderer.displayString(objects[i]));
		}
		return buffer.toString();
	}
	
	/**
	 * Returns the given input stream's contents as a byte array. If a length is
	 * specified (ie. if length != -1), only length bytes are returned.
	 * Otherwise all bytes in the stream are returned. Note this doesn't close
	 * the stream.
	 * 
	 * @throws IOException
	 *             if a problem occured reading the stream.
	 */
	public static byte[] getInputStreamAsByteArray(InputStream stream,
			int length) throws IOException {
		byte[] contents;
		if (length == -1) {
			contents = new byte[0];
			int contentsLength = 0;
			int amountRead = -1;
			do {
				int amountRequested = Math.max(stream.available(),
						DEFAULT_READING_SIZE); // read at least 8K

				// resize contents if needed
				if (contentsLength + amountRequested > contents.length) {
					System.arraycopy(contents, 0,
							contents = new byte[contentsLength
									+ amountRequested], 0, contentsLength);
				}

				// read as many bytes as possible
				amountRead = stream.read(contents, contentsLength,
						amountRequested);

				if (amountRead > 0) {
					// remember length of contents
					contentsLength += amountRead;
				}
			} while (amountRead != -1);

			// resize contents if necessary
			if (contentsLength < contents.length) {
				System.arraycopy(contents, 0,
						contents = new byte[contentsLength], 0, contentsLength);
			}
		} else {
			contents = new byte[length];
			int len = 0;
			int readSize = 0;
			while ((readSize != -1) && (len != length)) {
				// See PR 1FMS89U
				// We record first the read size. In this case len is the actual
				// read size.
				len += readSize;
				readSize = stream.read(contents, len, length - len);
			}
		}

		return contents;
	}

	/**
	 * Converts an array of Objects into String.
	 */
	public static String toString(Object[] objects) {
		return toString(objects, 
			new Displayable(){ 
				public String displayString(Object o) { 
					if (o == null) return "null"; //$NON-NLS-1$
					return o.toString(); 
				}
			});
	}

}

package org.epic.regexp.views;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.epic.regexp.RegExpPlugin;

/**
 * Convenience class for storing references to image descriptors used by the JS
 * editor.
 * 
 * @author Addi
 */
public class RegExpImages {

	protected static URL iconBaseURL;
	static {
		iconBaseURL = RegExpPlugin.getDefault().getBundle().getEntry("icons/"); //$NON-NLS-1$
	}

	public static final ImageDescriptor ICON_VIEW = createImageDescriptor(iconBaseURL, "rx.gif");
	public static final ImageDescriptor ICON_RUN = createImageDescriptor(iconBaseURL, "run.gif");
	public static final ImageDescriptor RESULT_GRAY = createImageDescriptor(iconBaseURL, "result_gray.gif");
	public static final ImageDescriptor RESULT_GREEN = createImageDescriptor(iconBaseURL, "result_green.gif");
	public static final ImageDescriptor RESULT_RED = createImageDescriptor(iconBaseURL, "result_red.gif");
	public static final ImageDescriptor EDIT_CUT = createImageDescriptor(iconBaseURL, "cut_edit.gif");
	public static final ImageDescriptor EDIT_COPY = createImageDescriptor(iconBaseURL, "copy_edit.gif");
	public static final ImageDescriptor EDIT_PASTE = createImageDescriptor(iconBaseURL, "paste_edit.gif");
	public static final ImageDescriptor ICON_DEBUG_STOP = createImageDescriptor(iconBaseURL, "debug_stop.gif");
	public static final ImageDescriptor ICON_DEBUG_BACK = createImageDescriptor(iconBaseURL, "debug_back.gif");
	public static final ImageDescriptor ICON_DEBUG_FORWARD = createImageDescriptor(iconBaseURL, "debug_forward.gif");

	/**
	 * Utility method to create an <code>ImageDescriptor</code> from a path to
	 * a file.
	 * 
	 * @param path
	 * 
	 * @return
	 */
	private static ImageDescriptor createImageDescriptor(URL path, String file) {
		try {
			URL url = new URL(path, file);

			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {}

		return ImageDescriptor.getMissingImageDescriptor();
	}
}
package org.rubypeople.rdt.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import sun.security.krb5.internal.crypto.e;

public class RubyPluginImages {

	private static final String NAME_PREFIX = "org.rubypeople.rdt.ui.";
	private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();

	private static URL iconBaseURL = null;

	static {
		String pathSuffix = "icons/full/";
		try {
			iconBaseURL = new URL(RubyPlugin.getDefault().getDescriptor().getInstallURL(), pathSuffix);
		} catch (MalformedURLException e) {
			System.out.println("RubyPluginImages: " + e);
		}
	}

	private final static ImageRegistry IMAGE_REGISTRY = new ImageRegistry();

	public static final String IMG_OBJS_ERROR = NAME_PREFIX + "error_obj.gif";
	public static final String IMG_OBJS_WARNING = NAME_PREFIX + "warning_obj.gif";
	public static final String IMG_OBJS_INFO = NAME_PREFIX + "info_obj.gif";

	private static final String T_OBJ = "obj16";
	private static final String T_OVR = "ovr16";
	private static final String T_CTOOL = "ctool16";

	public static final ImageDescriptor DESC_OBJS_REFACTORING_ERROR = createManaged(T_OBJ, IMG_OBJS_ERROR);
	public static final ImageDescriptor DESC_OBJS_REFACTORING_WARNING = createManaged(T_OBJ, IMG_OBJS_WARNING);
	public static final ImageDescriptor DESC_OBJS_REFACTORING_INFO = createManaged(T_OBJ, IMG_OBJS_INFO);

	/**
	 * Returns the image managed under the given key in this registry.
	 * 
	 * @param key the image's key
	 * @return the image managed under the given key
	 */
	public static Image get(String key) {
		return IMAGE_REGISTRY.get(key);
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an action. The actions
	 * are retrieved from the *tool16 folders.
	 */
	public static void setToolImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "tool16", iconName);
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an action. The actions
	 * are retrieved from the *lcl16 folders.
	 */
	public static void setLocalImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "lcl16", iconName);
	}

	public static ImageRegistry getImageRegistry() {
		return IMAGE_REGISTRY;
	}

	//---- Helper methods to access icons on the file system --------------------------------------

	private static void setImageDescriptors(IAction action, String type, String relPath) {

		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(makeIconFileURL("d" + type, relPath));
			if (id != null)
				action.setDisabledImageDescriptor(id);
		} catch (MalformedURLException e) {}

		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(makeIconFileURL("c" + type, relPath));
			if (id != null)
				action.setHoverImageDescriptor(id);
		} catch (MalformedURLException e) {}

		action.setImageDescriptor(create("e" + type, relPath));
	}

	private static ImageDescriptor createManaged(String prefix, String name) {
		try {
			ImageDescriptor result = ImageDescriptor.createFromURL(makeIconFileURL(prefix, name.substring(NAME_PREFIX_LENGTH)));
			IMAGE_REGISTRY.put(name, result);
			return result;
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (iconBaseURL == null)
			throw new MalformedURLException();

		StringBuffer buffer = new StringBuffer(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(iconBaseURL, buffer.toString());
	}
}
package org.rubypeople.rdt.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

public class RdtUiImages {

	protected static final String NAME_PREFIX = "org.rubypeople.rdt.ui.";
	protected static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();
	protected static URL iconBaseURL;

	static {
		iconBaseURL= RubyPlugin.getDefault().getBundle().getEntry("/icons/full/"); //$NON-NLS-1$
	}

	protected static final ImageRegistry IMAGE_REGISTRY = new ImageRegistry();

	protected static final String OBJ_PREFIX = "obj16";
	protected static final String OVR_PREFIX = "ovr16";
	private static final String T_ELCL= "elcl16"; 	//$NON-NLS-1$
	protected static final String CTOOL_PREFIX = "ctool16";

	public static final String IMG_OBJS_ERROR = NAME_PREFIX + "error_obj.gif";
	public static final String IMG_OBJS_WARNING = NAME_PREFIX + "warning_obj.gif";
	public static final String IMG_OBJS_INFO = NAME_PREFIX + "info_obj.gif";
	public static final String IMG_OBJS_HELP= NAME_PREFIX + "help.gif"; //$NON-NLS-1$

	public static final String IMG_CTOOLS_RUBY_IMPORT_CONTAINER = NAME_PREFIX + "imp_c.gif";
	public static final String IMG_CTOOLS_RUBY_IMPORT = NAME_PREFIX + "imp_obj.gif";
	public static final String IMG_TEMPLATE_PROPOSAL = NAME_PREFIX + "template_obj.gif";
	public static final String IMG_CTOOLS_RUBY_LOCAL_VAR = NAME_PREFIX + "localvariable_obj.gif";
	public static final String IMG_CTOOLS_RUBY_PAGE = NAME_PREFIX + "ruby_page.gif";
	public static final String IMG_CTOOLS_RUBY = NAME_PREFIX + "ruby.gif";
	public static final String IMG_CTOOLS_RUBY_GLOBAL = NAME_PREFIX + "ruby_global.gif";
	public static final String IMG_CTOOLS_RUBY_CLASS = NAME_PREFIX + "ruby_class.gif";
	public static final String IMG_CTOOLS_RUBY_MODULE = NAME_PREFIX + "ruby_module.gif";
	public static final String IMG_CTOOLS_RUBY_METHOD = NAME_PREFIX + "ruby_method.gif";
	public static final String IMG_CTOOLS_RUBYMETHOD_PRO = NAME_PREFIX + "ruby_method_pro.gif";
	public static final String IMG_CTOOLS_RUBYMETHOD_PUB = NAME_PREFIX + "ruby_method_pub.gif";
	public static final String IMG_CTOOLS_RUBY_CLASS_VAR = NAME_PREFIX + "ruby_class_var.gif";
	public static final String IMG_CTOOLS_RUBY_INSTANCE_VAR = NAME_PREFIX + "ruby_instance_var.gif";
	public static final String IMG_CTOOLS_RUBY_CONSTANT = NAME_PREFIX + "ruby_constant.gif";

	public static final String IMG_OBJS_FIXABLE_PROBLEM= NAME_PREFIX + "quickfix_warning_obj.gif"; //$NON-NLS-1$
	public static final String IMG_OBJS_FIXABLE_ERROR= NAME_PREFIX + "quickfix_error_obj.gif"; //$NON-NLS-1$

	static {
		createManaged(OBJ_PREFIX, IMG_OBJS_FIXABLE_ERROR);
		createManaged(OBJ_PREFIX, IMG_OBJS_FIXABLE_PROBLEM);
		createManaged(OBJ_PREFIX, IMG_OBJS_ERROR);
		createManaged(OBJ_PREFIX, IMG_OBJS_WARNING);
		createManaged(OBJ_PREFIX, IMG_OBJS_INFO);
		createManaged(OBJ_PREFIX, IMG_TEMPLATE_PROPOSAL);
		createManaged(T_ELCL, IMG_OBJS_HELP);		
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_IMPORT_CONTAINER);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_IMPORT);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_PAGE);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_GLOBAL);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_CLASS);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_MODULE);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_METHOD);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBYMETHOD_PRO);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBYMETHOD_PUB);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_CLASS_VAR);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_CONSTANT);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_LOCAL_VAR);
		createManaged(CTOOL_PREFIX, IMG_CTOOLS_RUBY_INSTANCE_VAR);
	}
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

	protected static void setImageDescriptors(IAction action, String type, String relPath) {

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

	protected static ImageDescriptor createManaged(String prefix, String name) {
		try {
			ImageDescriptor result = ImageDescriptor.createFromURL(makeIconFileURL(prefix, name.substring(NAME_PREFIX_LENGTH)));
			IMAGE_REGISTRY.put(name, result);
			return result;
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	protected static ImageDescriptor create(String prefix, String name) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(prefix, name));
		} catch (MalformedURLException e) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	protected static URL makeIconFileURL(String prefix, String name) throws MalformedURLException {
		if (iconBaseURL == null)
			throw new MalformedURLException();

		StringBuffer buffer = new StringBuffer(prefix);
		buffer.append('/');
		buffer.append(name);
		return new URL(iconBaseURL, buffer.toString());
	}
}
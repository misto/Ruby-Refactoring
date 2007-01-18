package org.rubypeople.rdt.internal.ui;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;

public class RubyPluginImages {

	protected static final String NAME_PREFIX = "org.rubypeople.rdt.ui.";
	protected static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();
	protected static URL iconBaseURL;

	static {
		iconBaseURL= RubyPlugin.getDefault().getBundle().getEntry("/icons/full/"); //$NON-NLS-1$
	}
    
    public static final IPath ICONS_PATH= new Path("$nl$/icons/full"); //$NON-NLS-1$

	private static final ImageRegistry IMAGE_REGISTRY = new ImageRegistry();

	private static final String T_OBJ = "obj16"; 	//$NON-NLS-1$
    private static final String T_OVR= "ovr16";         //$NON-NLS-1$
	private static final String T_ELCL= "elcl16"; 	//$NON-NLS-1$
    private static final String T_DLCL= "dlcl16";   //$NON-NLS-1$
	private static final String T_CTOOL = "ctool16"; 	//$NON-NLS-1$
	private static final String T_WIZBAN= "wizban"; 	//$NON-NLS-1$

    /*
     * Keys for images available from the Ruby-UI plug-in image registry.
     */
    public static final String IMG_MISC_PUBLIC= NAME_PREFIX + "methpub_obj.gif";            //$NON-NLS-1$
    public static final String IMG_MISC_PROTECTED= NAME_PREFIX + "methpro_obj.gif";         //$NON-NLS-1$
    public static final String IMG_MISC_PRIVATE= NAME_PREFIX + "methpri_obj.gif";       //$NON-NLS-1$
    
    public static final String IMG_OBJS_ERROR = NAME_PREFIX + "error_obj.gif";
    public static final String IMG_OBJS_WARNING = NAME_PREFIX + "warning_obj.gif";
    public static final String IMG_OBJS_INFO = NAME_PREFIX + "info_obj.gif";
    public static final String IMG_OBJS_HELP= NAME_PREFIX + "help.gif"; //$NON-NLS-1$
    private static final String IMG_OBJS_GHOST= NAME_PREFIX + "ghost.gif";               //$NON-NLS-1$
    private static final String IMG_OBJS_CLASS= NAME_PREFIX + "class_obj.gif";          //$NON-NLS-1$
    private static final String IMG_OBJS_INNER_CLASS= NAME_PREFIX + "innerclass_obj.gif"; //$NON-NLS-1$
    private static final String IMG_OBJS_CLASSALT= NAME_PREFIX + "classfo_obj.gif";          //$NON-NLS-1$
    public static final String IMG_OBJS_MODULE = NAME_PREFIX + "module_obj.gif"; //$NON-NLS-1$
    private static final String IMG_OBJS_MODULEALT= NAME_PREFIX + "modulefo_obj.gif";          //$NON-NLS-1$
    private static final String IMG_OBJS_RUBY_MODEL= NAME_PREFIX + "ruby_model_obj.gif"; //$NON-NLS-1$
    private static final String IMG_OBJS_SOURCE_FOLDER= NAME_PREFIX + "fldr_obj.gif"; //$NON-NLS-1$
    private static final String IMG_OBJS_SCRIPT= NAME_PREFIX + "rscript_obj.gif";                 //$NON-NLS-1$
    private static final String IMG_OBJS_RUBY_RESOURCE= NAME_PREFIX + "rscript_resource_obj.gif"; //$NON-NLS-1$      
    private static final String IMG_OBJS_UNKNOWN= NAME_PREFIX + "unknown_obj.gif"; //$NON-NLS-1$

    public static final String IMG_OBJS_LIBRARY= NAME_PREFIX + "library_obj.gif"; 		//$NON-NLS-1$
    
    private static final String IMG_CTOOLS_RUBY_IMPORT_CONTAINER = NAME_PREFIX + "imp_c.gif";
    private static final String IMG_CTOOLS_RUBY_IMPORT = NAME_PREFIX + "imp_obj.gif";
    public static final String IMG_OBJS_TEMPLATE = NAME_PREFIX + "template_obj.gif";
    private static final String IMG_CTOOLS_RUBY_LOCAL_VAR = NAME_PREFIX + "localvariable_obj.gif";
    public static final String IMG_CTOOLS_RUBY_PAGE = NAME_PREFIX + "ruby_page.gif";
    public static final String IMG_CTOOLS_RUBY = NAME_PREFIX + "ruby.gif";
    private static final String IMG_CTOOLS_RUBY_GLOBAL = NAME_PREFIX + "ruby_global.gif";
    private static final String IMG_CTOOLS_RUBY_CLASS = NAME_PREFIX + "ruby_class.gif";
    private static final String IMG_CTOOLS_RUBY_METHOD = NAME_PREFIX + "ruby_method.gif";
    private static final String IMG_CTOOLS_RUBYMETHOD_PRO = NAME_PREFIX + "ruby_method_pro.gif";
    private static final String IMG_CTOOLS_RUBYMETHOD_PUB = NAME_PREFIX + "ruby_method_pub.gif";
    private static final String IMG_CTOOLS_RUBY_SINGLETONMETHOD = NAME_PREFIX + "ruby_singletonmethod.gif";
    private static final String IMG_CTOOLS_RUBY_SINGLETONMETHOD_PUB = NAME_PREFIX + "ruby_singletonmethod_pub.gif";
    private static final String IMG_CTOOLS_RUBY_SINGLETONMETHOD_PRO = NAME_PREFIX + "ruby_singletonmethod_pro.gif";
    private static final String IMG_CTOOLS_RUBY_CLASS_VAR = NAME_PREFIX + "ruby_class_var.gif";
    private static final String IMG_CTOOLS_RUBY_INSTANCE_VAR = NAME_PREFIX + "ruby_instance_var.gif";
	private static final String IMG_CTOOLS_RUBY_CONSTANT = NAME_PREFIX + "ruby_constant.gif";

    public static final String IMG_OBJS_FIXABLE_PROBLEM= NAME_PREFIX + "quickfix_warning_obj.gif"; //$NON-NLS-1$
    private static final String IMG_OBJS_FIXABLE_ERROR= NAME_PREFIX + "quickfix_error_obj.gif"; //$NON-NLS-1$

	public static final ImageDescriptor DESC_WIZBAN_NEWJPRJ = createUnManaged(T_WIZBAN, "newrprj_wiz.gif"); 			//$NON-NLS-1$
	public static final ImageDescriptor DESC_WIZBAN_NEWCLASS = createUnManaged(T_WIZBAN, "newclass_wiz.gif"); 			//$NON-NLS-1$

	// RI
	public static final ImageDescriptor TOOLBAR_REFRESH = createUnManaged(T_ELCL, "refresh.png");    
    
    public static final ImageDescriptor DESC_OBJ_OVERRIDES= createUnManaged(T_OBJ, "over_co.gif");                      //$NON-NLS-1$
    public static final ImageDescriptor DESC_OBJ_IMPLEMENTS= createUnManaged(T_OBJ, "implm_co.gif");                //$NON-NLS-1$
	public static final ImageDescriptor DESC_OBJS_LIBRARY= createManaged(T_OBJ, IMG_OBJS_LIBRARY);
	
    
    public static final ImageDescriptor DESC_OVR_STATIC= createUnManaged(T_OVR, "static_co.gif");                       //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_FINAL= createUnManaged(T_OVR, "final_co.gif");                         //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_ABSTRACT= createUnManaged(T_OVR, "abstract_co.gif");                   //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_SYNCH= createUnManaged(T_OVR, "synch_co.gif");                         //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_RUN= createUnManaged(T_OVR, "run_co.gif");                             //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_WARNING= createUnManaged(T_OVR, "warning_co.gif");                     //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_ERROR= createUnManaged(T_OVR, "error_co.gif");                         //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_OVERRIDES= createUnManaged(T_OVR, "over_co.gif");                      //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_IMPLEMENTS= createUnManaged(T_OVR, "implm_co.gif");                //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_SYNCH_AND_OVERRIDES= createUnManaged(T_OVR, "sync_over.gif");      //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_SYNCH_AND_IMPLEMENTS= createUnManaged(T_OVR, "sync_impl.gif");   //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_CONSTRUCTOR= createUnManaged(T_OVR, "constr_ovr.gif");         //$NON-NLS-1$
    public static final ImageDescriptor DESC_OVR_DEPRECATED= createUnManaged(T_OVR, "deprecated.gif");  
	    
    public static final ImageDescriptor DESC_ELCL_FILTER= createUnManaged(T_ELCL, "filter_ps.gif"); //$NON-NLS-1$
    public static final ImageDescriptor DESC_DLCL_FILTER= createUnManaged(T_DLCL, "filter_ps.gif"); //$NON-NLS-1$

    public static final ImageDescriptor DESC_OBJS_GHOST= createManaged(T_OBJ, IMG_OBJS_GHOST);
    public static final ImageDescriptor DESC_OBJS_IMPDECL= createManaged(T_OBJ, IMG_CTOOLS_RUBY_IMPORT);
    public static final ImageDescriptor DESC_OBJS_IMPCONT= createManaged(T_OBJ, IMG_CTOOLS_RUBY_IMPORT_CONTAINER);
             
    public static final ImageDescriptor DESC_OBJS_RUBY_MODEL= createManaged(T_OBJ, IMG_OBJS_RUBY_MODEL);
    public static final ImageDescriptor DESC_OBJS_SOURCE_FOLDER= createManaged(T_OBJ, IMG_OBJS_SOURCE_FOLDER);    
    public static final ImageDescriptor DESC_OBJS_LOCAL_VAR = createManaged(T_OBJ, IMG_CTOOLS_RUBY_LOCAL_VAR);
    public static final ImageDescriptor DESC_OBJS_GLOBAL = createManaged(T_OBJ, IMG_CTOOLS_RUBY_GLOBAL);
    public static final ImageDescriptor DESC_OBJS_MODULE = createManaged(T_OBJ, IMG_OBJS_MODULE);
    public static final ImageDescriptor DESC_OBJS_CLASS_VAR = createManaged(T_OBJ, IMG_CTOOLS_RUBY_CLASS_VAR);
    public static final ImageDescriptor DESC_OBJS_INSTANCE_VAR = createManaged(T_OBJ, IMG_CTOOLS_RUBY_INSTANCE_VAR);
    public static final ImageDescriptor DESC_OBJS_CONSTANT = createManaged(T_OBJ, IMG_CTOOLS_RUBY_CONSTANT);
    
    public static final ImageDescriptor DESC_OBJS_CLASS= createManaged(T_OBJ, IMG_OBJS_CLASS);
    public static final ImageDescriptor DESC_OBJS_CLASSALT= createManaged(T_OBJ, IMG_OBJS_CLASSALT);    
    public static final ImageDescriptor DESC_OBJS_INNER_CLASS= createManaged(T_OBJ, IMG_OBJS_INNER_CLASS);
    public static final ImageDescriptor DESC_OBJS_MODULEALT = createManaged(T_OBJ, IMG_OBJS_MODULEALT); 
    
    
    public static final ImageDescriptor DESC_OBJS_SCRIPT= createManaged(T_OBJ, IMG_OBJS_SCRIPT);
    public static final ImageDescriptor DESC_OBJS_RUBY_RESOURCE= createManaged(T_OBJ, IMG_OBJS_RUBY_RESOURCE);
    
    public static final ImageDescriptor DESC_MISC_PUBLIC= createManaged(T_OBJ, IMG_MISC_PUBLIC);
    public static final ImageDescriptor DESC_MISC_PROTECTED= createManaged(T_OBJ, IMG_MISC_PROTECTED);
    public static final ImageDescriptor DESC_MISC_PRIVATE= createManaged(T_OBJ, IMG_MISC_PRIVATE);
    
    public static final ImageDescriptor DESC_OBJS_UNKNOWN= createManaged(T_OBJ, IMG_OBJS_UNKNOWN);
       
    
	static {
		createManaged(T_OBJ, IMG_OBJS_FIXABLE_ERROR);
		createManaged(T_OBJ, IMG_OBJS_FIXABLE_PROBLEM);
		createManaged(T_OBJ, IMG_OBJS_ERROR);
		createManaged(T_OBJ, IMG_OBJS_WARNING);
		createManaged(T_OBJ, IMG_OBJS_INFO);
		createManaged(T_OBJ, IMG_OBJS_TEMPLATE);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_IMPORT_CONTAINER);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_IMPORT);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_PAGE);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_GLOBAL);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_CLASS);
		createManaged(T_CTOOL, IMG_OBJS_MODULE);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_METHOD);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBYMETHOD_PRO);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBYMETHOD_PUB);
        createManaged(T_CTOOL, IMG_CTOOLS_RUBY_SINGLETONMETHOD );
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_SINGLETONMETHOD_PUB );
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_SINGLETONMETHOD_PRO );
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_CLASS_VAR);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_CONSTANT);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_LOCAL_VAR);
		createManaged(T_CTOOL, IMG_CTOOLS_RUBY_INSTANCE_VAR);
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

		// we don't use hover images. If we set it nonetheless it would be preferred to the "normal" image descriptor
		// see ActionContributionItem.updateImages
		// ImageDescriptor.createFromURL(makeIconFileURL("c" + type, relPath));

		action.setImageDescriptor(createUnManaged("e" + type, relPath));
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

	protected static ImageDescriptor createUnManaged(String prefix, String name) {
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

	/*
	 * Creates an image descriptor for the given path in a bundle. The path can contain variables
	 * like $NL$.
	 * If no image could be found, <code>useMissingImageDescriptor</code> decides if either
	 * the 'missing image descriptor' is returned or <code>null</code>.
	 * Added for 3.1.1.
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle, IPath path, boolean useMissingImageDescriptor) {
		URL url= FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}
}
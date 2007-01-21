package org.rubypeople.rdt.internal.ui;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

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
    
    public static final IPath ICONS_PATH= new Path("$nl$/icons/full"); //$NON-NLS-1$

	// The plug-in registry
	private static ImageRegistry fgImageRegistry= null;
    private static HashMap fgAvoidSWTErrorMap= null;

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
    
    public static final String IMG_OBJS_EXTJAR_WSRC= NAME_PREFIX + "jar_lsrc_obj.gif";	//$NON-NLS-1$
    
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
	public static final ImageDescriptor DESC_OBJS_LIBRARY= createManagedFromKey(T_OBJ, IMG_OBJS_LIBRARY);
	public static final ImageDescriptor DESC_OBJS_EXTJAR_WSRC= createManagedFromKey(T_OBJ, IMG_OBJS_EXTJAR_WSRC);
    
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

    public static final ImageDescriptor DESC_OBJS_GHOST= createManagedFromKey(T_OBJ, IMG_OBJS_GHOST);
    public static final ImageDescriptor DESC_OBJS_IMPDECL= createManagedFromKey(T_OBJ, IMG_CTOOLS_RUBY_IMPORT);
    public static final ImageDescriptor DESC_OBJS_IMPCONT= createManagedFromKey(T_OBJ, IMG_CTOOLS_RUBY_IMPORT_CONTAINER);
             
    public static final ImageDescriptor DESC_OBJS_RUBY_MODEL= createManagedFromKey(T_OBJ, IMG_OBJS_RUBY_MODEL);
    public static final ImageDescriptor DESC_OBJS_SOURCE_FOLDER= createManagedFromKey(T_OBJ, IMG_OBJS_SOURCE_FOLDER);    
    public static final ImageDescriptor DESC_OBJS_LOCAL_VAR = createManagedFromKey(T_OBJ, IMG_CTOOLS_RUBY_LOCAL_VAR);
    public static final ImageDescriptor DESC_OBJS_GLOBAL = createManagedFromKey(T_OBJ, IMG_CTOOLS_RUBY_GLOBAL);
    public static final ImageDescriptor DESC_OBJS_MODULE = createManagedFromKey(T_OBJ, IMG_OBJS_MODULE);
    public static final ImageDescriptor DESC_OBJS_CLASS_VAR = createManagedFromKey(T_OBJ, IMG_CTOOLS_RUBY_CLASS_VAR);
    public static final ImageDescriptor DESC_OBJS_INSTANCE_VAR = createManagedFromKey(T_OBJ, IMG_CTOOLS_RUBY_INSTANCE_VAR);
    public static final ImageDescriptor DESC_OBJS_CONSTANT = createManagedFromKey(T_OBJ, IMG_CTOOLS_RUBY_CONSTANT);
    
    public static final ImageDescriptor DESC_OBJS_CLASS= createManagedFromKey(T_OBJ, IMG_OBJS_CLASS);
    public static final ImageDescriptor DESC_OBJS_CLASSALT= createManagedFromKey(T_OBJ, IMG_OBJS_CLASSALT);    
    public static final ImageDescriptor DESC_OBJS_INNER_CLASS= createManagedFromKey(T_OBJ, IMG_OBJS_INNER_CLASS);
    public static final ImageDescriptor DESC_OBJS_MODULEALT = createManagedFromKey(T_OBJ, IMG_OBJS_MODULEALT); 
    
    
    public static final ImageDescriptor DESC_OBJS_SCRIPT= createManagedFromKey(T_OBJ, IMG_OBJS_SCRIPT);
    public static final ImageDescriptor DESC_OBJS_RUBY_RESOURCE= createManagedFromKey(T_OBJ, IMG_OBJS_RUBY_RESOURCE);
    
    public static final ImageDescriptor DESC_MISC_PUBLIC= createManagedFromKey(T_OBJ, IMG_MISC_PUBLIC);
    public static final ImageDescriptor DESC_MISC_PROTECTED= createManagedFromKey(T_OBJ, IMG_MISC_PROTECTED);
    public static final ImageDescriptor DESC_MISC_PRIVATE= createManagedFromKey(T_OBJ, IMG_MISC_PRIVATE);
    
    public static final ImageDescriptor DESC_OBJS_UNKNOWN= createManagedFromKey(T_OBJ, IMG_OBJS_UNKNOWN);
       
    
	static {
		createManagedFromKey(T_OBJ, IMG_OBJS_FIXABLE_ERROR);
		createManagedFromKey(T_OBJ, IMG_OBJS_FIXABLE_PROBLEM);
		createManagedFromKey(T_OBJ, IMG_OBJS_ERROR);
		createManagedFromKey(T_OBJ, IMG_OBJS_WARNING);
		createManagedFromKey(T_OBJ, IMG_OBJS_INFO);
		createManagedFromKey(T_OBJ, IMG_OBJS_TEMPLATE);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_IMPORT_CONTAINER);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_IMPORT);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_PAGE);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_GLOBAL);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_CLASS);
		createManagedFromKey(T_CTOOL, IMG_OBJS_MODULE);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_METHOD);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBYMETHOD_PRO);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBYMETHOD_PUB);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_SINGLETONMETHOD );
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_SINGLETONMETHOD_PUB );
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_SINGLETONMETHOD_PRO );
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_CLASS_VAR);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_CONSTANT);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_LOCAL_VAR);
		createManagedFromKey(T_CTOOL, IMG_CTOOLS_RUBY_INSTANCE_VAR);
	}
    
    
	/**
	 * Returns the image managed under the given key in this registry.
	 * 
	 * @param key the image's key
	 * @return the image managed under the given key
	 */ 
	public static Image get(String key) {
		return getImageRegistry().get(key);
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
	
	/*
	 * Helper method to access the image registry from the JavaPlugin class.
	 */
	/* package */ static ImageRegistry getImageRegistry() {
		if (fgImageRegistry == null) {
			fgImageRegistry= new ImageRegistry();
			for (Iterator iter= fgAvoidSWTErrorMap.keySet().iterator(); iter.hasNext();) {
				String key= (String) iter.next();
				fgImageRegistry.put(key, (ImageDescriptor) fgAvoidSWTErrorMap.get(key));
			}
			fgAvoidSWTErrorMap= null;
		}
		return fgImageRegistry;
	}
	
	private static ImageDescriptor createManagedFromKey(String prefix, String key) {
		return createManaged(prefix, key.substring(NAME_PREFIX_LENGTH), key);
	}

	//---- Helper methods to access icons on the file system --------------------------------------

	private static void setImageDescriptors(IAction action, String type, String relPath) {
		ImageDescriptor id= create("d" + type, relPath, false); //$NON-NLS-1$
		if (id != null)
			action.setDisabledImageDescriptor(id);
	
		/*
		 * id= create("c" + type, relPath, false); //$NON-NLS-1$
		 * if (id != null)
		 * 		action.setHoverImageDescriptor(id);
		 */
	
		ImageDescriptor descriptor= create("e" + type, relPath, true); //$NON-NLS-1$
		action.setHoverImageDescriptor(descriptor);
		action.setImageDescriptor(descriptor); 
	}
	
	private static ImageDescriptor createManaged(String prefix, String name, String key) {
		ImageDescriptor result= create(prefix, name, true);
		
		if (fgAvoidSWTErrorMap == null) {
			fgAvoidSWTErrorMap= new HashMap();
		}
		fgAvoidSWTErrorMap.put(key, result);
		if (fgImageRegistry != null) {
			RubyPlugin.logErrorMessage("Image registry already defined"); //$NON-NLS-1$
		}
		return result;
	}
	
	/*
	 * Creates an image descriptor for the given prefix and name in the JDT UI bundle. The path can
	 * contain variables like $NL$.
	 * If no image could be found, <code>useMissingImageDescriptor</code> decides if either
	 * the 'missing image descriptor' is returned or <code>null</code>.
	 * or <code>null</code>.
	 */
	private static ImageDescriptor create(String prefix, String name, boolean useMissingImageDescriptor) {
		IPath path= ICONS_PATH.append(prefix).append(name);
		return createImageDescriptor(RubyPlugin.getDefault().getBundle(), path, useMissingImageDescriptor);
	}
	
	/*
	 * Creates an image descriptor for the given prefix and name in the JDT UI bundle. The path can
	 * contain variables like $NL$.
	 * If no image could be found, the 'missing image descriptor' is returned.
	 */
	private static ImageDescriptor createUnManaged(String prefix, String name) {
		return create(prefix, name, true);
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

	/**
	 * Returns the image descriptor for the given key in this registry. Might be called in a non-UI thread.
	 * 
	 * @param key the image's key
	 * @return the image descriptor for the given key
	 */ 
	public static ImageDescriptor getDescriptor(String key) {
		if (fgImageRegistry == null) {
			return (ImageDescriptor) fgAvoidSWTErrorMap.get(key);
		}
		return getImageRegistry().getDescriptor(key);
	}
}
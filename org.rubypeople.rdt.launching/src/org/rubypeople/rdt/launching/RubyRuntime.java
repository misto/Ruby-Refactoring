package org.rubypeople.rdt.launching;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.launching.CompositeId;
import org.rubypeople.rdt.internal.launching.LaunchingMessages;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.internal.launching.ListenerList;
import org.rubypeople.rdt.internal.launching.RuntimeLoadpathEntry;
import org.rubypeople.rdt.internal.launching.RuntimeLoadpathEntryResolver;
import org.rubypeople.rdt.internal.launching.VMDefinitionsContainer;

public class RubyRuntime {
	
	/**
	 * Loadpath container used for a project's Ruby
	 * (value <code>"org.rubypeople.rdt.launching.RUBY_CONTAINER"</code>). A
	 * container is resolved in the context of a specific Ruby project, to one
	 * or more system libraries contained in the Ruby std library. The container can have zero
	 * or two path segments following the container name. When no segments
	 * follow the container name, the workspace default Ruby is used to build a
	 * project. Otherwise the segments identify a specific Ruby used to build a
	 * project:
	 * <ol>
	 * <li>VM Install Type Identifier - identifies the type of Ruby VM used to build the
	 * 	project. For example, the standard VM.</li>
	 * <li>VM Install Name - a user defined name that identifies that a specific VM
	 * 	of the above kind. For example, <code>JRuby 1.8.4</code>. This information is
	 *  shared in a projects loadpath file, so teams must agree on Ruby VM naming
	 * 	conventions.</li>
	 * </ol>
	 * @since 0.9.0
	 */
	public static final String RUBY_CONTAINER = LaunchingPlugin.getUniqueIdentifier() + "RUBY_CONTAINER"; //$NON-NLS-1$
	
	/**
	 * Preference key for the String of XML that defines all installed VMs.
	 * 
	 * @since 0.9.0
	 */
	public static final String PREF_VM_XML = LaunchingPlugin.getUniqueIdentifier() + ".PREF_VM_XML"; //$NON-NLS-1$

	/**
	 * Simple identifier constant (value <code>"vmInstalls"</code>) for the
	 * VM installs extension point.
	 * 
	 * @since 0.9.0
	 */
	public static final String EXTENSION_POINT_VM_INSTALLS = "vmInstalls";	 //$NON-NLS-1$			

	/**
	 * Classpath variable name used for the default RubyVM's library
	 * (value <code>"RUBY_LIB"</code>).
	 */
	public static final String RUBYLIB_VARIABLE= "RUBY_LIB"; //$NON-NLS-1$

	/**
	 * Simple identifier constant (value <code>"runtimeLoadpathEntryResolvers"</code>) for the
	 * runtime loadpath entry resolvers extension point.
	 * 
	 * @since 0.9.0
	 */
	public static final String EXTENSION_POINT_RUNTIME_CLASSPATH_ENTRY_RESOLVERS= "runtimeLoadpathEntryResolvers";	 //$NON-NLS-1$	
	
	private static IVMInstallType[] fgVMTypes= null;

	protected static RubyRuntime runtime;
	private static Object fgVMLock = new Object();
	private static boolean fgInitializingVMs;
	private static String fgDefaultVMId;
    private static ListenerList fgVMListeners = new ListenerList(5);
	private static String fgDefaultVMConnectorId;
	
    /**
     *  Set of IDs of VMs contributed via vmInstalls extension point.
     */
    private static Set<String> fgContributedVMs = new HashSet<String>();
		
	/**
	 * Resolvers keyed by variable name, container id,
	 * and runtime classpath entry id.
	 */
	private static Map fgVariableResolvers = null;
	private static Map fgContainerResolvers = null;
	private static Map fgRuntimeClasspathEntryResolvers = null;
    
	protected RubyRuntime() {
		super();
	}

	public static RubyRuntime getDefault() {
		if (runtime == null) {
			runtime = new RubyRuntime();
		}
		return runtime;
	}
        
    public static void removeVMInstallChangedListener(IVMInstallChangedListener listener) {
        fgVMListeners.remove(listener);
    }
	
	/**
	 * Return the default VM set with <code>setDefaultVM()</code>.
	 * @return	Returns the default VM. May return <code>null</code> when no default
	 * 			VM was set or when the default VM has been disposed.
	 */
	public static IVMInstall getDefaultVMInstall() {
		IVMInstall install= getVMFromCompositeId(getDefaultVMId());
		if (install != null && install.getInstallLocation().exists()) {
			return install;
		}
		// if the default Ruby VM goes missing, re-detect
		if (install != null) {
			install.getVMInstallType().disposeVMInstall(install.getId());
		}
		synchronized (fgVMLock) {
			fgDefaultVMId = null;
			fgVMTypes = null;
			initializeVMs();
		}
		return getVMFromCompositeId(getDefaultVMId());
	}
	
	/**
	 * Return the VM corresponding to the specified composite Id.  The id uniquely
	 * identifies a VM across all vm types.  
	 * 
	 * @param idString the composite id that specifies an instance of IVMInstall
	 * 
	 * @since 0.9.0
	 */
	public static IVMInstall getVMFromCompositeId(String idString) {
		if (idString == null || idString.length() == 0) {
			return null;
		}
		CompositeId id= CompositeId.fromString(idString);
		if (id.getPartCount() == 2) {
			IVMInstallType vmType= getVMInstallType(id.get(0));
			if (vmType != null) {
				return vmType.findVMInstall(id.get(1));
			}
		}
		return null;
	}
	
	private static String getDefaultVMId() {
		initializeVMs();
		return fgDefaultVMId;
	}

	public static void setSelectedInterpreter(IVMInstall vm) throws CoreException {
		setDefaultVMInstall(vm, true);
	}
	
	public static void setDefaultVMInstall(IVMInstall vm, boolean savePreference) throws CoreException {
		IVMInstall previous = null;
		if (fgDefaultVMId != null) {
			previous = getVMFromCompositeId(fgDefaultVMId);
		}
		fgDefaultVMId= getCompositeIdFromVM(vm);
		if (savePreference) {
			saveVMConfiguration();
		}
		IVMInstall current = null;
		if (fgDefaultVMId != null) {
			current = getVMFromCompositeId(fgDefaultVMId);
		}
		if (previous != current) {
			notifyDefaultVMChanged(previous, current);
		}
	}
	
	/**
	 * Saves the VM configuration information to the preferences. This includes
	 * the following information:
	 * <ul>
	 * <li>The list of all defined IVMInstall instances.</li>
	 * <li>The default VM</li>
	 * <ul>
	 * This state will be read again upon first access to VM
	 * configuration information.
	 */
	public static void saveVMConfiguration() throws CoreException {
		if (fgVMTypes == null) {
			// if the VM types have not been instantiated, there can be no changes.
			return;
		}
		try {
			String xml = getVMsAsXML();
			getPreferences().setValue(PREF_VM_XML, xml);
			savePreferences();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.RubyRuntime_exceptionsOccurred, e)); 
		} catch (ParserConfigurationException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.RubyRuntime_exceptionsOccurred, e)); 
		} catch (TransformerException e) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), IStatus.ERROR, LaunchingMessages.RubyRuntime_exceptionsOccurred, e)); 
		}
	}
	
	private static String getVMsAsXML() throws IOException, ParserConfigurationException, TransformerException {
		VMDefinitionsContainer container = new VMDefinitionsContainer();	
		container.setDefaultVMInstallCompositeID(getDefaultVMId());
		container.setDefaultVMInstallConnectorTypeID(getDefaultVMConnectorId());	
		IVMInstallType[] vmTypes= getVMInstallTypes();
		for (int i = 0; i < vmTypes.length; ++i) {
			IVMInstall[] vms = vmTypes[i].getVMInstalls();
			for (int j = 0; j < vms.length; j++) {
				IVMInstall install = vms[j];
				container.addVM(install);
			}
		}
		return container.getAsXML();
	}
	
	private static String getDefaultVMConnectorId() {
		initializeVMs();
		return fgDefaultVMConnectorId;
	}	
	
	/**
	 * Saves the preferences for the launching plug-in.
	 * 
	 * @since 0.9.0
	 */
	public static void savePreferences() {
		LaunchingPlugin.getDefault().savePluginPreferences();
	}

	public static void addVMInstallChangedListener(IVMInstallChangedListener listener) {
		fgVMListeners.add(listener);		
	}
	
	private static void notifyDefaultVMChanged(IVMInstall previous, IVMInstall current) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.defaultVMInstallChanged(previous, current);
		}
	}
	
	/**
	 * Returns the VM install type with the given unique id. 
	 * @param id the VM install type unique id
	 * @return	The VM install type for the given id, or <code>null</code> if no
	 * 			VM install type with the given id is registered.
	 */
	public static IVMInstallType getVMInstallType(String id) {
		IVMInstallType[] vmTypes= getVMInstallTypes();
			for (int i= 0; i < vmTypes.length; i++) {
				if (vmTypes[i].getId().equals(id)) {
					return vmTypes[i];
				}
			}
			return null;	
	}
	
	/**
	 * Returns the list of registered VM types. VM types are registered via
	 * <code>"org.rubypeople.rdt.launching.vmTypes"</code> extension point.
	 * Returns an empty list if there are no registered VM types.
	 * 
	 * @return the list of registered VM types
	 */
	public static IVMInstallType[] getVMInstallTypes() {
		initializeVMs();
		return fgVMTypes; 
	}

	/**
	 * Perform VM type and VM install initialization. Does not hold locks
	 * while performing change notification.
	 * 
	 * @since 0.9.0
	 */
	private static void initializeVMs() {
		VMDefinitionsContainer vmDefs = null;
		boolean setPref = false;
		synchronized (fgVMLock) {
			if (fgVMTypes == null) {
				try {
					fgInitializingVMs = true;
					// 1. load VM type extensions
					initializeVMTypeExtensions();
					try {
						vmDefs = new VMDefinitionsContainer();
						// 2. add persisted VMs
						setPref = addPersistedVMs(vmDefs);
						
						// 3. load contributed VM installs
						addVMExtensions(vmDefs);
						// 4. verify default VM is valid
						String defId = vmDefs.getDefaultVMInstallCompositeID();
						boolean validDef = false;
						if (defId != null) {
							Iterator iterator = vmDefs.getValidVMList().iterator();
							while (iterator.hasNext()) {
								IVMInstall vm = (IVMInstall) iterator.next();
								if (getCompositeIdFromVM(vm).equals(defId)) {
									validDef = true;
									break;
								}
							}
						}
						if (!validDef) {
							// use the first as the default
							setPref = true;
							List list = vmDefs.getValidVMList();
							if (!list.isEmpty()) {
								IVMInstall vm = (IVMInstall) list.get(0);
								vmDefs.setDefaultVMInstallCompositeID(getCompositeIdFromVM(vm));
							}
						}
						fgDefaultVMId = vmDefs.getDefaultVMInstallCompositeID();
						fgDefaultVMConnectorId = vmDefs.getDefaultVMInstallConnectorTypeID();
						
						// Create the underlying VMs for each valid VM
						List vmList = vmDefs.getValidVMList();
						Iterator vmListIterator = vmList.iterator();
						while (vmListIterator.hasNext()) {
							VMStandin vmStandin = (VMStandin) vmListIterator.next();
							vmStandin.convertToRealVM();
						}						
						

					} catch (IOException e) {
						LaunchingPlugin.log(e);
					}
				} finally {
					fgInitializingVMs = false;
				}
			}
		}
		if (vmDefs != null) {
			// notify of initial VMs for backwards compatibility
			IVMInstallType[] installTypes = getVMInstallTypes();
			for (int i = 0; i < installTypes.length; i++) {
				IVMInstallType type = installTypes[i];
				IVMInstall[] installs = type.getVMInstalls();
				for (int j = 0; j < installs.length; j++) {
					fireVMAdded(installs[j]);
				}
			}
			
			// save settings if required
			if (setPref) {
				try {
					String xml = vmDefs.getAsXML();
					LaunchingPlugin.getDefault().getPluginPreferences().setValue(PREF_VM_XML, xml);
				} catch (ParserConfigurationException e) {
					LaunchingPlugin.log(e);
				} catch (IOException e) {
					LaunchingPlugin.log(e);
				} catch (TransformerException e) {
					LaunchingPlugin.log(e);
				}
				
			}
		}
	}
	
	/**
	 * Initializes vm type extensions.
	 */
	private static void initializeVMTypeExtensions() {
		IExtensionPoint extensionPoint= Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID, "vmInstallTypes"); //$NON-NLS-1$
		IConfigurationElement[] configs= extensionPoint.getConfigurationElements(); 
		MultiStatus status= new MultiStatus(LaunchingPlugin.getUniqueIdentifier(), IStatus.OK, LaunchingMessages.RubyRuntime_exceptionOccurred, null); 
		fgVMTypes= new IVMInstallType[configs.length];

		for (int i= 0; i < configs.length; i++) {
			try {
				IVMInstallType vmType= (IVMInstallType)configs[i].createExecutableExtension("class"); //$NON-NLS-1$
				fgVMTypes[i]= vmType;
			} catch (CoreException e) {
				status.add(e.getStatus());
			}
		}
		if (!status.isOK()) {
			//only happens on a CoreException
			LaunchingPlugin.log(status);
			//cleanup null entries in fgVMTypes
			List<IVMInstallType> temp= new ArrayList<IVMInstallType>(fgVMTypes.length);
			for (int i = 0; i < fgVMTypes.length; i++) {
				if(fgVMTypes[i] != null) {
					temp.add(fgVMTypes[i]);
				}
				fgVMTypes= new IVMInstallType[temp.size()];
				fgVMTypes= temp.toArray(fgVMTypes);
			}
		}
	}

	/**
	 * This method loads installed JREs based an existing user preference
	 * or old vm configurations file. The VMs found in the preference
	 * or vm configurations file are added to the given VM definitions container.
	 * 
	 * Returns whether the user preferences should be set - i.e. if it was
	 * not already set when initialized.
	 */
	private static boolean addPersistedVMs(VMDefinitionsContainer vmDefs) throws IOException {
		// Try retrieving the VM preferences from the preference store
		String vmXMLString = getPreferences().getString(PREF_VM_XML);
		
		// If the preference was found, load VMs from it into memory
		if (vmXMLString.length() > 0) {
			try {
				ByteArrayInputStream inputStream = new ByteArrayInputStream(vmXMLString.getBytes());
				VMDefinitionsContainer.parseXMLIntoContainer(inputStream, vmDefs);
				return false;
			} catch (IOException ioe) {
				LaunchingPlugin.log(ioe);
			}			
		} else {			
			// Otherwise, look for the old file that previously held the VM definitions
			IPath stateLocation= LaunchingPlugin.getDefault().getStateLocation();
			IPath stateFile= stateLocation.append("vmConfiguration.xml"); //$NON-NLS-1$
			File file = new File(stateFile.toOSString());
			
			if (file.exists()) {        
				// If file exists, load VM definitions from it into memory and write the definitions to
				// the preference store WITHOUT triggering any processing of the new value
				FileInputStream fileInputStream = new FileInputStream(file);
				VMDefinitionsContainer.parseXMLIntoContainer(fileInputStream, vmDefs);			
			}		
		}
		return true;
	}
	
	/**
	 * Returns the preference store for the launching plug-in.
	 * 
	 * @return the preference store for the launching plug-in
	 * @since 0.9.0
	 */
	public static Preferences getPreferences() {
		return LaunchingPlugin.getDefault().getPluginPreferences();
	}
	
	/** 
	 * Returns a String that uniquely identifies the specified VM across all VM types.
	 * 
	 * @param vm the instance of IVMInstallType to be identified
	 * 
	 * @since 2.1
	 */
	public static String getCompositeIdFromVM(IVMInstall vm) {
		if (vm == null) {
			return null;
		}
		IVMInstallType vmType= vm.getVMInstallType();
		String typeID= vmType.getId();
		CompositeId id= new CompositeId(new String[] { typeID, vm.getId() });
		return id.toString();
	}

	/**
	 * Loads contributed VM installs
	 * @since 3.2
	 */
	private static void addVMExtensions(VMDefinitionsContainer vmDefs) {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID, RubyRuntime.EXTENSION_POINT_VM_INSTALLS);
		IConfigurationElement[] configs= extensionPoint.getConfigurationElements();
		for (int i = 0; i < configs.length; i++) {
			IConfigurationElement element = configs[i];
			try {
				if ("vmInstall".equals(element.getName())) { //$NON-NLS-1$
					String vmType = element.getAttribute("vmInstallType"); //$NON-NLS-1$
					if (vmType == null) {
						abort(MessageFormat.format("Missing required vmInstallType attribute for vmInstall contributed by {0}", //$NON-NLS-1$
								(Object[]) new String[]{element.getContributor().getName()}), null);
					}
					String id = element.getAttribute("id"); //$NON-NLS-1$
					if (id == null) {
						abort(MessageFormat.format("Missing required id attribute for vmInstall contributed by {0}", //$NON-NLS-1$
								(Object[]) new String[]{element.getContributor().getName()}), null);
					}
					IVMInstallType installType = getVMInstallType(vmType);
					if (installType == null) {
						abort(MessageFormat.format("vmInstall {0} contributed by {1} references undefined VM install type {2}", //$NON-NLS-1$
								(Object[]) new String[]{id, element.getContributor().getName(), vmType}), null);
					}
					IVMInstall install = installType.findVMInstall(id);
					if (install == null) {
						// only load/create if first time we've seen this VM install
						String name = element.getAttribute("name"); //$NON-NLS-1$
						if (name == null) {
							abort(MessageFormat.format("vmInstall {0} contributed by {1} missing required attribute name", //$NON-NLS-1$
									(Object[]) new String[]{id, element.getContributor().getName()}), null);
						}
						String home = element.getAttribute("home"); //$NON-NLS-1$
						if (home == null) {
							abort(MessageFormat.format("vmInstall {0} contributed by {1} missing required attribute home", //$NON-NLS-1$
									(Object[]) new String[]{id, element.getContributor().getName()}), null);
						}		
						String vmArgs = element.getAttribute("vmArgs"); //$NON-NLS-1$
						VMStandin standin = new VMStandin(installType, id);
						standin.setName(name);
						home = substitute(home);
						File homeDir = new File(home);
                        if (homeDir.exists()) {
                            try {
                            	// adjust for relative path names
                                home = homeDir.getCanonicalPath();
                                homeDir = new File(home);
                            } catch (IOException e) {
                            }
                        }
                        IStatus status = installType.validateInstallLocation(homeDir);
                        if (!status.isOK()) {
                        	abort(MessageFormat.format("Illegal install location {0} for vmInstall {1} contributed by {2}: {3}", //$NON-NLS-1$
                        			(Object[]) new String[]{home, id, element.getContributor().getName(), status.getMessage()}), null);
                        }
                        standin.setInstallLocation(homeDir);
						if (vmArgs != null) {
							standin.setVMArgs(vmArgs);
						}
                        IConfigurationElement[] libraries = element.getChildren("library"); //$NON-NLS-1$
                        IPath[] locations = null;
                        if (libraries.length > 0) {
                            locations = new IPath[libraries.length];
                            for (int j = 0; j < libraries.length; j++) {
                                IConfigurationElement library = libraries[j];
                                String libPathStr = library.getAttribute("path"); //$NON-NLS-1$
                                if (libPathStr == null) {
                                    abort(MessageFormat.format("library for vmInstall {0} contributed by {1} missing required attribute libPath", //$NON-NLS-1$
                                    		(Object[]) new String[]{id, element.getContributor().getName()}), null);
                                }

                                IPath homePath = new Path(home);
                                IPath libPath = homePath.append(substitute(libPathStr));
                                locations[j] = libPath;
                            }
                        }
                        standin.setLibraryLocations(locations);
                        vmDefs.addVM(standin);
					}
                    fgContributedVMs.add(id);
				} else {
					abort(MessageFormat.format("Illegal element {0} in vmInstalls extension contributed by {1}", //$NON-NLS-1$
							(Object[]) new String[]{element.getName(), element.getContributor().getName()}), null);
				}
			} catch (CoreException e) {
				LaunchingPlugin.log(e);
			}
		}
	}
	
    /**
     * Performs string substitution on the given expression.
     * 
     * @param expression
     * @return expression after string substitution 
     * @throws CoreException
     * @since 0.9.0
     */
    private static String substitute(String expression) throws CoreException {
        return VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(expression);
    }
	
	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, Throwable exception) throws CoreException {
		abort(message, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR, exception);
	}	
	
	/**
	 * Throws a core exception with an internal error status.
	 * 
	 * @param message the status message
	 * @param code status code
	 * @param exception lower level exception associated with the
	 * 
	 *  error, or <code>null</code> if none
	 */
	private static void abort(String message, int code, Throwable exception) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), code, message, exception));
	}	
	
	static void fireVMAdded(IVMInstall vm) {
		if (!fgInitializingVMs) {
			Object[] listeners = fgVMListeners.getListeners();
			for (int i = 0; i < listeners.length; i++) {
				IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
				listener.vmAdded(vm);
			}
		}
	}

	public static void fireVMChanged(PropertyChangeEvent event) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.vmChanged(event);
		}		
	}
	
	/**
	 * Notifies all VM install changed listeners of the VM removal
	 * 
	 * @param vm the VM that has been removed
	 * @since 0.9.0
	 */
	public static void fireVMRemoved(IVMInstall vm) {
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IVMInstallChangedListener listener = (IVMInstallChangedListener)listeners[i];
			listener.vmRemoved(vm);
		}		
	}		

	/**
	 * Evaluates library locations for a IVMInstall. If no library locations are set on the install, a default
	 * location is evaluated and checked if it exists.
	 * @return library locations with paths that exist or are empty
	 * @since 0.9.0
	 */
	public static IPath[] getLibraryLocations(IVMInstall vm)  {
		IPath[] locations= vm.getLibraryLocations();
		if (locations != null) return locations;

		IPath[] dflts= vm.getVMInstallType().getDefaultLibraryLocations(vm.getInstallLocation());
		IPath[] libraryPaths = new IPath[dflts.length];			
		for (int i = 0; i < dflts.length; i++) {
			libraryPaths[i]= dflts[i];               
			if (!libraryPaths[i].toFile().isFile()) {
				libraryPaths[i]= Path.EMPTY;
			}
		}
		return libraryPaths;
	}

	/**
	 * Returns the VM install for the given launch configuration.
	 * The VM install is determined in the following prioritized way:
	 * <ol>
	 * <li>The VM install is explicitly specified on the launch configuration
	 *  via the <code>ATTR_JRE_CONTAINER_PATH</code> attribute (since 3.2).</li>
	 * <li>The VM install is explicitly specified on the launch configuration
	 * 	via the <code>ATTR_VM_INSTALL_TYPE</code> and <code>ATTR_VM_INSTALL_ID</code>
	 *  attributes.</li>
	 * <li>If no explicit VM install is specified, the VM install associated with
	 * 	the launch configuration's project is returned.</li>
	 * <li>If no project is specified, or the project does not specify a custom
	 * 	VM install, the workspace default VM install is returned.</li>
	 * </ol>
	 * 
	 * @param configuration launch configuration
	 * @return vm install
	 * @exception CoreException if unable to compute a vm install
	 * @since 0.9.0
	 */
	public static IVMInstall computeVMInstall(ILaunchConfiguration configuration) throws CoreException {
		String jreAttr = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_JRE_CONTAINER_PATH, (String)null);
		if (jreAttr == null) {
			String type = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_TYPE, (String)null);
			if (type == null) {
				IRubyProject proj = getRubyProject(configuration);
				if (proj != null) {
					IVMInstall vm = getVMInstall(proj);
					if (vm != null) {
						return vm;
					}
				}
			} else {
				String name = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME, (String)null);
				return resolveVM(type, name, configuration);
			}
		} else {
			IPath jrePath = Path.fromPortableString(jreAttr);
			ILoadpathEntry entry = RubyCore.newContainerEntry(jrePath);
			IRuntimeLoadpathEntryResolver2 resolver = getVariableResolver(jrePath.segment(0));
			if (resolver != null) {
				return resolver.resolveVMInstall(entry);
			} else {
				resolver = getContainerResolver(jrePath.segment(0));
				if (resolver != null) {
					return resolver.resolveVMInstall(entry);
				}
			}
		}
		
		return getDefaultVMInstall();
	}
	
	/**
	 * Returns the VM of the given type with the specified name.
	 *  
	 * @param type vm type identifier
	 * @param name vm name
	 * @return vm install
	 * @exception CoreException if unable to resolve
	 * @since 0.9.0
	 */
	private static IVMInstall resolveVM(String type, String name, ILaunchConfiguration configuration) throws CoreException {
		IVMInstallType vt = getVMInstallType(type);
		if (vt == null) {
			// error type does not exist
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Specified_VM_install_type_does_not_exist___0__2, new String[] {type}), null); 
		}
		IVMInstall vm = null;
		// look for a name
		if (name == null) {
			// error - type specified without a specific install (could be an old config that specified a VM ID)
			// log the error, but choose the default VM.
			IStatus status = new Status(IStatus.WARNING, LaunchingPlugin.getUniqueIdentifier(), IRubyLaunchConfigurationConstants.ERR_UNSPECIFIED_VM_INSTALL, MessageFormat.format(LaunchingMessages.JavaRuntime_VM_not_fully_specified_in_launch_configuration__0____missing_VM_name__Reverting_to_default_VM__1, new String[] {configuration.getName()}), null); 
			LaunchingPlugin.log(status);
			return getDefaultVMInstall();
		} 
		vm = vt.findVMInstallByName(name);
		if (vm == null) {
			// error - install not found
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Specified_VM_install_not_found__type__0___name__1__2, new String[] {vt.getName(), name}), null);					 
		} else {
			return vm;
		}
		// won't reach here
		return null;
	}
	
	/**
	 * Returns the resolver registered for the given variable, or
	 * <code>null</code> if none.
	 * 
	 * @param variableName the variable to determine the resolver for
	 * @return the resolver registered for the given variable, or
	 * <code>null</code> if none
	 */
	private static IRuntimeLoadpathEntryResolver2 getVariableResolver(String variableName) {
		return (IRuntimeLoadpathEntryResolver2)getVariableResolvers().get(variableName);
	}
	
	/**
	 * Returns the resolver registered for the given container id, or
	 * <code>null</code> if none.
	 * 
	 * @param containerId the container to determine the resolver for
	 * @return the resolver registered for the given container id, or
	 * <code>null</code> if none
	 */	
	private static IRuntimeLoadpathEntryResolver2 getContainerResolver(String containerId) {
		return (IRuntimeLoadpathEntryResolver2)getContainerResolvers().get(containerId);
	}
	
	private static Map getVariableResolvers() {
		if (fgVariableResolvers == null) {
			initializeResolvers();
		}
		return fgVariableResolvers;
	}
	
	/**
	 * Returns all registered container resolvers.
	 */
	private static Map getContainerResolvers() {
		if (fgContainerResolvers == null) {
			initializeResolvers();
		}
		return fgContainerResolvers;
	}
	
	private static void initializeResolvers() {
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(LaunchingPlugin.PLUGIN_ID, EXTENSION_POINT_RUNTIME_CLASSPATH_ENTRY_RESOLVERS);
		IConfigurationElement[] extensions = point.getConfigurationElements();
		fgVariableResolvers = new HashMap(extensions.length);
		fgContainerResolvers = new HashMap(extensions.length);
		fgRuntimeClasspathEntryResolvers = new HashMap(extensions.length);
		for (int i = 0; i < extensions.length; i++) {
			RuntimeLoadpathEntryResolver res = new RuntimeLoadpathEntryResolver(extensions[i]);
			String variable = res.getVariableName();
			String container = res.getContainerId();
			String entryId = res.getRuntimeLoadpathEntryId();
			if (variable != null) {
				fgVariableResolvers.put(variable, res);
			}
			if (container != null) {
				fgContainerResolvers.put(container, res);
			}
			if (entryId != null) {
				fgRuntimeClasspathEntryResolvers.put(entryId, res);
			}
		}		
	}
	
	/**
	 * Returns the VM assigned to build the given Java project.
	 * The project must exist. The VM assigned to a project is
	 * determined from its build path.
	 * 
	 * @param project the project to retrieve the VM from
	 * @return the VM instance that is assigned to build the given Java project
	 * 		   Returns <code>null</code> if no VM is referenced on the project's build path.
	 * @throws CoreException if unable to determine the project's VM install
	 */
	public static IVMInstall getVMInstall(IRubyProject project) throws CoreException {
		// check the classpath
		IVMInstall vm = null;
		ILoadpathEntry[] classpath = project.getRawLoadpath();
		IRuntimeLoadpathEntryResolver resolver = null;
		for (int i = 0; i < classpath.length; i++) {
			ILoadpathEntry entry = classpath[i];
			switch (entry.getEntryKind()) {
				case ILoadpathEntry.CPE_VARIABLE:
					resolver = getVariableResolver(entry.getPath().segment(0));
					if (resolver != null) {
						vm = resolver.resolveVMInstall(entry);
					}
					break;
				case ILoadpathEntry.CPE_CONTAINER:
					resolver = getContainerResolver(entry.getPath().segment(0));
					if (resolver != null) {
						vm = resolver.resolveVMInstall(entry);
					}
					break;
			}
			if (vm != null) {
				return vm;
			}
		}
		return null;
	}
	
	/**
	 * Return the <code>IRubyProject</code> referenced in the specified configuration or
	 * <code>null</code> if none.
	 *
	 * @exception CoreException if the referenced Ruby project does not exist
	 * @since 0.9.0
	 */
	public static IRubyProject getRubyProject(ILaunchConfiguration configuration) throws CoreException {
		String projectName = configuration.getAttribute(IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME, (String)null);
		if ((projectName == null) || (projectName.trim().length() < 1)) {
			return null;
		}			
		IRubyProject javaProject = getRubyModel().getRubyProject(projectName);
		if (javaProject != null && javaProject.getProject().exists() && !javaProject.getProject().isOpen()) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_28, new String[] {configuration.getName(), projectName}), IRubyLaunchConfigurationConstants.ERR_PROJECT_CLOSED, null); 
		}
		if ((javaProject == null) || !javaProject.exists()) {
			abort(MessageFormat.format(LaunchingMessages.JavaRuntime_Launch_configuration__0__references_non_existing_project__1___1, new String[] {configuration.getName(), projectName}), IRubyLaunchConfigurationConstants.ERR_NOT_A_RUBY_PROJECT, null); 
		}
		return javaProject;
	}
	
	/**
	 * Convenience method to get the ruby model.
	 */
	private static IRubyModel getRubyModel() {
		return RubyCore.create(ResourcesPlugin.getWorkspace().getRoot());
	}

	/**
	 * Returns a new runtime classpath entry for the given archive (possibly
	 * external).
	 * 
	 * @param path absolute path to an archive
	 * @return runtime classpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newArchiveRuntimeLoadpathEntry(IPath path) {
		ILoadpathEntry cpe = RubyCore.newLibraryEntry(path);
		return newRuntimeLoadpathEntry(cpe);
	}
	
	/**
	 * Returns a runtime classpath entry that corresponds to the given
	 * classpath entry. The classpath entry may not be of type <code>CPE_SOURCE</code>
	 * or <code>CPE_CONTAINER</code>.
	 * 
	 * @param entry a classpath entry
	 * @return runtime classpath entry
	 * @since 0.9.0
	 */
	private static IRuntimeLoadpathEntry newRuntimeLoadpathEntry(ILoadpathEntry entry) {
		return new RuntimeLoadpathEntry(entry);
	}

	/**
	 * Returns a runtime classpath entry for the given container path with the given
	 * classpath property to be resolved in the context of the given Java project.
	 * 
	 * @param path container path
	 * @param classpathProperty the type of entry - one of <code>USER_CLASSES</code>,
	 * 	<code>BOOTSTRAP_CLASSES</code>, or <code>STANDARD_CLASSES</code>
	 * @param project Java project context used for resolution, or <code>null</code>
	 *  if to be resolved in the context of the launch configuration this entry
	 *  is referenced in
	 * @return runtime classpath entry
	 * @exception CoreException if unable to construct a runtime classpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newRuntimeContainerLoadpathEntry(IPath path, int classpathProperty, IRubyProject project) throws CoreException {
		ILoadpathEntry cpe = RubyCore.newContainerEntry(path);
		RuntimeLoadpathEntry entry = new RuntimeLoadpathEntry(cpe, classpathProperty);
		entry.setRubyProject(project);
		return entry;
	}

	/**
	 * Returns a new runtime classpath entry for the classpath
	 * variable with the given path.
	 * 
	 * @param path variable path; first segment is the name of the variable; 
	 * 	trailing segments are appended to the resolved variable value
	 * @return runtime loadpath entry
	 * @since 0.9.0
	 */
	public static IRuntimeLoadpathEntry newVariableRuntimeLoadpathEntry(
			IPath path) {
		ILoadpathEntry cpe = RubyCore.newVariableEntry(path);
		return newRuntimeLoadpathEntry(cpe);
	}	
}

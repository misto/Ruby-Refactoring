package org.rubypeople.rdt.launching;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.TransformerException;

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
import org.rubypeople.rdt.internal.launching.CompositeId;
import org.rubypeople.rdt.internal.launching.ListenerList;
import org.rubypeople.rdt.internal.launching.RdtLaunchingMessages;
import org.rubypeople.rdt.internal.launching.RdtLaunchingPlugin;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.VMDefinitionsContainer;
import org.rubypeople.rdt.internal.launching.VMStandin;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class RubyRuntime {
	private static final String TAG_INTERPRETER = "interpreter";
	private static final String ATTR_PATH = "path";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_SELECTED = "selected";
	
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
	public static final String RUBY_CONTAINER = RdtLaunchingPlugin.getUniqueIdentifier() + "RUBY_CONTAINER"; //$NON-NLS-1$
	
	/**
	 * Preference key for the String of XML that defines all installed VMs.
	 * 
	 * @since 0.9.0
	 */
	public static final String PREF_VM_XML = RdtLaunchingPlugin.getUniqueIdentifier() + ".PREF_VM_XML"; //$NON-NLS-1$

	/**
	 * Simple identifier constant (value <code>"vmInstalls"</code>) for the
	 * VM installs extension point.
	 * 
	 * @since 0.9.0
	 */
	public static final String EXTENSION_POINT_VM_INSTALLS = "vmInstalls";	 //$NON-NLS-1$			
	
	private static IInterpreterInstallType[] fgInterpreterTypes= null;

	protected static RubyRuntime runtime;
	private static Object fgVMLock = new Object();
	private static boolean fgInitializingVMs;
	private static String fgDefaultVMId;
	private static String fgDefaultVMConnectorId;
	
    /**
     *  Set of IDs of VMs contributed via vmInstalls extension point.
     */
    private static Set<String> fgContributedVMs = new HashSet<String>();
	
	protected List<IVMInstall> installedInterpreters;
	protected IVMInstall selectedInterpreter;
    private static ListenerList fgVMListeners = new ListenerList(5);
    
	protected RubyRuntime() {
		super();
	}

	public static RubyRuntime getDefault() {
		if (runtime == null) {
			runtime = new RubyRuntime();
		}
		return runtime;
	}
        
    public static void removeInterpreterInstallChangedListener(IInterpreterInstallChangedListener listener) {
        fgVMListeners.remove(listener);
    }
	
	public IVMInstall getSelectedInterpreter() {
		if (selectedInterpreter == null) {
			loadRuntimeConfiguration();
		}
		return selectedInterpreter;
	}

	public IVMInstall getInterpreter(String name) {
		Iterator interpreters = getInstalledInterpreters().iterator();
		while(interpreters.hasNext()) {
			IVMInstall each = (IVMInstall) interpreters.next();
			if (each.getName().equals(name))
				return each;
		}		
		return getSelectedInterpreter();
	}

	public void setSelectedInterpreter(IVMInstall anInterpreter) {
        if (selectedInterpreter == anInterpreter) return;
        IVMInstall oldInterpreter = selectedInterpreter;
		selectedInterpreter = anInterpreter;		
		saveRuntimeConfiguration();   
		Object[] listeners = fgVMListeners.getListeners();
		for (int i = 0; i < listeners.length; i++) {
			IInterpreterInstallChangedListener listener = (IInterpreterInstallChangedListener)listeners[i];
			listener.defaultInterpreterInstallChanged(oldInterpreter, anInterpreter);
		}		
	}

	public void addInstalledInterpreter(IVMInstall anInterpreter) {
		getInstalledInterpreters().add(anInterpreter);
		if (getInstalledInterpreters().size() == 1)
			setSelectedInterpreter(getInstalledInterpreters().get(0));
		else
			saveRuntimeConfiguration();
	}

	public List<IVMInstall> getInstalledInterpreters() {
		if (installedInterpreters == null)
			loadRuntimeConfiguration();
		return installedInterpreters;
	}
	
	public void setInstalledInterpreters(List<IVMInstall> newInstalledInterpreters) {
		installedInterpreters = newInstalledInterpreters;
		if (installedInterpreters.size() > 0)
			setSelectedInterpreter(installedInterpreters.get(0));
		else
			setSelectedInterpreter(null);
		saveRuntimeConfiguration();
	}
	
	protected void saveRuntimeConfiguration() {
		writeXML(getRuntimeConfigurationWriter());
	}

	protected Writer getRuntimeConfigurationWriter() {
		try {
			OutputStream stream = new BufferedOutputStream(new FileOutputStream(getRuntimeConfigurationFile()));
			return new OutputStreamWriter(stream);
		} catch (FileNotFoundException e) {}

		return null;
	}
	
	protected void loadRuntimeConfiguration() {
		installedInterpreters = new ArrayList<IVMInstall>();
		try {
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setContentHandler(getRuntimeConfigurationContentHandler());
			Reader fileReader = this.getRuntimeConfigurationReader();
			if (fileReader == null) {
				return ;
			}
			reader.parse(new InputSource(fileReader));
		} catch(Exception e) {
			RdtLaunchingPlugin.log(e);
		}
	}

	protected Reader getRuntimeConfigurationReader() {
		try {
			return new FileReader(getRuntimeConfigurationFile());
		} catch(FileNotFoundException e) {			
			return null ;
		}
	}
	
	protected void writeXML(Writer writer) {
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeconfig>");
			Iterator<IVMInstall> interpretersIterator = installedInterpreters.iterator();
			while (interpretersIterator.hasNext()) {
				writer.write("<");
				writer.write(TAG_INTERPRETER);
				writer.write(" ");
				writer.write(ATTR_NAME);
				writer.write("=\"");
				
				IVMInstall entry = interpretersIterator.next();
				writer.write(entry.getName());
				writer.write("\" ");
				writer.write(ATTR_PATH);
				writer.write("=\"");
				writer.write(entry.getInstallLocation().getPath());
				writer.write("\"");
				if (entry.equals(selectedInterpreter)) {
					writer.write(" ");
					writer.write(ATTR_SELECTED);
					writer.write("=\"true\"");
				}
					
				writer.write("/>");
			}
			writer.write("</runtimeconfig>");
			writer.flush();
		} catch(IOException e) {
			RdtLaunchingPlugin.log(e);
		}
	}

	protected ContentHandler getRuntimeConfigurationContentHandler() {
		return new ContentHandler() {
			public void setDocumentLocator(Locator locator) {}
			public void startDocument() throws SAXException {}
			public void endDocument() throws SAXException {}
			public void startPrefixMapping(String prefix, String uri) throws SAXException {}
			public void endPrefixMapping(String prefix) throws SAXException {}
			public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
				if (TAG_INTERPRETER.equals(qName)) {
					String interpreterName = atts.getValue(ATTR_NAME);
					File installLocation = new File(atts.getValue(ATTR_PATH));
					IVMInstall interpreter = new RubyInterpreter(interpreterName, installLocation);
					installedInterpreters.add(interpreter);
					if (atts.getValue(ATTR_SELECTED) != null)
						selectedInterpreter = interpreter;
				}
			}
			public void endElement(String namespaceURI, String localName, String qName) throws SAXException {}
			public void characters(char[] ch, int start, int length) throws SAXException {}
			public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
			public void processingInstruction(String target, String data) throws SAXException {}
			public void skippedEntity(String name) throws SAXException {}
		};
	}
	
	protected File getRuntimeConfigurationFile() {
		IPath stateLocation = RdtLaunchingPlugin.getDefault().getStateLocation();
		IPath fileLocation = stateLocation.append("runtimeConfiguration.xml");
		return new File(fileLocation.toOSString());
	}

	public static void addInterpreterInstallChangedListener(IInterpreterInstallChangedListener listener) {
		fgVMListeners.add(listener);		
	}
	
	/**
	 * Returns the VM install type with the given unique id. 
	 * @param id the VM install type unique id
	 * @return	The VM install type for the given id, or <code>null</code> if no
	 * 			VM install type with the given id is registered.
	 */
	public static IInterpreterInstallType getInterpreterInstallType(String id) {
		IInterpreterInstallType[] vmTypes= getInterpreterInstallTypes();
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
	public static IInterpreterInstallType[] getInterpreterInstallTypes() {
		initializeInterpreters();
		return fgInterpreterTypes; 
	}

	public static IVMInstall getDefaultInterpreterInstall() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Perform VM type and VM install initialization. Does not hold locks
	 * while performing change notification.
	 * 
	 * @since 3.2
	 */
	private static void initializeInterpreters() {
		VMDefinitionsContainer vmDefs = null;
		boolean setPref = false;
		synchronized (fgVMLock) {
			if (fgInterpreterTypes == null) {
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
						RdtLaunchingPlugin.log(e);
					}
				} finally {
					fgInitializingVMs = false;
				}
			}
		}
		if (vmDefs != null) {
			// notify of initial VMs for backwards compatibility
			IInterpreterInstallType[] installTypes = getInterpreterInstallTypes();
			for (int i = 0; i < installTypes.length; i++) {
				IInterpreterInstallType type = installTypes[i];
				IVMInstall[] installs = type.getInterpreterInstalls();
				for (int j = 0; j < installs.length; j++) {
					fireInterpreterAdded(installs[j]);
				}
			}
			
			// save settings if required
			if (setPref) {
				try {
					String xml = vmDefs.getAsXML();
					RdtLaunchingPlugin.getDefault().getPluginPreferences().setValue(PREF_VM_XML, xml);
				} catch (ParserConfigurationException e) {
					RdtLaunchingPlugin.log(e);
				} catch (IOException e) {
					RdtLaunchingPlugin.log(e);
				} catch (TransformerException e) {
					RdtLaunchingPlugin.log(e);
				}
				
			}
		}
	}
	
	/**
	 * Initializes vm type extensions.
	 */
	private static void initializeVMTypeExtensions() {
		IExtensionPoint extensionPoint= Platform.getExtensionRegistry().getExtensionPoint(RdtLaunchingPlugin.PLUGIN_ID, "vmInstallTypes"); //$NON-NLS-1$
		IConfigurationElement[] configs= extensionPoint.getConfigurationElements(); 
		MultiStatus status= new MultiStatus(RdtLaunchingPlugin.getUniqueIdentifier(), IStatus.OK, RdtLaunchingMessages.RubyRuntime_exceptionOccurred, null); 
		fgInterpreterTypes= new IInterpreterInstallType[configs.length];

		for (int i= 0; i < configs.length; i++) {
			try {
				IInterpreterInstallType vmType= (IInterpreterInstallType)configs[i].createExecutableExtension("class"); //$NON-NLS-1$
				fgInterpreterTypes[i]= vmType;
			} catch (CoreException e) {
				status.add(e.getStatus());
			}
		}
		if (!status.isOK()) {
			//only happens on a CoreException
			RdtLaunchingPlugin.log(status);
			//cleanup null entries in fgVMTypes
			List<IInterpreterInstallType> temp= new ArrayList<IInterpreterInstallType>(fgInterpreterTypes.length);
			for (int i = 0; i < fgInterpreterTypes.length; i++) {
				if(fgInterpreterTypes[i] != null) {
					temp.add(fgInterpreterTypes[i]);
				}
				fgInterpreterTypes= new IInterpreterInstallType[temp.size()];
				fgInterpreterTypes= temp.toArray(fgInterpreterTypes);
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
				RdtLaunchingPlugin.log(ioe);
			}			
		} else {			
			// Otherwise, look for the old file that previously held the VM definitions
			IPath stateLocation= RdtLaunchingPlugin.getDefault().getStateLocation();
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
		return RdtLaunchingPlugin.getDefault().getPluginPreferences();
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
		IInterpreterInstallType vmType= vm.getVMInstallType();
		String typeID= vmType.getId();
		CompositeId id= new CompositeId(new String[] { typeID, vm.getId() });
		return id.toString();
	}

	/**
	 * Loads contributed VM installs
	 * @since 3.2
	 */
	private static void addVMExtensions(VMDefinitionsContainer vmDefs) {
		IExtensionPoint extensionPoint = Platform.getExtensionRegistry().getExtensionPoint(RdtLaunchingPlugin.PLUGIN_ID, RubyRuntime.EXTENSION_POINT_VM_INSTALLS);
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
					IInterpreterInstallType installType = getInterpreterInstallType(vmType);
					if (installType == null) {
						abort(MessageFormat.format("vmInstall {0} contributed by {1} references undefined VM install type {2}", //$NON-NLS-1$
								(Object[]) new String[]{id, element.getContributor().getName(), vmType}), null);
					}
					IVMInstall install = installType.findInterpreterInstall(id);
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
				RdtLaunchingPlugin.log(e);
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
		throw new CoreException(new Status(IStatus.ERROR, RdtLaunchingPlugin.getUniqueIdentifier(), code, message, exception));
	}	
	
	private static void fireInterpreterAdded(IVMInstall interpreter) {
		// TODO Actually notify IInterpreterInstallChangedListeners
		
	}

	public static void fireInterpreterChanged(PropertyChangeEvent event) {
		// TODO Actually notify IInterpreterInstallChangedListeners
		
	}
}

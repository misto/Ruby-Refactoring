package org.rubypeople.rdt.internal.core;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.osgi.service.prefs.BackingStoreException;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.builder.ProjectFileFinder;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class RubyProject extends Openable implements IProjectNature, IRubyElement, IRubyProject {

    protected IProject project;
    protected List loadPathEntries;
    protected boolean scratched;
    
    /**
     * Name of file containing custom project preferences
     */
    private static final String PREF_FILENAME = ".rprefs";  //$NON-NLS-1$

    /*
     * Value of project's resolved loadpath while it is being resolved
     */

    public RubyProject() {
        super(null);
    }

    /**
     * @param aProject
     */
    public RubyProject(IProject aProject, RubyElement parent) {
        super(parent);
        setProject(aProject);
    }

    /**
     * Configure the project with Java nature.
     */
    public void configure() throws CoreException {

        // register Ruby builder
        addToBuildSpec(RubyCore.BUILDER_ID);
    }

    public boolean upgrade() throws CoreException {
        return addToBuildSpec(RubyCore.BUILDER_ID);
    }

    /**
     * Adds a builder to the build spec for the given project.
     */
    protected boolean addToBuildSpec(String builderID) throws CoreException {

        IProjectDescription description = this.project.getDescription();
        int commandIndex = getRubyCommandIndex(description.getBuildSpec());

        if (commandIndex == -1) {

            // Add a Java command to the build spec
            ICommand command = description.newCommand();
            command.setBuilderName(builderID);
            setRubyCommand(description, command);
            return true;
        }
        return false;
    }

    /**
     * Find the specific Ruby command amongst the given build spec and return
     * its index or -1 if not found.
     */
    private int getRubyCommandIndex(ICommand[] buildSpec) {

        for (int i = 0; i < buildSpec.length; ++i) {
            if (buildSpec[i].getBuilderName().equals(RubyCore.BUILDER_ID)) { return i; }
        }
        return -1;
    }

    /**
     * Update the Ruby command in the build spec (replace existing one if
     * present, add one first if none).
     */
    private void setRubyCommand(IProjectDescription description, ICommand newCommand)
            throws CoreException {

        ICommand[] oldBuildSpec = description.getBuildSpec();
        int oldRubyCommandIndex = getRubyCommandIndex(oldBuildSpec);
        ICommand[] newCommands;

        if (oldRubyCommandIndex == -1) {
            // Add a Ruby build spec before other builders (1FWJK7I)
            newCommands = new ICommand[oldBuildSpec.length + 1];
            System.arraycopy(oldBuildSpec, 0, newCommands, 1, oldBuildSpec.length);
            newCommands[0] = newCommand;
        } else {
            oldBuildSpec[oldRubyCommandIndex] = newCommand;
            newCommands = oldBuildSpec;
        }

        // Commit the spec change into the project
        description.setBuildSpec(newCommands);
        this.project.setDescription(description, null);
    }

    /**
     * /** Removes the Java nature from the project.
     */
    public void deconfigure() throws CoreException {

        // deregister Ruby builder
        removeFromBuildSpec(RubyCore.BUILDER_ID);
    }

    /**
     * Removes the given builder from the build spec for the given project.
     */
    protected void removeFromBuildSpec(String builderID) throws CoreException {

        IProjectDescription description = this.project.getDescription();
        ICommand[] commands = description.getBuildSpec();
        for (int i = 0; i < commands.length; ++i) {
            if (commands[i].getBuilderName().equals(builderID)) {
                ICommand[] newCommands = new ICommand[commands.length - 1];
                System.arraycopy(commands, 0, newCommands, 0, i);
                System.arraycopy(commands, i + 1, newCommands, i, commands.length - i - 1);
                description.setBuildSpec(newCommands);
                this.project.setDescription(description, null);
                return;
            }
        }
    }

    /**
     * Returns true if this handle represents the same Ruby project as the given
     * handle. Two handles represent the same project if they are identical or
     * if they represent a project with the same underlying resource and
     * occurrence counts.
     * 
     * @see RubyElement#equals(Object)
     */
    public boolean equals(Object o) {

        if (this == o) return true;

        if (!(o instanceof RubyProject)) return false;

        RubyProject other = (RubyProject) o;
        return this.project.equals(other.getProject());
    }
    
    public int hashCode() {
        return this.project.hashCode();
    }

    public boolean exists() {
        return hasRubyNature(this.project);
    }

    public RubyModelManager.PerProjectInfo getPerProjectInfo() throws RubyModelException {
        return RubyModelManager.getRubyModelManager().getPerProjectInfoCheckExistence(this.project);
    }

    private IPath getPluginWorkingLocation() {
        return this.project.getWorkingLocation(RubyCore.PLUGIN_ID);
    }   
    
    public IProject getProject() {
        return project;
    }

    /**
     * @see IRubyElement
     */
    public IPath getPath() {
        return this.project.getFullPath();
    }

    protected IProject getProject(String name) {
        return RubyCore.getWorkspace().getRoot().getProject(name);
    }

    public void setProject(IProject aProject) {
        project = aProject;
    }

    public IResource getResource() {
        return this.project;
    }

    public void addLoadPathEntry(IProject anotherRubyProject) {
        scratched = true;

        LoadpathEntry newEntry = new LoadpathEntry(anotherRubyProject);
        getLoadPathEntries().add(newEntry);
    }

    public void removeLoadPathEntry(IProject anotherRubyProject) {
        Iterator entries = getLoadPathEntries().iterator();
        while (entries.hasNext()) {
            LoadpathEntry entry = (LoadpathEntry) entries.next();
            if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT
                    && entry.getProject().getName().equals(anotherRubyProject.getName())) {
                getLoadPathEntries().remove(entry);
                scratched = true;
                break;
            }
        }
    }

    public List getLoadPathEntries() {
        if (loadPathEntries == null) {
            loadLoadPathEntries();
        }

        return loadPathEntries;
    }

    public List getReferencedProjects() {
        List referencedProjects = new ArrayList();

        Iterator iterator = getLoadPathEntries().iterator();
        while (iterator.hasNext()) {
            LoadpathEntry pathEntry = (LoadpathEntry) iterator.next();
            if (pathEntry.getEntryKind() == ILoadpathEntry.CPE_PROJECT)
                referencedProjects.add(pathEntry.getProject());
        }

        return referencedProjects;
    }

    public String[] getRequiredProjectNames() throws RubyModelException {
        List prerequisites = new ArrayList();
        // need resolution
        List entries = getLoadPathEntries();
        for (Iterator iter = entries.iterator(); iter.hasNext();) {
            ILoadpathEntry entry = (ILoadpathEntry) iter.next();
            if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT) {
                prerequisites.add(entry.getPath().lastSegment());
            }
        }
        int size = prerequisites.size();
        if (size == 0) { return new String[0]; }
        String[] result = new String[size];
        prerequisites.toArray(result);
        return result;
    }

    protected void loadLoadPathEntries() {
        loadPathEntries = new ArrayList();

        IFile loadPathsFile = getLoadPathEntriesFile();

        XMLReader reader = null;
        try {
            reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            reader.setContentHandler(getLoadPathEntriesContentHandler());
            reader.parse(new InputSource(loadPathsFile.getContents()));
        } catch (Exception e) {
            // the file is nonextant or unreadable
        }
    }

    protected ContentHandler getLoadPathEntriesContentHandler() {
        return new ContentHandler() {

            public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
            }

            public void endDocument() throws SAXException {
            }

            public void endElement(String arg0, String arg1, String arg2) throws SAXException {
            }

            public void endPrefixMapping(String arg0) throws SAXException {
            }

            public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {
            }

            public void processingInstruction(String arg0, String arg1) throws SAXException {
            }

            public void setDocumentLocator(Locator arg0) {
            }

            public void skippedEntity(String arg0) throws SAXException {
            }

            public void startDocument() throws SAXException {
            }

            public void startElement(String namespaceURI, String localName, String qName,
                    Attributes atts) throws SAXException {
                if ("pathentry".equals(qName)) if ("project".equals(atts.getValue("type"))) {
                    IPath referencedProjectPath = new Path(atts.getValue("path"));
                    IProject referencedProject = getProject(referencedProjectPath.lastSegment());
                    loadPathEntries.add(new LoadpathEntry(referencedProject));
                }
            }

            public void startPrefixMapping(String arg0, String arg1) throws SAXException {
            }
        };
    }

    protected IFile getLoadPathEntriesFile() {
        return project.getFile(".loadpath");
    }

    public void save() throws CoreException {
        if (scratched) {
            InputStream xmlPath = new ByteArrayInputStream(getLoadPathXML().getBytes());
            IFile loadPathsFile = getLoadPathEntriesFile();
            if (!loadPathsFile.exists())
                loadPathsFile.create(xmlPath, true, null);
            else
                loadPathsFile.setContents(xmlPath, true, false, null);

            scratched = false;
        }
    }

    protected String getLoadPathXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath>");

        Iterator pathEntriesIterator = loadPathEntries.iterator();

        while (pathEntriesIterator.hasNext()) {
            LoadpathEntry entry = (LoadpathEntry) pathEntriesIterator.next();
            buffer.append(entry.toXML());
        }

        buffer.append("</loadpath>");
        return buffer.toString();
    }

    /**
     * @see IRubyElement
     */
    public IResource getUnderlyingResource() throws RubyModelException {
        if (!exists()) throw newNotPresentException();
        return this.project;
    }

    /**
     * Returns the project custom preference pool.
     * Project preferences may include custom encoding.
     * @return IEclipsePreferences
     */
    public IEclipsePreferences getEclipsePreferences(){
        if (!RubyProject.hasRubyNature(this.project)) return null;
        // Get cached preferences if exist
        RubyModelManager.PerProjectInfo perProjectInfo = RubyModelManager.getRubyModelManager().getPerProjectInfo(this.project, true);
        if (perProjectInfo.preferences != null) return perProjectInfo.preferences;
        // Init project preferences
        IScopeContext context = new ProjectScope(getProject());
        final IEclipsePreferences eclipsePreferences = context.getNode(RubyCore.PLUGIN_ID);
        updatePreferences(eclipsePreferences);
        perProjectInfo.preferences = eclipsePreferences;

        // Listen to node removal from parent in order to reset cache (see bug 68993)
        IEclipsePreferences.INodeChangeListener nodeListener = new IEclipsePreferences.INodeChangeListener() {
            public void added(IEclipsePreferences.NodeChangeEvent event) {
                // do nothing
            }
            public void removed(IEclipsePreferences.NodeChangeEvent event) {
                if (event.getChild() == eclipsePreferences) {
                    RubyModelManager.getRubyModelManager().resetProjectPreferences(RubyProject.this);
                }
            }
        };
        ((IEclipsePreferences) eclipsePreferences.parent()).addNodeChangeListener(nodeListener);

        // Listen to preference changes
        IEclipsePreferences.IPreferenceChangeListener preferenceListener = new IEclipsePreferences.IPreferenceChangeListener() {
            public void preferenceChange(IEclipsePreferences.PreferenceChangeEvent event) {
                RubyModelManager.getRubyModelManager().resetProjectOptions(RubyProject.this);
            }
        };
        eclipsePreferences.addPreferenceChangeListener(preferenceListener);
        return eclipsePreferences;
    }
    
    /*
     * (non-Rubydoc)
     * 
     * @see org.rubypeople.rdt.core.IRubyElement#getElementName()
     */
    public String getElementName() {
        return project.getName();
    }

    /*
     * (non-Rubydoc)
     * 
     * @see org.rubypeople.rdt.internal.core.parser.RubyElement#getElementType()
     */
    public int getElementType() {
        return IRubyElement.PROJECT;
    }

    /*
     * (non-Rubydoc)
     * 
     * @see org.rubypeople.rdt.core.IRubyElement#hasChildren()
     */
    public boolean hasChildren() {
        return true;
    }

    /*
     * (non-Rubydoc)
     * 
     * @see org.rubypeople.rdt.core.IRubyElement#getParent()
     */
    public IRubyElement getParent() {
        return null;
    }

    /*
     * (non-Rubydoc)
     * 
     * @see org.rubypeople.rdt.core.IRubyProject#findType(java.lang.String)
     */
    public IType findType(String fullyQualifiedName) {
        int index = fullyQualifiedName.lastIndexOf("::");
        String className = null, packageName = null;
        if (index == -1) {
            packageName = "";
            className = fullyQualifiedName;
        } else {
            packageName = fullyQualifiedName.substring(0, index);
            className = fullyQualifiedName.substring(index + 2);
        }

        // FIXME Handle the namespaces properly. we ignore them so far!
        return searchChildren(this, className);
    }

    /**
     * @param element
     * @param className
     */
    private IType searchChildren(IRubyElement element, String className) {
        if (element.isType(IRubyElement.TYPE)) {
            if (element.getElementName().equals(className)) return (IType) element;
        }
        if (!(element instanceof IParent)) return null;
        try {
            IRubyElement[] children = ((IParent) element).getChildren();
            for (int i = 0; i < children.length; i++) {
                IRubyElement child = children[i];
                IType type = searchChildren(child, className);
                if (type != null) return type;
            }
        } catch (RubyModelException e) {
            RubyCore.log(e);
        }
        return null;
    }

    /**
     * @param project2
     * @return
     */
    public static boolean hasRubyNature(IProject project2) {
        try {
            return project2.hasNature(RubyCore.NATURE_ID);
        } catch (CoreException e) {
            // project does not exist or is not open
        }
        return false;
    }

    /**
     * @see Openable
     */
    protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm,
            Map newElements, IResource underlyingResource) throws RubyModelException {
        // check whether the ruby project can be opened
        if (!underlyingResource.isAccessible()) { throw newNotPresentException(); }

        // Find all the scripts in this project
        info.setChildren(getScripts());
        return true;
    }

    /**
     * @return
     */
    private IRubyElement[] getScripts() {
        ProjectFileFinder finder = new ProjectFileFinder(project);
        try {
            List projectFiles = finder.findFiles();
            IRubyElement[] scripts = new IRubyElement[projectFiles.size()];
            int i = 0;
            for (Iterator iter = projectFiles.iterator(); iter.hasNext();) {
                IResource resource = (IResource) iter.next();
                scripts[i] = new RubyScript(this, (IFile) resource, resource.getName(),
                        DefaultWorkingCopyOwner.PRIMARY);
                i++;
            }
            return scripts;
        } catch (CoreException e) {
            e.printStackTrace();
            return new IRubyElement[0];
        }
    }

    /**
     * Answers an ID which is used to distinguish project/entries during package
     * fragment root computations
     * 
     * @return String
     */
    public String rootID() {
        return "[PRJ]" + this.project.getFullPath(); //$NON-NLS-1$
    }

    /**
     * Returns a new element info for this element.
     */
    protected Object createElementInfo() {
        return new RubyProjectElementInfo();
    }

    /**
     * @return
     */
    public ILoadpathEntry[] getLoadpaths() {
        List entries = getLoadPathEntries();
        if (entries.isEmpty()) return new ILoadpathEntry[0];
        ILoadpathEntry[] dest = new ILoadpathEntry[entries.size()];
        System.arraycopy(entries.toArray(), 0, dest, 0, entries.size());
        return dest;
    }
    
    /**
     * @see org.rubypeople.rdt.core.IRubyProject#getOption(String, boolean)
     */ 
    public String getOption(String optionName, boolean inheritRubyCoreOptions) {
        
        String propertyName = optionName;
        if (RubyModelManager.getRubyModelManager().optionNames.contains(propertyName)){
            IEclipsePreferences projectPreferences = getEclipsePreferences();
            String javaCoreDefault = inheritRubyCoreOptions ? RubyCore.getOption(propertyName) : null;
            if (projectPreferences == null) return javaCoreDefault;
            String value = projectPreferences.get(propertyName, javaCoreDefault);
            return value == null ? null : value.trim();
        }
        return null;
    }
    
    /**
     * @see org.rubypeople.rdt.core.IRubyProject#getOptions(boolean)
     */
    public Map getOptions(boolean inheritRubyCoreOptions) {

        // initialize to the defaults from RubyCore options pool
        Map options = inheritRubyCoreOptions ? RubyCore.getOptions() : new Hashtable(5);

        // Get project specific options
        RubyModelManager.PerProjectInfo perProjectInfo = null;
        Hashtable projectOptions = null;
        HashSet optionNames = RubyModelManager.getRubyModelManager().optionNames;
        try {
            perProjectInfo = getPerProjectInfo();
            projectOptions = perProjectInfo.options;
            if (projectOptions == null) {
                // get eclipse preferences
                IEclipsePreferences projectPreferences= getEclipsePreferences();
                if (projectPreferences == null) return options; // cannot do better (non-Ruby project)
                // create project options
                String[] propertyNames = projectPreferences.keys();
                projectOptions = new Hashtable(propertyNames.length);
                for (int i = 0; i < propertyNames.length; i++){
                    String propertyName = propertyNames[i];
                    String value = projectPreferences.get(propertyName, null);
                    if (value != null && optionNames.contains(propertyName)){
                        projectOptions.put(propertyName, value.trim());
                    }
                }       
                // cache project options
                perProjectInfo.options = projectOptions;
            }
        } catch (RubyModelException jme) {
            projectOptions = new Hashtable();
        } catch (BackingStoreException e) {
            projectOptions = new Hashtable();
        }

        // Inherit from RubyCore options if specified
        if (inheritRubyCoreOptions) {
            Iterator propertyNames = projectOptions.keySet().iterator();
            while (propertyNames.hasNext()) {
                String propertyName = (String) propertyNames.next();
                String propertyValue = (String) projectOptions.get(propertyName);
                if (propertyValue != null && optionNames.contains(propertyName)){
                    options.put(propertyName, propertyValue.trim());
                }
            }
            return options;
        }
        return projectOptions;
    }
    
    /*
     * Update eclipse preferences from old preferences.
     */
     private void updatePreferences(IEclipsePreferences preferences) {
        
        Preferences oldPreferences = loadPreferences();
        if (oldPreferences != null) {
            String[] propertyNames = oldPreferences.propertyNames();
            for (int i = 0; i < propertyNames.length; i++){
                String propertyName = propertyNames[i];
                String propertyValue = oldPreferences.getString(propertyName);
                if (!"".equals(propertyValue)) { //$NON-NLS-1$
                    preferences.put(propertyName, propertyValue);
                }
            }
            try {
                // save immediately old preferences
                preferences.flush();
            } catch (BackingStoreException e) {
                // fails silently
            }
        }
     }
     
     /**
     * load preferences from a shareable format (VCM-wise)
     */
     private Preferences loadPreferences() {
        
        Preferences preferences = new Preferences();
        IPath projectMetaLocation = getPluginWorkingLocation();
        if (projectMetaLocation != null) {
            File prefFile = projectMetaLocation.append(PREF_FILENAME).toFile();
            if (prefFile.exists()) { // load preferences from file
                InputStream in = null;
                try {
                    in = new BufferedInputStream(new FileInputStream(prefFile));
                    preferences.load(in);
                } catch (IOException e) { // problems loading preference store - quietly ignore
                } finally {
                    if (in != null) {
                        try {
                            in.close();
                        } catch (IOException e) { // ignore problems with close
                        }
                    }
                }
                // one shot read, delete old preferences
                prefFile.delete();
                return preferences;
            }
        }
        return null;
     }

    public void resetCaches() {
        // TODO Auto-generated method stub        
    }

}

package org.rubypeople.rdt.internal.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
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

	/*
	 * Value of project's resolved loadpath while it is being resolved
	 */
	private static final ILoadpathEntry[] RESOLUTION_IN_PROGRESS = new ILoadpathEntry[0];

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
	
	/**
	 * Adds a builder to the build spec for the given project.
	 */
	protected void addToBuildSpec(String builderID) throws CoreException {

		IProjectDescription description = this.project.getDescription();
		int javaCommandIndex = getRubyCommandIndex(description.getBuildSpec());

		if (javaCommandIndex == -1) {

			// Add a Java command to the build spec
			ICommand command = description.newCommand();
			command.setBuilderName(builderID);
			setRubyCommand(description, command);
		}
	}
	
	/**
	 * Find the specific Ruby command amongst the given build spec
	 * and return its index or -1 if not found.
	 */
	private int getRubyCommandIndex(ICommand[] buildSpec) {

		for (int i = 0; i < buildSpec.length; ++i) {
			if (buildSpec[i].getBuilderName().equals(RubyCore.BUILDER_ID)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Update the Ruby command in the build spec (replace existing one if present,
	 * add one first if none).
	 */
	private void setRubyCommand(
		IProjectDescription description,
		ICommand newCommand)
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
	/**
	 * Removes the Java nature from the project.
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

	public boolean exists() {
		return hasRubyNature(this.project);
	}

	public RubyModelManager.PerProjectInfo getPerProjectInfo() throws RubyModelException {
		return RubyModelManager.getRubyModelManager().getPerProjectInfoCheckExistence(this.project);
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

		LoadPathEntry newEntry = new LoadPathEntry(anotherRubyProject);
		getLoadPathEntries().add(newEntry);
	}

	public void removeLoadPathEntry(IProject anotherRubyProject) {
		Iterator entries = getLoadPathEntries().iterator();
		while (entries.hasNext()) {
			LoadPathEntry entry = (LoadPathEntry) entries.next();
			if (entry.getEntryKind() == ILoadpathEntry.CPE_PROJECT && entry.getProject().getName().equals(anotherRubyProject.getName())) {
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
			LoadPathEntry pathEntry = (LoadPathEntry) iterator.next();
			if (pathEntry.getEntryKind() == ILoadpathEntry.CPE_PROJECT) referencedProjects.add(pathEntry.getProject());
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

			public void characters(char[] arg0, int arg1, int arg2) throws SAXException {}

			public void endDocument() throws SAXException {}

			public void endElement(String arg0, String arg1, String arg2) throws SAXException {}

			public void endPrefixMapping(String arg0) throws SAXException {}

			public void ignorableWhitespace(char[] arg0, int arg1, int arg2) throws SAXException {}

			public void processingInstruction(String arg0, String arg1) throws SAXException {}

			public void setDocumentLocator(Locator arg0) {}

			public void skippedEntity(String arg0) throws SAXException {}

			public void startDocument() throws SAXException {}

			public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
				if ("pathentry".equals(qName)) if ("project".equals(atts.getValue("type"))) {
					IPath referencedProjectPath = new Path(atts.getValue("path"));
					IProject referencedProject = getProject(referencedProjectPath.lastSegment());
					loadPathEntries.add(new LoadPathEntry(referencedProject));
				}
			}

			public void startPrefixMapping(String arg0, String arg1) throws SAXException {}
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
			LoadPathEntry entry = (LoadPathEntry) pathEntriesIterator.next();
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
	public IRubyType findType(String fullyQualifiedName) {
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
	private IRubyType searchChildren(IRubyElement element, String className) {
		if (element.isType(IRubyElement.TYPE)) {
			if (element.getElementName().equals(className)) return (IRubyType) element;
		}
		if (!(element instanceof IParent)) return null;
		try {
			IRubyElement[] children = ((IParent) element).getChildren();
			for (int i = 0; i < children.length; i++) {
				IRubyElement child = children[i];
				IRubyType type = searchChildren(child, className);
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
	protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws RubyModelException {
		// check whether the ruby project can be opened
		if (!underlyingResource.isAccessible()) { 
		  throw newNotPresentException();
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot wRoot = workspace.getRoot();

		// Find all the scripts in this project
		info.setChildren(getScripts());
		return true;
	}

	/**
	 * @return
	 */
	private IRubyElement[] getScripts() {
		// TODO Auto-generated method stub
		return null;
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

	protected void closing(Object info) {
		// forget source attachment recommendations
		Object[] children = ((RubyElementInfo) info).children;
		for (int i = 0, length = children.length; i < length; i++) {
			Object child = children[i];
		}
		super.closing(info);
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

}

package org.rubypeople.rdt.internal.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class RubyProject implements IProjectNature {
	protected IProject project;
	protected List loadPathEntries;
	protected boolean scratched;

	public RubyProject() {}

	public void configure() throws CoreException {
		System.out.println("Made it to RubyProject.configure()!!!!!!!!");
	}

	public void deconfigure() throws CoreException {
		System.out.println("Made it to RubyProject.deconfigure()!!!!!!!");
	}

	public IProject getProject() {
		return project;
	}
	
	protected IProject getProject(String name) {
		return RubyPlugin.getWorkspace().getRoot().getProject(name);
	}

	public void setProject(IProject aProject) {
		project = aProject;
	}

	public void addLoadPathEntry(IProject anotherRubyProject) {
		scratched = true;

		LoadPathEntry newEntry = new LoadPathEntry(anotherRubyProject);
		getLoadPathEntries().add(newEntry);
	}

	public void removeLoadPathEntry(IProject anotherRubyProject) {
		Iterator entries = getLoadPathEntries().iterator();
		while(entries.hasNext()) {
			LoadPathEntry entry = (LoadPathEntry) entries.next();
			if (entry.getType() == LoadPathEntry.TYPE_PROJECT && entry.getProject().getName().equals(anotherRubyProject.getName())) {
				getLoadPathEntries().remove(entry);
				scratched = true;
				break;
			}
		}
	}

	protected List getLoadPathEntries() {
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
			if (pathEntry.getType() == LoadPathEntry.TYPE_PROJECT)
				referencedProjects.add(pathEntry.getProject());
		}

		return referencedProjects;
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
			//the file is nonextant or unreadable
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
				if ("pathentry".equals(qName))
					if ("project".equals(atts.getValue("type"))) {
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

	public void save() {
		if (scratched) {
			try {
				InputStream xmlPath = new ByteArrayInputStream(getLoadPathXML().getBytes());
				IFile loadPathsFile = getLoadPathEntriesFile();
				if (!loadPathsFile.exists())
					loadPathsFile.create(xmlPath, true, null);
				else
					loadPathsFile.setContents(xmlPath, true, false, null);
					
				scratched = false;
			} catch (CoreException e) {
				System.out.println("RubyProject.save(): " + e);
			}
		}
	}

	public String getLoadPathXML() {
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
}

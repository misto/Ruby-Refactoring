package org.rubypeople.rdt.internal.core;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
import sun.security.krb5.internal.crypto.e;

public class RubyProject implements IProjectNature {
	protected IProject project;
	protected Map libraryPaths;
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

	public void addToLibrary(IProject anotherRubyProject) {
		scratched = true;

		LibraryPathEntry newEntry = new LibraryPathEntry(anotherRubyProject);
		getLibraryPaths().put(anotherRubyProject.getName(), newEntry);
	}

	public void removeFromLibrary(IProject anotherRubyProject) {
		scratched = true;

		getLibraryPaths().remove(anotherRubyProject.getName());
	}

	protected Map getLibraryPaths() {
		if (libraryPaths == null) {
			loadLibraryPaths();
		}

		return libraryPaths;
	}

	public List getReferencedProjects() {
		List referencedProjects = new ArrayList();

		Iterator iterator = getLibraryPaths().values().iterator();
		while (iterator.hasNext()) {
			LibraryPathEntry pathEntry = (LibraryPathEntry) iterator.next();
			if (pathEntry.getType() == LibraryPathEntry.TYPE_PROJECT)
				referencedProjects.add(pathEntry.getProject());
		}

		return referencedProjects;
	}

	protected void loadLibraryPaths() {
		libraryPaths = new HashMap();

		IFile libraryPathsFile = getLibraryPathsFile();

		XMLReader reader = null;
		try {
			reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setContentHandler(getLibraryPathsContentHandler());
			reader.parse(new InputSource(libraryPathsFile.getContents()));
		} catch (Exception e) {
			//the file is nonextant or unreadable
		}
	}

	protected ContentHandler getLibraryPathsContentHandler() {
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
						libraryPaths.put(referencedProject.getName(), new LibraryPathEntry(referencedProject));
					}
			}

			public void startPrefixMapping(String arg0, String arg1) throws SAXException {}
		};
	}

	protected IFile getLibraryPathsFile() {
		return project.getFile(".librarypath");
	}

	public void save() {
		if (scratched) {
			try {
				InputStream xmlPath = new ByteArrayInputStream(getLibraryPathXML().getBytes());
				IFile libraryPathsFile = getLibraryPathsFile();
				if (!libraryPathsFile.exists())
					libraryPathsFile.create(xmlPath, true, null);
				else
					libraryPathsFile.setContents(xmlPath, true, false, null);
					
				scratched = false;
			} catch (CoreException e) {
				System.out.println("RubyProject.save(): " + e);
			}
		}
	}

	public String getLibraryPathXML() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><librarypath>");

		Iterator pathEntriesIterator = libraryPaths.values().iterator();

		while (pathEntriesIterator.hasNext()) {
			LibraryPathEntry entry = (LibraryPathEntry) pathEntriesIterator.next();
			buffer.append(entry.toXML());
		}

		buffer.append("</librarypath>");
		return buffer.toString();
	}
}

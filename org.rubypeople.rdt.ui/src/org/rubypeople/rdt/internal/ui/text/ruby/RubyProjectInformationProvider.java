/*
 * Created on Feb 12, 2004
 * 
 * $Id$ Copyright (c) 2003 by Xerox Corporation. All rights reserved.
 * 
 * @author Chris Williams
 */
package org.rubypeople.rdt.internal.ui.text.ruby;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;
import org.rubypeople.rdt.internal.core.parser.ast.RubyScript;

/**
 * RubyProjectInformationProvider
 * 
 * @author CAWilliams
 *  
 */
public class RubyProjectInformationProvider {

	private static List library;
	private static RubyProjectInformationProvider instance;

	private RubyProjectInformationProvider() {
		library = new ArrayList();
	}

	public static RubyProjectInformationProvider instance() {
		if (instance == null) instance = new RubyProjectInformationProvider();
		return instance;
	}

	public List getAllElements(RubyElement element) {
		return getRubyElements(element.getElements());
	}

	public List getLibraryClassesAndModules() {
		List classes = new ArrayList();
		checkLibrary();
		for (Iterator iter = library.iterator(); iter.hasNext();) {
			RubyScript script = (RubyScript) iter.next();
			Object[] elements = script.getElements();
			for (int i = 0; i < elements.length; i++) {
				RubyElement element = (RubyElement) elements[i];
				if (element.isType(RubyElement.CLASS) || element.isType(RubyElement.MODULE)) {
					classes.add(element.getName());
				}
			}
		}
		Collections.sort(classes);
		return classes;
	}

	public List getAllLibraryElements() {
		checkLibrary();

		List elements = new ArrayList();
		for (int i = 0; i < library.size(); i++) {
			elements.addAll(getAllElements((RubyScript) library.get(i)));
		}

		return elements;
	}

	private void checkLibrary() {
		if (library == null) {
			library = new ArrayList();
		}
		if (library.isEmpty()) {
			createLibrary();
		}
	}

	private void createLibrary() {
		return ;
		/*
		RubyLibrary theLibrary = RubyPlugin.getDefault().getSelectedLibrary();
		if (theLibrary == null) return;
		String libPath = theLibrary.getInstallLocation().toOSString();
		File rubyLib = new File(libPath);
		library = getScripts(rubyLib);
		*/
	}

	/**
	 * @param file
	 * @return
	 */
	private List getScripts(File file) {
		List scripts = new ArrayList();
		File[] subDirs = file.listFiles(new FileFilter() {

			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});
		for (int i = 0; i < subDirs.length; i++) {
			scripts.addAll(getScripts(subDirs[i]));
		}
		File[] kids = file.listFiles(new FilenameFilter() {

			public boolean accept(File dir, String name) {
				return name.endsWith(".rb") || name.endsWith(".rbw");
			}
		});
		for (int i = 0; i < kids.length; i++) {
			File kid = kids[i];
			RubyScript script = getScript(kid);
			if (script != null) scripts.add(script);
		}
		return scripts;
	}

	/**
	 * @param kid
	 * @return
	 */
	private RubyScript getScript(File kid) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(kid));

			StringBuffer buffer = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
				buffer.append("\n");
			}
			return RubyParser.parse(buffer.toString());
		} catch (ParseException e) {
			log(e);
		} catch (FileNotFoundException e) {
			log(e);
		} catch (IOException e) {
			log(e);
		}
		return null;
	}

	/**
	 * @param e
	 */
	private void log(Exception e) {
		log(e.toString());
	}

	public List getAllRubyElements() {
		List scripts = new ArrayList();

		List rubyProjects = new ArrayList();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			try {
				if (projects[i].hasNature(RubyPlugin.RUBY_NATURE_ID)) {
					rubyProjects.add(projects[i]);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		Iterator iter = rubyProjects.iterator();
		while (iter.hasNext()) {
			IProject rubyProj = (IProject) iter.next();
			try {
				scripts.addAll(getResourcesRubyElements(rubyProj.members()));
			} catch (CoreException e) {
				log(e);
			}
		}

		return scripts;
	}

	/**
	 * @param resources
	 * @return
	 */
	private List getResourcesRubyElements(IResource[] members) {
		List elements = new ArrayList();
		try {
			for (int i = 0; i < members.length; i++) {
				int type = members[i].getType();
				switch (type) {
				case IResource.FILE:
					elements.addAll(getFilesRubyElements(members[i]));
				case IResource.PROJECT:
				case IResource.ROOT:
					break;
				case IResource.FOLDER:
					IFolder folder = (IFolder) members[i];
					elements.addAll(getResourcesRubyElements(folder.members()));
					break;
				default:
					break;
				}
			}
		} catch (CoreException e) {
			log(e);
		}
		return elements;
	}

	/**
	 * @param resource
	 * @return
	 */
	private List getFilesRubyElements(IResource resource) {
		List list = new ArrayList();
		IFile file = (IFile) resource;
		BufferedReader reader = getFileReader(file);
		if (reader == null) return list;

		try {
			RubyScript script = RubyParser.parse(getContents(reader));
			list.addAll(getRubyElements(script.getElements()));
		} catch (ParseException e) {
			log(e);
		}

		return list;
	}

	/**
	 * @param reader
	 * @return
	 */
	private String getContents(BufferedReader reader) {
		try {
			StringBuffer contents = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				contents.append(line);
				contents.append("\n");
			}
			return contents.toString();
		} catch (IOException e1) {
			log(e1);
		}
		return "";
	}

	/**
	 * Returns a null object if an exception occurs in trying to create a
	 * BufferedReader
	 * 
	 * @param file
	 * @return
	 */
	private BufferedReader getFileReader(IFile file) {
		try {
			return new BufferedReader(new InputStreamReader(file.getContents()));
		} catch (CoreException e) {
			log(e);
		}
		return null;
	}

	/**
	 * @return
	 */
	private List getRubyElements(Object[] elements) {
		List additions = new ArrayList();
		for (int i = 0; i < elements.length; i++) {
			RubyElement elem = (RubyElement) elements[i];
			if (elem.isType(RubyElement.METHOD) || elem.isType(RubyElement.CLASS_VAR) || elem.isType(RubyElement.INSTANCE_VAR)) {
				additions.add(elem.getName());
			}
			if (elem.isType(RubyElement.CLASS) || elem.isType(RubyElement.MODULE)) {
				additions.add(elem.getName());
				additions.addAll(getRubyElements(elem.getElements()));
			}
		}
		return additions;
	}

	/**
	 * @param script
	 * @return
	 */
	public List getImportedElements(RubyScript script) {
		List importedElements = new ArrayList();
        
        /*
		RubyLibrary theLibrary = RubyPlugin.getDefault().getSelectedLibrary();
		if (theLibrary == null) return importedElements;
		String libPath = theLibrary.getInstallLocation().toOSString();

		Set requires = script.getElements(RubyElement.REQUIRES);
		//requires.add(new RubyElement(RubyToken.REQUIRES, "kernel", 0, 0));
		Iterator iterator = requires.iterator();
		while (iterator.hasNext()) {
			RubyElement require = (RubyElement) iterator.next();
			String requirePath = require.getName();
			log(libPath + File.separator + requirePath + ".rb");
			RubyScript importedScript = getScript(new File(libPath + File.separator +requirePath + ".rb"));
			if (importedScript != null) importedElements.addAll(getAllElements(importedScript));
		}
		*/
		return importedElements;
	}

	/**
	 * @param string
	 */
	private void log(String string) {
		System.out.println(string);
		RubyPlugin.log(string);
	}
}

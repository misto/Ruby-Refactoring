/*
 * Created on Feb 12, 2004
 * 
 * $Id$ Copyright (c) 2003 by Xerox Corporation. All rights reserved.
 * 
 * @author Chris Williams
 */
package org.rubypeople.rdt.internal.ui.text.ruby;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.*;
import org.rubypeople.rdt.internal.core.RubyLibrary;

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

	public List getLibraryClasses() {
		List classes = new ArrayList();
		checkLibrary();
		for (Iterator iter = library.iterator(); iter.hasNext();) {
			RubyScript script = (RubyScript) iter.next();
			Object[] elements = script.getElements();
			for (int i = 0; i < elements.length; i++) {
				RubyElement element = (RubyElement) elements[i];
				if(element instanceof RubyClass) {
					classes.add(element.toString());
				}
			}
		}
		
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
		RubyLibrary theLibrary = RubyPlugin.getDefault().getSelectedLibrary();
		if (theLibrary == null) return;
		String libPath = theLibrary.getInstallLocation().toOSString();
		File rubyLib = new File(libPath);
		library = getScripts(rubyLib);	
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
		for(int i = 0; i < subDirs.length; i++) {
			scripts.addAll(getScripts(subDirs[i]));
		}
		File[] kids = file.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".rb") || name.endsWith(".rbw");
			}
		});
		for (int i = 0; i < kids.length; i++) {
			File kid = kids[i];
			try {
				BufferedReader reader = new BufferedReader(new FileReader(kid));

				StringBuffer buffer = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null) {
					buffer.append(line);
					buffer.append("\n");
				}
				// RubyScript script = RubyParser(kids[i].getName()).parse(buffer.toString());
				RubyScript script = RubyParser.parse(buffer.toString());
				scripts.add(script);
			}
			catch (ParseException e) {
				RubyPlugin.log(e);
			}
			catch (FileNotFoundException e) {
				RubyPlugin.log(e);
			}
			catch (IOException e) {
				RubyPlugin.log(e);
			}
		}
		return scripts;
	}

	public List getAllRubyElements() {
		List scripts = new ArrayList();

		List rubyProjects = new ArrayList();
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot()
				.getProjects();
		for (int i = 0; i < projects.length; i++) {
			try {
				if (projects[i].hasNature(RubyPlugin.RUBY_NATURE_ID)) {
					rubyProjects.add(projects[i]);
				}
			}
			catch (CoreException e) {
				e.printStackTrace();
			}
		}

		Iterator iter = rubyProjects.iterator();
		while (iter.hasNext()) {
			IProject rubyProj = (IProject) iter.next();
			try {
				scripts.addAll(getResourcesRubyElements(rubyProj.members()));
			}
			catch (CoreException e) {
				e.printStackTrace();
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
					case IResource.FILE :
						elements.addAll(getFilesRubyElements(members[i]));
					case IResource.PROJECT :
					case IResource.ROOT :
						break;
					case IResource.FOLDER :
						IFolder folder = (IFolder) members[i];
						elements.addAll(getResourcesRubyElements(folder
								.members()));
						break;
					default :
						break;
				}
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
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
		if (reader == null)
			return list;

		try {
			// RubyScript script = new RubyParser(file.getName()).parse(getContents(reader));
			RubyScript script = RubyParser.parse(getContents(reader));
			list.addAll(getRubyElements(script.getElements()));
		}
		catch (ParseException e) {
			RubyPlugin.log(new RuntimeException(e));
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
		}
		catch (IOException e1) {
			e1.printStackTrace();
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
		}
		catch (CoreException e) {
			e.printStackTrace();
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
			if (RubyMethod.class.equals(elem.getClass())
					|| RubyClassVariable.class.equals(elem.getClass())) {
				additions.add(elem.toString());
			}
			if (RubyClass.class.equals(elem.getClass()) || 
					RubyModule.class.equals(elem.getClass())) {
				additions.add(elem.toString());
				additions.addAll(getRubyElements(elem.getElements()));
			}

		}
		return additions;
	}
}

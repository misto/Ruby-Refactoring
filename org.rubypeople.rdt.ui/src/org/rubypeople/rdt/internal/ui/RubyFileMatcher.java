/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT
 * is subject to the "Common Public License (CPL) v 1.0". You may not use RDT
 * except in compliance with the License. For further information see
 * org.rubypeople.rdt/rdt.license.
 */

package org.rubypeople.rdt.internal.ui;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IFileEditorMapping;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.internal.WorkbenchPlugin;

/**
 * RubyViewerFilter uses the Editor mappings for recognising and filtering files
 * Both Editor mappings from plugin.xml and creating using the preferences page are considered
 */
public class RubyFileMatcher {
	public static final int PROP_MATCH_CRITERIA = 1 ;
	
	private String[] rubyFileExtensions ;
	private String[] rubyFileNames;	
	private ListenerList propChangeListeners ; 
	private IPropertyListener propertyListener = new IPropertyListener() {
		public void propertyChanged(Object source, int property) { 
			if (property == IEditorRegistry.PROP_CONTENTS && source instanceof IEditorRegistry) {
				createFileExtensions() ;
				firePropertyChange(PROP_MATCH_CRITERIA) ;
			}
		}
	} ;
	
	

	
	public RubyFileMatcher() {
		propChangeListeners = new ListenerList() ;
		this.createFileExtensions() ;
		WorkbenchPlugin.getDefault().getEditorRegistry().addPropertyListener(propertyListener) ;
	}

	public void addPropertyChangeListener(IPropertyListener propListener) {
		propChangeListeners.add(propListener) ;
	}
	
	private void firePropertyChange(final int type) {
		Object[] array = propChangeListeners.getListeners();
		for (int nX = 0; nX < array.length; nX++) {
			final IPropertyListener l = (IPropertyListener) array[nX];
			Platform.run(new SafeRunnable() {
				public void run() {
					l.propertyChanged(this, type);
				}
			});
		}
	}
	
	public void createFileExtensions() {
		ArrayList extensions = new ArrayList();
		ArrayList filenames = new ArrayList();
		IFileEditorMapping[] mappings = WorkbenchPlugin.getDefault().getEditorRegistry().getFileEditorMappings();
		for (int i = 0; i < mappings.length; i++) {
			IFileEditorMapping mapping = mappings[i];
			IEditorDescriptor[] editors = mapping.getEditors();
			for (int j = 0; j < editors.length; j++) {
				IEditorDescriptor descriptor = editors[j];
				if (descriptor.getId().equals("org.rubypeople.rdt.ui.EditorRubyFile")) {
					// a mapping can also use a filename instead of a suffix
					if (mapping.getExtension() != null && mapping.getExtension().length() != 0) {
						extensions.add(mapping.getExtension());
						break;
					}
					if (mapping.getName() != null && mapping.getName().length() != 0) {
						filenames.add(mapping.getName()) ;
						break ;
					}
				}

			}
		}
		this.rubyFileExtensions = (String[]) extensions.toArray(new String[extensions.size()]);
		this.rubyFileNames = (String[]) filenames.toArray(new String[filenames.size()]);
	}
	
	public boolean hasRubyEditorAssociation(IFile file)  {
		String fileExtension = file.getFileExtension() ;
		for (int i = 0; i < rubyFileExtensions.length; i++) {
			if (rubyFileExtensions[i].equalsIgnoreCase(fileExtension)) {
				return true;
			}
		}
		String fileName = file.getName() ;
		for (int i = 0; i < rubyFileNames.length; i++) {
			if (rubyFileNames[i].equalsIgnoreCase(fileName)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isRubyRunFile(IFile file) {
		return this.hasRubyEditorAssociation(file) ;
	}
		
}


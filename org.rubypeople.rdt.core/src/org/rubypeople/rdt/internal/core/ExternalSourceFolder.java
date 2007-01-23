package org.rubypeople.rdt.internal.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Util;

public class ExternalSourceFolder extends SourceFolder {

	public ExternalSourceFolder(SourceFolderRoot parent, String[] names) {
		super(parent, names);
	}
	
	/*
	 * @see RubyElement#generateInfos
	 */
	protected void generateInfos(Object info, HashMap newElements, IProgressMonitor pm) throws RubyModelException {
		// Open my folder: this creates all the pkg infos
		Openable openableParent = (Openable)this.parent;
		if (!openableParent.isOpen()) {
			openableParent.generateInfos(openableParent.createElementInfo(), newElements, pm);
		}
	}
	
	public boolean isReadOnly() {
		return true;
	}

	protected boolean computeChildren(OpenableElementInfo info) {
		ArrayList<IRubyElement> vChildren = new ArrayList<IRubyElement>();
		File file = getPath().toFile();
		File[] members = file.listFiles();
		for (int i = 0, max = members.length; i < max; i++) {
			File child = members[i];
			if (!child.isDirectory()) {
				IRubyElement childElement;
				if (Util.isValidRubyScriptName(child.getName())) {
					childElement = new ExternalRubyScript(this, child.getName(), DefaultWorkingCopyOwner.PRIMARY);
					vChildren.add(childElement);
				}
			}
		}
		IRubyElement[] children= new IRubyElement[vChildren.size()];
		vChildren.toArray(children);
		info.setChildren(children);
		return true;		
	}
}

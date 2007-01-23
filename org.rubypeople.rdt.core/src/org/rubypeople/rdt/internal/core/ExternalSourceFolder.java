package org.rubypeople.rdt.internal.core;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.RubyModelException;

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
		// XXX We need to modify ExtenralSourceFolerRoot's computeChildren method. None of the source folders' children are getting set!
	}
	
	public boolean isReadOnly() {
		return true;
	}
}

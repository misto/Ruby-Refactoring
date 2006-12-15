package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.Util;

public class SourceFolderInfo extends OpenableElementInfo {
	/**
	 * A array with all the non-ruby resources contained by this SourceFolder
	 */
	protected Object[] nonRubyResources;

/**
 * Create and initialize a new instance of the receiver
 */
public SourceFolderInfo() {
	this.nonRubyResources = null;
}
/**
 */
boolean containsRubyResources() {
	return this.children.length != 0;
}
/**
 * Returns an array of non-ruby resources contained in the receiver.
 */
Object[] getNonRubyResources(IResource underlyingResource) {
	if (this.nonRubyResources == null) {
		try {
			this.nonRubyResources = 
				computeFolderNonRubyResources((IContainer)underlyingResource);
		} catch (RubyModelException e) {
			// root doesn't exist: consider package has no nonRubyResources
			this.nonRubyResources = NO_NON_RUBY_RESOURCES;
		}
	}
	return this.nonRubyResources;
}
/**
 * Set the nonRubyResources to res value
 */
void setNonRubyResources(Object[] resources) {
	this.nonRubyResources = resources;
}

/**
 * Starting at this folder, create non-ruby resources for this package fragment root 
 * and add them to the non-ruby resources collection.
 * 
 * @exception RubyModelException  The resource associated with this package fragment does not exist
 */
static Object[] computeFolderNonRubyResources(IContainer folder) throws RubyModelException {
	Object[] nonRubyResources = new IResource[5];
	int nonRubyResourcesCounter = 0;
	try {
		IResource[] members = folder.members();
		nextResource: for (int i = 0, max = members.length; i < max; i++) {
			IResource member = members[i];
			switch (member.getType()) {
				case IResource.FILE :
					String fileName = member.getName();
					
					// ignore .rb files that are not excluded
					if (Util.isValidRubyScriptName(fileName)) 
						continue nextResource;
					break;

				case IResource.FOLDER :
						continue nextResource;
			}
			if (nonRubyResources.length == nonRubyResourcesCounter) {
				// resize
				System.arraycopy(nonRubyResources, 0, (nonRubyResources = new IResource[nonRubyResourcesCounter * 2]), 0, nonRubyResourcesCounter);
			}
			nonRubyResources[nonRubyResourcesCounter++] = member;

		}
		if (nonRubyResources.length != nonRubyResourcesCounter) {
			System.arraycopy(nonRubyResources, 0, (nonRubyResources = new IResource[nonRubyResourcesCounter]), 0, nonRubyResourcesCounter);
		}
		return nonRubyResources;
	} catch (CoreException e) {
		throw new RubyModelException(e);
	}
}
}

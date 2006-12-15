package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

public class SourceFolder extends Openable implements ISourceFolder {

	private String[] names;

	public SourceFolder(RubyElement parent, String[] names) {
		super(parent);
		this.names = names;
	}
	
	/**
	 * @see IParent 
	 */
	public boolean hasChildren() throws RubyModelException {
		return getChildren().length > 0;
	}

	@Override
	protected boolean buildStructure(OpenableElementInfo info,
			IProgressMonitor pm, Map newElements, IResource underlyingResource)
			throws RubyModelException {
		// check whether this folder can be opened
		if (!underlyingResource.isAccessible()) throw newNotPresentException();
		

		// add ruby scripts from resources
		HashSet vChildren = new HashSet();
		try {
			IResource[] members = ((IContainer) underlyingResource).members();
			for (int i = 0, max = members.length; i < max; i++) {
				IResource child = members[i];
				if (child.getType() != IResource.FOLDER) {
					IRubyElement childElement;
					if (Util.isValidRubyScriptName(child.getName())) {
						childElement = new RubyScript(this, (IFile) child, child.getName(), DefaultWorkingCopyOwner.PRIMARY);
						vChildren.add(childElement);
					}
				} else {
					// TODO Add Source Folders underneath this one
					String[] newNames = new String[names.length + 1];
					System.arraycopy(names, 0, newNames, 0, names.length);
					newNames[names.length] = child.getName();
					IRubyElement childElement = new SourceFolder(this, newNames);
					vChildren.add(childElement);
				}
			}
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
		

		// add primary ruby scripts
		IRubyScript[] primaryRubyScripts = getRubyScripts(DefaultWorkingCopyOwner.PRIMARY);
		for (int i = 0, length = primaryRubyScripts.length; i < length; i++) {
			IRubyScript primary = primaryRubyScripts[i];
			vChildren.add(primary);
		}
	
		
		IRubyElement[] children = new IRubyElement[vChildren.size()];
		vChildren.toArray(children);
		info.setChildren(children);
		return true;
	}

	@Override
	protected Object createElementInfo() {
		return new SourceFolderInfo();
	}

	@Override
	public int getElementType() {
		return IRubyElement.SOURCE_FOLDER;
	}
	
	@Override
	public String getElementName() {
		if (names.length == 0) return "";
		return names[names.length - 1];
	}

	public boolean containsRubyResources() throws RubyModelException {
		return ((SourceFolderInfo) getElementInfo()).containsRubyResources();
	}

	public IRubyScript createRubyScript(String name, String contents,
			boolean force, IProgressMonitor monitor) throws RubyModelException {
		CreateRubyScriptOperation op= new CreateRubyScriptOperation(this, name, contents, force);
		op.runOperation(monitor);
		IFile file = ((IContainer) getResource()).getFile(new Path(name));
		return new RubyScript(this, file, name, DefaultWorkingCopyOwner.PRIMARY);
	}

	public Object[] getNonRubyResources() throws RubyModelException {
//		if (this.isDefaultPackage()) {
//			// We don't want to show non ruby resources of the default package (see PR #1G58NB8)
//			return RubyElementInfo.NO_NON_RUBY_RESOURCES;
//		} else {
			return ((SourceFolderInfo) getElementInfo()).getNonRubyResources(getResource());
//		}
	}

	public IRubyScript[] getRubyScripts() throws RubyModelException {
		ArrayList list = getChildrenOfType(SCRIPT);
		IRubyScript[] array= new IRubyScript[list.size()];
		list.toArray(array);
		return array;
	}

	public IRubyScript[] getRubyScripts(WorkingCopyOwner owner)
			throws RubyModelException {
		IRubyScript[] workingCopies = RubyModelManager.getRubyModelManager().getWorkingCopies(owner, false/*don't add primary*/);
		if (workingCopies == null) return RubyModelManager.NO_WORKING_COPY;
		int length = workingCopies.length;
		IRubyScript[] result = new IRubyScript[length];
		int index = 0;
		for (int i = 0; i < length; i++) {
			IRubyScript wc = workingCopies[i];
			if (equals(wc.getParent()) && !Util.isExcluded(wc)) { // 59933 - excluded wc shouldn't be answered back
				result[index++] = wc;
			}
		}
		if (index != length) {
			System.arraycopy(result, 0, result = new IRubyScript[index], 0, index);
		}
		return result;
	}

	public IPath getPath() {
		IRubyProject root = this.getRubyProject();
		IPath path = root.getPath();
		for (int i = 0, length = this.names.length; i < length; i++) {
			String name = this.names[i];
			path = path.append(name);
		}
		return path;
	}

	public IResource getResource() {
		IRubyProject root = this.getRubyProject();
		int length = this.names.length;
		if (length == 0) {
			return root.getResource();
		} else {
			IPath path = new Path(this.names[0]);
			for (int i = 1; i < length; i++)
				path = path.append(this.names[i]);
			return ((IContainer)root.getResource()).getFolder(path);
		}
	}

	public IResource getUnderlyingResource() throws RubyModelException {
		IResource rootResource = this.parent.getUnderlyingResource();
		if (rootResource == null) {
			//jar package fragment root that has no associated resource
			return null;
		}
		// the underlying resource may be a folder or a project (in the case that the project folder
		// is atually the package fragment root)
		if (rootResource.getType() == IResource.FOLDER || rootResource.getType() == IResource.PROJECT) {
			IContainer folder = (IContainer) rootResource;
			String[] segs = this.names;
			for (int i = 0; i < segs.length; ++i) {
				IResource child = folder.findMember(segs[i]);
				if (child == null || child.getType() != IResource.FOLDER) {
					throw newNotPresentException();
				}
				folder = (IFolder) child;
			}
			return folder;
		} else {
			return rootResource;
		}
	}

	public IRubyScript getRubyScript(String name) {
		if (!org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(name)) {
			throw new IllegalArgumentException(Messages.convention_unit_notJavaName); 
		}
		IPath path = this.getResource().getFullPath();
		path.append(name);
		path.addFileExtension(".rb");
		IFile file = ((IContainer) getResource()).getFile(path);
		return new RubyScript(this, file, name, DefaultWorkingCopyOwner.PRIMARY);
	}

}

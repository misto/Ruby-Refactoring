package org.rubypeople.eclipse.shams.resources;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

public class ShamProject extends ShamResource implements IProject {
	protected String projectName;
	protected List natures = new ArrayList();

	public ShamProject(String theProjectName) {
		this(new Path("undefined by sham creator"), theProjectName);
	}

	public ShamProject(IPath aPath, String theProjectName) {
		super(aPath);
		projectName = theProjectName;
	}

	public void build(int kind, String builderName, Map args, IProgressMonitor monitor) throws CoreException {}

	public void build(int kind, IProgressMonitor monitor) throws CoreException {}

	public void close(IProgressMonitor monitor) throws CoreException {}

	public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException {}

	public void create(IProgressMonitor monitor) throws CoreException {}

	public void delete(boolean deleteContent, boolean force, IProgressMonitor monitor) throws CoreException {}

	public IProjectDescription getDescription() throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IFile getFile(String name) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IFolder getFolder(String name) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IProjectNature getNature(String natureId) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IPath getPluginWorkingLocation(IPluginDescriptor plugin) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IProject[] getReferencedProjects() throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IProject[] getReferencingProjects() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public boolean hasNature(String natureId) throws CoreException {
		return natures.contains(natureId);
	}

	public boolean isNatureEnabled(String natureId) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public boolean isOpen() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

	public void open(IProgressMonitor monitor) throws CoreException {}

	public void setDescription(IProjectDescription description, IProgressMonitor monitor) throws CoreException {}

	public void setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

	public boolean exists(IPath path) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IResource findMember(String name) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IResource findMember(String name, boolean includePhantoms) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IResource findMember(IPath path) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IResource findMember(IPath path, boolean includePhantoms) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IFile getFile(IPath path) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IFolder getFolder(IPath path) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IResource[] members() throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IResource[] members(boolean includePhantoms) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IResource[] members(int memberFlags) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public Object getAdapter(Class adapter) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void accept(IResourceVisitor visitor) throws CoreException {}

	public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {}

	public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {}

	public void clearHistory(IProgressMonitor monitor) throws CoreException {}

	public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

	public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

	public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

	public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

	public IMarker createMarker(String type) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void delete(boolean force, IProgressMonitor monitor) throws CoreException {}

	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {}

	public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {}

	public boolean exists() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IMarker findMarker(long id) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IPath getLocation() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IMarker getMarker(long id) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public long getModificationStamp() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public String getName() {
		return projectName;
	}

	public IContainer getParent() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public String getPersistentProperty(QualifiedName key) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IProject getProject() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IPath getProjectRelativePath() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public Object getSessionProperty(QualifiedName key) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public int getType() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IWorkspace getWorkspace() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public boolean isAccessible() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public boolean isLocal(int depth) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public boolean isPhantom() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public boolean isReadOnly() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public boolean isSynchronized(int depth) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

	public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

	public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

	public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

	public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {}

	public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {}

	public void setPersistentProperty(QualifiedName key, String value) throws CoreException {}

	public void setReadOnly(boolean readOnly) {}

	public void setSessionProperty(QualifiedName key, Object value) throws CoreException {}

	public void touch(IProgressMonitor monitor) throws CoreException {}

	public boolean isDerived() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void setDerived(boolean isDerived) throws CoreException {}

	public boolean isTeamPrivateMember() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {}

	public void addNature(String string) {
		natures.add(string);
	}

}

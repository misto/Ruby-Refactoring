package org.rubypeople.eclipse.shams.resources;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

public class ShamFolder extends ShamResource implements IFolder {

	public ShamFolder(String aPathString) {
		super(new Path(aPathString));
	}

	public ShamFolder(IPath aPath) {
		super(aPath);
	}

	public void create(boolean force, boolean local, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void create(int updateFlags, boolean local, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IFile getFile(String name) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IFolder getFolder(String name) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean exists(IPath path) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IResource findMember(String name) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IResource findMember(String name, boolean includePhantoms) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IResource findMember(IPath path) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IResource findMember(IPath path, boolean includePhantoms) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IFile getFile(IPath path) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IFolder getFolder(IPath path) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IResource[] members() throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IResource[] members(boolean includePhantoms) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IResource[] members(int memberFlags) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public Object getAdapter(Class adapter) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void accept(IResourceVisitor visitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void clearHistory(IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IMarker createMarker(String type) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean exists() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IMarker findMarker(long id) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IPath getLocation() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IMarker getMarker(long id) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public long getModificationStamp() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IContainer getParent() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public String getPersistentProperty(QualifiedName key) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IProject getProject() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IPath getProjectRelativePath() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public Object getSessionProperty(QualifiedName key) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public int getType() {
		return FOLDER;
	}

	public IWorkspace getWorkspace() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isAccessible() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isLocal(int depth) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isPhantom() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isReadOnly() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isSynchronized(int depth) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setPersistentProperty(QualifiedName key, String value) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setReadOnly(boolean readOnly) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setSessionProperty(QualifiedName key, Object value) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void touch(IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isDerived() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setDerived(boolean isDerived) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isTeamPrivateMember() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

}

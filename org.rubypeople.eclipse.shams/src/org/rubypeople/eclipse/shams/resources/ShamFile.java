package org.rubypeople.eclipse.shams.resources;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;

public class ShamFile extends ShamResource implements IFile {
	protected String contents = "";
	protected boolean readContentFromFile;

	public ShamFile(String fullPath) {
		this(fullPath, false);
	}

	public ShamFile(IPath aPath) {
		this(aPath, false);
	}

	public ShamFile(String fullPath, boolean readContentFromFile) {
		this(new Path(fullPath), readContentFromFile);
	}

	public ShamFile(IPath aPath, boolean readContentFromFile) {
		super(aPath);
		this.readContentFromFile = readContentFromFile;
	}

	public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
	}

	public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {
	}

	public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
	}

	public InputStream getContents() throws CoreException {
		if (readContentFromFile) {
			try {
				return new FileInputStream(this.path.toString());
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		return new ByteArrayInputStream(contents.getBytes());
	}

	public InputStream getContents(boolean force) throws CoreException {
		return getContents();
	}

	public int getEncoding() throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public boolean isReadOnly() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
	}

	public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
	}

	public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
	}

	public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public void setContents(String shamContents) {
		this.contents = shamContents;
	}

	public void accept(IResourceVisitor visitor) throws CoreException {
	}

	public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {
	}

	public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {
	}

	public void clearHistory(IProgressMonitor monitor) throws CoreException {
	}

	public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {
	}

	public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
	}

	public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public IMarker createMarker(String type) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void delete(boolean force, IProgressMonitor monitor) throws CoreException {
	}

	public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
	}

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

	public IContainer getParent() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public String getPersistentProperty(QualifiedName key) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public IProject getProject() {
		return new ShamProject("not specified");
	}

	public IPath getProjectRelativePath() {
		return new Path("");
	}

	public Object getSessionProperty(QualifiedName key) throws CoreException {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public int getType() {
		return FILE;
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

	public boolean isSynchronized(int depth) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {
	}

	public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {
	}

	public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {
	}

	public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {
	}

	public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {
	}

	public void setPersistentProperty(QualifiedName key, String value) throws CoreException {
	}

	public void setReadOnly(boolean readOnly) {
	}

	public void setSessionProperty(QualifiedName key, Object value) throws CoreException {
	}

	public void touch(IProgressMonitor monitor) throws CoreException {
	}

	public boolean isDerived() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void setDerived(boolean isDerived) throws CoreException {
	}

	public boolean isTeamPrivateMember() {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {
	}

	public Object getAdapter(Class adapter) {
		throw new RuntimeException("Unimplemented method in sham");
	}

	public void createLink(
		IPath localLocation,
		int updateFlags,
		IProgressMonitor monitor)
		throws CoreException {
	}

	public IPath getRawLocation() {
		return null;
	}

	public boolean isLinked() {
		return false;
	}
}

package org.rubypeople.rdt.core.tests.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

import junit.framework.TestCase;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
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
import org.eclipse.core.runtime.QualifiedName;
import org.rubypeople.rdt.core.*;

public class TC_RubyProject extends TestCase {

	public TC_RubyProject(String name) {
		super(name);
	}

	public void testGetLibraryPathXML() {
		ShamRubyProject rubyProject = new ShamRubyProject();
		rubyProject.setProject(new ShamProject("TheWorkingProject"));

		IProject referencedProject = new ShamProject("TheReferencedProject");
		rubyProject.addLoadPathEntry(referencedProject);
		assertEquals("XML should indicate only one referenced project.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"/TheReferencedProject\"/></loadpath>", rubyProject.getLoadPathXML());

		IProject anotherReferencedProject = new ShamProject("AnotherReferencedProject");
		rubyProject.addLoadPathEntry(anotherReferencedProject);
		assertEquals("XML should indicate two referenced projects.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"/TheReferencedProject\"/><pathentry type=\"project\" path=\"/AnotherReferencedProject\"/></loadpath>", rubyProject.getLoadPathXML());
		
		rubyProject.removeLoadPathEntry(referencedProject);
		assertEquals("XML should indicate one referenced project after removing one.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"/AnotherReferencedProject\"/></loadpath>", rubyProject.getLoadPathXML());
	}
	
	public void testGetReferencedProjects() {
		ShamRubyProject rubyProject = new ShamRubyProject();
		
		IProject referencedProject = (IProject) rubyProject.getReferencedProjects().get(0);
		assertNotNull(referencedProject);
	}

	public class ShamRubyProject extends RubyProject {
		
		protected IFile getLoadPathEntriesFile() {
			return new LibraryPathsFile();
		}
		
		protected IProject getProject(String name) {
			return new ShamProject();
		}

		protected String getLoadPathXML() {
			return super.getLoadPathXML();
		}

	}

	public class LibraryPathsFile implements IFile {

		public LibraryPathsFile() {
			super();
		}

		public void appendContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

		public void appendContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void create(InputStream source, boolean force, IProgressMonitor monitor) throws CoreException {}

		public void create(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void delete(boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

		public InputStream getContents() throws CoreException {
			return new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"/StoredReferencedProject\"/><pathentry type=\"project\" path=\"/AnotherStoredReferencedProject\"/></loadpath>".getBytes());
		}

		public InputStream getContents(boolean force) throws CoreException {
			return getContents();
		}

		public IPath getFullPath() {
			return null;
		}

		public IFileState[] getHistory(IProgressMonitor monitor) throws CoreException {
			return null;
		}

		public String getName() {
			return null;
		}

		public boolean isReadOnly() {
			return false;
		}

		public void move(IPath destination, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

		public void setContents(IFileState source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

		public void setContents(IFileState source, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void setContents(InputStream source, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

		public void setContents(InputStream source, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {}

		public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {}

		public void accept(IResourceVisitor visitor) throws CoreException {}

		public void clearHistory(IProgressMonitor monitor) throws CoreException {}

		public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

		public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

		public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public IMarker createMarker(String type) throws CoreException {
			return null;
		}

		public void delete(boolean force, IProgressMonitor monitor) throws CoreException {}

		public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {}

		public boolean exists() {
			return false;
		}

		public IMarker findMarker(long id) throws CoreException {
			return null;
		}

		public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
			return null;
		}

		public String getFileExtension() {
			return null;
		}

		public IPath getLocation() {
			return null;
		}

		public IMarker getMarker(long id) {
			return null;
		}

		public long getModificationStamp() {
			return 0;
		}

		public IContainer getParent() {
			return null;
		}

		public String getPersistentProperty(QualifiedName key) throws CoreException {
			return null;
		}

		public IProject getProject() {
			return null;
		}

		public IPath getProjectRelativePath() {
			return null;
		}

		public Object getSessionProperty(QualifiedName key) throws CoreException {
			return null;
		}

		public int getType() {
			return 0;
		}

		public IWorkspace getWorkspace() {
			return null;
		}

		public boolean isAccessible() {
			return false;
		}

		public boolean isDerived() {
			return false;
		}

		public boolean isLocal(int depth) {
			return false;
		}

		public boolean isPhantom() {
			return false;
		}

		public boolean isTeamPrivateMember() {
			return false;
		}

		public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

		public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

		public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {}

		public void setDerived(boolean isDerived) throws CoreException {}

		public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {}

		public void setPersistentProperty(QualifiedName key, String value) throws CoreException {}

		public void setReadOnly(boolean readOnly) {}

		public void setSessionProperty(QualifiedName key, Object value) throws CoreException {}

		public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {}

		public void touch(IProgressMonitor monitor) throws CoreException {}

		public Object getAdapter(Class adapter) {
			return null;
		}

		/**
		 * @see IResource#isSynchronized(int)
		 */
		public boolean isSynchronized(int depth) {
			return false;
		}

		public int getEncoding() throws CoreException {
			return 0;
		}

	}

	public class ShamProject implements IProject {
		protected String name;
		
		public ShamProject() {
			name = "InitialName";
		}
		
		public ShamProject(String theProjectName) {
			name = theProjectName;
		}
		
		public IPath getFullPath() {
			return new IPath() {
				public IPath addFileExtension(String extension) {
					return null;
				}

				public IPath addTrailingSeparator() {
					return null;
				}

				public IPath append(String path) {
					return null;
				}

				public IPath append(IPath path) {
					return null;
				}

				public Object clone() {
					return null;
				}

				public boolean equals(Object obj) {
					return false;
				}

				public String getDevice() {
					return null;
				}

				public String getFileExtension() {
					return null;
				}

				public boolean hasTrailingSeparator() {
					return false;
				}

				public boolean isAbsolute() {
					return false;
				}

				public boolean isEmpty() {
					return false;
				}

				public boolean isPrefixOf(IPath anotherPath) {
					return false;
				}

				public boolean isRoot() {
					return false;
				}

				public boolean isUNC() {
					return false;
				}

				public boolean isValidPath(String path) {
					return false;
				}

				public boolean isValidSegment(String segment) {
					return false;
				}

				public String lastSegment() {
					return null;
				}

				public IPath makeAbsolute() {
					return null;
				}

				public IPath makeRelative() {
					return null;
				}

				public IPath makeUNC(boolean toUNC) {
					return null;
				}

				public int matchingFirstSegments(IPath anotherPath) {
					return 0;
				}

				public IPath removeFileExtension() {
					return null;
				}

				public IPath removeFirstSegments(int count) {
					return null;
				}

				public IPath removeLastSegments(int count) {
					return null;
				}

				public IPath removeTrailingSeparator() {
					return null;
				}

				public String segment(int index) {
					return null;
				}

				public int segmentCount() {
					return 0;
				}

				public String[] segments() {
					return null;
				}

				public IPath setDevice(String device) {
					return null;
				}

				public File toFile() {
					return null;
				}

				public String toOSString() {
					return null;
				}

				public String toString() {
					return "/" + name;
				}

				public IPath uptoSegment(int count) {
					return null;
				}

			}
;
		}
		public void build(int kind, IProgressMonitor monitor) throws CoreException {}

		public void build(int kind, String builderName, Map args, IProgressMonitor monitor) throws CoreException {}

		public void close(IProgressMonitor monitor) throws CoreException {}

		public void create(IProgressMonitor monitor) throws CoreException {}

		public void create(IProjectDescription description, IProgressMonitor monitor) throws CoreException {}

		public void delete(boolean deleteContent, boolean force, IProgressMonitor monitor) throws CoreException {}

		public IProjectDescription getDescription() throws CoreException {
			return null;
		}

		public IFile getFile(String name) {
			return null;
		}

		public IFolder getFolder(String name) {
			return null;
		}

		public IProjectNature getNature(String natureId) throws CoreException {
			return null;
		}

		public IPath getPluginWorkingLocation(IPluginDescriptor plugin) {
			return null;
		}

		public IProject[] getReferencedProjects() throws CoreException {
			return null;
		}

		public IProject[] getReferencingProjects() {
			return null;
		}

		public boolean hasNature(String natureId) throws CoreException {
			return false;
		}

		public boolean isNatureEnabled(String natureId) throws CoreException {
			return false;
		}

		public boolean isOpen() {
			return false;
		}

		public void move(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

		public void open(IProgressMonitor monitor) throws CoreException {}

		public void setDescription(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void setDescription(IProjectDescription description, IProgressMonitor monitor) throws CoreException {}

		public boolean exists(IPath path) {
			return false;
		}

		public IFile[] findDeletedMembersWithHistory(int depth, IProgressMonitor monitor) throws CoreException {
			return null;
		}

		public IResource findMember(IPath path, boolean includePhantoms) {
			return null;
		}

		public IResource findMember(IPath path) {
			return null;
		}

		public IResource findMember(String name, boolean includePhantoms) {
			return null;
		}

		public IResource findMember(String name) {
			return null;
		}

		public IFile getFile(IPath path) {
			return null;
		}

		public IFolder getFolder(IPath path) {
			return null;
		}

		public IResource[] members() throws CoreException {
			return null;
		}

		public IResource[] members(boolean includePhantoms) throws CoreException {
			return null;
		}

		public IResource[] members(int memberFlags) throws CoreException {
			return null;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public void accept(IResourceVisitor visitor, int depth, boolean includePhantoms) throws CoreException {}

		public void accept(IResourceVisitor visitor, int depth, int memberFlags) throws CoreException {}

		public void accept(IResourceVisitor visitor) throws CoreException {}

		public void clearHistory(IProgressMonitor monitor) throws CoreException {}

		public void copy(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

		public void copy(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void copy(IProjectDescription description, boolean force, IProgressMonitor monitor) throws CoreException {}

		public void copy(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public IMarker createMarker(String type) throws CoreException {
			return null;
		}

		public void delete(boolean force, IProgressMonitor monitor) throws CoreException {}

		public void delete(int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void deleteMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {}

		public boolean exists() {
			return false;
		}

		public IMarker findMarker(long id) throws CoreException {
			return null;
		}

		public IMarker[] findMarkers(String type, boolean includeSubtypes, int depth) throws CoreException {
			return null;
		}

		public String getFileExtension() {
			return null;
		}

		public IPath getLocation() {
			return null;
		}

		public IMarker getMarker(long id) {
			return null;
		}

		public long getModificationStamp() {
			return 0;
		}

		public String getName() {
			return name;
		}

		public IContainer getParent() {
			return null;
		}

		public String getPersistentProperty(QualifiedName key) throws CoreException {
			return null;
		}

		public IProject getProject() {
			return null;
		}

		public IPath getProjectRelativePath() {
			return null;
		}

		public Object getSessionProperty(QualifiedName key) throws CoreException {
			return null;
		}

		public int getType() {
			return 0;
		}

		public IWorkspace getWorkspace() {
			return null;
		}

		public boolean isAccessible() {
			return false;
		}

		public boolean isDerived() {
			return false;
		}

		public boolean isLocal(int depth) {
			return false;
		}

		public boolean isPhantom() {
			return false;
		}

		public boolean isReadOnly() {
			return false;
		}

		public boolean isTeamPrivateMember() {
			return false;
		}

		public void move(IPath destination, boolean force, IProgressMonitor monitor) throws CoreException {}

		public void move(IPath destination, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void move(IProjectDescription description, boolean force, boolean keepHistory, IProgressMonitor monitor) throws CoreException {}

		public void move(IProjectDescription description, int updateFlags, IProgressMonitor monitor) throws CoreException {}

		public void refreshLocal(int depth, IProgressMonitor monitor) throws CoreException {}

		public void setDerived(boolean isDerived) throws CoreException {}

		public void setLocal(boolean flag, int depth, IProgressMonitor monitor) throws CoreException {}

		public void setPersistentProperty(QualifiedName key, String value) throws CoreException {}

		public void setReadOnly(boolean readOnly) {}

		public void setSessionProperty(QualifiedName key, Object value) throws CoreException {}

		public void setTeamPrivateMember(boolean isTeamPrivate) throws CoreException {}

		public void touch(IProgressMonitor monitor) throws CoreException {}

		/**
		 * @see IResource#isSynchronized(int)
		 */
		public boolean isSynchronized(int depth) {
			return false;
		}

	}
}
package org.rubypeople.rdt.internal.ui.tests;

import java.io.InputStream;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.util.List;

import junit.framework.TestCase;

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
import org.eclipse.core.runtime.QualifiedName;
import org.rubypeople.rdt.core.RubyParsedComponent;
import org.rubypeople.rdt.core.RubyParser;

public class TC_RubyParser extends TestCase {
	protected RubyParser parser;
	protected ShamIFile file;
	
	public TC_RubyParser(String name) {
		super(name);
	}
	
	protected void setUp() {
		parser = new RubyParser();
		file = new ShamIFile();
	}
	
	public void testGetComponentHierarchy() {
		file.setFileContents("class Simple\n\ndef initialize()\n@variable = \"hello\"\nend\n\nend");
		
		RubyParsedComponent parsedComponent = parser.getComponentHierarchy(file);
		
		assertTrue(parsedComponent instanceof RubyParsedComponent);
		assertEquals("There should be one child", 1, parsedComponent.getChildren().size());
		assertEquals("nameoffset for top level should be zero", 0, parsedComponent.nameOffset());
		assertEquals("namelength for top level should be zero", 0, parsedComponent.nameLength());
		assertEquals("length for top level should be zero", 0, parsedComponent.length());
		assertEquals("offset for top level should be zero", 0, parsedComponent.offset());
		
		RubyParsedComponent classComponent = (RubyParsedComponent) parsedComponent.getChildren().get(0);
		assertEquals("Simple", classComponent.getName());
		assertEquals(5, classComponent.nameOffset());
		assertEquals(6, classComponent.nameLength());
		//assertEquals(61, classComponent.length());
		assertEquals(5, classComponent.offset());
		
		
		RubyParsedComponent firstMethodComponent = (RubyParsedComponent) classComponent.getChildren().get(0);
		assertEquals("The first method should be named correctly", "initialize()", firstMethodComponent.getName());
	}
	
	public void testGetComponentHierarchy_multipleMethods() {
		file.setFileContents("class Simple\n\n\n\ndef someMethod var = 1\nend\n\ndef initialize()\n@variable = \"hello\"\nend\n\nend");
		
		RubyParsedComponent parsedComponent = parser.getComponentHierarchy(file);
		List children = ((RubyParsedComponent)parsedComponent.getChildren().get(0)).getChildren();
		
		assertEquals("there should be two children", 2, children.size());
		assertEquals("The first childs name should be correct", "someMethod", ((RubyParsedComponent)children.get(0)).getName());
		assertEquals("The second childs name should be correct", "initialize()", ((RubyParsedComponent)children.get(1)).getName());		
	}

	
	class ShamIFile implements IFile {
		protected String fileContents;
		
		public void appendContents(InputStream arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
		}

		public void appendContents(InputStream arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void create(InputStream arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void create(InputStream arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void delete(boolean arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
		}

		public InputStream getContents() throws CoreException {
			return new StringBufferInputStream(fileContents);
		}

		public InputStream getContents(boolean arg0) throws CoreException {
			return null;
		}

		public int getEncoding() throws CoreException {
			return 0;
		}

		public IPath getFullPath() {
			return null;
		}

		public IFileState[] getHistory(IProgressMonitor arg0) throws CoreException {
			return null;
		}

		public String getName() {
			return null;
		}

		public boolean isReadOnly() {
			return false;
		}

		public void move(IPath arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
		}

		public void setContents(IFileState arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
		}

		public void setContents(IFileState arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void setContents(InputStream arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
		}

		public void setContents(InputStream arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void accept(IResourceVisitor arg0, int arg1, boolean arg2) throws CoreException {
		}

		public void accept(IResourceVisitor arg0, int arg1, int arg2) throws CoreException {
		}

		public void accept(IResourceVisitor arg0) throws CoreException {
		}

		public void clearHistory(IProgressMonitor arg0) throws CoreException {
		}

		public void copy(IPath arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void copy(IPath arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void copy(IProjectDescription arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void copy(IProjectDescription arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public IMarker createMarker(String arg0) throws CoreException {
			return null;
		}

		public void delete(boolean arg0, IProgressMonitor arg1) throws CoreException {
		}

		public void delete(int arg0, IProgressMonitor arg1) throws CoreException {
		}

		public void deleteMarkers(String arg0, boolean arg1, int arg2) throws CoreException {
		}

		public boolean exists() {
			return false;
		}

		public IMarker findMarker(long arg0) throws CoreException {
			return null;
		}

		public IMarker[] findMarkers(String arg0, boolean arg1, int arg2) throws CoreException {
			return null;
		}

		public String getFileExtension() {
			return null;
		}

		public IPath getLocation() {
			return null;
		}

		public IMarker getMarker(long arg0) {
			return null;
		}

		public long getModificationStamp() {
			return 0;
		}

		public IContainer getParent() {
			return null;
		}

		public String getPersistentProperty(QualifiedName arg0) throws CoreException {
			return null;
		}

		public IProject getProject() {
			return null;
		}

		public IPath getProjectRelativePath() {
			return null;
		}

		public Object getSessionProperty(QualifiedName arg0) throws CoreException {
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

		public boolean isLocal(int arg0) {
			return false;
		}

		public boolean isPhantom() {
			return false;
		}

		public boolean isSynchronized(int arg0) {
			return false;
		}

		public boolean isTeamPrivateMember() {
			return false;
		}

		public void move(IPath arg0, boolean arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void move(IPath arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void move(IProjectDescription arg0, boolean arg1, boolean arg2, IProgressMonitor arg3) throws CoreException {
		}

		public void move(IProjectDescription arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void refreshLocal(int arg0, IProgressMonitor arg1) throws CoreException {
		}

		public void setDerived(boolean arg0) throws CoreException {
		}

		public void setLocal(boolean arg0, int arg1, IProgressMonitor arg2) throws CoreException {
		}

		public void setPersistentProperty(QualifiedName arg0, String arg1) throws CoreException {
		}

		public void setReadOnly(boolean arg0) {
		}

		public void setSessionProperty(QualifiedName arg0, Object arg1) throws CoreException {
		}

		public void setTeamPrivateMember(boolean arg0) throws CoreException {
		}

		public void touch(IProgressMonitor arg0) throws CoreException {
		}

		public Object getAdapter(Class arg0) {
			return null;
		}
		
		public void setFileContents(String fileContents) {
			this.fileContents = fileContents;
		}

	}

}

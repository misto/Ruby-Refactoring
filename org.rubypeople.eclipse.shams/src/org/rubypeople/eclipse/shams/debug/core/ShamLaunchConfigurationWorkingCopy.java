package org.rubypeople.eclipse.shams.debug.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

public class ShamLaunchConfigurationWorkingCopy implements ILaunchConfigurationWorkingCopy {
	protected Map attributes = new HashMap();

	public ShamLaunchConfigurationWorkingCopy() {
		super();
	}

	public boolean isDirty() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfiguration doSave() throws CoreException {
		return new ShamLaunchConfiguration();
	}

	public void setAttribute(String attributeName, int value) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setAttribute(String attributeName, String value) {
		attributes.put(attributeName, value);
	}

	public void setAttribute(String attributeName, List value) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setAttribute(String attributeName, Map value) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setAttribute(String attributeName, boolean value) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfiguration getOriginal() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void rename(String name) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setContainer(IContainer container) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunch launch(String mode, IProgressMonitor monitor) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean supportsMode(String mode) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public String getName() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IPath getLocation() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean exists() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public int getAttribute(String attributeName, int defaultValue) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public String getAttribute(String attributeName, String defaultValue) throws CoreException {
		String value = (String) attributes.get(attributeName);
		return value == null ? defaultValue : value;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public List getAttribute(String attributeName, List defaultValue) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public Map getAttribute(String attributeName, Map defaultValue) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IFile getFile() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfigurationType getType() throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isLocal() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfigurationWorkingCopy copy(String name) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isWorkingCopy() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void delete() throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public String getMemento() throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean contentsEqual(ILaunchConfiguration configuration) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public Object getAdapter(Class adapter) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void setAttributes(Map attributes) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public String getCategory() throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public Map getAttributes() throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchConfiguration#launch(java.lang.String, org.eclipse.core.runtime.IProgressMonitor, boolean)
	 */
	public ILaunch launch(String mode, IProgressMonitor monitor, boolean build) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.debug.core.ILaunchConfiguration#launch(java.lang.String, org.eclipse.core.runtime.IProgressMonitor, boolean, boolean)
	 */
	public ILaunch launch(String mode, IProgressMonitor monitor, boolean build, boolean register) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}
}

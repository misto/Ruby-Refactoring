package org.rubypeople.eclipse.shams.debug.core;

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

	public ShamLaunchConfigurationWorkingCopy() {
		super();
	}

	public boolean isDirty() {
		return false;
	}

	public ILaunchConfiguration doSave() throws CoreException {
		return null;
	}

	public void setAttribute(String attributeName, int value) {
	}

	public void setAttribute(String attributeName, String value) {
	}

	public void setAttribute(String attributeName, List value) {
	}

	public void setAttribute(String attributeName, Map value) {
	}

	public void setAttribute(String attributeName, boolean value) {
	}

	public ILaunchConfiguration getOriginal() {
		return null;
	}

	public void rename(String name) {
	}

	public void setContainer(IContainer container) {
	}

	public ILaunch launch(String mode, IProgressMonitor monitor) throws CoreException {
		return null;
	}

	public boolean supportsMode(String mode) throws CoreException {
		return false;
	}

	public String getName() {
		return null;
	}

	public IPath getLocation() {
		return null;
	}

	public boolean exists() {
		return false;
	}

	public int getAttribute(String attributeName, int defaultValue) throws CoreException {
		return 0;
	}

	public String getAttribute(String attributeName, String defaultValue) throws CoreException {
		return null;
	}

	public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException {
		return false;
	}

	public List getAttribute(String attributeName, List defaultValue) throws CoreException {
		return null;
	}

	public Map getAttribute(String attributeName, Map defaultValue) throws CoreException {
		return null;
	}

	public IFile getFile() {
		return null;
	}

	public ILaunchConfigurationType getType() throws CoreException {
		return null;
	}

	public boolean isLocal() {
		return false;
	}

	public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
		return null;
	}

	public ILaunchConfigurationWorkingCopy copy(String name) throws CoreException {
		return null;
	}

	public boolean isWorkingCopy() {
		return false;
	}

	public void delete() throws CoreException {
	}

	public String getMemento() throws CoreException {
		return null;
	}

	public boolean contentsEqual(ILaunchConfiguration configuration) {
		return false;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

}

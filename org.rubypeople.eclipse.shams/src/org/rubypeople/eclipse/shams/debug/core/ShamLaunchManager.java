package org.rubypeople.eclipse.shams.debug.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IProcess;

public class ShamLaunchManager implements ILaunchManager {
	protected List launches = new ArrayList();

	public ShamLaunchManager() {
		super();
	}

	public void addLaunchListener(ILaunchListener listener) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void removeLaunch(ILaunch launch) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IDebugTarget[] getDebugTargets() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunch[] getLaunches() {
		return (ILaunch[]) launches.toArray(new ILaunch[launches.size()]);
	}

	public IProcess[] getProcesses() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void addLaunch(ILaunch launch) {
		launches.add(launch);
	}

	public void removeLaunchListener(ILaunchListener listener) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfiguration[] getLaunchConfigurations() throws CoreException {
		List configurations = new ArrayList();
		for (Iterator iter = launches.iterator(); iter.hasNext();) {
			ILaunch aLaunch = (ILaunch) iter.next();
			configurations.add(aLaunch.getLaunchConfiguration());
		}
		return (ILaunchConfiguration[]) configurations.toArray(new ILaunchConfiguration[configurations.size()]);
	}

	public ILaunchConfiguration[] getLaunchConfigurations(ILaunchConfigurationType type) throws CoreException {
		return getLaunchConfigurations();
	}

	public ILaunchConfiguration getLaunchConfiguration(IFile file) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfiguration getLaunchConfiguration(String memento) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfigurationType[] getLaunchConfigurationTypes() {
		throw new RuntimeException("Need to implement on sham.");
	}

	public ILaunchConfigurationType getLaunchConfigurationType(String id) {
		return new ILaunchConfigurationType() {
			public boolean supportsMode(String mode) {
				throw new RuntimeException("Need to implement on sham.");
			}

			public String getName() {
				throw new RuntimeException("Need to implement on sham.");
			}

			public String getIdentifier() {
				throw new RuntimeException("Need to implement on sham.");
			}

			public boolean isPublic() {
				throw new RuntimeException("Need to implement on sham.");
			}

			public ILaunchConfigurationWorkingCopy newInstance(IContainer container, String name) throws CoreException {
				throw new RuntimeException("Need to implement on sham.");
			}

			public ILaunchConfigurationDelegate getDelegate() throws CoreException {
				throw new RuntimeException("Need to implement on sham.");
			}
		};
	}

	public void addLaunchConfigurationListener(ILaunchConfigurationListener listener) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public void removeLaunchConfigurationListener(ILaunchConfigurationListener listener) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public boolean isExistingLaunchConfigurationName(String name) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

	public String generateUniqueLaunchConfigurationNameFrom(String namePrefix) {
		throw new RuntimeException("Need to implement on sham.");
	}

	public IPersistableSourceLocator newSourceLocator(String identifier) throws CoreException {
		throw new RuntimeException("Need to implement on sham.");
	}

}

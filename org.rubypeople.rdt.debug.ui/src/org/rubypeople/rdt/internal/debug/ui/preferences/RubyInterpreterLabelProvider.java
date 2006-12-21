package org.rubypeople.rdt.internal.debug.ui.preferences;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.launching.IInterpreter;

public class RubyInterpreterLabelProvider implements ITableLabelProvider {

	public RubyInterpreterLabelProvider() {
		super();
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		IInterpreter interpreter = (IInterpreter) element;
		switch (columnIndex) {
			case 0 :
				return interpreter.getName();
			case 1 :
				IPath installLocation = interpreter.getInstallLocation();
				return installLocation != null ? installLocation.toOSString() : "In user path";
			default :
				return "Unknown Column Index";
		}
	}

	public void addListener(ILabelProviderListener listener) {}

	public void dispose() {}

	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {}

}
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.texteditor.AbstractMarkerAnnotationModel;

public class ExternalFileRubyAnnotationModel extends AbstractMarkerAnnotationModel {

	protected void deleteMarkers(IMarker[] markers) throws CoreException {
		// TODO Auto-generated method stub

	}

	protected boolean isAcceptable(IMarker marker) {
		// TODO Auto-generated method stub
		return false;
	}

	protected void listenToMarkerChanges(boolean listen) {
		// TODO Auto-generated method stub

	}

	protected IMarker[] retrieveMarkers() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}

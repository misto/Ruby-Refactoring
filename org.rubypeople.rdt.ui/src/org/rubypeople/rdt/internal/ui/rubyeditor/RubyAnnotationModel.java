package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

public class RubyAnnotationModel extends ResourceMarkerAnnotationModel {

	public RubyAnnotationModel(IFileEditorInput input) {
		super(input.getFile());
	}

	public RubyAnnotationModel(IFile file) {
		super(file);
	}

	protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
		return new RubyMarkerAnnotation(marker);
	}

}
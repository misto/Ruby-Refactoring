package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.awt.image.RescaleOp;
import java.util.Iterator;
import javax.swing.AbstractAction;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.MarkerAnnotation;
import org.eclipse.ui.texteditor.ResourceMarkerAnnotationModel;

public class RubyAnnotationModel extends ResourceMarkerAnnotationModel {
	public RubyAnnotationModel(IFileEditorInput input) {
		super(input.getFile()) ;
	}

	protected MarkerAnnotation createMarkerAnnotation(IMarker marker) {
		return new RubyMarkerAnnotation(marker);
	}

}

package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.ui.texteditor.MarkerAnnotation;

public class RubyMarkerAnnotation extends MarkerAnnotation {
	IDebugModelPresentation fPresentation ;
	public RubyMarkerAnnotation(IMarker marker) {
		super(marker);
	}
	
	protected void initialize() {
		super.initialize();
		if (fPresentation == null) {
				fPresentation= DebugUITools.newDebugModelPresentation();
		}
				
		setLayer(4);
		setImage(fPresentation.getImage(this.getMarker()));
	}
}

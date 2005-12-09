package org.rubypeople.rdt.ui.extensions;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;


/**
 * Provides the text for a TextHover request.
 * 
 * @author murphee
 *
 */
public interface ITextHoverProvider {
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion);	
}

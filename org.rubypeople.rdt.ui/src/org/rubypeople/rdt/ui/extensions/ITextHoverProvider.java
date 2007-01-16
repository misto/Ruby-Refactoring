package org.rubypeople.rdt.ui.extensions;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorInput;


/**
 * Provides the text for a TextHover request.
 * 
 * @author murphee
 *
 */
public interface ITextHoverProvider {
	/**
	 * 
	 * @param input 
	 * @param textViewer the ITextViewer that shows this hover
	 * @param hoverRegion the region that was preselected by the Ruby Hover system
	 * @return the hover text OR null if no text was found 
	 */
	public String getHoverInfo(IEditorInput input, ITextViewer textViewer, IRegion hoverRegion);	
}

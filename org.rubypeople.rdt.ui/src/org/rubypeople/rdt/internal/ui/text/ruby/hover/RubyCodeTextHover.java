package org.rubypeople.rdt.internal.ui.text.ruby.hover;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.extensions.ITextHoverProvider;


/**
 * Generic TextHover that uses installed extensions for getting information; when the TextHover is requested  
 * then all installed TextHoverProvider extensions will be asked to provide a String to show for the location.  
 * 
 * @author murphee
 *
 */
public class RubyCodeTextHover extends AbstractRubyEditorTextHover {

	
	public static final String RDT_UI_NAMESPACE = "org.rubypeople.rdt.ui";
	public static final String RDT_UI_TEXTHOVERPROVIDER = "textHoverProvider";
	
	private List fExtensions;

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		List extensions = initExtensions();
		try {
			final String symbol = textViewer.getDocument().get(hoverRegion.getOffset(), hoverRegion.getLength());
			// first ask the extensions
			if(extensions.size() > 0){
				for(int i=0; i< extensions.size(); i++){
					ITextHoverProvider currentProvider = (ITextHoverProvider) extensions.get(i);
					String hoverText = currentProvider.getHoverInfo(textViewer, hoverRegion);
					if(hoverText != null){
						return hoverText;
					}					
				}

			} 		

		} catch (BadLocationException e1) {
			RubyPlugin.log(e1);
		}
		return null;
	}

	private List initExtensions() {
		if(fExtensions == null){
			fExtensions = new ArrayList();
			IExtensionRegistry reg = Platform.getExtensionRegistry();
			
			IExtensionPoint[] points = reg.getExtensionPoints(RDT_UI_NAMESPACE);
			// TODO: Look for textProvider!
			IExtensionPoint point = null;
			
			if(points != null){
				for (int i = 0; i < points.length; i++) {
					IExtensionPoint currentPoint = points[i];
					if(currentPoint.getUniqueIdentifier().endsWith(RDT_UI_TEXTHOVERPROVIDER)){
						point = currentPoint;
						break;
					}				
				}
				
				if(point != null){
					IExtension[] exts = points[0].getExtensions();
					
					ITextHoverProvider prov = null;
					
					for (int i = 0; i < exts.length; i++) {
						IConfigurationElement[] elem = exts[i].getConfigurationElements();
						String attrs[] = elem[0].getAttributeNames();
						try {
							Object tempProv = elem[0].createExecutableExtension("class");
							if (tempProv instanceof ITextHoverProvider) {
								prov = (ITextHoverProvider) tempProv;
								fExtensions.add(prov);
							}
//							Object tempExtension = elem[0].getAttribute("fileExtension");
//							if (tempExtension instanceof String) {
//							String extension = (String) tempExtension;
//							fileExtensionToProvider.put(extension.trim(), prov);
//							}
						} catch (Exception e) {
							RubyPlugin.log(e);
						}
						
					}
				}
			}
			
		}
		return fExtensions;
		
	}

}

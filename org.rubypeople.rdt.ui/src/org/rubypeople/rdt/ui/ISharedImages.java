package org.rubypeople.rdt.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;

public interface ISharedImages {
	/**
	 * Key to access the shared image or image descriptor for a library (class path container).
	 * @since 0.9.0
	 */
	public static final String IMG_OBJS_LIBRARY= RubyPluginImages.IMG_OBJS_LIBRARY;
	
	Image getImage(String key);

	ImageDescriptor getImageDescriptor(String key);

}

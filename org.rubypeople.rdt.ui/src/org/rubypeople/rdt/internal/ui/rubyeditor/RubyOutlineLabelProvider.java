package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.core.parser.RubyClass;
import org.rubypeople.rdt.internal.core.parser.RubyClassVariable;
import org.rubypeople.rdt.internal.core.parser.RubyInstanceVariable;
import org.rubypeople.rdt.internal.core.parser.RubyMethod;
import org.rubypeople.rdt.internal.core.parser.RubyParsedComponent;
import org.rubypeople.rdt.internal.ui.RdtUiImages;

public class RubyOutlineLabelProvider implements ILabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object object) {
		if (RubyInstanceVariable.class.equals(object.getClass()))
			return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYINSTVAR);

		if (RubyClassVariable.class.equals(object.getClass()))
			return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYCLASSVAR);

		if (RubyMethod.class.equals(object.getClass()))
			return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYMETHOD);

		if (RubyClass.class.equals(object.getClass()))
			return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYCLASS);

		return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBY);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object obj) {
		if (obj instanceof RubyParsedComponent)
			return ((RubyParsedComponent) obj).getName();
		else
			return obj.toString();
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener arg0) {
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object, java.lang.String)
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener arg0) {
	}

}

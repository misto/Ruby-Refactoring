package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ast.IRubyElement;
import org.rubypeople.rdt.internal.core.parser.ast.RubyClass;
import org.rubypeople.rdt.internal.core.parser.ast.RubyClassVariable;
import org.rubypeople.rdt.internal.core.parser.ast.RubyGlobal;
import org.rubypeople.rdt.internal.core.parser.ast.RubyInstanceVariable;
import org.rubypeople.rdt.internal.core.parser.ast.RubyMethod;
import org.rubypeople.rdt.internal.core.parser.ast.RubyModule;
import org.rubypeople.rdt.internal.core.parser.ast.RubyRequires;
import org.rubypeople.rdt.internal.ui.RdtUiImages;

public class RubyOutlineLabelProvider implements ILabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object object) {
		if (object == null) {
			log("Attempting to get Image for null object in outline elements");
			return RdtUiImages.get(RdtUiImages.IMG_OBJS_ERROR);
		}
		if (RubyGlobal.class.equals(object.getClass())) return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYGLOBAL);

		if (RubyRequires.class.equals(object.getClass())) return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYIMPORT);

		if (RubyModule.class.equals(object.getClass())) return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYMODULE);

		if (RubyClassVariable.class.equals(object.getClass())) return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYCLASSVAR);

		if (RubyInstanceVariable.class.equals(object.getClass())) {
			RubyInstanceVariable var = (RubyInstanceVariable) object;
			if (var.getAccess().equals(RubyInstanceVariable.READ)) { return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYINSTVAR_READ); }
			if (var.getAccess().equals(RubyInstanceVariable.WRITE)) { return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYINSTVAR_WRITE); }
			if (var.getAccess().equals(RubyInstanceVariable.PUBLIC)) { return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYINSTVAR); }
			return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYINSTVAR_PRIV);
		}

		if (RubyMethod.class.equals(object.getClass())) {
			RubyMethod method = (RubyMethod) object;
			if (method.getAccess().equals(RubyMethod.PUBLIC)) { return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYMETHOD_PUB); }
			if (method.getAccess().equals(RubyMethod.PROTECTED)) { return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYMETHOD_PRO); }
			// assume it's private
			return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYMETHOD);
		}

		if (RubyClass.class.equals(object.getClass())) return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBYCLASS);

		log("Attempting to get Image for unknown object in outline elements: " + object);
		return RdtUiImages.get(RdtUiImages.IMG_OBJS_ERROR);
	}

	/**
	 * @param string
	 */
	protected void log(String string) {
		RubyPlugin.log(string);
	}

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public String getText(Object obj) {
		if (obj instanceof IRubyElement) return ((IRubyElement) obj).getName();
		return "Invalid object: " + obj.getClass().getName();
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#addListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener arg0) {}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#dispose()
	 */
	public void dispose() {}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#isLabelProperty(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.IBaseLabelProvider#removeListener(org.eclipse.jface.viewers.ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener arg0) {}

}

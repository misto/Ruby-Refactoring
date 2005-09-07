package org.rubypeople.rdt.ui;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyElementLabelProvider implements ILabelProvider {

	/**
	 * @see org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
	 */
	public Image getImage(Object object) {
		if (object == null) {
			log("Attempting to get Image for null object");
			return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_ERROR);
		}

		if (object instanceof IRubyElement) {
			IRubyElement rubyElement = (IRubyElement) object;
			if (rubyElement.isType(IRubyElement.IMPORT_CONTAINER)) return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_IMPORT_CONTAINER);
			if (rubyElement.isType(IRubyElement.GLOBAL)) return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_GLOBAL);
			if (rubyElement.isType(IRubyElement.CONSTANT)) return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_CONSTANT);
			if (rubyElement.isType(IRubyElement.CLASS_VAR)) return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_CLASS_VAR);
			if (rubyElement.isType(IRubyElement.LOCAL_VAR)) return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_LOCAL_VAR);
			if (rubyElement.isType(IRubyElement.IMPORT)) return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_IMPORT);
			if (rubyElement.isType(IRubyElement.SINGLETON_METHOD)) {
				IMethod method = (IMethod) rubyElement;				
				try {
					if (method.getVisibility() == IMethod.PUBLIC) { return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_SINGLETONMETHOD_PUB); }
					if (method.getVisibility() == IMethod.PROTECTED) { return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_SINGLETONMETHOD_PRO); }
				} catch (RubyModelException e) {
					RubyPlugin.log(e);
				}
				// assume it's private
				return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_SINGLETONMETHOD);
			}
			if (rubyElement.isType(IRubyElement.METHOD)) {
				IMethod method = (IMethod) rubyElement;
				try {
					if (method.getVisibility() == IMethod.PUBLIC) { return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBYMETHOD_PUB); }
					if (method.getVisibility() == IMethod.PROTECTED) { return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBYMETHOD_PRO); }
				} catch (RubyModelException e) {
					RubyPlugin.log(e);
				}
				// assume it's private
				return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_METHOD);
			}
			if (rubyElement.isType(IRubyElement.INSTANCE_VAR)) {
				// FIXME Transition to showing the methods that attr adds! Rather than saying these variables are read/write
				return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_INSTANCE_VAR);
			}
			if (rubyElement.isType(IRubyElement.TYPE)) {
			    IType rubyType = (IType) rubyElement;
			    if(rubyType.isClass()) return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_CLASS);
			    return RubyPluginImages.get(RubyPluginImages.IMG_CTOOLS_RUBY_MODULE);
			}
		}
		log("Attempting to get Image for unknown object: " + object);
		return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_ERROR);
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
		if (obj instanceof IRubyElement) return ((IRubyElement) obj).getElementName();
		if (obj instanceof SyntaxException) {
			SyntaxException syntaxException = (SyntaxException) obj ;
			StringBuffer sb = new StringBuffer() ;
			sb.append("Line ") ;
			sb.append(syntaxException.getPosition().getStartLine()) ;
			sb.append(": ") ;
			sb.append(syntaxException.getMessage()) ;
			return sb.toString() ;
		}
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

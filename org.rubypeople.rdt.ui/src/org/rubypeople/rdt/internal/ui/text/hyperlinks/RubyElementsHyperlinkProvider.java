package org.rubypeople.rdt.internal.ui.text.hyperlinks;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PartInitException;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.OpenActionUtil;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.text.hyperlinks.IHyperlinkProvider;

public class RubyElementsHyperlinkProvider implements IHyperlinkProvider {

	public RubyElementsHyperlinkProvider() {}

	class RubyElementsHyperlink implements IHyperlink {
		private IRegion fRegion;
		private final IRubyElement[] fElements;

		public RubyElementsHyperlink(IRegion region, IRubyElement[] elements) {
			fRegion = region;
			this.fElements = elements;
		}

		public IRegion getHyperlinkRegion() {
			return fRegion;
		}

		public String getHyperlinkText() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getTypeLabel() {
			// TODO Auto-generated method stub
			return null;
		}

		public void open() {
			try {
				// FIXME Check for first element which is an instanceof of
				// IMember, don't just try to access the first element!
				if (fElements != null && fElements.length > 0) {					
					OpenActionUtil.open(fElements[0], true);
				}
			} catch (PartInitException e) {
				RubyPlugin.log(e);
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
			}
		}
	}

	public IHyperlink getHyperlink(IEditorInput input, ITextViewer textViewer, Node node, IRegion region, boolean canShowMultipleHyperlinks) {
		IRegion newRegion = RubyWordFinder.findWord(textViewer.getDocument(), region.getOffset());
		try {
			IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
			IRubyScript script = manager.getWorkingCopy(input);			
			IRubyElement[] elements = SelectionConverter.codeResolve(script, newRegion.getOffset(), newRegion.getLength());
			if (elements == null || elements.length == 0) {
				return null;
			}
			return new RubyElementsHyperlink(newRegion, elements);
		} catch (Exception e) {
			RubyPlugin.log(e);
		}
		return null;
	}
}
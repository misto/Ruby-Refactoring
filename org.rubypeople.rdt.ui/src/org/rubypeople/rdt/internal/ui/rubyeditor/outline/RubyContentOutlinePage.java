package org.rubypeople.rdt.internal.ui.rubyeditor.outline;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyAbstractEditor;
import org.rubypeople.rdt.ui.RubyElementLabelProvider;
import org.rubypeople.rdt.ui.RubyElementSorter;

public class RubyContentOutlinePage extends ContentOutlinePage {

	protected IDocument document;
	private RubyAbstractEditor fTextEditor;
	private IDocumentModelListener fListener;
	private RubyCore fCore;
	private IRubyElement fInput;

	public RubyContentOutlinePage(IDocument document, RubyAbstractEditor textEditor) {
		this.document = document;
		this.fTextEditor = textEditor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new RubyOutlineContentProvider());
		tree.setLabelProvider(new RubyElementLabelProvider());

		if (fListener == null) {
			fListener = createRubyModelChangeListener();
		}
		fCore = RubyCore.getDefault();
		fCore.addDocumentModelListener(fListener);

		tree.setInput(fInput);
		tree.setSorter(new RubyElementSorter());
	}
	
	public void setInput(IRubyElement inputElement) {
		fInput= inputElement;
		if (getTreeViewer() != null)
			getTreeViewer().setInput(fInput);
	}

	private IDocumentModelListener createRubyModelChangeListener() {
		return new IDocumentModelListener() {

			public void documentModelChanged(final DocumentModelChangeEvent event) {
				// FIXME The outline view seems to be "behind" in terms of reconciling!
				if (event.getModel() == fTextEditor.getRubyModel() && !getControl().isDisposed()) {
					getControl().getDisplay().asyncExec(new Runnable() {

						public void run() {
							Control ctrl = getControl();
							if (ctrl != null && !ctrl.isDisposed()) {
								getTreeViewer().refresh();
							}
						}
					});
				}
			}
		};
	}
}
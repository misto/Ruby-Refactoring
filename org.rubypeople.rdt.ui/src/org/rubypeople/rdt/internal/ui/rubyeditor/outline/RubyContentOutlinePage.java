package org.rubypeople.rdt.internal.ui.rubyeditor.outline;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyAbstractEditor;

public class RubyContentOutlinePage extends ContentOutlinePage {

	protected IDocument document;
	private RubyAbstractEditor fTextEditor;
	private IDocumentModelListener fListener;
	private RubyCore fCore;

	public RubyContentOutlinePage(IDocument document, RubyAbstractEditor textEditor) {
		this.document = document;
		this.fTextEditor = textEditor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new RubyOutlineContentProvider());
		tree.setLabelProvider(new RubyOutlineLabelProvider());

		try {
			fTextEditor.getRubyModel().setScript(RubyParser.parse(document.get()));
		} catch (ParseException e) {
			RubyPlugin.log(e);
		}

		if (fListener == null) {
			fListener = createRubyModelChangeListener();
		}
		fCore = RubyCore.getDefault();
		fCore.addDocumentModelListener(fListener);

		tree.setInput(fTextEditor.getRubyModel());
		tree.setSorter(new RubyElementSorter());
	}

	private IDocumentModelListener createRubyModelChangeListener() {
		return new IDocumentModelListener() {

			public void documentModelChanged(final DocumentModelChangeEvent event) {
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
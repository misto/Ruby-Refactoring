package org.rubypeople.rdt.internal.ui.rubyeditor.outline;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.texteditor.ExtendedTextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyDocumentProvider;

public class RubyContentOutlinePage extends ContentOutlinePage {

	protected IDocument document;
	private ExtendedTextEditor fTextEditor;
	private IDocumentModelListener fListener;
	private RubyCore fCore;
	private RubyModel model;

	public RubyContentOutlinePage(IDocument document, ExtendedTextEditor textEditor) {
		this.document = document;
		this.fTextEditor = textEditor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new RubyOutlineContentProvider(fTextEditor));
		tree.setLabelProvider(new RubyOutlineLabelProvider());

		//if (script == null) {
			try {
				RubyDocumentProvider provider = (RubyDocumentProvider) this.fTextEditor.getDocumentProvider();
				model = provider.getRubyModel(fTextEditor.getEditorInput()) ;
				model.setScript(RubyParser.parse(document.get()));
			} catch (ParseException e) {
				RubyPlugin.log(e);
			}
		

		if (fListener == null) {
			fListener = createRubyModelChangeListener();
		}
		fCore = RubyCore.getDefault();
		fCore.addDocumentModelListener(fListener);

		tree.setInput(model);
		tree.setSorter(new RubyElementSorter());
	}

	private IDocumentModelListener createRubyModelChangeListener() {
		return new IDocumentModelListener() {

			public void documentModelChanged(final DocumentModelChangeEvent event) {
				if (event.getModel() == model && !getControl().isDisposed()) {
					getControl().getDisplay().asyncExec(new Runnable() {
						public void run() {
							Control ctrl = getControl();
							if (ctrl != null && !ctrl.isDisposed()) {
								//model.setScript(event.getModel());
								// TODO Just refresh, don't set input so that expansions are kept
								//getTreeViewer().setInput(script);
								getTreeViewer().refresh();
							}
						}
					});
				}
			}
		};
	}
}
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.texteditor.ExtendedTextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.ast.RubyScript;

public class RubyContentOutlinePage extends ContentOutlinePage {

	protected IDocument document;
	private ExtendedTextEditor fTextEditor;

	public RubyContentOutlinePage(IDocument document, ExtendedTextEditor textEditor) {
		this.document = document;
		this.fTextEditor = textEditor;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new RubyOutlineContentProvider(fTextEditor));
		tree.setLabelProvider(new RubyOutlineLabelProvider());
		
		RubyScript script = null;
		try {
			script = RubyParser.parse(document.get());
		} catch (ParseException e) {
			RubyPlugin.log(e);
		}
		tree.setInput(script);
		tree.setSorter(new RubyElementSorter());
	}
}

/*
 * Created on Jan 12, 2005
 */
package org.rubypeople.rdt.internal.ui.text.folding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyAbstractEditor;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider;

/**
 * @author cawilliams
 */
public class DefaultRubyFoldingStructureProvider implements IProjectionListener, IRubyFoldingStructureProvider {

	private ITextEditor fEditor;
	private ProjectionViewer fViewer;
	private IDocument fCachedDocument;
	private boolean fAllowCollapsing;
	private IRubyElement fInput;
	private boolean fCollapseInnerTypes;
	private boolean fCollapseRubydoc;
	private boolean fCollapseMethods;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider#install(org.eclipse.ui.texteditor.ITextEditor,
	 *      org.eclipse.jface.text.source.projection.ProjectionViewer)
	 */
	public void install(ITextEditor editor, ProjectionViewer viewer) {
		if (editor instanceof RubyAbstractEditor) {
			fEditor = editor;
			fViewer = viewer;
			fViewer.addProjectionListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider#uninstall()
	 */
	public void uninstall() {
		if (isInstalled()) {
			projectionDisabled();
			fViewer.removeProjectionListener(this);
			fViewer = null;
			fEditor = null;
		}
	}

	protected boolean isInstalled() {
		return fEditor != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider#initialize()
	 */
	public void initialize() {
		if (!isInstalled()) return;

		initializePreferences();
		try {
			IDocumentProvider provider = fEditor.getDocumentProvider();
			fCachedDocument = provider.getDocument(fEditor.getEditorInput());
			fAllowCollapsing = true;

			if (fEditor instanceof RubyEditor) {
				IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
				fInput = manager.getWorkingCopy(fEditor.getEditorInput());
			}

			if (fInput != null) {
				ProjectionAnnotationModel model = (ProjectionAnnotationModel) fEditor.getAdapter(ProjectionAnnotationModel.class);
				if (model != null) {
					if (fInput instanceof IRubyScript) {
						IRubyScript unit = (IRubyScript) fInput;
						synchronized (unit) {
							try {
								unit.reconcile();
							} catch (RubyModelException e) {}
						}
					}
					Map additions = computeAdditions((IParent) fInput);
					model.removeAllAnnotations();
					model.replaceAnnotations(null, additions);
				}
			}

		} finally {
			fCachedDocument = null;
			fAllowCollapsing = false;
		}
	}

	/**
	 * @param input
	 * @return
	 */
	private Map computeAdditions(IParent parent) {
		Map map = new HashMap();
		try {
			computeAdditions(parent.getChildren(), map);
		} catch (RubyModelException x) {
			RubyPlugin.log(x);
		}
		return map;
	}

	private void computeAdditions(IRubyElement[] elements, Map map) throws RubyModelException {
		for (int i = 0; i < elements.length; i++) {
			IRubyElement element = elements[i];
			computeAdditions(element, map);

			if (element instanceof IParent) {
				IParent parent = (IParent) element;
				computeAdditions(parent.getChildren(), map);
			}
		}
	}

	/**
	 * @param element
	 * @param map
	 */
	private void computeAdditions(IRubyElement element, Map map) {
		boolean createProjection = false;

		boolean collapse = false;
		switch (element.getElementType()) {
		case IRubyElement.TYPE:
			collapse = fAllowCollapsing && fCollapseInnerTypes && isInnerType((IType) element);
			createProjection = true;
			break;
		case IRubyElement.METHOD:
			collapse = fAllowCollapsing && fCollapseMethods;
			createProjection = true;
			break;
		}

		if (createProjection) {
			IRegion[] regions = computeProjectionRanges(element);
			if (regions != null) {
				// comments
				for (int i = 0; i < regions.length - 1; i++) {
					Position position = createProjectionPosition(regions[i]);
					if (position != null) map.put(new RubyProjectionAnnotation(element, fAllowCollapsing && fCollapseRubydoc, true), position);
				}
				// code
				Position position = createProjectionPosition(regions[regions.length - 1]);
				if (position != null) map.put(new RubyProjectionAnnotation(element, collapse, false), position);
			}
		}
	}

	private void initializePreferences() {
		IPreferenceStore store = RubyPlugin.getDefault().getPreferenceStore();
		fCollapseInnerTypes = store.getBoolean(PreferenceConstants.EDITOR_FOLDING_INNERTYPES);
		fCollapseRubydoc = store.getBoolean(PreferenceConstants.EDITOR_FOLDING_RDOC);
		fCollapseMethods = store.getBoolean(PreferenceConstants.EDITOR_FOLDING_METHODS);
	}

	private boolean isInnerType(IType type) {
		IRubyElement parent = type.getParent();
		if (parent != null) {
			int parentType = parent.getElementType();
			return (parentType != IRubyElement.SCRIPT);
		}
		return false;
	}

	private IRegion[] computeProjectionRanges(IRubyElement element) {
		try {
			if (element instanceof ISourceReference) {
				ISourceReference reference = (ISourceReference) element;
				ISourceRange range = reference.getSourceRange();
				// TODO Uncomment when getSource is set up right!
//				String contents = reference.getSource();
//				if (contents == null) return null;

				List regions = new ArrayList();
				int shift = range.getOffset();
				int start = shift;

				regions.add(new Region(start, range.getOffset() + range.getLength() - start));

				if (regions.size() > 0) {
					IRegion[] result = new IRegion[regions.size()];
					regions.toArray(result);
					return result;
				}
			}
		} catch (RubyModelException e) {}

		return null;
	}

	private Position createProjectionPosition(IRegion region) {
		if (fCachedDocument == null) return null;

		try {
			int start = fCachedDocument.getLineOfOffset(region.getOffset());
			int end = fCachedDocument.getLineOfOffset(region.getOffset() + region.getLength());
			if (start != end) {
				int offset = fCachedDocument.getLineOffset(start);
				int endOffset = fCachedDocument.getLineOffset(end + 1);
				return new Position(offset, endOffset - offset);
			}
		} catch (BadLocationException x) {}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionEnabled()
	 */
	public void projectionEnabled() {
		// http://home.ott.oti.com/teams/wswb/anon/out/vms/index.html
		// projectionEnabled messages are not always paired with
		// projectionDisabled
		// i.e. multiple enabled messages may be sent out.
		// we have to make sure that we disable first when getting an enable
		// message.
		projectionDisabled();

		if (fEditor instanceof RubyAbstractEditor) {
			initialize();
			// TODO Uncomment so we can react to changes!
			// fElementListener = new ElementChangedListener();
			// RubyPlugin.addElementChangedListener(fElementListener);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionDisabled()
	 */
	public void projectionDisabled() {
		fCachedDocument = null;
		// TODO Uncomment so we can react to changes!
		// if (fElementListener != null) {
		// RubyPlugin.removeElementChangedListener(fElementListener);
		// fElementListener = null;
		// }
	}

	private static class RubyProjectionAnnotation extends ProjectionAnnotation {

		private IRubyElement fRubyElement;
		private boolean fIsComment;

		public RubyProjectionAnnotation(IRubyElement element, boolean isCollapsed, boolean isComment) {
			super(isCollapsed);
			fRubyElement = element;
			fIsComment = isComment;
		}

		public IRubyElement getElement() {
			return fRubyElement;
		}

		public void setElement(IRubyElement element) {
			fRubyElement = element;
		}

		public boolean isComment() {
			return fIsComment;
		}

		public void setIsComment(boolean isComment) {
			fIsComment = isComment;
		}
	}
}

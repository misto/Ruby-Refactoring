package org.rubypeople.rdt.internal.ui.text;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditorPreferences;
import org.rubypeople.rdt.internal.ui.text.ruby.AbstractRubyScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCodeScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.SingleTokenRubyCodeScanner;

public class RubyTextTools {

	/**
	 * This tools' preference listener.
	 */
	private class PreferenceListener implements IPropertyChangeListener, Preferences.IPropertyChangeListener {

		public void propertyChange(PropertyChangeEvent event) {
			adaptToPreferenceChange(event);
		}

		public void propertyChange(Preferences.PropertyChangeEvent event) {
			adaptToPreferenceChange(new PropertyChangeEvent(event.getSource(), event.getProperty(), event.getOldValue(), event.getNewValue()));
		}
	}

	protected static String[] keywords;
	protected RubyColorManager fColorManager;
	protected RubyPartitionScanner partitionScanner;
	protected AbstractRubyScanner fCodeScanner;
	protected AbstractRubyScanner fMultilineCommentScanner, fSinglelineCommentScanner, stringScanner;
	private IPreferenceStore fPreferenceStore;
	private Preferences fCorePreferenceStore;
	/** The preference change listener */
	private PreferenceListener fPreferenceListener = new PreferenceListener();
	private SingleTokenRubyCodeScanner fRegexpScanner;
	private SingleTokenRubyCodeScanner fCommandScanner;

	/**
	 * Creates a new Ruby text tools collection.
	 * 
	 * @param store
	 *            the preference store to initialize the text tools. The text
	 *            tool instance installs a listener on the passed preference
	 *            store to adapt itself to changes in the preference store. In
	 *            general <code>PreferenceConstants.
	 *			getPreferenceStore()</code>
	 *            should be used to initialize the text tools.
	 * @param coreStore
	 *            optional preference store to initialize the text tools. The
	 *            text tool instance installs a listener on the passed
	 *            preference store to adapt itself to changes in the preference
	 *            store.
	 * @see org.rubypeople.rdt.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.1
	 */
	public RubyTextTools(IPreferenceStore store, Preferences coreStore) {
		this(store, coreStore, true);
	}

	/**
	 * Creates a new Ruby text tools collection.
	 * 
	 * @param store
	 *            the preference store to initialize the text tools. The text
	 *            tool instance installs a listener on the passed preference
	 *            store to adapt itself to changes in the preference store. In
	 *            general <code>PreferenceConstants.
	 *			getPreferenceStore()</code>
	 *            should be used to initialize the text tools.
	 * @param coreStore
	 *            optional preference store to initialize the text tools. The
	 *            text tool instance installs a listener on the passed
	 *            preference store to adapt itself to changes in the preference
	 *            store.
	 * @param autoDisposeOnDisplayDispose
	 *            if <code>true</code> the color manager automatically
	 *            disposes all managed colors when the current display gets
	 *            disposed and all calls to
	 *            {@link org.eclipse.jface.text.source.ISharedTextColors#dispose()}
	 *            are ignored.
	 * @see org.rubypeople.rdt.ui.PreferenceConstants#getPreferenceStore()
	 * @since 2.1
	 */
	public RubyTextTools(IPreferenceStore store, Preferences coreStore, boolean autoDisposeOnDisplayDispose) {
		super();

		fColorManager = new RubyColorManager(autoDisposeOnDisplayDispose);
		partitionScanner = new RubyPartitionScanner();

		fCodeScanner = new RubyCodeScanner(fColorManager, store);
		fMultilineCommentScanner = new RubyCommentScanner(fColorManager, store, coreStore, IRubyColorConstants.RUBY_MULTI_LINE_COMMENT);
		fSinglelineCommentScanner = new RubyCommentScanner(fColorManager, store, coreStore, IRubyColorConstants.RUBY_SINGLE_LINE_COMMENT);
		stringScanner = new SingleTokenRubyCodeScanner(fColorManager, store, IRubyColorConstants.RUBY_STRING);
		fRegexpScanner = new SingleTokenRubyCodeScanner(fColorManager, store, IRubyColorConstants.RUBY_REGEXP);
		fCommandScanner = new SingleTokenRubyCodeScanner(fColorManager, store, IRubyColorConstants.RUBY_COMMAND);		
		
		fPreferenceStore = store;
		fPreferenceStore.addPropertyChangeListener(fPreferenceListener);

		fCorePreferenceStore = coreStore;
		if (fCorePreferenceStore != null) fCorePreferenceStore.addPropertyChangeListener(fPreferenceListener);

		// fJavaDocScanner= new JavaDocScanner(fColorManager, store, coreStore);
		// fPartitionScanner= new FastJavaPartitionScanner();
	}

	/**
	 * Adapts the behavior of the contained components to the change encoded in
	 * the given event.
	 * 
	 * @param event
	 *            the event to which to adapt
	 * @since 2.0
	 * @deprecated As of 3.0, no replacement
	 */
	protected void adaptToPreferenceChange(PropertyChangeEvent event) {
		if (fCodeScanner.affectsBehavior(event)) fCodeScanner.adaptToPreferenceChange(event);
		if (fMultilineCommentScanner.affectsBehavior(event)) fMultilineCommentScanner.adaptToPreferenceChange(event);
		if (fSinglelineCommentScanner.affectsBehavior(event)) fSinglelineCommentScanner.adaptToPreferenceChange(event);
		if (stringScanner.affectsBehavior(event)) stringScanner.adaptToPreferenceChange(event);
		if (fRegexpScanner.affectsBehavior(event)) fRegexpScanner.adaptToPreferenceChange(event);
		if (fCommandScanner.affectsBehavior(event)) fCommandScanner.adaptToPreferenceChange(event);
		// if (fJavaDocScanner.affectsBehavior(event))
		// fJavaDocScanner.adaptToPreferenceChange(event);
	}

	public IDocumentPartitioner createDocumentPartitioner() {
		return new DefaultPartitioner(getPartitionScanner(), RubyPartitionScanner.LEGAL_CONTENT_TYPES);
	}

	protected IPartitionTokenScanner getPartitionScanner() {
		return partitionScanner;
	}

	public AbstractRubyScanner getCodeScanner() {
		return fCodeScanner;
	}

	protected ITokenScanner getMultilineCommentScanner() {
		return fMultilineCommentScanner;
	}

	protected ITokenScanner getSinglelineCommentScanner() {
		return fSinglelineCommentScanner;
	}

	protected ITokenScanner getStringScanner() {
		return stringScanner;
	}

	public IPreferenceStore getPreferenceStore() {
		return RubyPlugin.getDefault().getPreferenceStore();
	}

	public static String[] getKeyWords() {
		if (keywords == null) {
			String csvKeywords = RubyEditorPreferences.getString("keywords");

			List keywordList = new ArrayList();
			StringTokenizer tokenizer = new StringTokenizer(csvKeywords, ",");
			while (tokenizer.hasMoreTokens())
				keywordList.add(tokenizer.nextToken());

			keywords = new String[keywordList.size()];
			keywordList.toArray(keywords);
		}

		return keywords;
	}

	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return  fCodeScanner.affectsBehavior(event)
		|| fMultilineCommentScanner.affectsBehavior(event)
		|| fSinglelineCommentScanner.affectsBehavior(event)
		|| stringScanner.affectsBehavior(event)
	    || fRegexpScanner.affectsBehavior(event)
	    || fCommandScanner.affectsBehavior(event);
	}

	/**
	 * Sets up the Ruby document partitioner for the given document for the
	 * given partitioning.
	 * 
	 * @param document
	 *            the document to be set up
	 * @param partitioning
	 *            the document partitioning
	 * @since 3.0
	 */
	public void setupRubyDocumentPartitioner(IDocument document, String partitioning) {
		IDocumentPartitioner partitioner = createDocumentPartitioner();
		if (document instanceof IDocumentExtension3) {
			IDocumentExtension3 extension3 = (IDocumentExtension3) document;
			extension3.setDocumentPartitioner(partitioning, partitioner);
		} else {
			document.setDocumentPartitioner(partitioner);
		}
		partitioner.connect(document);
	}

	/**
	 * Disposes all the individual tools of this tools collection.
	 */
	public void dispose() {

		fCodeScanner = null;
		fMultilineCommentScanner = null;
		fSinglelineCommentScanner = null;
		stringScanner = null;
		fRegexpScanner = null;
		fCommandScanner = null;
		// fJavaDocScanner= null;
		partitionScanner = null;

		if (fColorManager != null) {
			fColorManager.dispose();
			fColorManager = null;
		}

		if (fPreferenceStore != null) {
			fPreferenceStore.removePropertyChangeListener(fPreferenceListener);
			fPreferenceStore = null;

			if (fCorePreferenceStore != null) {
				fCorePreferenceStore.removePropertyChangeListener(fPreferenceListener);
				fCorePreferenceStore = null;
			}

			fPreferenceListener = null;
		}
	}

	public ITokenScanner getRegexpScanner() {
		return fRegexpScanner;
	}

	public ITokenScanner getCommandScanner() {
		return fCommandScanner;
	}

}

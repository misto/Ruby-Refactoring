package org.rubypeople.rdt.ui;

import org.eclipse.jface.preference.IPreferenceStore;

public class PreferenceConstants {

    private PreferenceConstants() {}

    public static final String FORMAT_INDENTATION = "formatIndentation"; //$NON-NLS-1$
    public static final String FORMAT_USE_TAB = "formatUseTab"; //$NON-NLS-1$	
    public static final String CREATE_PARSER_ANNOTATIONS = "createParserAnnotations"; //$NON-NLS-1$
    // TODO Finish implementing this option!
    public static final String TEMPLATES_USE_CODEFORMATTER = "templatesUSeCodeFormatter";

    /**
     * A named preference that controls whether folding is enabled in the Ruby
     * editor.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     * 
     * @since 3.0
     */
    public static final String EDITOR_FOLDING_ENABLED = "editor_folding_enabled"; //$NON-NLS-1$

    /**
     * A named preference that stores the configured folding provider.
     * <p>
     * Value is of type <code>String</code>.
     * </p>
     * 
     * @since 3.0
     */
    public static final String EDITOR_FOLDING_PROVIDER = "editor_folding_provider"; //$NON-NLS-1$

    /**
     * A named preference that stores the value for Rdoc folding for the default
     * folding provider.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     * 
     * @since 3.0
     */
    public static final String EDITOR_FOLDING_RDOC = "editor_folding_default_rdoc"; //$NON-NLS-1$

    /**
     * A named preference that stores the value for inner type folding for the
     * default folding provider.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     * 
     * @since 3.0
     */
    public static final String EDITOR_FOLDING_INNERTYPES = "editor_folding_default_innertypes"; //$NON-NLS-1$

    /**
     * A named preference that stores the value for method folding for the
     * default folding provider.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     * 
     * @since 3.0
     */
    public static final String EDITOR_FOLDING_METHODS = "editor_folding_default_methods"; //$NON-NLS-1$

    /**
	 * Preference key suffix for bold text style preference keys.
	 * 
	 * @since 2.1
	 */
	public static final String EDITOR_BOLD_SUFFIX= "_bold"; //$NON-NLS-1$

	/**
	 * Preference key suffix for italic text style preference keys.
	 * 
	 * @since 3.0
	 */
	public static final String EDITOR_ITALIC_SUFFIX= "_italic"; //$NON-NLS-1$

    public static void initializeDefaultValues(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.FORMAT_INDENTATION, 2);
        store.setDefault(PreferenceConstants.FORMAT_USE_TAB, false);
        store.setDefault(PreferenceConstants.CREATE_PARSER_ANNOTATIONS, false);

        // folding
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_PROVIDER,
                "org.rubypeople.rdt.ui.text.defaultFoldingProvider"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_RDOC, false);
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_INNERTYPES, true);
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_METHODS, false);
    }

}

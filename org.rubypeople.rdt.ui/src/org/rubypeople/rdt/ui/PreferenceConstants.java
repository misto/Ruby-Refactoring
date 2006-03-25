package org.rubypeople.rdt.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.preference.IPreferenceStore;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.RubyRuntime;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.preferences.formatter.ProfileManager;

public class PreferenceConstants {

    private PreferenceConstants() {
    }

    public static final String RI_PATH = "riDirectoryPath";
    public static final String RDOC_PATH = "rdocDirectoryPath";

    public static final String FORMAT_INDENTATION = "formatIndentation"; //$NON-NLS-1$
    public static final String FORMAT_USE_TAB = "formatUseTab"; //$NON-NLS-1$	
    public static final String TEMPLATES_USE_CODEFORMATTER = "templatesUseCodeFormatter"; //$NON-NLS-1$	

    private final static String DEFAULT_RDOC_CMD = "rdoc"; //$NON-NLS-1$	
    private final static String DEFAULT_RI_CMD = "ri"; //$NON-NLS-1$	

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
     * A named preference that controls if temporary problems are evaluated and
     * shown in the UI.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     */
    public final static String EDITOR_EVALUTE_TEMPORARY_PROBLEMS = "handleTemporaryProblems"; //$NON-NLS-1$

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
    public static final String EDITOR_BOLD_SUFFIX = "_bold"; //$NON-NLS-1$

    /**
     * Preference key suffix for italic text style preference keys.
     * 
     * @since 3.0
     */
    public static final String EDITOR_ITALIC_SUFFIX = "_italic"; //$NON-NLS-1$

    /**
     * A named preference that controls if correction indicators are shown in
     * the UI.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     */
    public final static String EDITOR_CORRECTION_INDICATION = "RubyEditor.ShowTemporaryProblem"; //$NON-NLS-1$
    /**
     * A named preference that defines whether the hint to make hover sticky
     * should be shown.
     * 
     * @see RubyUI
     * @since 0.8.0
     */
    public static final String EDITOR_SHOW_TEXT_HOVER_AFFORDANCE = "PreferenceConstants.EDITOR_SHOW_TEXT_HOVER_AFFORDANCE"; //$NON-NLS-1$

    /**
     * A named preference that controls if segmented view (show selected element
     * only) is turned on or off.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     */
    public static final String EDITOR_SHOW_SEGMENTS = "org.rubypeople.rdt.ui.editor.showSegments"; //$NON-NLS-1$
    /**
     * A named preference that controls whether the outline view selection
     * should stay in sync with with the element at the current cursor position.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     * 
     * @since 0.8.0
     */
    public final static String EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE = "RubyEditor.SyncOutlineOnCursorMove"; //$NON-NLS-1$

    /**
     * A named preference that defines how member elements are ordered by the
     * Ruby views using the <code>RubyElementSorter</code>.
     * <p>
     * Value is of type <code>String</code>: A comma separated list of the
     * following entries. Each entry must be in the list, no duplication. List
     * order defines the sort order.
     * <ul>
     * <li><b>T</b>: Types</li>
     * <li><b>C</b>: Constructors</li>
     * <li><b>M</b>: Methods</li>
     * <li><b>F</b>: Fields</li>
     * <li><b>SM</b>: Static Methods</li>
     * <li><b>SF</b>: Static Fields</li>
     * </ul>
     * </p>
     * 
     * @since 0.8.0
     */
    public static final String APPEARANCE_MEMBER_SORT_ORDER = "outlinesortoption"; //$NON-NLS-1$

    /**
     * A named preference that defines how member elements are ordered by
     * visibility in the Ruby views using the <code>RubyElementSorter</code>.
     * <p>
     * Value is of type <code>String</code>: A comma separated list of the
     * following entries. Each entry must be in the list, no duplication. List
     * order defines the sort order.
     * <ul>
     * <li><b>B</b>: Public</li>
     * <li><b>V</b>: Private</li>
     * <li><b>R</b>: Protected</li>
     * </ul>
     * </p>
     * 
     * @since 0.8.0
     */
    public static final String APPEARANCE_VISIBILITY_SORT_ORDER = "org.eclipse.jdt.ui.visibility.order"; //$NON-NLS-1$

    /**
     * A named preferences that controls if Ruby elements are also sorted by
     * visibility.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     * 
     * @since 0.8.0
     */
    public static final String APPEARANCE_ENABLE_VISIBILITY_SORT_ORDER = "org.rubypeople.rdt.ui.enable.visibility.order"; //$NON-NLS-1$

    /**
     * A named preference that controls if package name compression is turned on
     * or off.
     * <p>
     * Value is of type <code>Boolean</code>.
     * </p>
     * 
     * @see #APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW
     */
    public static final String APPEARANCE_COMPRESS_PACKAGE_NAMES = "org.rubypeople.rdt.ui.compresspackagenames";//$NON-NLS-1$

    /**
     * A named preference that defines the pattern used for package name
     * compression.
     * <p>
     * Value is of type <code>String</code>. For example for the given
     * package name 'org.eclipse.jdt' pattern '.' will compress it to '..jdt',
     * '1~' to 'o~.e~.jdt'.
     * </p>
     */
    public static final String APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW = "PackagesView.pkgNamePatternForPackagesView";//$NON-NLS-1$
    /**
     * The symbolic font name for the Ruby editor text font 
     * (value <code>"org.rubypeople.rdt.ui.editors.textfont"</code>).
     * 
     * @since 0.8.0
     */
    public final static String EDITOR_TEXT_FONT= "org.rubypeople.rdt.ui.editors.textfont"; //$NON-NLS-1$
    /**
     * A named preference that controls which profile is used by the code formatter.
     * <p>
     * Value is of type <code>String</code>.
     * </p>
     *
     * @since 0.8.0
     */ 
    public static final String FORMATTER_PROFILE = "formatter_profile"; //$NON-NLS-1$

	/**
	 * A named preference that controls the layout of the Ruby Browsing views vertically. Boolean value.
	 * <p>
	 * Value is of type <code>Boolean</code>. If <code>true<code> the views are stacked vertical.
	 * If <code>false</code> they are stacked horizontal.
	 * </p>
	 */
	public static final String BROWSING_STACK_VERTICALLY= "org.rubypeople.rdt.ui.browsing.stackVertically"; //$NON-NLS-1$

    public static void initializeDefaultValues(IPreferenceStore store) {
        store.setDefault(PreferenceConstants.EDITOR_SHOW_SEGMENTS, false);
        
        // FIXME We can't enabling using code formatter yet, because it breaks on formatting templates (when inserting via content assist)
        // FIXME Uncomment when we have an AST based formatter which spits out TextEdits (rather than one huge replace)
        //        store.setDefault(PreferenceConstants.TEMPLATES_USE_CODEFORMATTER, true);
        
        store.setDefault(PreferenceConstants.FORMATTER_PROFILE, ProfileManager.DEFAULT_PROFILE);

        // MembersOrderPreferencePage
        // TODO Expose to users!
        store.setDefault(PreferenceConstants.APPEARANCE_MEMBER_SORT_ORDER, "T,SF,SM,F,C,M"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.APPEARANCE_VISIBILITY_SORT_ORDER, "B,V,R"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.APPEARANCE_ENABLE_VISIBILITY_SORT_ORDER, false);

        store.setDefault(PreferenceConstants.FORMAT_INDENTATION, 2);
        store.setDefault(PreferenceConstants.FORMAT_USE_TAB, false);

        // AppearancePreferencePage
        store.setDefault(PreferenceConstants.APPEARANCE_COMPRESS_PACKAGE_NAMES, false);
        store.setDefault(PreferenceConstants.APPEARANCE_PKG_NAME_PATTERN_FOR_PKG_VIEW, ""); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.BROWSING_STACK_VERTICALLY, false);

        // TODO Expose these preferences to the user!
        store.setDefault(PreferenceConstants.EDITOR_CORRECTION_INDICATION, true);
        store.setDefault(PreferenceConstants.EDITOR_EVALUTE_TEMPORARY_PROBLEMS, true);

        // folding
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_ENABLED, true);
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_PROVIDER,
                "org.rubypeople.rdt.ui.text.defaultFoldingProvider"); //$NON-NLS-1$
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_RDOC, false);
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_INNERTYPES, true);
        store.setDefault(PreferenceConstants.EDITOR_FOLDING_METHODS, false);

        store.setDefault(PreferenceConstants.EDITOR_SHOW_TEXT_HOVER_AFFORDANCE, true);

        store.setDefault(PreferenceConstants.RDOC_PATH, PreferenceConstants
                .getDefaultPath(PreferenceConstants.DEFAULT_RDOC_CMD));
        store.setDefault(PreferenceConstants.RI_PATH, PreferenceConstants
                .getDefaultPath(PreferenceConstants.DEFAULT_RI_CMD));

        store.setDefault(PreferenceConstants.EDITOR_SYNC_OUTLINE_ON_CURSOR_MOVE, true);

    }

    private static String getDefaultPath(String programName) {
        RubyInterpreter interpreter = RubyRuntime.getDefault().getSelectedInterpreter();
        if (interpreter == null) { return programName; }
        IPath path = interpreter.getInstallLocation();
        path = path.uptoSegment(path.segmentCount() - 1).append(programName);
        return path.toOSString();
    }

    /**
     * Returns the RDT-UI preference store.
     * 
     * @return the RDT-UI preference store
     */
    public static IPreferenceStore getPreferenceStore() {
        return RubyPlugin.getDefault().getPreferenceStore();
    }

}

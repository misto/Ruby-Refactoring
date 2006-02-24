package org.rubypeople.rdt.ui.actions;

import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public interface IRubyEditorActionDefinitionIds extends ITextEditorActionDefinitionIds {
	/**
	 * Value: org.rubypeople.rdt.ui.edit.text.ruby.comment
	 */
	public static final String COMMENT= "org.rubypeople.rdt.ui.edit.text.ruby.comment";

	/**
	 * Value: org.rubypeople.rdt.ui.edit.text.ruby.content.assist.proposals
	 */
	public static final String CONTENT_ASSIST_PROPOSALS= "org.rubypeople.rdt.ui.edit.text.ruby.content.assist.proposals";

	/**
	 * Value: org.rubypeople.rdt.ui.edit.text.ruby.uncomment
	 */
	public static final String UNCOMMENT = "org.rubypeople.rdt.ui.edit.text.ruby.uncomment";
	
	public static final String FORMAT = "org.rubypeople.rdt.ui.edit.text.ruby.format";

	/**
	 * Action definition ID of the source -> toggle comment action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.toggle.comment"</code>).
	 * @since 3.0
	 */
	public static final String TOGGLE_COMMENT= "org.rubypeople.rdt.ui.edit.text.ruby.toggle.comment"; //$NON-NLS-1$
		
	/**
	 * Action definition ID of the edit -> show RDoc action
	 * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.show.rdoc"</code>).
	 */
	public static final String SHOW_RDOC= "org.rubypeople.rdt.ui.edit.text.ruby.show.rdoc"; //$NON-NLS-1$

    public static final String SURROUND_WITH_BEGIN_RESCUE = "org.rubypeople.rdt.ui.edit.text.ruby.surround.with.begin.rescue"; //$NON-NLS-1$

    /**
     * Action definition ID of the edit -> go to matching bracket action
     * (value <code>"org.rubypeople.rdt.ui.edit.text.ruby.goto.matching.bracket"</code>).
     *
     * @since 0.8.0
     */
    public static final String GOTO_MATCHING_BRACKET= "org.rubypeople.rdt.ui.edit.text.ruby.goto.matching.bracket"; //$NON-NLS-1$


}

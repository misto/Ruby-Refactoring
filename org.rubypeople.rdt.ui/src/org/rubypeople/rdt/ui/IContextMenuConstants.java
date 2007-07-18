package org.rubypeople.rdt.ui;


public interface IContextMenuConstants {
	/**
	 * Type hierarchy view part: pop-up menu target ID for type hierarchy viewer
	 * (value <code>"org.rubypeople.rdt.ui.TypeHierarchy.typehierarchy"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String TARGET_ID_HIERARCHY_VIEW= RubyUI.ID_TYPE_HIERARCHY + ".typehierarchy"; //$NON-NLS-1$	

	/**
	 * Type hierarchy view part: pop-up menu target ID for supertype hierarchy viewer
	 * (value <code>"org.rubypeople.rdt.ui.TypeHierarchy.supertypes"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String TARGET_ID_SUPERTYPES_VIEW= RubyUI.ID_TYPE_HIERARCHY + ".supertypes"; //$NON-NLS-1$	

	/**
	 * Type hierarchy view part: Pop-up menu target ID for the subtype hierarchy viewer
	 * (value <code>"org.rubypeople.rdt.ui.TypeHierarchy.subtypes"</code>).
	 * 
	 * @since 2.0
	 */
	public static final String TARGET_ID_SUBTYPES_VIEW= RubyUI.ID_TYPE_HIERARCHY + ".subtypes"; //$NON-NLS-1$	
}

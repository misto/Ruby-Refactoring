package org.rubypeople.rdt.internal.ui.search;

import org.eclipse.osgi.util.NLS;

public class SearchMessages extends NLS {

	private static final String BUNDLE_NAME = SearchMessages.class.getName();

	public static String WorkspaceScope;
	public static String WorkspaceScopeNoJRE;
	public static String RubySearchScopeFactory_undefined_projects;
	public static String EnclosingProjectScope;
	public static String EnclosingProjectScopeNoJRE;
	public static String EnclosingProjectsScope2;
	public static String EnclosingProjectsScope2NoJRE;
	public static String EnclosingProjectsScope;
	public static String EnclosingProjectsScopeNoJRE;
	public static String ProjectScope;
	public static String ProjectScopeNoJRE;
	public static String HierarchyScope;
	public static String RubySearchScopeFactory_undefined_selection;
	public static String SingleSelectionScope;
	public static String SingleSelectionScopeNoJRE;
	public static String DoubleSelectionScope;
	public static String DoubleSelectionScopeNoJRE;
	public static String SelectionScope;
	public static String SelectionScopeNoJRE;
	public static String RubySearchScopeFactory_undefined_workingsets;
	public static String SingleWorkingSetScope;
	public static String SingleWorkingSetScopeNoJRE;
	public static String DoubleWorkingSetScope;
	public static String DoubleWorkingSetScopeNoJRE;
	public static String WorkingSetsScope;
	public static String WorkingSetsScopeNoJRE;

	public static String SearchPage_searchFor_type;
	public static String SearchPage_searchFor_method;
	public static String SearchPage_searchFor_package;
	public static String SearchPage_searchFor_constructor;
	public static String SearchPage_searchFor_field;
	public static String SearchPage_limitTo_declarations;
	public static String SearchPage_limitTo_implementors;
	public static String SearchPage_limitTo_references;
	public static String SearchPage_limitTo_allOccurrences;
	public static String SearchPage_limitTo_readReferences;
	public static String SearchPage_limitTo_writeReferences;
	public static String SearchPage_searchJRE_label;
	public static String SearchPage_searchFor_label;
	public static String SearchPage_limitTo_label;
	public static String SearchPage_expression_caseSensitive;
	public static String SearchPage_expression_label;
	
	public static String SearchUtil_workingSetConcatenation;

	public static String Search_FindReferencesAction_BinPrimConstWarnDialog_title;
	public static String Search_FindReferencesAction_BinPrimConstWarnDialog_message;

	public static String Search_Error_javaElementAccess_message;
	public static String Search_Error_javaElementAccess_title;

	public static String RubySearchQuery_error_participant_estimate;
	public static String RubySearchQuery_error_element_does_not_exist;
	public static String RubySearchQuery_error_unsupported_pattern;
	public static String RubySearchQuery_task_label;
	public static String RubySearchQuery_error_participant_search;
	public static String RubySearchQuery_status_ok_message;
	public static String RubySearchQuery_label;

	public static String RubySearchOperation_singularDeclarationsPostfix;
	public static String RubySearchOperation_singularReferencesPostfix;
	public static String RubySearchOperation_singularOccurrencesPostfix;
	public static String RubySearchOperation_singularReadReferencesPostfix;
	public static String RubySearchOperation_singularWriteReferencesPostfix;
	public static String RubySearchOperation_pluralDeclarationsPostfix;
	public static String RubySearchOperation_pluralReferencesPostfix;
	public static String RubySearchOperation_pluralOccurrencesPostfix;
	public static String RubySearchOperation_pluralReadReferencesPostfix;
	public static String RubySearchOperation_pluralWriteReferencesPostfix;

	public static String MatchFilter_PotentialFilter_name;
	public static String MatchFilter_PotentialFilter_actionLabel;
	public static String MatchFilter_PotentialFilter_description;
	public static String MatchFilter_ImportFilter_name;
	public static String MatchFilter_ImportFilter_actionLabel;
	public static String MatchFilter_ImportFilter_description;
	public static String MatchFilter_WriteFilter_name;
	public static String MatchFilter_WriteFilter_actionLabel;
	public static String MatchFilter_WriteFilter_description;
	public static String MatchFilter_ReadFilter_name;
	public static String MatchFilter_ReadFilter_actionLabel;
	public static String MatchFilter_ReadFilter_description;
	public static String MatchFilter_RubydocFilter_name;
	public static String MatchFilter_RubydocFilter_actionLabel;
	public static String MatchFilter_RubydocFilter_description;

	public static String SearchParticipant_error_noID;
	public static String SearchParticipant_error_noNature;
	public static String SearchParticipant_error_noClass;
	public static String SearchParticipant_error_classCast;

	public static String RubySearchResultPage_error_marker;
	public static String RubySearchResultPage_sortBylabel;
	public static String RubySearchResultPage_preferences_label;
	public static String RubySearchResultPage_filtered_message;
	public static String RubySearchResultPage_filteredWithCount_message;
	public static String RubySearchResultPage_open_editor_error_title;
	public static String RubySearchResultPage_open_editor_error_message;
	public static String RubySearchResultPage_sortByName;
	public static String RubySearchResultPage_sortByPath;
	public static String RubySearchResultPage_sortByParentName;
	public static String RubySearchResultPage_groupby_project;
	public static String RubySearchResultPage_groupby_project_tooltip;
	public static String RubySearchResultPage_groupby_package;
	public static String RubySearchResultPage_groupby_package_tooltip;
	public static String RubySearchResultPage_groupby_file;
	public static String RubySearchResultPage_groupby_file_tooltip;
	public static String RubySearchResultPage_groupby_type;
	public static String RubySearchResultPage_groupby_type_tooltip;

	public static String Search_Error_openEditor_title;
	public static String Search_Error_openEditor_message;

	public static String FiltersDialogAction_label;
	public static String FiltersDialog_limit_error;
	public static String FiltersDialog_limit_label;
	public static String FiltersDialog_description_label;
	public static String FiltersDialog_filters_label;
	public static String FiltersDialog_title;

	public static String SearchLabelProvider_potential_singular;
	public static String SearchLabelProvider_exact_singular;
	public static String SearchLabelProvider_potential_noCount;
	public static String SearchLabelProvider_exact_noCount;
	public static String SearchLabelProvider_exact_and_potential_plural;
	public static String SearchLabelProvider_potential_plural;
	public static String SearchLabelProvider_exact_plural;

	static {
		NLS.initializeMessages(BUNDLE_NAME, SearchMessages.class);
	}
}
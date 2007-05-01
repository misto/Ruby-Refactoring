package org.rubypeople.rdt.core.search;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.search.BasicSearchEngine;

public class SearchEngine {

	private BasicSearchEngine basicEngine;

	/**
	 * Creates a new search engine.
	 */
	public SearchEngine() {
		this.basicEngine = new BasicSearchEngine();
	}
	
	/**
	 * Creates a new search engine with the given working copy owner.
	 * The working copies owned by this owner will take precedence over 
	 * the primary compilation units in the subsequent search operations.
	 * 
	 * @param workingCopyOwner the owner of the working copies that take precedence over their original compilation units
	 * @since 1.0
	 */
	public SearchEngine(WorkingCopyOwner workingCopyOwner) {
		this.basicEngine = new BasicSearchEngine(workingCopyOwner);
	}

	public static IRubySearchScope createWorkspaceScope() {
		return BasicSearchEngine.createWorkspaceScope();
	}
	
	/**
	 * Searches for all top-level types and member types in the given scope.
	 * The search can be selecting specific types (given a package or a type name
	 * prefix and match modes). 
	 * 
	 * @param packageName the full name of the package of the searched types, or a prefix for this
	 *						package, or a wild-carded string for this package.
	 * @param typeName the dot-separated qualified name of the searched type (the qualification include
	 *					the enclosing types if the searched type is a member type), or a prefix
	 *					for this type, or a wild-carded string for this type.
	 * @param matchRule one of
	 * <ul>
	 *		<li>{@link SearchPattern#R_EXACT_MATCH} if the package name and type name are the full names
	 *			of the searched types.</li>
	 *		<li>{@link SearchPattern#R_PREFIX_MATCH} if the package name and type name are prefixes of the names
	 *			of the searched types.</li>
	 *		<li>{@link SearchPattern#R_PATTERN_MATCH} if the package name and type name contain wild-cards.</li>
	 *		<li>{@link SearchPattern#R_CAMELCASE_MATCH} if type name are camel case of the names of the searched types.</li>
	 * </ul>
	 * combined with {@link SearchPattern#R_CASE_SENSITIVE},
	 *   e.g. {@link SearchPattern#R_EXACT_MATCH} | {@link SearchPattern#R_CASE_SENSITIVE} if an exact and case sensitive match is requested, 
	 *   or {@link SearchPattern#R_PREFIX_MATCH} if a prefix non case sensitive match is requested.
	 * @param searchFor determines the nature of the searched elements
	 *	<ul>
	 * 	<li>{@link IJavaSearchConstants#CLASS}: only look for classes</li>
	 *		<li>{@link IJavaSearchConstants#INTERFACE}: only look for interfaces</li>
	 * 	<li>{@link IJavaSearchConstants#ENUM}: only look for enumeration</li>
	 *		<li>{@link IJavaSearchConstants#ANNOTATION_TYPE}: only look for annotation type</li>
	 * 	<li>{@link IJavaSearchConstants#CLASS_AND_ENUM}: only look for classes and enumerations</li>
	 *		<li>{@link IJavaSearchConstants#CLASS_AND_INTERFACE}: only look for classes and interfaces</li>
	 * 	<li>{@link IJavaSearchConstants#TYPE}: look for all types (ie. classes, interfaces, enum and annotation types)</li>
	 *	</ul>
	 * @param scope the scope to search in
	 * @param nameRequestor the requestor that collects the results of the search
	 * @param waitingPolicy one of
	 * <ul>
	 *		<li>{@link IJavaSearchConstants#FORCE_IMMEDIATE_SEARCH} if the search should start immediately</li>
	 *		<li>{@link IJavaSearchConstants#CANCEL_IF_NOT_READY_TO_SEARCH} if the search should be cancelled if the
	 *			underlying indexer has not finished indexing the workspace</li>
	 *		<li>{@link IJavaSearchConstants#WAIT_UNTIL_READY_TO_SEARCH} if the search should wait for the
	 *			underlying indexer to finish indexing the workspace</li>
	 * </ul>
	 * @param progressMonitor the progress monitor to report progress to, or <code>null</code> if no progress
	 *							monitor is provided
	 * @exception JavaModelException if the search failed. Reasons include:
	 *	<ul>
	 *		<li>the classpath is incorrectly set</li>
	 *	</ul>
	 * @since 1.0
	 */
	public void searchAllTypeNames(
		final char[] packageName, 
		final char[] typeName,
		final int matchRule, 
		int searchFor, 
		IRubySearchScope scope, 
		final TypeNameRequestor nameRequestor,
		int waitingPolicy,
		IProgressMonitor progressMonitor)  throws RubyModelException {
		
		this.basicEngine.searchAllTypeNames(packageName, typeName, matchRule, searchFor, scope, nameRequestor, waitingPolicy, progressMonitor);
	}

	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements) {
		return BasicSearchEngine.createRubySearchScope(elements);
	}

}

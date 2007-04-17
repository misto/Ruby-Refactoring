package org.rubypeople.rdt.internal.core.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.core.search.IRubySearchConstants;
import org.rubypeople.rdt.core.search.IRubySearchScope;
import org.rubypeople.rdt.core.search.SearchDocument;
import org.rubypeople.rdt.core.search.SearchMatch;
import org.rubypeople.rdt.core.search.SearchParticipant;
import org.rubypeople.rdt.core.search.SearchPattern;
import org.rubypeople.rdt.core.search.SearchRequestor;
import org.rubypeople.rdt.internal.core.DefaultWorkingCopyOwner;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.search.indexing.IndexManager;
import org.rubypeople.rdt.internal.core.search.matching.MatchLocator;
import org.rubypeople.rdt.internal.core.util.Messages;
import org.rubypeople.rdt.internal.core.util.Util;

public class BasicSearchEngine {
	
	public static final boolean VERBOSE = false;
	
	/*
	 * A list of working copies that take precedence over their original 
	 * compilation units.
	 */
	private IRubyScript[] workingCopies;
	
	/*
	 * A working copy owner whose working copies will take precedent over 
	 * their original compilation units.
	 */
	private WorkingCopyOwner workingCopyOwner;

	/**
	 * Searches for matches of a given search pattern. Search patterns can be created using helper
	 * methods (from a String pattern or a Ruby element) and encapsulate the description of what is
	 * being searched (for example, search method declarations in a case sensitive way).
	 *
	 * @see SearchEngine#search(SearchPattern, SearchParticipant[], IRubySearchScope, SearchRequestor, IProgressMonitor)
	 * 	for detailed comment
	 */
	public void search(SearchPattern pattern, SearchParticipant[] participants, IRubySearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		if (VERBOSE) {
			Util.verbose("BasicSearchEngine.search(SearchPattern, SearchParticipant[], IRubySearchScope, SearchRequestor, IProgressMonitor)"); //$NON-NLS-1$
		}
		findMatches(pattern, participants, scope, requestor, monitor);
	}
	
	/**
	 * @see SearchEngine#createRubySearchScope(IRubyElement[]) for detailed comment.
	 */
	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements) {
		return createRubySearchScope(elements, true);
	}

	/**
	 * @see SearchEngine#createRubySearchScope(IRubyElement[], boolean) for detailed comment.
	 */
	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements, boolean includeReferencedProjects) {
		int includeMask = IRubySearchScope.SOURCES | IRubySearchScope.APPLICATION_LIBRARIES | IRubySearchScope.SYSTEM_LIBRARIES;
		if (includeReferencedProjects) {
			includeMask |= IRubySearchScope.REFERENCED_PROJECTS;
		}
		return createRubySearchScope(elements, includeMask);
	}

	/**
	 * @see SearchEngine#createRubySearchScope(IRubyElement[], int) for detailed comment.
	 */
	public static IRubySearchScope createRubySearchScope(IRubyElement[] elements, int includeMask) {
		RubySearchScope scope = new RubySearchScope();
		HashSet visitedProjects = new HashSet(2);
		for (int i = 0, length = elements.length; i < length; i++) {
			IRubyElement element = elements[i];
			if (element != null) {
				try {
					if (element instanceof RubyProject) {
						scope.add((RubyProject)element, includeMask, visitedProjects);
					} else {
						scope.add(element);
					}
				} catch (RubyModelException e) {
					// ignore
				}
			}
		}
		return scope;
	}
	
	/**
	 * Searches for matches to a given query. Search queries can be created using helper
	 * methods (from a String pattern or a Ruby element) and encapsulate the description of what is
	 * being searched (for example, search method declarations in a case sensitive way).
	 *
	 * @param scope the search result has to be limited to the given scope
	 * @param requestor a callback object to which each match is reported
	 */
	void findMatches(SearchPattern pattern, SearchParticipant[] participants, IRubySearchScope scope, SearchRequestor requestor, IProgressMonitor monitor) throws CoreException {
		if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
		try {
			/* initialize progress monitor */
			if (monitor != null)
				monitor.beginTask(Messages.engine_searching, 100); 
			if (VERBOSE) {
				Util.verbose("Searching for pattern: " + pattern.toString()); //$NON-NLS-1$
				Util.verbose(scope.toString());
			}
			if (participants == null) {
				if (VERBOSE) Util.verbose("No participants => do nothing!"); //$NON-NLS-1$
				return;
			}
	
			IndexManager indexManager = RubyModelManager.getRubyModelManager().getIndexManager();
			requestor.beginReporting();
			for (int i = 0, l = participants.length; i < l; i++) {
				if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
	
				SearchParticipant participant = participants[i];
				SubProgressMonitor subMonitor= monitor==null ? null : new SubProgressMonitor(monitor, 1000);
				if (subMonitor != null) subMonitor.beginTask("", 1000); //$NON-NLS-1$
				try {
					if (subMonitor != null) subMonitor.subTask(Messages.bind(Messages.engine_searching_indexing, new String[] {participant.getDescription()})); 
					participant.beginSearching();
					requestor.enterParticipant(participant);
					PathCollector pathCollector = new PathCollector();
					indexManager.performConcurrentJob(
						new PatternSearchJob(pattern, participant, scope, pathCollector),
						IRubySearchConstants.WAIT_UNTIL_READY_TO_SEARCH,
						subMonitor);
					if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();
	
					// locate index matches if any (note that all search matches could have been issued during index querying)
					if (subMonitor != null) subMonitor.subTask(Messages.bind(Messages.engine_searching_matching, new String[] {participant.getDescription()})); 
					String[] indexMatchPaths = pathCollector.getPaths();
					if (indexMatchPaths != null) {
						pathCollector = null; // release
						int indexMatchLength = indexMatchPaths.length;
						SearchDocument[] indexMatches = new SearchDocument[indexMatchLength];
						for (int j = 0; j < indexMatchLength; j++) {
							indexMatches[j] = participant.getDocument(indexMatchPaths[j]);
						}
						SearchDocument[] matches = MatchLocator.addWorkingCopies(pattern, indexMatches, getWorkingCopies(), participant);
						participant.locateMatches(matches, pattern, scope, requestor, subMonitor);
					}
				} finally {		
					requestor.exitParticipant(participant);
					participant.doneSearching();
				}
			}
		} finally {
			requestor.endReporting();
			if (monitor != null)
				monitor.done();
		}
	}
	
	/*
	 * Returns the list of working copies used by this search engine.
	 * Returns null if none.
	 */
	private IRubyScript[] getWorkingCopies() {
		IRubyScript[] copies;
		if (this.workingCopies != null) {
			if (this.workingCopyOwner == null) {
				copies = RubyModelManager.getRubyModelManager().getWorkingCopies(DefaultWorkingCopyOwner.PRIMARY, false/*don't add primary WCs a second time*/);
				if (copies == null) {
					copies = this.workingCopies;
				} else {
					HashMap pathToCUs = new HashMap();
					for (int i = 0, length = copies.length; i < length; i++) {
						IRubyScript unit = copies[i];
						pathToCUs.put(unit.getPath(), unit);
					}
					for (int i = 0, length = this.workingCopies.length; i < length; i++) {
						IRubyScript unit = this.workingCopies[i];
						pathToCUs.put(unit.getPath(), unit);
					}
					int length = pathToCUs.size();
					copies = new IRubyScript[length];
					pathToCUs.values().toArray(copies);
				}
			} else {
				copies = this.workingCopies;
			}
		} else if (this.workingCopyOwner != null) {
			copies = RubyModelManager.getRubyModelManager().getWorkingCopies(this.workingCopyOwner, true/*add primary WCs*/);
		} else {
			copies = RubyModelManager.getRubyModelManager().getWorkingCopies(DefaultWorkingCopyOwner.PRIMARY, false/*don't add primary WCs a second time*/);
		}
		if (copies == null) return null;
		
		// filter out primary working copies that are saved
		IRubyScript[] result = null;
		int length = copies.length;
		int index = 0;
		for (int i = 0; i < length; i++) {
			RubyScript copy = (RubyScript)copies[i];
			try {
				if (!copy.isPrimary()
						|| copy.hasUnsavedChanges()
						|| copy.hasResourceChanged()) {
					if (result == null) {
						result = new IRubyScript[length];
					}
					result[index++] = copy;
				}
			}  catch (RubyModelException e) {
				// copy doesn't exist: ignore
			}
		}
		if (index != length && result != null) {
			System.arraycopy(result, 0, result = new IRubyScript[index], 0, index);
		}
		return result;
	}
	
	
	public static SearchParticipant getDefaultSearchParticipant() {
		return new RubySearchParticipant();
	}

	public static IRubySearchScope createWorkspaceScope() {
		return RubyModelManager.getRubyModelManager().getWorkspaceScope();
	}

	public static Collection<? extends IType> findType(String typeName) {
		SearchPattern pattern = SearchPattern.createPattern(IRubyElement.TYPE, typeName, IRubySearchConstants.DECLARATIONS, SearchPattern.R_EXACT_MATCH);
		SearchParticipant[] participants = new SearchParticipant[] {getDefaultSearchParticipant()};
		IRubySearchScope scope = createWorkspaceScope();
		TypeRequestor requestor = new TypeRequestor();
		try {
			new BasicSearchEngine().search(pattern, participants, scope, requestor, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return requestor.getTypes();
	}
	
	private static class TypeRequestor extends SearchRequestor {
		private List<IType> types = new ArrayList<IType>();
		@Override
		public void acceptSearchMatch(SearchMatch match) throws CoreException {
			Object element = match.getElement();
			types.add((IType) element);
		}
		public List<IType> getTypes() {
			return types;
		}
	}
}

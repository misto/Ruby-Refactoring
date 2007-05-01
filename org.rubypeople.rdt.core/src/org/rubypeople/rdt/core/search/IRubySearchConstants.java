package org.rubypeople.rdt.core.search;

import org.rubypeople.rdt.internal.core.search.processing.IJob;

public interface IRubySearchConstants {
	/**
	 * The search operation waits for the underlying indexer to finish indexing 
	 * the workspace before starting the search.
	 */
	int WAIT_UNTIL_READY_TO_SEARCH = IJob.WaitUntilReady;
	
	/**
	 * The search result is a declaration.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 */
	int DECLARATIONS= 0;
	
	/**
	 * The search result is a reference.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 * References can contain implementers since they are more generic kind
	 * of matches.
	 */
	int REFERENCES= 2;
	
	/**
	 * The search result is a declaration, a reference, or an implementer 
	 * of an interface.
	 * Can be used in conjunction with any of the nature of searched elements
	 * so as to better narrow down the search.
	 */
	int ALL_OCCURRENCES= 3;
	
	/**
	 * When searching for field matches, it will exclusively find read accesses, as
	 * opposed to write accesses. Note that some expressions are considered both
	 * as field read/write accesses: for example, x++; x+= 1;
	 * 
	 * @since 2.0
	 */
	int READ_ACCESSES = 4;
	
	/**
	 * When searching for field matches, it will exclusively find write accesses, as
	 * opposed to read accesses. Note that some expressions are considered both
	 * as field read/write accesses: for example,  x++; x+= 1;
	 * 
	 * @since 2.0
	 */
	int WRITE_ACCESSES = 5;
	
/* Nature of searched element */
	
	/**
	 * The searched element is a type, which may include classes and modules.
	 */
	int TYPE= 0;
	
	/**
	 * The searched element is a method.
	 */
	int METHOD= 1;

	/**
	 * The searched element is a constructor.
	 */
	int CONSTRUCTOR= 3;

	/**
	 * The searched element is a field.
	 */
	int FIELD= 4;

	/**
	 * The searched element is a class. 
	 * More selective than using {@link #TYPE}.
	 */
	int CLASS= 5;

	/**
	 * The searched element is a module.
	 * More selective than using {@link #TYPE}.
	 */
	int MODULE= 6;

}

/*
 * Created on Feb 1, 2005
 *
 */
package org.rubypeople.rdt.internal.ui.text;


/**
 * @author Chris
 *
 */
public interface IRubyPartitions {

	/**
	 * The name of the Ruby partitioning.
	 * @since 0.7.0
	 */
	public final static String RUBY_PARTITIONING= "___ruby_partitioning";  //$NON-NLS-1$
    
    /**
     * The identifier of the single-line end comment partition content type.
     */
    String RUBY_SINGLE_LINE_COMMENT= "__ruby_singleline_comment"; //$NON-NLS-1$

    /**
     * The identifier multi-line comment partition content type.
     */
    String RUBY_MULTI_LINE_COMMENT= "__ruby_multiline_comment"; //$NON-NLS-1$

    /**
     * The identifier of the Rdoc (JLS2: DocumentationComment) partition content type.
     */
    String RUBY_DOC= "__ruby_rdoc"; //$NON-NLS-1$

    /**
     * The identifier of the Ruby string partition content type.
     */
    String RUBY_STRING= "__ruby_string"; //$NON-NLS-1$

    /**
     * The identifier of the Ruby character partition content type.
     */
    String RUBY_CHARACTER= "__ruby_character";  //$NON-NLS-1$
	
}

/*
 * Created on Feb 20, 2005
 */
package org.rubypeople.rdt.internal.core.parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.jruby.lexer.yacc.ISourcePosition;

/**
 * @author Chris
 */
public class RdtWarnings implements IRdtWarnings     {

	private List warnings;

	public RdtWarnings() {
		warnings = new ArrayList();
	}

	public List getWarnings() {
		return Collections.unmodifiableList(warnings);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.common.RubyWarnings#warning(java.lang.String)
	 */
	public void warning(String message) {
		warn(new RdtPosition(1, 0, 0), message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.common.RubyWarnings#warning(org.jruby.lexer.yacc.SourcePosition,
	 *      java.lang.String)
	 */
	public void warning(ISourcePosition position, String message) {
		warn(position, message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.common.RubyWarnings#warn(org.jruby.lexer.yacc.SourcePosition,
	 *      java.lang.String)
	 */
	public void warn(ISourcePosition position, String message) {
		if (message.startsWith("Useless")) {
			return ;
		}
		warnings.add(new Warning(position, message));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.common.RubyWarnings#warn(java.lang.String)
	 */
	public void warn(String message) {
		warn(new RdtPosition(1, 0, 0), message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.common.RubyWarnings#isVerbose()
	 */
	public boolean isVerbose() {
		return true;
	}

	public void clear() {
		warnings.clear();		
	}

    public void setFile(IFile file) {
    }

}

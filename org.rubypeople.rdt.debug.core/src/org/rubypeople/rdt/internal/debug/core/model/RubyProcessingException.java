
package org.rubypeople.rdt.internal.debug.core.model;


public class RubyProcessingException extends Exception {
	private String rubyExceptionType ;
	public RubyProcessingException(String type, String message) {
		super(message) ;	
		this.rubyExceptionType = type ;
	}
	

    public String getRubyExceptionType() {
        return rubyExceptionType;
    }

}

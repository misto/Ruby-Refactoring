/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2005 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT is
 * subject to the "Common Public License (CPL) v 1.0". You may not use RDT except in 
 * compliance with the License. For further information see org.rubypeople.rdt/rdt.license.
 */

package org.rubypeople.rdt.internal.debug.ui.evaluation;



public class EvaluationExpression {
    private final String VARIABLE_TOKEN = "%s" ;
    private String name ;
    private String description;
    private String expression;
    
    public EvaluationExpression(String name, String description, String expression) {
    	this.name = name ;
        this.description = description ;
        this.expression = expression ;
    }
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
    
    public String substitute(String value) {        
        return this.expression.replaceAll(VARIABLE_TOKEN, value) ; 
    }
    
    public boolean hasVariable() {
    	return this.expression.indexOf(VARIABLE_TOKEN) != -1 ;
    }       
    
	public Object clone() throws CloneNotSupportedException {
		return new EvaluationExpression(this.getName(), this.getDescription(), this.getExpression()) ;
	}
    
}

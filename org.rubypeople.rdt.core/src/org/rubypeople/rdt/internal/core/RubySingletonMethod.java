/*
 * Created on Jul 10, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rubypeople.rdt.internal.core;

/**
 * RubySingletonMethod represents a singleton method in ruby. Example:
 * [code]
 *     class A
 *        def self.method1
 *        end
 *   
 *        def A.method2
 *        end
 *       
 *        class << self
 *           def method3
 *           end
 *        end
 *     end
 * [/code]
 * 
 * @author zdennis
 */
public class RubySingletonMethod extends RubyMethod {

	/**
	 * @param parent
	 * @param name
	 * @param parameterNames 
	 */
	public RubySingletonMethod(RubyElement parent, String name, String[] parameterNames) {
		super(parent, name, parameterNames);
	}
	
    public boolean isSingleton() {
        return true;
    }
    
    public String getElementName() {
    	 return parent.getElementName() + "." + this.name;
    }

}

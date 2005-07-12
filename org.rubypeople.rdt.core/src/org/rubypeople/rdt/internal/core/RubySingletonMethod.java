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
	 */
	public RubySingletonMethod(RubyElement parent, String name) {
		super(parent, name);
		// TODO Auto-generated constructor stub
	}
	
    /*
     *  (non-Javadoc)
     * @see org.rubypeople.rdt.core.IRubyElement#getElementType()
     */
	public int getElementType(){
		return RubyElement.SINGLETON_METHOD;
	}

}

package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RubyValue implements IValue {

	private String valueString ;
	private String referenceTypeName ;
	private boolean hasChildren ;
	private RubyVariable owner ;
	private RubyVariable[] variables ;
	
	public RubyValue(RubyVariable owner) {
		this(owner, "nil", null, false) ;
	}	
	
	public RubyValue(RubyVariable owner, String valueString, String type, boolean hasChildren) {
		this.valueString = valueString ;	
		this.owner = owner ;
		this.hasChildren = hasChildren ;
		this.referenceTypeName = type ;
	}
	

	/**
	 * @see org.eclipse.debug.core.model.IValue#getReferenceTypeName()
	 */
	public String getReferenceTypeName()  {
		return this.referenceTypeName;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#getValueString()
	 */
	public String getValueString() {
		return valueString;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#isAllocated()
	 */
	public boolean isAllocated() throws DebugException {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#getVariables()
	 */
	public IVariable[] getVariables() throws DebugException {
		if (!hasChildren) {
			return new RubyVariable[0] ;	
		}
		if (variables == null) {
			variables = ((RubyDebugTarget) this.getDebugTarget()).getRubyDebuggerProxy().readInstanceVariables(owner)	;
		}
		return variables;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValue#hasVariables()
	 */
	public boolean hasVariables() throws DebugException {
		return hasChildren;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return owner.getModelIdentifier();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return owner.getDebugTarget();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return this.getDebugTarget().getLaunch();
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public String toString() {
		if (this.getReferenceTypeName() == null) {			
			return this.getValueString() ;				
		}	
		return this.getValueString() ;
	}

}

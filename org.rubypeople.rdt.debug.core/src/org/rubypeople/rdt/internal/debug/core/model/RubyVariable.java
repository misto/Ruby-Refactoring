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
public class RubyVariable implements IVariable {


	private RubyStackFrame stackFrame ;
	private String name ;
	private RubyValue value ;
	private RubyVariable parent ;



	public RubyVariable(RubyStackFrame stackFrame, String name) {
		this.stackFrame = stackFrame ;
		this.name = name ;
		this.value = new RubyValue(this) ;
	}
	
	public RubyVariable(RubyStackFrame stackFrame, String name, RubyValue value) {
		this.stackFrame = stackFrame ;	
		this.value = value ;
		this.name = name ;
	}
	
	public RubyVariable(RubyStackFrame stackFrame, String name, String value, String type, boolean hasChildren) {
		this.stackFrame = stackFrame ;
		this.name = name ;
		this.value = new RubyValue(this, value, type, hasChildren) ;
	}
	
	/**
	 * @see org.eclipse.debug.core.model.IVariable#getValue()
	 */
	public IValue getValue()  {
		return value;
	}

	/**
	 * @see org.eclipse.debug.core.model.IVariable#getName()
	 */
	public String getName() {
		return name;
	}

	/**
	 * @see org.eclipse.debug.core.model.IVariable#getReferenceTypeName()
	 */
	public String getReferenceTypeName() throws DebugException {
		return "RefTypeName";
	}

	/**
	 * @see org.eclipse.debug.core.model.IVariable#hasValueChanged()
	 */
	public boolean hasValueChanged() throws DebugException {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
	 */
	public String getModelIdentifier() {
		return this.getDebugTarget().getModelIdentifier();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
	 */
	public IDebugTarget getDebugTarget() {
		return stackFrame.getDebugTarget();
	}

	/**
	 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
	 */
	public ILaunch getLaunch() {
		return this.getDebugTarget().getLaunch();
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(String)
	 */
	public void setValue(String expression) throws DebugException {
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#setValue(IValue)
	 */
	public void setValue(IValue value) throws DebugException {
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#supportsValueModification()
	 */
	public boolean supportsValueModification() {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(String)
	 */
	public boolean verifyValue(String expression) throws DebugException {
		return false;
	}

	/**
	 * @see org.eclipse.debug.core.model.IValueModification#verifyValue(IValue)
	 */
	public boolean verifyValue(IValue value) throws DebugException {
		return false;
	}

	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}
	
	public String toString() {
		return this.getName() + " = " + ((RubyValue) this.getValue()) ;	
	}

	/**
	 * Returns the stackFrame.
	 * @return RubyStackFrame
	 */
	public RubyStackFrame getStackFrame() {
		return stackFrame;
	}

	/**
	 * Returns the parent.
	 * @return RubyVariable
	 */
	public RubyVariable getParent() {
		return parent;
	}

	/**
	 * Sets the parent.
	 * @param parent The parent to set
	 */
	public void setParent(RubyVariable parent) {
		this.parent = parent;
	}
	
	public String getQualifiedName() {
		if (parent != null)	{
			return parent.getQualifiedName() + "." + this.getName() ;	
		}
		else {
			return this.getName() ;	
		}		
	}

}

package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IVariable;


public class RubyVariable implements IVariable {

	private boolean isStatic ;
	private boolean isLocal ;
	private boolean isInstance ;
	private boolean isConstant ;
	private RubyStackFrame stackFrame ;
	private String name ;
	private RubyValue value ;
	private RubyVariable parent ;

	public RubyVariable(RubyStackFrame stackFrame, String name, String scope) {
		this.initialize(stackFrame, name, scope, new RubyValue(this)) ;
	}
	
	public RubyVariable(RubyStackFrame stackFrame, String name, String scope, RubyValue value) {
		this.initialize(stackFrame, name, scope, value) ;
	}
	
	public RubyVariable(RubyStackFrame stackFrame, String name, String scope, String value, String type, boolean hasChildren) {
		this.initialize(stackFrame, name, scope,  new RubyValue(this, value, type, hasChildren)) ;
	}

	protected final void initialize(RubyStackFrame stackFrame, String name, String scope, RubyValue value) {
		this.stackFrame = stackFrame ;	
		this.value = value ;
		this.name = name ;

		this.isStatic = scope.equals("class") ;
		this.isLocal = scope.equals("local") ;
		this.isInstance = scope.equals("instance") ;
		this.isConstant = scope.equals("constant") ;
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

	public RubyStackFrame getStackFrame() {
		return stackFrame;
	}

	public RubyVariable getParent() {
		return parent;
	}

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

	public boolean isInstance() {
		return isInstance;
	}

	public boolean isLocal() {
		return isLocal;
	}

	public boolean isStatic() {
		return isStatic;
	}

	public boolean isConstant() {
		return isConstant;
	}


}

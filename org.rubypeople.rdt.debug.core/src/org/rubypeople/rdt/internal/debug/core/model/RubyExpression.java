package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IExpression;
import org.eclipse.debug.core.model.IValue;

public class RubyExpression implements IExpression {

  private RubyVariable inspectionResult;
  private String expression;
  public RubyExpression(String expression, RubyVariable inspectionResult) {
    this.inspectionResult = inspectionResult;
    this.expression = expression;

  }

  public String getExpressionText() {
    return expression;
  }

  public IValue getValue() {
    return inspectionResult.getValue();
  }

  public IDebugTarget getDebugTarget() {    
    return inspectionResult.getDebugTarget();
  }

  public void dispose() {

  }

  public String getModelIdentifier() {
    return this.getDebugTarget().getModelIdentifier();
  }

  public ILaunch getLaunch() {
    return this.getDebugTarget().getLaunch();
  }

  public Object getAdapter(Class arg0) {
    return null;
  }

}

package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyValue;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class VariableReader extends XmlStreamReader {

	private RubyStackFrame stackFrame;
	private RubyVariable parent ;
	private ArrayList variables ;

	public VariableReader(XmlPullParser xpp) {
		super(xpp);
	}

	public VariableReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}

	public RubyVariable[] readVariables(RubyVariable variable) {
		return readVariables(variable.getStackFrame(), variable) ;
	}

	public RubyVariable[] readVariables(RubyStackFrame stackFrame) {
		return readVariables(stackFrame, null) ;

	}
		
	public RubyVariable[] readVariables(RubyStackFrame stackFrame, RubyVariable parent) {
		this.stackFrame = stackFrame ;
		this.parent = parent ;
		this.variables = new ArrayList() ;
		try {			
			this.read();
		} catch (Exception ex) {
			RdtDebugCorePlugin.log(ex) ;
			return new RubyVariable[0] ;
		}
		RubyVariable[] variablesArray = new RubyVariable[variables.size()];
		variables.toArray(variablesArray);
		return variablesArray ;		
	}


	protected boolean processStartElement(XmlPullParser xpp) {
		String name = xpp.getName();
		if (name.equals("variables")) {
			return true ;
		}
		if (name.equals("variable")) {
			String varName = xpp.getAttributeValue("", "name");
			String varValue = xpp.getAttributeValue("", "value");
			RubyVariable newVariable ;
			if (varValue == null) {
				newVariable = new RubyVariable(stackFrame, varName);
			}
			else {
			String typeName = xpp.getAttributeValue("", "type") ;
			boolean hasChildren = xpp.getAttributeValue("", "hasChildren").equals("true") ;
				newVariable = new RubyVariable(stackFrame, varName, varValue, typeName, hasChildren);			
			}
			newVariable.setParent(parent) ;
			variables.add(newVariable) ;						
			return true ;
		}
		return false ;
	}


}

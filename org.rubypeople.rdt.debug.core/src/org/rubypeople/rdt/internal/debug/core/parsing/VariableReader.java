package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyValue;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class VariableReader extends XmlStreamReader {

	private RubyStackFrame stackFrame;
	private RubyVariable parent ;
	private ArrayList variables ;

	public RubyVariable[] readVariables(RubyVariable variable, XmlPullParser xpp) {
		return readVariables(variable.getStackFrame(), variable, xpp) ;
	}

	public RubyVariable[] readVariables(RubyStackFrame stackFrame, XmlPullParser xpp) {
		return readVariables(stackFrame, null, xpp) ;

	}
		
	public RubyVariable[] readVariables(RubyStackFrame stackFrame, RubyVariable parent, XmlPullParser xpp) {
		this.stackFrame = stackFrame ;
		this.parent = parent ;
		this.variables = new ArrayList() ;
		try {
			
			this.readElement(xpp);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		RubyVariable[] variablesArray = new RubyVariable[variables.size()];
		variables.toArray(variablesArray);
		return variablesArray ;		
	}


	protected void processStartElement(XmlPullParser xpp) {
		String name = xpp.getName();
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
		}
	}


}

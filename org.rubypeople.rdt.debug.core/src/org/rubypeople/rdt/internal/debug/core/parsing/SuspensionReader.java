package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;

import org.rubypeople.rdt.internal.debug.core.BreakpointSuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.ExceptionSuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.StepSuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
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
public class SuspensionReader extends XmlStreamReader {
	private SuspensionPoint suspensionPoint;

	public SuspensionPoint readSuspension(XmlPullParser xpp) throws XmlPullParserException, IOException, XmlStreamReaderException  {
		this.readElement(xpp);
		return suspensionPoint;
	}


	protected void processStartElement(XmlPullParser xpp) throws XmlStreamReaderException {
		String name = xpp.getName();
		if (name.equals("breakpoint")) {
			suspensionPoint = new BreakpointSuspensionPoint();
		}
		else if (name.equals("exception")) {
			ExceptionSuspensionPoint exceptionPoint = new ExceptionSuspensionPoint() ;
			exceptionPoint.setExceptionMessage(xpp.getAttributeValue("", "message")) ;
			exceptionPoint.setExceptionType(xpp.getAttributeValue("", "type")) ;
			suspensionPoint = exceptionPoint ;
		}
		else if (name.equals("suspended")) {
			StepSuspensionPoint stepPoint = new StepSuspensionPoint() ;
			stepPoint.setFramesNumber(Integer.parseInt(xpp.getAttributeValue("", "frames"))) ;
			suspensionPoint = stepPoint ;	
		}
		else {
			throw new XmlStreamReaderException("Error while reading suspension point. Unexpected element: " + name) ;	
		}
		suspensionPoint.setLine(Integer.parseInt(xpp.getAttributeValue("", "line")));
		suspensionPoint.setFile(xpp.getAttributeValue("", "file"));
	}


}

package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;

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
public class BreakpointReader extends XmlStreamReader {
	private SuspensionPoint breakpointHit;

	public SuspensionPoint readBreakpointHit(XmlPullParser xpp) {
		try {
			this.readElement(xpp);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return breakpointHit;
	}


	protected void processStartElement(XmlPullParser xpp) {
		String name = xpp.getName();
		if (name.equals("breakpoint")) {
			int line = Integer.parseInt(xpp.getAttributeValue("", "line"));
			String file = xpp.getAttributeValue("", "file");
			breakpointHit = new SuspensionPoint(file, line);
		}
	}


}

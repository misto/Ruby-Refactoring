package org.rubypeople.rdt.internal.debug.core.parsing;

import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
import org.xmlpull.v1.XmlPullParser;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StepEndReader extends XmlStreamReader {
	private SuspensionPoint suspensionPoint;
	public SuspensionPoint readEndOfStep(XmlPullParser xpp) {
		try {
			this.readElement(xpp);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return suspensionPoint;

	}

	protected void processStartElement(XmlPullParser xpp) {
		String name = xpp.getName();
		if (name.equals("suspended")) {
			suspensionPoint = new SuspensionPoint();
			suspensionPoint.setLine(Integer.parseInt(xpp.getAttributeValue("", "line")));
			suspensionPoint.setFile(xpp.getAttributeValue("", "file"));
			suspensionPoint.setFramesNumber(Integer.parseInt(xpp.getAttributeValue("", "frames")));
		}
		if (name.equals("breakpoint")) {
			suspensionPoint = new SuspensionPoint();
			suspensionPoint.setLine(Integer.parseInt(xpp.getAttributeValue("", "line")));
			suspensionPoint.setFile(xpp.getAttributeValue("", "file"));
		}

	}

}

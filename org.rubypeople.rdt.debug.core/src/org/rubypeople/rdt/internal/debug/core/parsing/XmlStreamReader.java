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
public abstract class XmlStreamReader {
	private SuspensionPoint breakpointHit;

	public void readElement(XmlPullParser xpp) throws XmlPullParserException, IOException {
		int eventType = xpp.getEventType();
		do {
			if (eventType == xpp.START_DOCUMENT) {
				System.out.println("Start document");
			} else if (eventType == xpp.END_DOCUMENT) {
				System.out.println("End document");
			} else if (eventType == xpp.START_TAG) {
				processStartElement(xpp);
			} else if (eventType == xpp.END_TAG) {
				processEndElement(xpp);
			} else if (eventType == xpp.TEXT) {
				//processText(xpp);
			}
			eventType = xpp.next();
		} while (eventType != xpp.END_DOCUMENT);
	}

	protected abstract void processStartElement(XmlPullParser xpp) ;
	protected void processEndElement(XmlPullParser xpp) {
		String name = xpp.getName();
		String uri = xpp.getNamespace();
		if ("".equals(uri))
			System.out.println("End element: " + name);
		else
			System.out.println("End element:   {" + uri + "}" + name);
	}

}

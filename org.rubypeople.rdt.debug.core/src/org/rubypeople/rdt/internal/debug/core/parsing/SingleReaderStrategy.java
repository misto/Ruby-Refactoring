package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class SingleReaderStrategy extends AbstractReadStrategy {

	public SingleReaderStrategy(XmlPullParser xpp) {
		super(xpp);
	}

	public void readElement(XmlStreamReader streamReader) throws XmlPullParserException, IOException, XmlStreamReaderException  {
	
		int eventType = xpp.getEventType();
		do {
			if (eventType == xpp.START_DOCUMENT) {
				System.out.println("Start document");
			} else if (eventType == xpp.END_DOCUMENT) {
				System.out.println("End document");
				break ;
			} else if (eventType == xpp.START_TAG) {
				streamReader.processStartElement(xpp);
			} else if (eventType == xpp.END_TAG) {
				streamReader.processEndElement(xpp);
				if (xpp.getDepth() == 1) {
					break ;	
				}
			} else if (eventType == xpp.TEXT) {
				//processText(xpp);
			}
			eventType = xpp.next();
		} while (true);
	}

}

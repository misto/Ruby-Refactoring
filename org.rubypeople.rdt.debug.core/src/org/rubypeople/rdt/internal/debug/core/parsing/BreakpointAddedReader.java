package org.rubypeople.rdt.internal.debug.core.parsing;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.xmlpull.v1.XmlPullParser;

public class BreakpointAddedReader extends XmlStreamReader {

	private String no;

	public BreakpointAddedReader(XmlPullParser xpp) {
		super(xpp);
	}

	public BreakpointAddedReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}

	@Override
	protected boolean processStartElement(XmlPullParser xpp) throws XmlStreamReaderException {
		boolean result = false;
		if (xpp.getName().equals("breakpointAdded")) {
			no = xpp.getAttributeValue("", "no");
			result = true;
		}
		return result;
	}

	public int readBreakpointNo() throws NumberFormatException {

		try {
			this.read();
		} catch (Exception ex) {
			RdtDebugCorePlugin.log(ex);
			return -1;
		}
		return Integer.parseInt(no) ;
	}

	@Override
	public void processContent(String text) {}

	@Override
	protected boolean processEndElement(XmlPullParser xpp) {
		return xpp.getName().equals("breakpointAdded") ;
	}

}

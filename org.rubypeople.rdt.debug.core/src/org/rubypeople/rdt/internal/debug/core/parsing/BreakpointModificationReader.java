package org.rubypeople.rdt.internal.debug.core.parsing;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.xmlpull.v1.XmlPullParser;

public class BreakpointModificationReader extends XmlStreamReader {

	private String no;

	public BreakpointModificationReader(XmlPullParser xpp) {
		super(xpp);
	}

	public BreakpointModificationReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}

	public int readBreakpointNo() throws NumberFormatException {
		try {
			this.read();
		} catch (Exception ex) {
			RdtDebugCorePlugin.log(ex);
			return -1;
		}
		return Integer.parseInt(no);
	}

	@Override
	protected boolean processStartElement(XmlPullParser xpp) throws XmlStreamReaderException {
		if (xpp.getName().equals("breakpointAdded")) {
			no = xpp.getAttributeValue("", "no");
			return true;
		}
		return false;
	}
	
	@Override
	public void processContent(String text) {}

	@Override
	protected boolean processEndElement(XmlPullParser xpp) {
		return xpp.getName().equals("breakpointAdded");
	}

}

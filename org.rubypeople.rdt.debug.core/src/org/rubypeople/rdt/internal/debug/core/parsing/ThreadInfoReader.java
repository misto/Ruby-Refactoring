package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;
import java.util.ArrayList;

import org.rubypeople.rdt.internal.debug.core.model.ThreadInfo;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class ThreadInfoReader extends XmlStreamReader {

	private ArrayList threads = new ArrayList();

	public ThreadInfoReader(XmlPullParser xpp) {
		super(xpp);
	}

	public ThreadInfoReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}

	public ThreadInfo[] readThreads() throws XmlPullParserException, IOException, XmlStreamReaderException {
		this.read();
		return (ThreadInfo[]) threads.toArray(new ThreadInfo[threads.size()]) ;
	}

	protected boolean processStartElement(XmlPullParser xpp) {
		String name = xpp.getName();
		if (name.equals("threads")) {
			return true ;	
		}
		if (name.equals("thread")) {
			ThreadInfo info = new ThreadInfo() ;
			info.setId(Integer.parseInt(xpp.getAttributeValue("", "id")));
			info.setStatus(xpp.getAttributeValue("", "status")) ;
			threads.add(info) ;
			return true ;
		}
		return false ;
	}


	protected boolean processEndElement(XmlPullParser xpp) {
		
		return xpp.getName().equals("threads") ;
	}
}

package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;
import java.net.SocketException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class MultiReaderStrategy extends AbstractReadStrategy {

	private Vector streamReaders;
	private Hashtable threads;
	private XmlStreamReader currentReader;

	public MultiReaderStrategy(XmlPullParser xpp) {
		super(xpp);
		streamReaders = new Vector();
		threads = new Hashtable();

		new Thread("xml reader") {
			public void run() {
				try {
					readLoop();
				} catch (SocketException e) {
					RdtDebugCorePlugin.debug("read loop stopped because socket has been closed.") ;
				} catch (Exception e) {
					RdtDebugCorePlugin.debug("read loop stopped due to error : ", e);
					// needs PDE Junit otherwise
					// RdtDebugCorePlugin.log(e);
					e.printStackTrace();
				} finally {
					releaseAllReader() ;	
				}
				
			}
		}
		.start();
	}

	protected void readLoop() throws XmlPullParserException, IOException, XmlStreamReaderException {
		RdtDebugCorePlugin.debug("Starting xml read loop.");
		int eventType = xpp.getEventType();
		do {
			if (eventType == XmlPullParser.START_TAG) {
				this.dispatchStartTag();
			} else if (eventType == XmlPullParser.END_TAG && currentReader != null) {
				if (xpp.getDepth() == 1) {
					this.removeReader(currentReader);
					currentReader = null;
				} else {
					currentReader.processEndElement(xpp);
				}
			}
			eventType = xpp.next();
		} while (eventType != XmlPullParser.END_DOCUMENT);
		RdtDebugCorePlugin.debug("Read loop stopped because end of stream was reached.");
	}

	protected void dispatchStartTag() throws XmlPullParserException, IOException, XmlStreamReaderException {
		RdtDebugCorePlugin.debug("Dispatching start tag " + xpp.getName());
		if (currentReader != null) {
			currentReader.processStartElement(xpp);
			return;
		}
		int missed = 0 ;
		do {
			for (Iterator iter = streamReaders.iterator(); iter.hasNext();) {
				XmlStreamReader streamReader = (XmlStreamReader) iter.next();
				if (streamReader.processStartElement(xpp)) {
					currentReader = streamReader;
					break;
				}
			}
			if (currentReader == null) {
				missed += 1 ;
				RdtDebugCorePlugin.debug("Missed Start Tag : " + xpp.getName());
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
				}
			}
		} while (currentReader == null && missed < 10);
	}

	protected void releaseAllReader() {
		for (Iterator iter = streamReaders.iterator(); iter.hasNext();) {
			XmlStreamReader streamReader = (XmlStreamReader) iter.next();
			((Thread) threads.get(streamReader)).interrupt();			
			iter.remove() ;	
		}	
		threads.clear() ;
	}

	protected synchronized void removeReader(XmlStreamReader streamReader) {
		((Thread) threads.get(streamReader)).interrupt();
		threads.remove(streamReader);
		streamReaders.remove(streamReader);
	}

	protected synchronized void addReader(XmlStreamReader streamReader) {
		streamReaders.add(streamReader);
		threads.put(streamReader, Thread.currentThread());
	}

	public void readElement(XmlStreamReader streamReader) {
		this.addReader(streamReader);
		try {
			RdtDebugCorePlugin.debug("Thread is waiting for input: " + Thread.currentThread());
			Thread.sleep(Long.MAX_VALUE);
		} catch (InterruptedException e) {
			RdtDebugCorePlugin.debug("Thread has finished processing : " + Thread.currentThread());
		}
	}

}

package org.rubypeople.rdt.internal.debug.core.parsing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
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
public class FramesReader extends XmlStreamReader {
	
	private RubyThread thread;
	int index = 1 ;
	private ArrayList frames ;
	
	public RubyStackFrame[] readFrames(RubyThread thread, XmlPullParser xpp) {
		this.thread = thread ;
		this.frames = new ArrayList() ;
		try {
			this.readElement(xpp);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		RubyStackFrame[] frameArray = new RubyStackFrame[frames.size()] ;
		frames.toArray(frameArray) ;
		thread.setStackFrames(frameArray) ;
		return frameArray ;
	}


	protected void processStartElement(XmlPullParser xpp) {
		String name = xpp.getName();		
		if (name.equals("frame")) {
			int line = Integer.parseInt(xpp.getAttributeValue("", "line"));			
			String file = xpp.getAttributeValue("", "file");
			this.frames.add(new RubyStackFrame(thread, file, line, index++)) ;			
		}
	}


}

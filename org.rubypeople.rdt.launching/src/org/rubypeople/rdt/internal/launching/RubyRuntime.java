package org.rubypeople.rdt.internal.launching;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class RubyRuntime {
	protected static RubyRuntime runtime;
	
	protected List installedInterpreters;
	protected RubyInterpreter selectedInterpreter;
	protected RubyRuntime() {
		super();
	}

	public static RubyRuntime getDefault() {
		if (runtime == null) {
			runtime = new RubyRuntime();
		}
		return runtime;
	}
	
	public RubyInterpreter getSelectedInterpreter() {
		if (selectedInterpreter == null) {
			loadRuntimeConfiguration();
		}
		return selectedInterpreter;
	}

	public RubyInterpreter getInterpreter(String name) {
		Iterator interpreters = getInstalledInterpreters().iterator();
		while(interpreters.hasNext()) {
			RubyInterpreter each = (RubyInterpreter) interpreters.next();
			if (each.getName().equals(name))
				return each;
		}
		
		return getSelectedInterpreter();
	}

	public void setSelectedInterpreter(RubyInterpreter anInterpreter) {
		selectedInterpreter = anInterpreter;
		saveRuntimeConfiguration();
	}

	public void addInstalledInterpreter(RubyInterpreter anInterpreter) {
		getInstalledInterpreters().add(anInterpreter);
		if (getInstalledInterpreters().size() == 1)
			setSelectedInterpreter((RubyInterpreter) getInstalledInterpreters().get(0));

		saveRuntimeConfiguration();
	}

	public List getInstalledInterpreters() {
		if (installedInterpreters == null)
			loadRuntimeConfiguration();
		return installedInterpreters;
	}
	
	public void setInstalledInterpreters(List newInstalledInterpreters) {
		installedInterpreters = newInstalledInterpreters;
		if (installedInterpreters.size() > 0)
			setSelectedInterpreter((RubyInterpreter)installedInterpreters.get(0));
		else
			setSelectedInterpreter(null);
	}
	
	protected void saveRuntimeConfiguration() {
		writeXML(getRuntimeConfigurationWriter());
	}

	protected Writer getRuntimeConfigurationWriter() {
		try {
			OutputStream stream = new BufferedOutputStream(new FileOutputStream(getRuntimeConfigurationFile()));
			return new OutputStreamWriter(stream);
		} catch (FileNotFoundException e) {}

		return null;
	}
	
	protected void loadRuntimeConfiguration() {
		installedInterpreters = new ArrayList();
		try {
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setContentHandler(getRuntimeConfigurationContentHandler());
			reader.parse(new InputSource(getRuntimeConfigurationReader()));
		} catch(Exception e) {
			RdtLaunchingPlugin.log(e);
		}
	}

	protected Reader getRuntimeConfigurationReader() {
		try {
			return new FileReader(getRuntimeConfigurationFile());
		} catch(FileNotFoundException e) {}
		return new StringReader("");
	}
	
	protected void writeXML(Writer writer) {
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeconfig>");
			Iterator interpretersIterator = installedInterpreters.iterator();
			while (interpretersIterator.hasNext()) {
				writer.write("<interpreter name=\"");
				
				RubyInterpreter entry = (RubyInterpreter) interpretersIterator.next();
				writer.write(entry.getName());
				writer.write("\" path=\"");
				writer.write(entry.getInstallLocation().toString());
				writer.write("\"");
				if (entry.equals(selectedInterpreter))
					writer.write(" selected=\"true\"");
					
				writer.write("/>");
			}
			writer.write("</runtimeconfig>");
			writer.flush();
		} catch(IOException e) {
			RdtLaunchingPlugin.log(e);
		}
	}

	protected ContentHandler getRuntimeConfigurationContentHandler() {
		return new ContentHandler() {
			public void setDocumentLocator(Locator locator) {}
			public void startDocument() throws SAXException {}
			public void endDocument() throws SAXException {}
			public void startPrefixMapping(String prefix, String uri) throws SAXException {}
			public void endPrefixMapping(String prefix) throws SAXException {}
			public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
				if ("interpreter".equals(qName)) {
					String interpreterName = atts.getValue("name");
					IPath installLocation = new Path(atts.getValue("path"));
					RubyInterpreter interpreter = new RubyInterpreter(interpreterName, installLocation);
					installedInterpreters.add(interpreter);
					if (atts.getValue("selected") != null)
						selectedInterpreter = interpreter;
				}
			}
			public void endElement(String namespaceURI, String localName, String qName) throws SAXException {}
			public void characters(char[] ch, int start, int length) throws SAXException {}
			public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {}
			public void processingInstruction(String target, String data) throws SAXException {}
			public void skippedEntity(String name) throws SAXException {}
		};
	}
	
	protected File getRuntimeConfigurationFile() {
		IPath stateLocation = RdtLaunchingPlugin.getDefault().getStateLocation();
		IPath fileLocation = stateLocation.append("runtimeConfiguration.xml");
		return new File(fileLocation.toOSString());
	}
}

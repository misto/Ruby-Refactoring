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
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.rubypeople.rdt.launching.IInterpreter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class RubyRuntime {
	private static final String TAG_INTERPRETER = "interpreter";
	private static final String ATTR_PATH = "path";
	private static final String ATTR_NAME = "name";
	private static final String ATTR_SELECTED = "selected";

	protected static RubyRuntime runtime;
	
	protected List<IInterpreter> installedInterpreters;
	protected IInterpreter selectedInterpreter;
    private List<Listener> listeners = new ArrayList<Listener>();
    
    public static interface Listener {
        void selectedInterpreterChanged();
    }
    
	protected RubyRuntime() {
		super();
	}

	public static RubyRuntime getDefault() {
		if (runtime == null) {
			runtime = new RubyRuntime();
		}
		return runtime;
	}
    
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(Listener listener) {
        listeners.remove(listener);
    }
	
	public IInterpreter getSelectedInterpreter() {
		if (selectedInterpreter == null) {
			loadRuntimeConfiguration();
		}
		return selectedInterpreter;
	}

	public IInterpreter getInterpreter(String name) {
		Iterator interpreters = getInstalledInterpreters().iterator();
		while(interpreters.hasNext()) {
			IInterpreter each = (IInterpreter) interpreters.next();
			if (each.getName().equals(name))
				return each;
		}
		
		return getSelectedInterpreter();
	}

	public void setSelectedInterpreter(IInterpreter anInterpreter) {
        if (selectedInterpreter == anInterpreter) return;
		selectedInterpreter = anInterpreter;
		saveRuntimeConfiguration();
        for (Iterator iter = listeners.iterator(); iter.hasNext();) {
            Listener listener = (Listener) iter.next();
            listener.selectedInterpreterChanged();
        }        
	}

	public void addInstalledInterpreter(IInterpreter anInterpreter) {
		getInstalledInterpreters().add(anInterpreter);
		if (getInstalledInterpreters().size() == 1)
			setSelectedInterpreter((RubyInterpreter) getInstalledInterpreters().get(0));
		else
			saveRuntimeConfiguration();
	}

	public List<IInterpreter> getInstalledInterpreters() {
		if (installedInterpreters == null)
			loadRuntimeConfiguration();
		return installedInterpreters;
	}
	
	public void setInstalledInterpreters(List<IInterpreter> newInstalledInterpreters) {
		installedInterpreters = newInstalledInterpreters;
		if (installedInterpreters.size() > 0)
			setSelectedInterpreter((IInterpreter)installedInterpreters.get(0));
		else
			setSelectedInterpreter(null);
		saveRuntimeConfiguration();
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
		installedInterpreters = new ArrayList<IInterpreter>();
		try {
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setContentHandler(getRuntimeConfigurationContentHandler());
			Reader fileReader = this.getRuntimeConfigurationReader() ;
			if (fileReader == null) {
				// FIXME If we get a better algorithm for auto detection we should use it, but apparently this didn't make people happy
//				autoDetectRubyInterpreter();
				return ;
			}
			reader.parse(new InputSource(fileReader)) ;
		} catch(Exception e) {
			RdtLaunchingPlugin.log(e);
		}
	}

	private void autoDetectRubyInterpreter() {		
		IPath path = null;
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
		  path = new Path("/ruby/bin/ruby.exe");
		} else  {
			path = new Path("/usr/local/bin/ruby");
		}
		IInterpreter interpreter = new RubyInterpreter("Default Ruby Interpreter", path);
		installedInterpreters.add(interpreter);
		selectedInterpreter = interpreter;
	}

	protected Reader getRuntimeConfigurationReader() {
		try {
			return new FileReader(getRuntimeConfigurationFile());
		} catch(FileNotFoundException e) {			
			return null ;
		}
	}
	
	protected void writeXML(Writer writer) {
		try {
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><runtimeconfig>");
			Iterator interpretersIterator = installedInterpreters.iterator();
			while (interpretersIterator.hasNext()) {
				writer.write("<");
				writer.write(TAG_INTERPRETER);
				writer.write(" ");
				writer.write(ATTR_NAME);
				writer.write("=\"");
				
				IInterpreter entry = (IInterpreter) interpretersIterator.next();
				writer.write(entry.getName());
				writer.write("\" ");
				writer.write(ATTR_PATH);
				writer.write("=\"");
				writer.write(entry.getInstallLocation().toString());
				writer.write("\"");
				if (entry.equals(selectedInterpreter)) {
					writer.write(" ");
					writer.write(ATTR_SELECTED);
					writer.write("=\"true\"");
				}
					
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
				if (TAG_INTERPRETER.equals(qName)) {
					String interpreterName = atts.getValue(ATTR_NAME);
					IPath installLocation = new Path(atts.getValue(ATTR_PATH));
					IInterpreter interpreter = new RubyInterpreter(interpreterName, installLocation);
					installedInterpreters.add(interpreter);
					if (atts.getValue(ATTR_SELECTED) != null)
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

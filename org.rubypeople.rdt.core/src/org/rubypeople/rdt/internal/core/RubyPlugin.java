package org.rubypeople.rdt.internal.core;

import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.SAXParserFactory;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.*;
import org.rubypeople.rdt.core.RubyElement;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.xml.sax.*;

public class RubyPlugin extends Plugin {
	private static final String CONFIGURATION_FILE = "libraryConfiguration.xml";
	public final static String PLUGIN_ID = "org.rubypeople.rdt.core";
	public final static String RUBY_NATURE_ID = PLUGIN_ID + ".rubynature";

	private List libraries;
	private RubyLibrary selectedLibrary;
	private boolean isCodeFormatterDebugging = false ;
	
	protected static RubyPlugin plugin;

	public RubyPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static RubyPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public void startup() throws CoreException {
		super.startup();
		IAdapterManager manager= Platform.getAdapterManager();
		manager.registerAdapters(new RubyElementAdapterFactory(), RubyElement.class);
		manager.registerAdapters(new ResourceAdapterFactory(), IResource.class);
		String codeFormatterOption = Platform.getDebugOption(RubyPlugin.PLUGIN_ID + "/codeformatter") ;
		isCodeFormatterDebugging = codeFormatterOption == null ? false : codeFormatterOption.equalsIgnoreCase("true") ;
		String rubyParserOption = Platform.getDebugOption(RubyPlugin.PLUGIN_ID + "/rubyparser") ;
		RubyParser.setDebugging(rubyParserOption == null ? false : rubyParserOption.equalsIgnoreCase("true")) ;
	}

	public static void log(Exception runtimeException) {
		getDefault().getLog().log(new Status(Status.INFO, RubyPlugin.PLUGIN_ID, 0, runtimeException.getMessage(), runtimeException));
	}

	/**
	 * @return
	 */
	public RubyLibrary getSelectedLibrary() {
		return selectedLibrary;
	}

	/**
	 * @param installedInterpreters
	 */
	public void setInstalledLibraries(List installedInterpreters) {
		libraries = installedInterpreters;		
		if (installedInterpreters.size() > 0)
			setSelectedLibrary((RubyLibrary)installedInterpreters.get(0));
		else
			setSelectedLibrary(null);
	}

	/**
	 * @param library
	 */
	public void setSelectedLibrary(RubyLibrary library) {
		selectedLibrary = library;
		saveRuntimeConfiguration();
	}

	/**
	 * @return
	 */
	public List getInstalledLibraries() {
		if (libraries == null)
			loadRuntimeConfiguration();
		return libraries;
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
		libraries = new ArrayList();
		try {
			XMLReader reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			reader.setContentHandler(getRuntimeConfigurationContentHandler());
			Reader fileReader = this.getRuntimeConfigurationReader() ;
			if (fileReader == null) {
				return ;
			}
			reader.parse(new InputSource(fileReader)) ;
		} catch(Exception e) {
			RubyPlugin.log(e);
		}
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
			writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?><libraryconfig>");
			Iterator librariesIterator = libraries.iterator();
			while (librariesIterator.hasNext()) {
				writer.write("<library name=\"");
				
				RubyLibrary entry = (RubyLibrary) librariesIterator.next();
				writer.write(entry.getName());
				writer.write("\" path=\"");
				writer.write(entry.getInstallLocation().toString());
				writer.write("\"");
				if (entry.equals(selectedLibrary))
					writer.write(" selected=\"true\"");
				
				writer.write("/>");
			}
			writer.write("</libraryconfig>");
			writer.flush();
		} catch(IOException e) {
			RubyPlugin.log(e);
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
				if ("library".equals(qName)) {
					String libraryName = atts.getValue("name");
					IPath installLocation = new Path(atts.getValue("path"));
					RubyLibrary library = new RubyLibrary(libraryName, installLocation);
					libraries.add(library);
					if (atts.getValue("selected") != null)
						selectedLibrary = library;
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
		IPath stateLocation = RubyPlugin.getDefault().getStateLocation();
		IPath fileLocation = stateLocation.append(CONFIGURATION_FILE);
		return new File(fileLocation.toOSString());
	}

	/**
	 * @param string
	 */
	public static void log(String string) {
		log(new Exception(string));		
	}

	public boolean isCodeFormatterDebugging() {
		return isCodeFormatterDebugging;
	}
}

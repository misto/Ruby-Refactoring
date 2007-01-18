package org.rubypeople.rdt.internal.launching;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.RubyCore;
import org.w3c.dom.Document;
import org.xml.sax.helpers.DefaultHandler;

public class LaunchingPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.rubypeople.rdt.launching"; //$NON-NLS-1$
    public static String osDependentPath(String aPath) {
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            if (aPath.startsWith(File.separator)) {
                aPath = aPath.substring(1) ;
            }
        }

        return aPath;
    }
	protected static LaunchingPlugin plugin;
	private static DocumentBuilder fgXMLParser;

	public LaunchingPlugin() {
		super();
		plugin = this;
	}

	public static Plugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyCore.getWorkspace();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RdtLaunchingMessages.RdtLaunchingPlugin_internalErrorOccurred, e));
	}
	
	public static void debug(String message) {
		if (getDefault().isDebugging()) {
			System.out.println(message);
		}
	}
	@Override
	public void stop(BundleContext arg0) throws Exception {
		super.stop(arg0);
		savePluginPreferences() ;
	}

	public static String getUniqueIdentifier() {
		return PLUGIN_ID;
	}

	/**
	 * Returns a Document that can be used to build a DOM tree
	 * @return the Document
	 * @throws ParserConfigurationException if an exception occurs creating the document builder
	 */
	public static Document getDocument() throws ParserConfigurationException {
		DocumentBuilderFactory dfactory= DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder= dfactory.newDocumentBuilder();
		Document doc= docBuilder.newDocument();
		return doc;
	}

		/**
	 * Serializes a XML document into a string - encoded in UTF8 format,
	 * with platform line separators.
	 * 
	 * @param doc document to serialize
	 * @return the document as a string
	 */
	public static String serializeDocument(Document doc) throws IOException, TransformerException {
		ByteArrayOutputStream s= new ByteArrayOutputStream();
		
		TransformerFactory factory= TransformerFactory.newInstance();
		Transformer transformer= factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.METHOD, "xml"); //$NON-NLS-1$
		transformer.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
		
		DOMSource source= new DOMSource(doc);
		StreamResult outputTarget= new StreamResult(s);
		transformer.transform(source, outputTarget);
		
		return s.toString("UTF8"); //$NON-NLS-1$			
	}

	public static void log(String message) {
		log(new Status(IStatus.ERROR, getUniqueIdentifier(), IStatus.ERROR, message, null));			
	}

		/**
	 * Returns a shared XML parser.
	 * 
	 * @return an XML parser 
	 * @throws CoreException if unable to create a parser
	 * @since 3.0
	 */
	public static DocumentBuilder getParser() throws CoreException {
		if (fgXMLParser == null) {
			try {
				fgXMLParser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				fgXMLParser.setErrorHandler(new DefaultHandler());
			} catch (ParserConfigurationException e) {
				abort(RdtLaunchingMessages.LaunchingPlugin_33, e); 
			} catch (FactoryConfigurationError e) {
				abort(RdtLaunchingMessages.LaunchingPlugin_34, e); 
			}
		}
		return fgXMLParser;
	}
	
	/**
	 * Throws an exception with the given message and underlying exception.
	 * 
	 * @param message error message
	 * @param exception underlying exception or <code>null</code> if none
	 * @throws CoreException
	 */
	protected static void abort(String message, Throwable exception) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), 0, message, exception);
		throw new CoreException(status);
	}	
}

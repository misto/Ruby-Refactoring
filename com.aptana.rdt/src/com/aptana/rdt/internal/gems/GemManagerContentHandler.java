package com.aptana.rdt.internal.gems;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;

public class GemManagerContentHandler implements ContentHandler {

	private HashSet<Gem> gems;
	private String name;
	private String version;
	private String description;
	
	private StringBuffer data;
	private String platform;

	public void characters(char[] ch, int start, int length) throws SAXException {
		for (int i = start; i < start + length; i++) {
			data.append(ch[i]);
		}
	}

	public void endDocument() throws SAXException {
		// do nothing
	}

	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		if (qName.equals("name")) {
			name = data.toString();
		} else if (qName.equals("version")) {
			version = data.toString();
		} else if (qName.equals("description")) {
			description = data.toString();
		} else if (qName.equals("platform")) {
			platform = data.toString();
		} else if (qName.equals("gem")) {
			gems.add(new Gem(name, version, description, platform));
		}
	}

	public void endPrefixMapping(String arg0) throws SAXException {
		// do nothing
	}

	public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
			throws SAXException {
		// do nothing
	}

	public void processingInstruction(String arg0, String arg1)
			throws SAXException {
		// do nothing
	}

	public void setDocumentLocator(Locator arg0) {
		// do nothing
	}

	public void skippedEntity(String arg0) throws SAXException {
		// do nothing
	}

	public void startDocument() throws SAXException {
		gems = new HashSet<Gem>();
	}

	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {
		data = new StringBuffer();
	}

	public void startPrefixMapping(String arg0, String arg1)
			throws SAXException {
		// do nothing
	}

	public Set<Gem> getGems() {
		return Collections.unmodifiableSet(gems);
	}

}
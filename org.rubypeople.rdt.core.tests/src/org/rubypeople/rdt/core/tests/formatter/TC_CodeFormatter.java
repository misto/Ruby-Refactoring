package org.rubypeople.rdt.core.tests.formatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import org.apache.xerces.parsers.DOMParser;
import org.rubypeople.rdt.internal.formatter.CodeFormatter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TC_CodeFormatter extends TestCase {

	public class TestData {
		public TestData(String formattedText, String unformattedText, String assertionMessage) {
			this.formattedText = formattedText ;
			this.unformattedText = unformattedText ;
			this.assertionMessage = assertionMessage ;	
		}
		
		public String formattedText;
		public String unformattedText;
		public String assertionMessage;
	}

	private Hashtable testMap;
	public TC_CodeFormatter(String name) throws IOException, SAXException {
		super(name);
		testMap = new Hashtable();
		this.parseXmlConfiguration();
	}

	public String stripFirstNewLine(String input) {
		return input.substring(input.indexOf("\n") + 1);
	}

	public void parseXmlConfiguration() throws IOException, SAXException {
		DOMParser parser = new DOMParser();
		parser.parse(new InputSource(this.getClass().getResourceAsStream("FormatTestData.xml")));
		Document document = parser.getDocument();

		NodeList tests = document.getElementsByTagName("test");
		for (int i = 0; i < tests.getLength(); i++) {
			Node test = tests.item(i);
			String name = test.getAttributes().getNamedItem("ID").getNodeValue();
			ArrayList partList = new ArrayList();
			NodeList nl = test.getChildNodes();
		    createTestData(partList,  nl);
			for (int j = 0; j < nl.getLength(); j++) {
				Node partNode = nl.item(j);				
				if (partNode.getNodeName().equals("part")) {
					createTestData(partList,  partNode.getChildNodes());
				}
			}
			testMap.put(name, partList);
		}
	}

	private void createTestData(ArrayList partList, NodeList partNodes) {
		String formattedText = null;
		String unformattedText = null;
		String assertionMessage = null;
		for (int k = 0; k < partNodes.getLength(); k++) {
			Node node = partNodes.item(k);
			if (node.getNodeName().equals("formatted")) {
				formattedText = this.stripFirstNewLine(node.getFirstChild().getNodeValue());
			} else if (node.getNodeName().equals("unformatted")) {
				unformattedText = this.stripFirstNewLine(node.getFirstChild().getNodeValue());
			} else if (node.getNodeName().equals("assertionMessage")) {
				assertionMessage = node.getFirstChild().getNodeValue().trim();
			}
		}
		if (formattedText != null && unformattedText != null) {
			partList.add(new TestData(formattedText, unformattedText, assertionMessage));
		}
	}

	public void doTest(String name) {
		ArrayList partList = (ArrayList) testMap.get(name);
		for (int i = 0; i < partList.size(); i++) {
			TestData data = (TestData) partList.get(i);
			String formatted = new CodeFormatter('*').formatString(data.unformattedText);
			System.out.println("---------- " + data.assertionMessage + " --------") ;
			System.out.println(data.unformattedText) ;
			System.out.println("------------") ;
			System.out.println(formatted) ;
			Assert.assertEquals(data.assertionMessage, data.formattedText, formatted);
		}
	}

	public void testSimple() {
		this.doTest("simple");
	}

	public void testKeywordInComment() {
		this.doTest("KeywordInComment");
	}

	public void testTakeIndentationOfFirstKeyword() {
		this.doTest("takeIndentationOfFirstKeyword");
	}

	public void testBlockWithBrackets() {
		this.doTest("blockWithBrackets");
	}

	public void testBlocks() {
		this.doTest("Blocks");
	}
	
	public void testParameters() {
		this.doTest("Parameters");
	}	

	public void testLiterals() {
		this.doTest("Literals");
	}	
	
	public void testLiteralsStartingWithPercentSign() {
		this.doTest("LiteralsStartingWithPercentSign");
	}	
	
}

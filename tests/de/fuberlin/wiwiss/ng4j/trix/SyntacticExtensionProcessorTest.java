// $Id: SyntacticExtensionProcessorTest.java,v 1.1 2004/12/13 02:05:53 cyganiak Exp $
package de.fuberlin.wiwiss.ng4j.trix;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMResult;

import junit.framework.TestCase;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Tests for {@link SyntacticExtensionProcessor}
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SyntacticExtensionProcessorTest extends TestCase {
	private static String TRIX_NS = "http://www.w3.org/2004/03/trix/trix-1/";
	private static String XSL1 = "tests/de/fuberlin/wiwiss/ng4j/trix/tests/extension1.xsl";
	private static String XSL2 = "tests/de/fuberlin/wiwiss/ng4j/trix/tests/extension2.xsl";

	public void testEmptyDocNoExtension() throws Exception {
		Document doc = toDOM("<TriX xmlns=\"" + TRIX_NS + "\"/>");
		DOMResult dom = new DOMResult();
		new SyntacticExtensionProcessor(doc).process(dom);
		Node processed = dom.getNode();
		assertNotNull(processed);
		assertEquals(1, processed.getChildNodes().getLength());
		Node node = processed.getChildNodes().item(0);
		assertEquals("TriX", node.getNodeName());
		assertEquals(TRIX_NS, node.getNamespaceURI());
		assertFalse(node.hasChildNodes());
	}
 
	public void testEmptyDocWithExtension() {
		Document doc = toDOM(
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"application/xml\"?>" +
				"<TriX xmlns=\"" + TRIX_NS + "\"/>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		List transforms = processor.getTransforms();
		assertNotNull(transforms);
		assertEquals(1, transforms.size());
		assertEquals(XSL1, transforms.get(0));
	}

	public void testOtherProcessingInstruction() {
		Document doc = toDOM(
				"<?foobar href=\"" + XSL1 + "\" type=\"application/xml\"?>" +
				"<TriX xmlns=\"" + TRIX_NS + "\"/>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		assertTrue(processor.getTransforms().isEmpty());
	}

	public void testNonXSLTStylesheet() {
		Document doc = toDOM(
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"text/css\"?>" +
				"<TriX xmlns=\"" + TRIX_NS + "\"/>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		assertTrue(processor.getTransforms().isEmpty());		
	}

	public void testXMLDeclarationBeforePI() {
		Document doc = toDOM(
				"<?xml version=\"1.0\"?>" +
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"application/xml\"?>" +
				"<TriX xmlns=\"" + TRIX_NS + "\"/>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		List transforms = processor.getTransforms();
		assertNotNull(transforms);
		assertEquals(1, transforms.size());
		assertEquals(XSL1, transforms.get(0));		
	}

	public void testWSBeforePI() {
		Document doc = toDOM(
				"    \n   \t   \r   " +
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"application/xml\"?>" +
				"<TriX xmlns=\"" + TRIX_NS + "\"/>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		List transforms = processor.getTransforms();
		assertNotNull(transforms);
		assertEquals(1, transforms.size());
		assertEquals(XSL1, transforms.get(0));		
	}

	public void testDoctypeBeforePI() {
		Document doc = toDOM(
				"<!DOCTYPE aaa>" +
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"application/xml\"?>" +
				"<TriX xmlns=\"" + TRIX_NS + "\"/>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		List transforms = processor.getTransforms();
		assertNotNull(transforms);
		assertEquals(1, transforms.size());
		assertEquals(XSL1, transforms.get(0));		
	}

	public void testIgnoreStylesheetAtEndOfDocument() {
		Document doc = toDOM(
				"<TriX xmlns=\"" + TRIX_NS + "\"/>" + 
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"application/xml\"?>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		assertTrue(processor.getTransforms().isEmpty());				
	}

	public void testIgnoreStylesheetWithinDocument() {
		Document doc = toDOM(
				"<TriX xmlns=\"" + TRIX_NS + "\">" + 
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"application/xml\"?>" +
				"</TriX>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		assertTrue(processor.getTransforms().isEmpty());				
	}

	public void testSeveralStylesheets() {
		Document doc = toDOM(
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"application/xml\"?>" +
				"<?xml-stylesheet href=\"" + XSL2 + "\" type=\"application/xml\"?>" +
				"<TriX xmlns=\"" + TRIX_NS + "\"/>");
		SyntacticExtensionProcessor processor = new SyntacticExtensionProcessor(doc);
		List transforms = processor.getTransforms();
		assertNotNull(transforms);
		assertEquals(2, transforms.size());
		assertEquals(XSL1, transforms.get(0));		
		assertEquals(XSL2, transforms.get(1));		
	}

	public void testTwoExtensions() throws Exception {
		Document in = toDOM(
				"<?xml-stylesheet href=\"" + XSL1 + "\" type=\"application/xml\"?>" +
				"<?xml-stylesheet href=\"" + XSL2 + "\" type=\"application/xml\"?>" +
				"<TriX xmlns=\"" + TRIX_NS + "\"/>");
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		DOMResult out = new DOMResult(doc);
		new SyntacticExtensionProcessor(in).process(out);
		assertEquals(3, doc.getElementsByTagNameNS(TRIX_NS, "uri").getLength());
	}

	private Document toDOM(String xml) {
		InputSource source = new InputSource(new StringReader(xml));
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			return factory.newDocumentBuilder().parse(source);
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);			
		} catch (IOException ex) {
			throw new RuntimeException(ex);			
		}
	}
}

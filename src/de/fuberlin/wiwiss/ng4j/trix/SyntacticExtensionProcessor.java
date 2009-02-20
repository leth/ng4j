// $Id: SyntacticExtensionProcessor.java,v 1.5 2009/02/20 08:09:52 hartig Exp $
package de.fuberlin.wiwiss.ng4j.trix;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implements TriX syntactic extensions by taking a
 * DOM representation of a TriX document by finding any
 * stylesheet processing instructions therein, fetching
 * the stylesheets, applying them to the DOM tree, and
 * returning the modified tree.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SyntacticExtensionProcessor {
	private Document doc;

	public SyntacticExtensionProcessor(Document doc) {
		this.doc = doc;
	}

	List<String> getTransforms() {
		List<String> transformURIs = new ArrayList<String>();
		for (int i = 0; i < this.doc.getChildNodes().getLength(); i++) {
			Node node = this.doc.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				break;
			}
			if (node.getNodeType() != Node.PROCESSING_INSTRUCTION_NODE) {
				continue;
			}
			ProcessingInstruction pi = (ProcessingInstruction) node;
			if (!"xml-stylesheet".equals(pi.getTarget())) {
				return Collections.emptyList();
			}
			NamedNodeMap pseudoAttribs =
					extractPseudoAttribsFromPI(pi);
			if (!isXSLTMimeType(pseudoAttribs.getNamedItem("type").getNodeValue())) {
				return Collections.emptyList();
			}
			transformURIs.add(pseudoAttribs.getNamedItem("href").getNodeValue());
		}
		return transformURIs;
	}

	public void process(Result target) throws TransformerException {
		Iterator<String> it = getTransforms().iterator();
		while (it.hasNext()) {
			String stylesheetURI = (String) it.next();
			try {
				Document transformed = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				TransformerFactory.newInstance().newTransformer(new StreamSource(stylesheetURI)).transform(new DOMSource(this.doc), new DOMResult(transformed));
				this.doc = transformed;
			} catch (ParserConfigurationException e) {
				throw new RuntimeException(e);
			}
		}
		TransformerFactory.newInstance().newTransformer().transform(
				new DOMSource(this.doc), target);
		return;
	}

	/**
	 * The <?xml-stylesheet?> processing instruction consists of XML attributes,
	 * but PIs normally are just unstructured text. To get at the attribute values,
	 * we stuff the unstructured text into an XML attribute and send it through
	 * the XML parser.
	 */
	private static NamedNodeMap extractPseudoAttribsFromPI(ProcessingInstruction pi) {
		String dataAsPseudoXML = "<dummy " + pi.getData() + "/>";
		return parseXML(dataAsPseudoXML).getFirstChild().getAttributes();
	}

	private static Document parseXML(String xml) {
		InputSource source = new InputSource(new StringReader(xml));
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(source);
		} catch (ParserConfigurationException ex) {
			throw new RuntimeException(ex);
		} catch (SAXException ex) {
			throw new RuntimeException(ex);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * I don't know what the proper MIME type for XSLT stylesheets is or what
	 * other types are commonly used, so I assume that it's XSLT iff it contains
	 * "xml" or "xsl".
	 */
	private static boolean isXSLTMimeType(String mimeType) {
		return mimeType.indexOf("xml") >= 0 || mimeType.indexOf("xsl") >= 0;
	}
}
/*
 *  (c) Copyright 2004 - 2009 Christian Bizer (chris@bizer.de)
 *   All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
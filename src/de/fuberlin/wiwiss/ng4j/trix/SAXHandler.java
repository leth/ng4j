/*
 * $Id: SAXHandler.java,v 1.4 2009/02/20 08:09:52 hartig Exp $
 */
package de.fuberlin.wiwiss.ng4j.trix;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * A SAX content handler that reads a TriX file and calls methods
 * on a {@link ParserCallback} for its graphs and triples. Assumes
 * that the file is a valid TriX file, so no error checking.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SAXHandler extends DefaultHandler {
	private final static int STATE_GRAPH_START = 1;
	private final static int STATE_BEFORE_SUBJECT = 2;
	private final static int STATE_BEFORE_PREDICATE = 3;
	private final static int STATE_BEFORE_OBJECT = 4;

	private final ParserCallback callback;
	private final URI baseURI;
	private int state = STATE_GRAPH_START;
	private List<String> graphNameURIs;
	private StringBuffer currentContent;
	private String lang;
	private String datatypeURI;

	/**
	 * Creates a new instance from a parser callback.
	 * @param callback the parser callback for this handler
	 * @param baseURI the URI of the parsed file (for resolving relative URIs)
	 */
	public SAXHandler(ParserCallback callback, URI baseURI) {
		this.callback = callback;
		this.baseURI = baseURI;
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public void startElement(String namespaceURI, String localName, String qName,
			Attributes atts) throws SAXException {
		if ("graph".equals(localName)) {
			this.graphNameURIs = new ArrayList<String>(1);
			this.state = STATE_GRAPH_START;
		} else if ("triple".equals(localName)) {
			if (this.state == STATE_GRAPH_START) {
				this.callback.startGraph(this.graphNameURIs);
			}
			this.state = STATE_BEFORE_SUBJECT;
		}
		this.currentContent = new StringBuffer();
		this.lang = atts.getValue("xml:lang");
		this.datatypeURI = atts.getValue("datatype");
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#characters(char[], int, int)
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		this.currentContent.append(ch, start, length);
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.ContentHandler#endElement(java.lang.String, java.lang.String, java.lang.String)
	 */
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {
		String content = this.currentContent.toString();
		if ("graph".equals(localName)) {
			if (this.state == STATE_GRAPH_START) {
				// graph with no triples
				this.callback.startGraph(this.graphNameURIs);
			}
			this.callback.endGraph();
		} else if ("uri".equals(localName) && this.state == STATE_GRAPH_START) {
			this.graphNameURIs.add(resolveURI(content));
		} else if ("id".equals(localName)) {
			if (this.state == STATE_BEFORE_SUBJECT) {
				this.callback.subjectBNode(content);
				this.state = STATE_BEFORE_PREDICATE;
			} else {
				this.callback.objectBNode(content);
				this.state = STATE_BEFORE_SUBJECT;
			}
		} else if ("uri".equals(localName)) {
			String uri = resolveURI(content);
			if (this.state == STATE_BEFORE_SUBJECT) {
				this.callback.subjectURI(uri);
				this.state = STATE_BEFORE_PREDICATE;
			} else if (this.state == STATE_BEFORE_PREDICATE) {
				this.callback.predicate(uri);
				this.state = STATE_BEFORE_OBJECT;
			} else {
				this.callback.objectURI(uri);
				this.state = STATE_BEFORE_SUBJECT;
			}
		} else if ("plainLiteral".equals(localName)) {
			if (this.state == STATE_BEFORE_SUBJECT) {
				this.callback.subjectPlainLiteral(content, this.lang);
				this.state = STATE_BEFORE_PREDICATE;
			} else {
				this.callback.objectPlainLiteral(content, this.lang);
				this.state = STATE_BEFORE_SUBJECT;
			}
		} else if ("typedLiteral".equals(localName)) {
			if (this.state == STATE_BEFORE_SUBJECT) {
				this.callback.subjectTypedLiteral(content, this.datatypeURI);
				this.state = STATE_BEFORE_PREDICATE;
			} else {
				this.callback.objectTypedLiteral(content, this.datatypeURI);
				this.state = STATE_BEFORE_SUBJECT;
			}
		}
	}
	
	private String resolveURI(String relativeOrAbsoluteURI) throws SAXException {
		try {
			return this.baseURI.resolve(relativeOrAbsoluteURI).toString();
		} catch (IllegalArgumentException iaex) {
			throw new SAXException("Malformed URI: " + relativeOrAbsoluteURI);
		}
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

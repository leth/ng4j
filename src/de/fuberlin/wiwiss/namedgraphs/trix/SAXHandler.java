/*
 * $Id: SAXHandler.java,v 1.1 2004/09/13 14:37:28 cyganiak Exp $
 */
package de.fuberlin.wiwiss.namedgraphs.trix;

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
	private List graphNameURIs;
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
			this.graphNameURIs = new ArrayList(1);
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

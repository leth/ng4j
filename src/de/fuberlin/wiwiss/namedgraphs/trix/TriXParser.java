/*
 * $Id: TriXParser.java,v 1.2 2004/09/15 08:22:00 bizer Exp $
 */
package de.fuberlin.wiwiss.namedgraphs.trix;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * A parser for TriX files (see
 * <a href="http://www.hpl.hp.com/techreports/2004/HPL-2004-56">TriX
 * specification</a>). Parsed graphs and triples are passed to a
 * {@link ParserCallback} for further processing.
 * <p>
 * TODO: Deal with TriX XSLT syntactic extensions<br>
 * TODO: Have only one parse() method, with InputSource argument
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriXParser {
	static final String JAXP_SCHEMA_LANGUAGE =
			"http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	static final String W3C_XML_SCHEMA =
			"http://www.w3.org/2001/XMLSchema";
	static final String JAXP_SCHEMA_SOURCE =
			"http://java.sun.com/xml/jaxp/properties/schemaSource";

	/**
	 * Parses a TriX file from an InputStream. Parsed graphs and triples are passed
	 * to the callback.
	 * @param source a stream containing a TriX file
	 * @param baseURI the URI of the input file (for resolving relative URIs)
	 * @param callback receives parsed graphs and triples
	 * @throws SAXException on XML parse error
	 * @throws IOException on I/O error when reading from source
	 */
	public void parse(InputStream source, URI baseURI, ParserCallback callback)
			throws IOException, SAXException {
		parse(new InputSource(source), baseURI, callback);
	}
	
	/**
	 * Parses a TriX file from a Reader. Parsed graphs and triples are passed
	 * to the callback.
	 * @param source a stream containing a TriX file
	 * @param baseURI the URI of the input file (for resolving relative URIs)
	 * @param callback receives parsed graphs and triples
	 * @throws SAXException on XML parse error
	 * @throws IOException on I/O error when reading from source
	 */
	public void parse(Reader source, URI baseURI, ParserCallback callback)
			throws IOException, SAXException {
		parse(new InputSource(source), baseURI, callback);
	}
	
	private void parse(InputSource source, URI baseURI, ParserCallback callback)
			throws IOException, SAXException {
		// I've no idea what I'm doing here ... seems to work for now
		try {
			String schemaSource = this.getClass().getResource("trix.xsd").toString();
			SAXParserFactory spf = SAXParserFactory.newInstance();
			spf.setNamespaceAware(true);
			spf.setValidating(true);
			SAXParser saxParser = spf.newSAXParser();
			saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
			saxParser.setProperty(JAXP_SCHEMA_SOURCE, schemaSource);
			XMLReader parser = saxParser.getXMLReader();
			SAXHandler handler = new SAXHandler(callback, baseURI);
			parser.setContentHandler(handler);
			registerSilentErrorHandler(parser);
			parser.parse(source);
		} catch (ParserConfigurationException pcex) {
			throw new SAXException(pcex);
		}
	}

	private void registerSilentErrorHandler(XMLReader parser) {
		parser.setErrorHandler(new ErrorHandler() {
			public void error(SAXParseException exception) throws SAXException {
				throw exception;
			}
			public void warning(SAXParseException exception) throws SAXException {
				// silently ignore
			}
			public void fatalError(SAXParseException exception) throws SAXException {
				throw exception;
			}
		});
	}
}

/*
 *  (c)   Copyright 2004 Christian Bizer (chris@bizer.de)
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

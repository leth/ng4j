/*
 * $Id$
 */
package de.fuberlin.wiwiss.ng4j.trix;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFErrorHandler;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.RDFReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.shared.JenaException;

/**
 * Simple RDFReader that adds support for the TriX syntax (see
 * <a href="http://www.hpl.hp.com/techreports/2004/HPL-2004-56">TriX
 * specification</a>) to the Jena framework. Does not support
 * TriX's named graph features. It adds all statements from
 * the first graph to a Jena model, ignoring its name if present,
 * and ignoring all further graphs if present.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class JenaRDFReaderWithExtensions implements RDFReader, ParserCallback {
	private Model targetModel;
	private boolean done = false;
	private Resource subject;
	private Property predicate;
	private RDFNode object;

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFReader#read(com.hp.hpl.jena.rdf.model.Model, java.io.Reader, java.lang.String)
	 */
	public void read(Model model, Reader r, String base) {
		this.targetModel = model;
		try {
			new TriXParserWithExtensions().parse(r, new URI(base), this);
		} catch (IOException e) {
			throw new JenaException(e);
		} catch (SAXException e) {
			throw new JenaException(e);
		} catch (URISyntaxException e) {
			throw new JenaException(e);
		} catch (TransformerException e) {
			throw new JenaException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFReader#read(com.hp.hpl.jena.rdf.model.Model, java.io.InputStream, java.lang.String)
	 */
	public void read(Model model, InputStream r, String base) {
		this.targetModel = model;
		try {
			new TriXParserWithExtensions().parse(r, new URI(base), this);
		} catch (IOException e) {
			throw new JenaException(e);
		} catch (SAXException e) {
			throw new JenaException(e);
		} catch (URISyntaxException e) {
			throw new JenaException(e);
		} catch (TransformerException e) {
			throw new JenaException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFReader#read(com.hp.hpl.jena.rdf.model.Model, java.lang.String)
	 */
	public void read(Model model, String url) {
		try {
			URLConnection conn = new URL(url).openConnection();
			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				read(model, conn.getInputStream(), url);
			} else {
				read(model, new InputStreamReader(conn.getInputStream(), encoding), url);
			}
		} catch (IOException e) {
			throw new JenaException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFReader#setProperty(java.lang.String, java.lang.Object)
	 */
	public Object setProperty(String propName, Object propValue) {
		// TODO: Figure out what RDFReader.setProperty is good for
		return null;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFReader#setErrorHandler(com.hp.hpl.jena.rdf.model.RDFErrorHandler)
	 */
	public RDFErrorHandler setErrorHandler(RDFErrorHandler errHandler) {
		// TODO: Figure out what RDFReader.setErrorHandler is good for
		return null;
	}

	public void startGraph(List uris) {
		// ignore graph names
	}

	public void endGraph() {
		this.done = true;	// ignore all further graphs
	}

	public void subjectURI(String uri) {
		this.subject = this.targetModel.createResource(uri);
	}

	public void subjectBNode(String id) {
		this.subject = this.targetModel.createResource(new AnonId(id));
	}

	public void subjectPlainLiteral(String value, String lang) {
		throw new JenaException("Literals are not allowed as subjects in RDF");
	}

	public void subjectTypedLiteral(String value, String datatypeURI) {
		throw new JenaException("Literals are not allowed as subjects in RDF");
	}

	public void predicate(String uri) {
		this.predicate = this.targetModel.createProperty(uri);
	}

	public void objectURI(String uri) {
		this.object = this.targetModel.createResource(uri);
		addStatement();
	}

	public void objectBNode(String id) {
		this.object = this.targetModel.createResource(new AnonId(id));
		addStatement();
	}

	public void objectPlainLiteral(String value, String lang) {
		this.object = this.targetModel.createLiteral(value, lang);
		addStatement();
	}

	public void objectTypedLiteral(String value, String datatypeURI) {
		this.object = this.targetModel.createTypedLiteral(value, datatypeURI);
		addStatement();
	}
	
	private void addStatement() {
		if (this.done) {
			return;
		}
		this.targetModel.add(this.targetModel.createStatement(
				this.subject, this.predicate, this.object));
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
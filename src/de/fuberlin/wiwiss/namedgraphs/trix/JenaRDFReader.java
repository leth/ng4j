/*
 * $Id: JenaRDFReader.java,v 1.1 2004/09/13 14:37:27 cyganiak Exp $
 */
package de.fuberlin.wiwiss.namedgraphs.trix;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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
 * all graphs to a Jena model, ignoring graph names and boundaries.
 *
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class JenaRDFReader implements RDFReader, ParserCallback {
	private Model targetModel;
	private List statements = new ArrayList();
	private Resource subject;
	private Property predicate;
	private RDFNode object;

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFReader#read(com.hp.hpl.jena.rdf.model.Model, java.io.Reader, java.lang.String)
	 */
	public void read(Model model, Reader r, String base) {
		this.targetModel = model;
		try {
			new TriXParser().parse(r, new URI(base), this);
			addTriplesToModel();
		} catch (IOException e) {
			throw new JenaException(e);
		} catch (SAXException e) {
			throw new JenaException(e);
		} catch (URISyntaxException e) {
			throw new JenaException(e);
		}
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.RDFReader#read(com.hp.hpl.jena.rdf.model.Model, java.io.InputStream, java.lang.String)
	 */
	public void read(Model model, InputStream r, String base) {
		this.targetModel = model;
		try {
			new TriXParser().parse(r, new URI(base), this);
			addTriplesToModel();
		} catch (IOException e) {
			throw new JenaException(e);
		} catch (SAXException e) {
			throw new JenaException(e);
		} catch (URISyntaxException e) {
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

	private void addTriplesToModel() {
		this.targetModel.add(this.statements);
	}

	public void startGraph(List uris) {
		// ignore individual graphs
	}

	public void endGraph() {
		// ignore individual graphs
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
		this.statements.add(this.targetModel.createStatement(
				this.subject, this.predicate, this.object));
	}
}
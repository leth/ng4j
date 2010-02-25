// $Header: /cvsroot/ng4j/ng4j/src/de/fuberlin/wiwiss/ng4j/SingleNamedGraphModel.java,v 1.3 2010/02/25 14:28:21 hartig Exp $

package de.fuberlin.wiwiss.ng4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;

import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;
import de.fuberlin.wiwiss.ng4j.impl.SingleNamedGraphResourceImpl;
import de.fuberlin.wiwiss.ng4j.impl.SingleNamedGraphStatementIterator;
import de.fuberlin.wiwiss.ng4j.trig.TriGWriter;
import de.fuberlin.wiwiss.ng4j.trix.TriXWriter;

/** Model that wraps a single NamedGraph. <p>
 * 
 * "Modelled" after NamedGraphModel but deals with a single NamedGraph
 * rather than a NamedGraphSet.
 * 
 * @author Jennifer Cormier, Architecture Technology Corporation
 */
public class SingleNamedGraphModel extends ModelCom implements Model {
// TODO Share these language constants with NamedGraphModel
	public static final String DEFAULT_READ_LANGUAGE = "RDF/XML";
	public static final String TRIX_LANGUAGE = "TRIX";
	public static final String TRIG_LANGUAGE = "TRIG";
	
	/**
	 */
	public SingleNamedGraphModel(NamedGraph namedGraph) {
		super(namedGraph);
		// The namedGraph is stored as "graph" in a parent class
	}

	/** Returns a TriGWriter object, primed with the namespaces of this Model.
	 * 
	 * @return the TriGWriter, primed with the namespaces of this Model.
	 */
	public TriGWriter getTriGWriter() {
		TriGWriter writer = new TriGWriter();
		Map<String,String> map = this.getNsPrefixMap();
		for ( Map.Entry<String, String> mapEntry : map.entrySet() ) {
			writer.addNamespace(mapEntry.getKey(), mapEntry.getValue());
		}
		return writer;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#createStatement(com.hp.hpl.jena.rdf.model.Resource, com.hp.hpl.jena.rdf.model.Property, com.hp.hpl.jena.rdf.model.RDFNode)
	 */
	@Override
	public SingleNamedGraphStatement createStatement(Resource r, Property p, RDFNode o) {
		return new SingleNamedGraphStatement(r, p, o, this);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#listStatements()
	 */
	@Override
	public StmtIterator listStatements() {
		return new SingleNamedGraphStatementIterator(super.listStatements(), this);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#listStatements(com.hp.hpl.jena.rdf.model.Selector)
	 */
	@Override
	public StmtIterator listStatements(Selector selector) {
		return new SingleNamedGraphStatementIterator(super.listStatements(selector), this);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#read(java.io.InputStream, java.lang.String, java.lang.String)
	 */
	@Override
	public Model read(InputStream reader, String base, String lang) {
		this.graph = readNamedGraph(reader, base, lang);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#read(java.io.InputStream, java.lang.String)
	 */
	@Override
	public Model read(InputStream reader, String base) {
		return read(reader, base, DEFAULT_READ_LANGUAGE);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#read(java.io.Reader, java.lang.String, java.lang.String)
	 */
	@Override
	public Model read(Reader reader, String base, String lang) {
		this.graph = readNamedGraph(reader, base, lang);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#read(java.io.Reader, java.lang.String)
	 */
	@Override
	public Model read(Reader reader, String base) {
		return read(reader, base, DEFAULT_READ_LANGUAGE);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#read(java.lang.String, java.lang.String)
	 */
	@Override
	public Model read(String url, String lang) {
		this.graph = readNamedGraph(url, lang);
		return this;
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#read(java.lang.String)
	 */
	@Override
	public Model read(String url) {
		return read(url, DEFAULT_READ_LANGUAGE);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.OutputStream, java.lang.String, java.lang.String)
	 */
	@Override
	public Model write(OutputStream writer, String lang, String base) {
		NamedGraphSetImpl graphSet = new NamedGraphSetImpl();
		graphSet.addGraph((NamedGraph)graph);
		if (TRIX_LANGUAGE.equals(lang)) {
			new TriXWriter().write(graphSet, writer, base);
			return this;
		}
		if (TRIG_LANGUAGE.equals(lang)) {
			getTriGWriter().write(graphSet, writer, base);
			return this;
		}
		return super.write(writer, lang, base);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.OutputStream, java.lang.String)
	 */
	@Override
	public Model write(OutputStream writer, String lang) {
		NamedGraphSetImpl graphSet = new NamedGraphSetImpl();
		graphSet.addGraph((NamedGraph)graph);
		if (TRIX_LANGUAGE.equals(lang)) {
			new TriXWriter().write(graphSet, writer, null);
			return this;
		}
		if (TRIG_LANGUAGE.equals(lang)) {
			getTriGWriter().write(graphSet, writer, null);
			return this;
		}
		return super.write(writer, lang);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.Writer, java.lang.String, java.lang.String)
	 */
	@Override
	public Model write(Writer writer, String lang, String base) {
		NamedGraphSetImpl graphSet = new NamedGraphSetImpl();
		graphSet.addGraph((NamedGraph)graph);
		if (TRIX_LANGUAGE.equals(lang)) {
			new TriXWriter().write(graphSet, writer, base);
			return this;
		}
		if (TRIG_LANGUAGE.equals(lang)) {
			getTriGWriter().write(graphSet, writer, base);
			return this;
		}
		return super.write(writer, lang, base);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.Writer, java.lang.String)
	 */
	@Override
	public Model write(Writer writer, String lang) {
		NamedGraphSetImpl graphSet = new NamedGraphSetImpl();
		graphSet.addGraph((NamedGraph)graph);
		if (TRIX_LANGUAGE.equals(lang)) {
			new TriXWriter().write(graphSet, writer, null);
			return this;
		}
		if (TRIG_LANGUAGE.equals(lang)) {
			getTriGWriter().write(graphSet, writer, null);
			return this;
		}
		return super.write(writer, lang);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#getResource(java.lang.String, com.hp.hpl.jena.rdf.model.ResourceF)
	 */
	@Override
	public Resource getResource(String uri, ResourceF f) {
        return new SingleNamedGraphResourceImpl(super.getResource(uri, f),this);
		
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#getResource(java.lang.String)
	 */
	@Override
	public Resource getResource(String uri) {
		return new SingleNamedGraphResourceImpl(super.getResource(uri), this);
	}


	/** Utility method that checks that the given named graph set contains exactly 1 graph.
	 * 
	 * @param graphSet NamedGraphSet to check.
	 * @return the single named graph that it contains.
	 * @throws RuntimeException if the set contains no graphs or more than 1 graph.
	 */
	public static NamedGraph verifySingleGraph(NamedGraphSet graphSet) {
		if ( graphSet.countGraphs() > 1 ) {
			throw new RuntimeException("Set contains more than 1 named graph.");
		}
		if ( graphSet.isEmpty() ) {
			throw new RuntimeException("Set contains no named graphs.");
		}
		return graphSet.listGraphs().next();
	}

	/** Utility method that reads in the named graph from the given input stream.
	 * 
	 * @param reader The source input stream.
	 * @param base A base URI which is used to resolve relative URI's in the document.
	 * @param lang The language of the source.
	 */
	public static NamedGraph readNamedGraph(InputStream reader, String base, String lang) {
		NamedGraphSetImpl graphSet = new NamedGraphSetImpl();
		GraphReaderService service = new GraphReaderService();
		service.setSourceInputStream(reader, base);
		service.setLanguage(lang);
		service.readInto(graphSet);
		NamedGraph graph = verifySingleGraph(graphSet);
		return graph;
	}

	/** Utility method that reads in the named graph from the given reader.
	 * 
	 * @param reader The source reader.
	 * @param base A base URI which is used to resolve relative URI's in the document.
	 * @param lang The language of the source.
	 */
	public static NamedGraph readNamedGraph(Reader reader, String base, String lang) {
		NamedGraphSetImpl graphSet = new NamedGraphSetImpl();
		GraphReaderService service = new GraphReaderService();
		service.setSourceReader(reader, base);
		service.setLanguage(lang);
		service.readInto(graphSet);
		NamedGraph graph = verifySingleGraph(graphSet);
		return graph;
	}

	/** Utility method that reads in the named graph from the given URL location.
	 * 
	 * @param url The source URL from which to read.
	 * @param lang The language of the source.
	 * @return the named graph read from the provided url.
	 */
	public static NamedGraph readNamedGraph(String url, String lang) {
		NamedGraphSetImpl graphSet = new NamedGraphSetImpl();
		GraphReaderService service = new GraphReaderService();
		service.setSourceURL(url);
		service.setLanguage(lang);
		service.readInto(graphSet);
		NamedGraph graph = verifySingleGraph(graphSet);
		return graph;
	}

	/*
	 *  (c)   Copyright 2009 - 2010 Christian Bizer (chris@bizer.de)
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
}

/*
 * $Id: TriXReader.java,v 1.7 2010/02/10 09:20:14 timp Exp $
 */
package de.fuberlin.wiwiss.ng4j.trix;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.rdf.model.impl.LiteralImpl;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.NamedGraphSetReader;

/**
 * Reads TriX files (see
 * <a href="http://www.hpl.hp.com/techreports/2004/HPL-2004-56">TriX
 * specification</a>) into {@link NamedGraphSet}s.
 * <p>
 * Ignores additional graph names if there are more than one name
 * per graph. Doesn't allow the same name for multiple graphs.
 * Graphs without name get assigned a default name given by the caller.
 * <p>
 * TODO: Discuss if above cases are really wanted for TriX and move them up into the SAXHandler
 * 
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class TriXReader implements ParserCallback, NamedGraphSetReader {
	private NamedGraphSet set;
	private Node defaultGraph;
	private NamedGraph currentGraph;
	private Node subject;
	private Node predicate;
	private Node object;
	private Set<Node> pastGraphNames = new HashSet<Node>();

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSetReader#read(de.fuberlin.wiwiss.ng4j.NamedGraphSet, java.io.Reader, java.lang.String, java.lang.String)
	 */
	public void read(NamedGraphSet namedGraphSet, Reader source,
			String baseURI, String defaultGraphName) {
		this.set = namedGraphSet;
		this.defaultGraph = Node.createURI(defaultGraphName);
		try {
			new TriXParser().parse(source, new URI(baseURI), this);
		} catch (IOException e) {
			throw new JenaException(e);
		} catch (SAXException e) {
			throw new JenaException(e);
		} catch (URISyntaxException e) {
			throw new JenaException(e);
		}
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSetReader#read(de.fuberlin.wiwiss.ng4j.NamedGraphSet, java.io.InputStream, java.lang.String, java.lang.String)
	 */
	public void read(NamedGraphSet namedGraphSet, InputStream source,
			String baseURI, String defaultGraphName) {
		this.set = namedGraphSet;
		this.defaultGraph = Node.createURI(defaultGraphName);
		try {
			new TriXParser().parse(source, new URI(baseURI), this);
		} catch (IOException e) {
			throw new JenaException(e);
		} catch (SAXException e) {
			throw new JenaException(e);
		} catch (URISyntaxException e) {
			throw new JenaException(e);
		}
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#startGraph(java.util.List)
	 */
	public void startGraph(List<String> uris) {
		Node graphName = uris.isEmpty() ?
				this.defaultGraph :
				Node.createURI(uris.get(0));
		if (this.pastGraphNames.contains(graphName)) {
			throw new JenaException("Multiple graphs with same name: " + graphName);
		}
		this.currentGraph = this.set.createGraph(graphName);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#endGraph()
	 */
	public void endGraph() {
		// don't have to do anything
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#subjectURI(java.lang.String)
	 */
	public void subjectURI(String uri) {
		this.subject = Node.createURI(uri);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#subjectBNode(java.lang.String)
	 */
	public void subjectBNode(String id) {
		this.subject = Node.createAnon(new AnonId(id));
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#subjectPlainLiteral(java.lang.String, java.lang.String)
	 */
	public void subjectPlainLiteral(String value, String lang) {
		throw new JenaException("Literals are not allowed as subjects in RDF");
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#subjectTypedLiteral(java.lang.String, java.lang.String)
	 */
	public void subjectTypedLiteral(String value, String datatypeURI) {
		throw new JenaException("Literals are not allowed as subjects in RDF");
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#predicate(java.lang.String)
	 */
	public void predicate(String uri) {
		this.predicate = Node.createURI(uri);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#objectURI(java.lang.String)
	 */
	public void objectURI(String uri) {
		this.object = Node.createURI(uri);
		addTriple();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#objectBNode(java.lang.String)
	 */
	public void objectBNode(String id) {
		this.object = Node.createAnon(new AnonId(id));
		addTriple();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#objectPlainLiteral(java.lang.String, java.lang.String)
	 */
	public void objectPlainLiteral(String value, String lang) {
		LiteralLabel ll = LiteralLabelFactory.create(value, lang);
        this.object =  Node.createLiteral(ll);
		addTriple();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.trix.ParserCallback#objectTypedLiteral(java.lang.String, java.lang.String)
	 */
	public void objectTypedLiteral(String value, String datatypeURI) {
		// No idea what that line does, is copy&paste from ModelCom.createTypedLiteral
		RDFDatatype dt = TypeMapper.getInstance().getSafeTypeByName(datatypeURI);
		LiteralLabel ll = LiteralLabelFactory.createLiteralLabel( value, "", dt );
        this.object =  Node.createLiteral(ll);
		addTriple();
	}
	
	private void addTriple() {
		this.currentGraph.add(new Triple(this.subject, this.predicate, this.object));
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
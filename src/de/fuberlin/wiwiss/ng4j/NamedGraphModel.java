// $Id: NamedGraphModel.java,v 1.9 2009/05/27 14:33:22 jenpc Exp $
package de.fuberlin.wiwiss.ng4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import com.hp.hpl.jena.graph.Node;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ResourceF;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Selector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.rdf.model.impl.ModelCom;

import de.fuberlin.wiwiss.ng4j.impl.GraphReaderService;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphResourceImpl;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphStatementIterator;
import de.fuberlin.wiwiss.ng4j.trig.TriGWriter;
import de.fuberlin.wiwiss.ng4j.trix.TriXWriter;

/**
 * Jena {@link Model} implementation providing a resource-centric view on
 * a {@link NamedGraphSet}'s union graph. It is backed by the NamedGraphSet.
 * Changes to one are reflected by the other.
 * <p>
 * All Statements returned by the NamedGraphModel can be casted to
 * {@link NamedGraphStatement}s. They provide information about which
 * NamedGraphs they are contained in.
 * <p>
 * All Statements that are added to the model are added to a distinguished
 * default NamedGraph. Removing Statements deletes them from all
 * NamedGraphs in the backing set.
 * <p>
 * All flavours of the <tt>read</tt> and <tt>write</tt> methods support
 * the TriX format which keeps the association of statements and their
 * graph names.
 * <p>
 * All <tt>read</tt> operations on other RDF files will load
 * the statements into a graph named with the file's URI, replacing
 * older statements from the same source. This can be
 * very handy for data management.
 * <p>
 * Methods that return new Models, like {@link Model#query} and
 * {@link Model#union}, will not return new NamedGraphModels but
 * normal Jena ModelMems. (This is because Jena makes it hard to
 * return anything but ModelMems without much copy & paste.)
 * <p>
 * <strong>Note:</strong> This class was implemented without considering
 * reification. It still contains reification methods inherited from
 * Jena, but those do probably not work.
 * <p>
 * TODO: Properly test read and write methods
 *  
 * @author Chris Bizer
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class NamedGraphModel extends ModelCom implements Model {
	private String baseGraphName;
	private NamedGraphSet graphSet;
	
	public static final String DEFAULT_READ_LANGUAGE = "RDF/XML";
	public static final String TRIX_LANGUAGE = "TRIX";
	public static final String TRIG_LANGUAGE = "TRIG";
	
	/**
	 * Initialises a NamedGraphModel.
	 * @param graphSet A NamedGraphSet to back the model
	 * @param defaultGraphForAdding a default graph name to be used when
	 * 		statements are added to the model
	 */
	public NamedGraphModel(NamedGraphSet graphSet, String defaultGraphForAdding) { 
		super(graphSet.asJenaGraph(Node.createURI(defaultGraphForAdding)));
		this.baseGraphName = defaultGraphForAdding;
		this.graphSet = graphSet;
	}

	/**
	 * Returns the NamedGraphSet backing this model.
	 * @return The NamedGraphSet on which the model is based.
	 */
	public NamedGraphSet getNamedGraphSet() {
		return this.graphSet;
	}
	
	/**
	 * Returns a TriGWriter object, primed with the namespaces of this Model
	 * @return the TriGWriter
	 */
	public TriGWriter getTriGWriter() {
		TriGWriter writer = new TriGWriter();
		Map<String,String> map = this.getNsPrefixMap();
		for ( Map.Entry<String, String> mapEntry : map.entrySet() ) {
			writer.addNamespace(mapEntry.getKey(), mapEntry.getValue());
		}
		return writer;
	}
	
	/**
	 * Returns the name of the default graph (which new statements are
	 * added to).
	 * @return The name of the default NamedGraph
	 */
	public Resource getDefaultGraphName() {
		return createResource(this.baseGraphName);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#createStatement(com.hp.hpl.jena.rdf.model.Resource, com.hp.hpl.jena.rdf.model.Property, com.hp.hpl.jena.rdf.model.RDFNode)
	 */
	@Override
	public Statement createStatement(Resource s, Property p, RDFNode o) {
		return new NamedGraphStatement(s, p, o, this);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#listStatements()
	 */
	@Override
	public StmtIterator listStatements() {
		return new NamedGraphStatementIterator(super.listStatements(), this);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#listStatements(com.hp.hpl.jena.rdf.model.Selector)
	 */
	@Override
	public StmtIterator listStatements(Selector selector) {
		return convertStatementsToNamedGraphStatements(
				super.listStatements(selector));
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#read(java.io.InputStream, java.lang.String, java.lang.String)
	 */
	@Override
	public Model read(InputStream reader, String base, String lang) {
		GraphReaderService service = new GraphReaderService();
		service.setSourceInputStream(reader, base);
		service.setLanguage(lang);
		service.readInto(this.graphSet);
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
		GraphReaderService service = new GraphReaderService();
		service.setSourceReader(reader, base);
		service.setLanguage(lang);
		service.readInto(this.graphSet);
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
		GraphReaderService service = new GraphReaderService();
		service.setSourceURL(url);
		service.setLanguage(lang);
		service.readInto(this.graphSet);
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
		if (TRIX_LANGUAGE.equals(lang)) {
			new TriXWriter().write(this.graphSet, writer, base);
			return this;
		}
		if (TRIG_LANGUAGE.equals(lang)) {
			getTriGWriter().write(this.graphSet, writer, base);
			return this;
		}
		return super.write(writer, lang, base);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.OutputStream, java.lang.String)
	 */
	@Override
	public Model write(OutputStream writer, String lang) {
		if (TRIX_LANGUAGE.equals(lang)) {
			new TriXWriter().write(this.graphSet, writer, null);
			return this;
		}
		if (TRIG_LANGUAGE.equals(lang)) {
			getTriGWriter().write(this.graphSet, writer, null);
			return this;
		}
		return super.write(writer, lang);
	}

// Commenting out because its implementation is no different from that in the parent class
//	/* (non-Javadoc)
//	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.OutputStream)
//	 */
//	public Model write(OutputStream writer) {
//		return super.write(writer);
//	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.Writer, java.lang.String, java.lang.String)
	 */
	@Override
	public Model write(Writer writer, String lang, String base) {
		if (TRIX_LANGUAGE.equals(lang)) {
			new TriXWriter().write(this.graphSet, writer, base);
			return this;
		}
		if (TRIG_LANGUAGE.equals(lang)) {
			getTriGWriter().write(this.graphSet, writer, base);
			return this;
		}
		return super.write(writer, lang, base);
	}

	/* (non-Javadoc)
	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.Writer, java.lang.String)
	 */
	@Override
	public Model write(Writer writer, String lang) {
		if (TRIX_LANGUAGE.equals(lang)) {
			new TriXWriter().write(this.graphSet, writer, null);
			return this;
		}
		if (TRIG_LANGUAGE.equals(lang)) {
			getTriGWriter().write(this.graphSet, writer, null);
			return this;
		}
		return super.write(writer, lang);
	}

// Commenting out because its implementation is no different from that in the parent class
//	/* (non-Javadoc)
//	 * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#write(java.io.Writer)
//	 */
//	public Model write(Writer writer) {
//		return super.write(writer);
//	}

	private StmtIterator convertStatementsToNamedGraphStatements(StmtIterator it) {
		return new NamedGraphStatementIterator(it, this);
	}

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#getResource(java.lang.String)
     */
	@Override
    public Resource getResource(String uri) {
        return new NamedGraphResourceImpl(super.getResource(uri),this);
    }

    /* (non-Javadoc)
     * @see com.hp.hpl.jena.rdf.model.impl.ModelCom#getResource(java.lang.String, com.hp.hpl.jena.rdf.model.ResourceF)
     */
	@Override
    public Resource getResource(String uri, ResourceF f) {
        return new NamedGraphResourceImpl(super.getResource(uri, f),this);
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
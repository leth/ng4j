/*
 * $Id: NamedGraphSetReader.java,v 1.1 2004/09/13 14:37:28 cyganiak Exp $
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.TypeMapper;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.impl.LiteralLabel;
import com.hp.hpl.jena.rdf.model.AnonId;
import com.hp.hpl.jena.shared.JenaException;

import de.fuberlin.wiwiss.namedgraphs.NamedGraph;
import de.fuberlin.wiwiss.namedgraphs.NamedGraphSet;

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
public class NamedGraphSetReader implements ParserCallback {
	private NamedGraphSet set;
	private Node defaultGraph;
	private NamedGraph currentGraph;
	private Node subject;
	private Node predicate;
	private Node object;
	private Set pastGraphNames = new HashSet();

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

	public void read(NamedGraphSet namedGraphSet, String url,
			String defaultGraphName) {
		try {
			URLConnection conn = new URL(url).openConnection();
			String encoding = conn.getContentEncoding();
			if (encoding == null) {
				read(namedGraphSet, conn.getInputStream(), url,
						defaultGraphName);
			} else {
				read(namedGraphSet, new InputStreamReader(conn.getInputStream(), encoding), url, defaultGraphName);
			}
		} catch (IOException e) {
			throw new JenaException(e);
		}
	}

	public void startGraph(List uris) {
		Node graphName = uris.isEmpty() ?
				this.defaultGraph :
				Node.createURI((String) uris.get(0));
		if (this.pastGraphNames.contains(graphName)) {
			throw new JenaException("Multiple graphs with same name: " + graphName);
		}
		this.currentGraph = this.set.createGraph(graphName);
	}

	public void endGraph() {
		// don't have to do anything
	}

	public void subjectURI(String uri) {
		this.subject = Node.createURI(uri);
	}

	public void subjectBNode(String id) {
		this.subject = Node.createAnon(new AnonId(id));
	}

	public void subjectPlainLiteral(String value, String lang) {
		throw new JenaException("Literals are not allowed as subjects in RDF");
	}

	public void subjectTypedLiteral(String value, String datatypeURI) {
		throw new JenaException("Literals are not allowed as subjects in RDF");
	}

	public void predicate(String uri) {
		this.predicate = Node.createURI(uri);
	}

	public void objectURI(String uri) {
		this.object = Node.createURI(uri);
		addTriple();
	}

	public void objectBNode(String id) {
		this.object = Node.createAnon(new AnonId(id));
		addTriple();
	}

	public void objectPlainLiteral(String value, String lang) {
		this.object = Node.createLiteral(new LiteralLabel(value, lang));
		addTriple();
	}

	public void objectTypedLiteral(String value, String datatypeURI) {
		// No idea what that line does, is copy&paste from ModelCom.createTypedLiteral
		RDFDatatype dt = TypeMapper.getInstance().getSafeTypeByName(datatypeURI);
		this.object = Node.createLiteral(new LiteralLabel(value, null, dt));
		addTriple();
	}
	
	private void addTriple() {
		this.currentGraph.add(new Triple(this.subject, this.predicate, this.object));
	}
}
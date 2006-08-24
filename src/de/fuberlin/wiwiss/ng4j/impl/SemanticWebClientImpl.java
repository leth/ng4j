/*
 * Created on 23.08.2006
 * 
 */
package de.fuberlin.wiwiss.ng4j.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.TripleMatch;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.SemanticWebClient;

public class SemanticWebClientImpl implements SemanticWebClient {
	private NamedGraphSet ngs;
	private List derefURIs;
	private List toDerefURIs;
	
	public SemanticWebClientImpl() {
		this.ngs = new NamedGraphSetImpl();
		this.derefURIs.clear();
		this.toDerefURIs.clear();
	}

	public Iterator find(TripleMatch pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	public void addRemoteGraph(String URI) {
		// TODO Auto-generated method stub
	}

	public void reloadRemoteGraph(String URI) {
		// TODO Auto-generated method stub

	}

	public void setConfig(String option, Object value) {
		// TODO Auto-generated method stub

	}

	public void getConfig(String option) {
		// TODO Auto-generated method stub

	}

	public Iterator successfullyDereferencedURIs() {
		// TODO Auto-generated method stub
		return null;
	}

	public Iterator unsuccessfullyDereferencedURIs() {
		// TODO Auto-generated method stub
		return null;
	}
//------------------------------------------------------
	public void addGraph(NamedGraph graph) {
		this.ngs.addGraph(graph);

	}

	public void removeGraph(Node graphName) {
		this.ngs.removeGraph(graphName);
	}

	public void removeGraph(String graphNameURI) {
		this.ngs.removeGraph(graphNameURI);

	}

	public boolean containsGraph(Node graphName) {
		return this.ngs.containsGraph(graphName);
	}

	public boolean containsGraph(String graphNameURI) {
		return this.ngs.containsGraph(graphNameURI);
	}

	public NamedGraph getGraph(Node graphName) {
		return this.ngs.getGraph(graphName);
	}

	public NamedGraph getGraph(String graphNameURI) {
		return this.ngs.getGraph(graphNameURI);
	}

	public NamedGraph createGraph(Node graphName) {
		return this.ngs.createGraph(graphName);
	}

	public NamedGraph createGraph(String graphNameURI) {
		return this.ngs.createGraph(graphNameURI);
	}

	public Iterator listGraphs() {
		return this.ngs.listGraphs();
	}

	public void clear() {
		this.ngs.clear();
	}

	public long countGraphs() {
		return this.ngs.countGraphs();
	}

	public boolean isEmpty() {
		return this.ngs.isEmpty();
	}

	public void addQuad(Quad quad) {
		this.ngs.addQuad(quad);
	}

	public boolean containsQuad(Quad pattern) {
		return this.ngs.containsQuad(pattern);
	}

	public void removeQuad(Quad pattern) {
		this.ngs.removeQuad(pattern);
	}

	public int countQuads() {
		return this.ngs.countQuads();
	}

	public Iterator findQuads(Quad pattern) {
		return this.ngs.findQuads(pattern);
	}

	public Iterator findQuads(Node graphName, Node subject, Node predicate,
			Node object) {
		return this.ngs.findQuads(graphName, subject, predicate, object);
	}

	public Graph asJenaGraph(Node defaultGraphForAdding) {
		return this.ngs.asJenaGraph(defaultGraphForAdding);
	}

	public NamedGraphModel asJenaModel(String defaultGraphForAdding) {
		return this.ngs.asJenaModel(defaultGraphForAdding);
	}

	public void close() {
		this.ngs.close();
		this.derefURIs.clear();
		this.toDerefURIs.clear();
	}

	public void read(String url, String lang) {
		this.ngs.read(url,lang);
	}

	public void read(InputStream source, String lang, String baseURI) {
		this.ngs.read(source,lang,baseURI);
	}

	public void read(Reader source, String lang, String baseURI) {
		this.ngs.read(source,lang,baseURI);

	}

	public void write(OutputStream out, String lang, String baseURI) {
		this.ngs.write(out,lang,baseURI);
	}

	public void write(Writer out, String lang, String baseURI) {
		this.ngs.write(out,lang,baseURI);
	}
}

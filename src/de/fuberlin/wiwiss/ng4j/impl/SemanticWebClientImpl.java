/*
 * Created on 23.08.2006
 * 
 */
package de.fuberlin.wiwiss.ng4j.impl;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphModel;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.SemanticWebClient;

public class SemanticWebClientImpl implements SemanticWebClient {
	private NamedGraphSet ngs;
	private UriList derefURIs;
	private UriList toDerefURIs;
	private URIRetriever retriever;
	
	//public int threadcounter = 0;
	
	public SemanticWebClientImpl() {
		this.ngs = new NamedGraphSetImpl();
		this.ngs.createGraph("http://localhost/provenanceInformation");
		
		this.retriever = new URIRetriever(this);
		this.derefURIs = new UriList(); 
		this.toDerefURIs = new UriList(); 
		this.derefURIs.addListListener(this.retriever);
		this.toDerefURIs.addListListener(this.retriever);
	}

	public Iterator find(TripleMatch pattern) {
		// TODO Auto-generated method stub
		Triple t =pattern.asTriple();
		Node sub  = t.getSubject();
		Node pred = t.getPredicate();
		Node obj  = t.getObject();
		
		
	//	Iterator iter = this.ngs.findQuads(Node.ANY,sub,pred,obj);
	
		if(sub.isURI()){
			if(!this.toDerefURIs.contains(sub.getURI())&&!this.derefURIs.contains(sub.getURI()))
			this.toDerefURIs.add(sub.getURI());
		}
		if(pred.isURI()){
			if(!this.toDerefURIs.contains(pred.getURI())&&!this.derefURIs.contains(pred.getURI()))
			this.toDerefURIs.add(pred.getURI());		
		}
		if(obj.isURI()){
			if(!this.toDerefURIs.contains(obj.getURI())&&!this.derefURIs.contains(obj.getURI()))
			this.toDerefURIs.add(obj.getURI());
		}
		return this.ngs.findQuads(Node.ANY,sub,pred,obj);
	}

	public void addRemoteGraph(String URI) {
		this.derefURIs.add(URI);
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
		return this.derefURIs.iterator();
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
	
	public List getUrisToRetrieve(){
		return this.toDerefURIs;
	}
	public List getRetrievedUris(){
		return this.derefURIs;
	}
	public NamedGraphSet getNamedGraphSet(){
		return this.ngs;
	}
}

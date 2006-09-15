/*
 * Created on 23.08.2006
 * 
 */
package de.fuberlin.wiwiss.ng4j.semwebclient;

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
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

public class SemanticWebClientImpl implements SemanticWebClient {
	private NamedGraphSet ngs;
	private List retrievedUris;
	private UriList urisToRetrieve;
	private URIRetriever retriever;
	private FindListener listener = null;
	private List unretrievedURIs;
	public boolean retrievalFinished;
	
	/**
	 * Constructor for the SemantciWebClient.
	 */
	public SemanticWebClientImpl() {
		this.ngs = new NamedGraphSetImpl();
		this.createGraph("http://localhost/provenanceInformation");
		this.retriever       = new URIRetriever(this);
		this.retrievedUris   = Collections.synchronizedList(new ArrayList());
		this.unretrievedURIs = Collections.synchronizedList(new ArrayList());
		this.urisToRetrieve  = new UriList(); 
		this.urisToRetrieve.addListListener(this.retriever);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#find(com.hp.hpl.jena.graph.TripleMatch)
	 */
	public Iterator find(TripleMatch pattern) {
		this.retrievalFinished = false;
		Triple t =pattern.asTriple();
		
		Node sub  = t.getSubject();
		Node pred = t.getPredicate();
		Node obj  = t.getObject();
		
		SemWebIterator iter2 = new SemWebIterator(this,sub,pred,obj);
		Iterator iter = this.findQuads(Node.ANY,sub,pred,obj);

		this.inspectTriple(t,-1);
		
		while(iter.hasNext()){
			Quad quad = (Quad) iter.next();
			Triple tr = quad.getTriple();
			this.inspectTriple(tr,-1);
		}
		return iter2;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#addRemoteGraph(java.lang.String)
	 */
	public void addRemoteGraph(String URI) {
		this.urisToRetrieve.add(URI,-1);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#reloadRemoteGraph(java.lang.String)
	 */
	public void reloadRemoteGraph(String URI) {
		if(this.retrievedUris.contains(URI)){
			this.retrievedUris.remove(URI);
			this.urisToRetrieve.add(URI);
		}
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#setConfig(java.lang.String, java.lang.Object)
	 */
	public void setConfig(String option, ConfigValue value) {
		if(option.equals("maxsteps"))
			this.retriever.setMaxsteps(value.steps);
		if(option.equals("maxthreads"))
			this.retriever.setMaxsteps(value.threads);
		if(option.equals("timeout"))
			this.retriever.setTimeout(value.timeout);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#getConfig(java.lang.String)
	 */
	public ConfigValue getConfig(String option) {
		ConfigValue value = new ConfigValue();
		
		if(option.toLowerCase().equals("maxsteps"))
			value.steps = this.retriever.getMaxsteps();
		if(option.toLowerCase().equals("maxthreads"))
			value.threads = this.retriever.getMaxthreads();
		if(option.toLowerCase().equals("timeout"))
			value.timeout = this.retriever.getTimeout();
		
		return value;

	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#successfullyDereferencedURIs()
	 */
	public Iterator successfullyDereferencedURIs() {
		return this.retrievedUris.iterator();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#unsuccessfullyDereferencedURIs()
	 */
	public Iterator unsuccessfullyDereferencedURIs() {
		return this.unretrievedURIs.iterator();
	}
//------------------------------------------------------
	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#addGraph(de.fuberlin.wiwiss.ng4j.NamedGraph)
	 */
	public void addGraph(NamedGraph graph) {
		this.ngs.addGraph(graph);
		if(this.listener != null){
			this.listener.graphAdded(new GraphAddedEvent(this,graph.getGraphName().getURI()));
		}
		

	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeGraph(com.hp.hpl.jena.graph.Node)
	 */
	public void removeGraph(Node graphName){
		this.ngs.removeGraph(graphName);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeGraph(java.lang.String)
	 */
	public void removeGraph(String graphNameURI) {
		this.ngs.removeGraph(graphNameURI);

	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsGraph(com.hp.hpl.jena.graph.Node)
	 */
	public boolean containsGraph(Node graphName) {
		return this.ngs.containsGraph(graphName);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsGraph(java.lang.String)
	 */
	public boolean containsGraph(String graphNameURI) {
		return this.ngs.containsGraph(graphNameURI);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#getGraph(com.hp.hpl.jena.graph.Node)
	 */
	public NamedGraph getGraph(Node graphName) {
		return this.ngs.getGraph(graphName);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#getGraph(java.lang.String)
	 */
	synchronized public NamedGraph getGraph(String graphNameURI) {
		return this.ngs.getGraph(graphNameURI);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#createGraph(com.hp.hpl.jena.graph.Node)
	 */
	public NamedGraph createGraph(Node graphName) {
		return this.ngs.createGraph(graphName);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#createGraph(java.lang.String)
	 */
	public NamedGraph createGraph(String graphNameURI) {
		return this.ngs.createGraph(graphNameURI);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#listGraphs()
	 */
	public Iterator listGraphs() {
		return this.ngs.listGraphs();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#clear()
	 */
	public void clear() {
		this.ngs.clear();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#countGraphs()
	 */
	public long countGraphs() {
		return this.ngs.countGraphs();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#isEmpty()
	 */
	public boolean isEmpty() {
		return this.ngs.isEmpty();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#addQuad(de.fuberlin.wiwiss.ng4j.Quad)
	 */
	public void addQuad(Quad quad) {
		this.ngs.addQuad(quad);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#containsQuad(de.fuberlin.wiwiss.ng4j.Quad)
	 */
	public boolean containsQuad(Quad pattern) {
		return this.ngs.containsQuad(pattern);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#removeQuad(de.fuberlin.wiwiss.ng4j.Quad)
	 */
	public void removeQuad(Quad pattern) {
		this.ngs.removeQuad(pattern);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#countQuads()
	 */
	public int countQuads() {
		return this.ngs.countQuads();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#findQuads(de.fuberlin.wiwiss.ng4j.Quad)
	 */
	public Iterator findQuads(Quad pattern) {
		return this.ngs.findQuads(pattern);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#findQuads(com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node, com.hp.hpl.jena.graph.Node)
	 */
	public Iterator findQuads(Node graphName, Node subject, Node predicate,
			Node object) {
		return this.ngs.findQuads(graphName, subject, predicate, object);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#asJenaGraph(com.hp.hpl.jena.graph.Node)
	 */
	public Graph asJenaGraph(Node defaultGraphForAdding) {
		return this.ngs.asJenaGraph(defaultGraphForAdding);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#asJenaModel(java.lang.String)
	 */
	public NamedGraphModel asJenaModel(String defaultGraphForAdding) {
		return this.ngs.asJenaModel(defaultGraphForAdding);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#close()
	 */
	public void close() {
		this.ngs.close();
		this.retrievedUris.clear();
		this.urisToRetrieve.clear();
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#read(java.lang.String, java.lang.String)
	 */
	public void read(String url, String lang) {
		this.ngs.read(url,lang);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#read(java.io.InputStream, java.lang.String, java.lang.String)
	 */
	public void read(InputStream source, String lang, String baseURI) {
		this.ngs.read(source,lang,baseURI);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#read(java.io.Reader, java.lang.String, java.lang.String)
	 */
	public void read(Reader source, String lang, String baseURI) {
		this.ngs.read(source,lang,baseURI);

	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#write(java.io.OutputStream, java.lang.String, java.lang.String)
	 */
	public void write(OutputStream out, String lang, String baseURI) {
		this.ngs.write(out,lang,baseURI);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#write(java.io.Writer, java.lang.String, java.lang.String)
	 */
	public void write(Writer out, String lang, String baseURI) {
		this.ngs.write(out,lang,baseURI);
	}
	
	/**
	 * Returns a UriList which contains the URIs to retrieve.
	 * 
	 * @return UriList
	 */
	public UriList getUrisToRetrieve(){
		return this.urisToRetrieve;
	}
	/**
	 * Returns a List with alreadz retrieved URIs.
	 * 
	 * @return List
	 */
	public List getRetrievedUris(){
		return this.retrievedUris;
	}
	
	/**
	 * Returns the NamedGraphSet.
	 * 
	 * @return NamedGraphSet.
	 */
	public NamedGraphSet getNamedGraphSet(){
		return this.ngs;
	}
	
	/**
	 * Inspects a Triple if it contains URIs. If a URI is found it is
	 * added to the UriList.
	 * 
	 * @param t The triple to inspect.
	 * @param step The retrieval step.
	 */
	public void inspectTriple(Triple t, int step){
		Node sub  = t.getSubject();
		Node pred = t.getPredicate();
		Node obj  = t.getObject();
		
	
		if(sub.isURI()){
			if(!this.urisToRetrieve.contains(sub.getURI())&&!this.retrievedUris.contains(sub.getURI()))
			this.urisToRetrieve.add(sub.getURI(),step);
		}
		if(pred.isURI()){
			if(!this.urisToRetrieve.contains(pred.getURI())&&!this.retrievedUris.contains(pred.getURI()))
			this.urisToRetrieve.add(pred.getURI(),step);		
		}
		if(obj.isURI()){
			if(!this.urisToRetrieve.contains(obj.getURI())&&!this.retrievedUris.contains(obj.getURI()))
			this.urisToRetrieve.add(obj.getURI(),step);
		}
	
	}
	/**
	 * Adds a FindListener to the Semantic Web Client.
	 * 
	 * @param listener The FindListener to add.
	 */
	public void addFindListener(FindListener listener){
		this.listener = listener;
	}
	
	/**
	 * Is performed when the retrieval is finished.
	 */
	public void retrievalFinished(){
		this.retrievalFinished = true;
		if(this.listener != null){
			this.listener.uriRetrievalFininshed(new GraphAddedEvent(this,null));
		}
	}
	
	
	/**
	 * Returns a List with unretrievable URIs
	 * 
	 * @return List of unretrieved URIs.
	 */
	public List getUnretrievedURIs(){
		return this.unretrievedURIs;
	}
	
	
}

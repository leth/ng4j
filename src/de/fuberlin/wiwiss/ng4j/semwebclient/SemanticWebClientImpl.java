/*
 * Created on 23.08.2006
 * 
 */
package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.Quad;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * @author Tobias Gauﬂ
 * 
 */
public class SemanticWebClientImpl extends NamedGraphSetImpl implements
		SemanticWebClient {
	private static final int MAXSTEPS_DEFAULT = 3;

	private static final int MAXTHREADS_DEFAULT = 30;

	private static final long TIMEOUT_DEFAULT = 30000;

	private List retrievedUris;

	private UriList urisToRetrieve;

	private URIRetriever retriever;

	private FindListener listener = null;

	private List unretrievedURIs;

	public boolean retrievalFinished;
	
	//dbg
	private List listenerList = Collections.synchronizedList(new ArrayList());

	/**
	 * Creates a Semantic Web Client.
	 */
	public SemanticWebClientImpl() {
		super();
		this.createGraph("http://localhost/provenanceInformation");
		this.retriever = new URIRetriever(this);
		this.retriever.setMaxsteps(MAXSTEPS_DEFAULT);
		this.retriever.setMaxthreads(MAXTHREADS_DEFAULT);
		this.retriever.setTimeout(TIMEOUT_DEFAULT);
		this.retrievedUris = Collections.synchronizedList(new ArrayList());
		this.unretrievedURIs = Collections.synchronizedList(new ArrayList());
		this.urisToRetrieve = new UriList();
		this.urisToRetrieve.addListListener(this.retriever);
		this.retrievalFinished = true;	
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#find(com.hp.hpl.jena.graph.TripleMatch)
	 */
	public SemWebIterator find(TripleMatch pattern) {
	//	while(!this.retrievalFinished){
	//	}
		this.retriever.setTriplePattern(pattern);
		Triple t = pattern.asTriple();

		Node sub = t.getSubject();
		Node pred = t.getPredicate();
		Node obj = t.getObject();
		this.inspectTriple(t, -1);
		
		SemWebIterator iter2 = new SemWebIterator(this, sub, pred, obj);
		Iterator iter = this.findQuads(Node.ANY, sub, pred, obj);

		

		while (iter.hasNext()) {
			Quad quad = (Quad) iter.next();
			Triple tr = quad.getTriple();
			this.inspectTriple(tr, -1);
		}
		return iter2;
	}
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semwebclient.SemanticWebClient#find(com.hp.hpl.jena.graph.TripleMatch,
	 *      de.fuberlin.wiwiss.ng4j.semwebclient.TripleListener)
	 */
	public void find(TripleMatch pattern, TripleListener listener) {
		this.retriever.setTriplePattern(pattern);
		//this.retrievalFinished = false;
		Triple t = pattern.asTriple();

		Node sub = t.getSubject();
		Node pred = t.getPredicate();
		Node obj = t.getObject();

		Iterator iter = this.findQuads(Node.ANY, sub, pred, obj);

		this.inspectTriple(t, -1);

		while (iter.hasNext()) {
			Quad quad = (Quad) iter.next();
			Triple tr = quad.getTriple();
			this.inspectTriple(tr, -1);
		}
		TripleFinder finder = new TripleFinder(sub, pred, obj, this, listener);
		finder.start();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#addRemoteGraph(java.lang.String)
	 */
	public void addRemoteGraph(String URI) {
		this.retrievalFinished = false;
		this.urisToRetrieve.add(URI, -1);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#reloadRemoteGraph(java.lang.String)
	 */
	public void reloadRemoteGraph(String URI) {
		this.retrievalFinished = false;

		if (this.retrievedUris.contains(URI)) {
			this.retrievedUris.remove(URI);
			this.urisToRetrieve.add(URI, -1);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#setConfig(java.lang.String,
	 *      java.lang.Object)
	 */
	public void setConfig(String option, String value) {
		if (option.equals(SemanticWebClient.CONFIG_MAXSTEPS)) {
			int val;
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"value '" + value + "' for config " + 
						SemanticWebClient.CONFIG_MAXSTEPS + 
						" is not numeric");
			}
			this.retriever.setMaxsteps(val);
		}
		if (option.equals(SemanticWebClient.CONFIG_MAXTHREADS)) {
			int val;
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"value '" + value + "' for config " + 
						SemanticWebClient.CONFIG_MAXTHREADS + 
						" is not numeric");
			}
			this.retriever.setMaxthreads(val);
		}
		if (option.equals(SemanticWebClient.CONFIG_TIMEOUT)) {
			long val;
			try {
				val = Long.parseLong(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException(
						"value '" + value + "' for config " + 
						SemanticWebClient.CONFIG_TIMEOUT + 
						" is not numeric");
			}
			this.retriever.setTimeout(val);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#getConfig(java.lang.String)
	 */
	public String getConfig(String option) {
		String value = null;
		if (option.toLowerCase().equals(SemanticWebClient.CONFIG_MAXSTEPS))
			value = String.valueOf(this.retriever.getMaxsteps());
		if (option.toLowerCase().equals(SemanticWebClient.CONFIG_MAXTHREADS))
			value = String.valueOf(this.retriever.getMaxthreads());
		if (option.toLowerCase().equals(SemanticWebClient.CONFIG_TIMEOUT))
			value = String.valueOf(this.retriever.getTimeout());

		return value;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#successfullyDereferencedURIs()
	 */
	public Iterator successfullyDereferencedURIs() {
		return this.retrievedUris.iterator();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.SemanticWebClient#unsuccessfullyDereferencedURIs()
	 */
	public Iterator unsuccessfullyDereferencedURIs() {
		return this.unretrievedURIs.iterator();
	}

	// ------------------------------------------------------
	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#addGraph(de.fuberlin.wiwiss.ng4j.NamedGraph)
	 */
	public void addGraph(NamedGraph graph) {
		super.addGraph(graph);
		
	//	if (this.listener != null) {
	//		this.listener.graphAdded(new GraphAddedEvent(this, graph.getGraphName().getURI()));
	//	}
		
		
		String name = graph.getGraphName().getURI();
		Iterator it = this.listenerList.iterator();
		while(it.hasNext()){
			FindListener l = (FindListener) it.next();
			l.graphAdded(new GraphAddedEvent(this, name));
		}
		
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#close()
	 */
	public void close() {
		this.close();
		this.retrievedUris.clear();
		this.urisToRetrieve.clear();
	}

	/**
	 * Returns a UriList which contains the URIs to retrieve.
	 * 
	 * @return UriList
	 */
	public UriList getUrisToRetrieve() {
		return this.urisToRetrieve;
	}

	/**
	 * Returns a List with already retrieved URIs.
	 * 
	 * @return List
	 */
	public List getRetrievedUris() {
		return this.retrievedUris;
	}

	/**
	 * Inspects a Triple if it contains URIs. If a URI is found it is added to
	 * the UriList.
	 * 
	 * @param t
	 *            The triple to inspect.
	 * @param step
	 *            The retrieval step.
	 */
	synchronized public void inspectTriple(Triple t, int step) {
		Node sub = t.getSubject();
		Node pred = t.getPredicate();
		Node obj = t.getObject();

		if (sub.isURI()) {
			if (!this.urisToRetrieve.contains(sub.getURI())){
				this.urisToRetrieve.add(sub.getURI(), step);
			this.retrievalFinished = false;}
		}
		if (pred.isURI()) {
			if (!this.urisToRetrieve.contains(pred.getURI())){
				this.urisToRetrieve.add(pred.getURI(), step);
			this.retrievalFinished = false;}
		}
		if (obj.isURI()) {
			if (!this.urisToRetrieve.contains(obj.getURI())){
				this.urisToRetrieve.add(obj.getURI(), step);
			this.retrievalFinished = false;}
		}

	}

	/**
	 * Adds a FindListener to the Semantic Web Client.
	 * 
	 * @param listener
	 *            The FindListener to add.
	 */
	public void addFindListener(FindListener listener) {
		this.listenerList.add(listener);
		//this.listener = listener;
	}

	/**
	 * Is performed when the retrieval is finished.
	 */
	public void retrievalFinished() {
		
		this.retrievalFinished = true;
		/*
		if(this.listener != null){
			this.listener.uriRetrievalFininshed(new GraphAddedEvent(this, null));
		}
		*/
		
		Iterator it = this.listenerList.iterator();
		while(it.hasNext()){
			FindListener l = (FindListener) it.next();
			l.uriRetrievalFininshed(new GraphAddedEvent(this, null));
		}
		
	}

	/**
	 * Returns a List with unretrievable URIs
	 * 
	 * @return List of unretrieved URIs.
	 */
	public List getUnretrievedURIs() {
		return this.unretrievedURIs;
	}
	
	public Graph asJenaGraph(Node defaultGraphForAdding){
		return new SemWebMultiUnion(this);
	}
	
	public void removeFindListener(){
		this.listener = null;
	}
	
	synchronized void addUriToRetrieve(String uri, int step){
		this.urisToRetrieve.add(uri,step);
	}


}

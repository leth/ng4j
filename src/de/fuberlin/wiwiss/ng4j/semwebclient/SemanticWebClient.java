/*
 * Created on 23.08.2006
 * 
 */
package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.NamedGraphSetFactory;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

import de.fuberlin.wiwiss.ng4j.semwebclient.urisearch.URISearchListener;
import de.fuberlin.wiwiss.ng4j.semwebclient.urisearch.URISearchResult;
import de.fuberlin.wiwiss.ng4j.semwebclient.urisearch.URISearchTask;
import de.fuberlin.wiwiss.ng4j.semwebclient.urisearch.URISearchTaskQueue;

/**
 * <p>
 * The SematicWebClient interface enables applications to access the Semantic
 * Web.
 * </p>
 * 
 * <p>
 * The Semantic Web is represented as a single, global RDF graph. The library 
 * enables applications to query this global graph using SPARQL- and find(SPO) 
 * queries. To answer queries, the library dynamically retrieves information 
 * from the Semantic Web.
 * </p>
 * 
 * <p>
 * During the execution of a query, information about all resources that
 * appear in triple patterns and in the query results is dynamically loaded
 * from the Semantic Web by:
 * <ul>
 * <li>dereferencing the URI of each resource using the HTTP protocol,
 * <li>following all known rdf:seeAlso links for a resource.
 * </ul>
 * </p>
 * 
 * <p>
 * Internally, retrieved information is represented as a set of named graphs,
 * which allows applications to keep track of information provenance.
 * </p>
 * 
 * <p>
 * If the result of dereferencing a URI is a RDF graph, the graph is added as a
 * named graph (named with the retrieval URI) to graph set. <BR>
 * 
 * Within each graphset there is a graph http://localhost/provenanceInformation,
 * which contains provenance information for each retrieved graph. The graph 
 * contains a swp:sourceURL and a swp:retrievalTimestamp triple for each 
 * retrieved graph.
 * 
 * More information about the Semantic Web Client is found on the project's website:
 * http://sites.wiwiss.fu-berlin.de/suhl/bizer/ng4j/semwebclient/
 * 
 * @author Chris Bizer (chris@bizer.de)
 * @author Richard Cyganiak (richard@cyganiak.de)
 * @author Tobias Gauß (tobias.gauss@web.de)
 * @author Olaf Hartig (hartig@informatik.hu-berlin.de)
 */
// public class SemanticWebClient extends NamedGraphSetImpl {
public class SemanticWebClient extends de.fuberlin.wiwiss.ng4j.impl.idbased.IdBasedNamedGraphSetImpl {
	private List<String> retrievedUris;

	private Set<String> markedUris = new HashSet<String>();

	private DereferencingTaskQueue derefQueue = null;
	private URISearchTaskQueue searchQueue = null;

	private Map<String,Exception> unretrievedURIs;

	private Map<String,String> redirectedURIs;

	protected Map<String,Set<String>> successfullySearchedURIs;
	protected Set<String> unsuccessfullySearchedURIs;

	private boolean isClosed = false;

	final private SemanticWebClientConfig config;

	private Log log = LogFactory.getLog(SemanticWebClient.class);

	/**
	 * Creates a Semantic Web Client with the default configuration.
	 */
	public SemanticWebClient() {
		this( new SemanticWebClientConfig() );
	}

	/**
	 * Creates a Semantic Web Client using the given configuration.
	 */
	public SemanticWebClient( SemanticWebClientConfig config ) {
		super();
		this.createGraph("http://localhost/provenanceInformation");
		this.retrievedUris = Collections.synchronizedList(new ArrayList<String>());
		this.unretrievedURIs = Collections.synchronizedMap(new HashMap<String,Exception>());
		this.redirectedURIs = Collections.synchronizedMap(new HashMap<String,String>());
		this.successfullySearchedURIs = Collections.synchronizedMap(new HashMap<String,Set<String>>());
		this.unsuccessfullySearchedURIs = Collections.synchronizedSet(new HashSet<String>());
		this.config = config;
	}

	/**
	* Finds Triples that match a triple pattern. The argument may contain
	* wildcards ({@link Node#ANY}).
	* 
	* The implementation of the find method uses multithreading, thus the first
	* triples can already be used while there is still information being
	* retrieved in the background.
	* 
	* Returned triples have a getSource() method which returns the URL from 
	* which the triple was retrieved.
	* 
	* @param pattern
	*/
	public SemWebIterator find(TripleMatch pattern) {
		return new FindQuery( this, pattern.asTriple(), config.getEnableSindice() ).iterator();
	}

	/**
	* Finds Triples that match a triple pattern. The argument may contain
	* wildcards ({@link Node#ANY}).
	* 
	* The implementation of the find method uses multithreading, thus the first
	* triples can already be used while there is still information being
	* retrieved in the background.
	* 
	* This method is called with a TripleListener as second parameter which is
	* notified each time when a triple is found and when the retrieval process 
	* is finished.
	* 
	* @param pattern
	*/
	public void find(TripleMatch pattern, TripleListener listener) {
		TripleFinder finder = new TripleFinder(pattern.asTriple(), this, listener);
		finder.start();
	}

	/**
	* Adds a remote graph to the graphset. The graph is retrieved by
	* dereferencing the URI.
	*/	
	public void addRemoteGraph(String uri) {
		requestDereferencing(uri, 0, null);
	}

   /**
	* Reloads a remote graph. The current graph is replaced by the new graph.
	*/
	public synchronized void reloadRemoteGraph(String uri) {
		if (containsGraph(uri)) {
			removeGraph(uri);
			addRemoteGraph(uri);
		}
	}

	/**
	 * Returns the configuration of this Semantic Web client.
	 */
	public SemanticWebClientConfig getConfig() {
		return config;
	}

	/**
	 * Returns an iterator over all successfully dereferenced URIs.
	 */
	public List<String> successfullyDereferencedURIs() {
		return this.retrievedUris;
	}

	/**
	 * Returns an iterator over all URIs that couldn't be dereferenced.
	 */
	public Set<String> unsuccessfullyDereferencedURIs() {
		return this.unretrievedURIs.keySet();
	}

	/**
	 * Returns the exception that caused the dereferencing of the given URI
	 * to fail.
	 */
	public Exception getReasonForFailedDereferencing( String uri ) {
		return this.unretrievedURIs.get( uri );
	}

	/**
	 * Returns a set of all URIs that have been redirected.
	 */
	public Set<String> redirectedURIs() {
		return this.redirectedURIs.keySet();
	}

	/**
	 * Returns the redirect URI for the given URI (if the given URI has been redirected).
	 */
	public String getRedirectURI( String uri ) {
		return this.redirectedURIs.get( uri );
	}

	/**
	 * Returns all URIs that have been searched successfully.
	 */
	public Set<String> successfullySearchedURIs() {
		return successfullySearchedURIs.keySet();
	}

	/**
	 * Returns all URLs of documents that mention the given URI according to a
	 * search.
	 */
	public Set<String> getMentioningURLs( String uri ) {
		return successfullySearchedURIs.get( uri );
	}

	/**
	 * Returns all URIs that could not be searched for.
	 */
	public Set<String> unsuccessfullySearchedURIs() {
		return unsuccessfullySearchedURIs;
	}

	/** 
	 * Determines all retrieval threads. 
	 * Has to be called to determine a Sementic Web Client.
	 */
	public synchronized void close() {
		// TODO Throw exception if find() and other methods are called after close()?
		this.log.debug("Closing ...");
		this.isClosed = true;
		if (this.derefQueue != null) {
			this.derefQueue.close();
		}
		if (searchQueue != null) {
			searchQueue.close();
		}
		this.retrievedUris.clear();
		this.markedUris.clear();
		super.close();
		this.log.debug("Closed");
	}

	/**
	 * Returns true if the SemanticWebCliend is already closed false if not.
	 */
	public boolean isClosed() {
		return this.isClosed;
	}

	/**
	 * Returns true if the Semantic Web client is not retrieving any URIs at the moment.
	 */
	public synchronized boolean isIdle() {
		if ( (derefQueue != null) && ! derefQueue.isIdle() ) {
			return false;
		}

		if ( (searchQueue != null) && ! searchQueue.isIdle() ) {
			return false;
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#asJenaGraph(com.hp.hpl.jena.graph.Node)
	 */
	public Graph asJenaGraph(Node defaultGraphForAdding) {
		return new SemWebMultiUnion(this);
	}
	
	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#addGraph(de.fuberlin.wiwiss.ng4j.NamedGraph)
	 */
	public synchronized void addGraph(NamedGraph graph){
	//	if(this.countGraphs()-1>=maxgraphs)
	//		throw new JenaException("SemWebClient full.");
		super.addGraph(graph);
	}

	/**
	 * Deletes all NamedGraphs from the set and clears the sets of successfully,
	 * unsuccessfully, and redirected URIs.
	 */
	public synchronized void clear() {
		super.clear();
		markedUris.clear();
		retrievedUris.clear();
		unretrievedURIs.clear();
		redirectedURIs.clear();
		createGraph("http://localhost/provenanceInformation");
	}

	/**
	 * Initiates a new retrieval process that dereferences the given URI and
	 * queries the Sindice search engine for the URI.
	 * If the given URI is a hash-URI this method dereferences the URI without
	 * the hash and the subsequent fragment. However, for the URI search the
	 * whole URI is being used.
	 */
	public boolean requestDereferencingWithSearch(String uri, int step,
			final DereferencingListener derefListener,
			final URISearchListener searchListener) {
		boolean derefStartResult = startDereferencing(uri, step, derefListener);
		if ( derefStartResult ) {
			startSearching(uri, searchListener, step);
		}

		return derefStartResult;
	}

	/**
	 * Initiates a new retrieval process that dereferences the given URI.
	 * If the given URI is a hash-URI this method dereferences the URI without
	 * the hash and the subsequent fragment.
	 */
	public boolean requestDereferencing(String uri, int step,
			final DereferencingListener listener) {
		return startDereferencing(uri, step, listener);
	}

	/**
	 * Initiates dereferencing of the given URI.
	 */
	private boolean startDereferencing(String uri, int step,
			final DereferencingListener listener) {
		String derefURI = ( uri.contains("#") ) ? uri.substring( 0, uri.indexOf("#") ) : uri;
		if (    unretrievedURIs.containsKey(derefURI)
		     && unretrievedURIs.get(derefURI) instanceof SocketTimeoutException ) {
			log.debug("Retry derefencing of: " + derefURI );
			unretrievedURIs.remove( derefURI );
			markedUris.remove( derefURI );
		}
		if (redirectedURIs.containsKey(derefURI) || retrievedUris.contains(derefURI)) {
			// already retrieved, don't queue again
			return false;
		}
		if (markedUris.contains(derefURI) && unretrievedURIs.containsKey(derefURI)) {
			// unretrievable, don't queue again
			return false;
		}
		if (markedUris.contains(derefURI)) {
			// already in queue but not retrieved yet
			// attach the given listener to the corresponding dereferencing task
			DereferencingTask existingTask = getDerefQueue().getTask( derefURI );
			if ( existingTask != null && listener != null && ! existingTask.isAttached(listener) ) {
				existingTask.attachListener( listener );
				return true;
			} else {
				return false;
			}
		}
		if (step > config.getMaxSteps()) {
			this.log.debug("Ignored (maxsteps reached): " + uri);
			return false;
		}
		this.log.debug("Queued for dereferencing (" + step + " steps): " + derefURI);
		this.markedUris.add(derefURI);
		DereferencingListener myListener = new DereferencingListener() {
			public void dereferenced(DereferencingResult result) {
				if (isClosed()) {
					return;
				}
				if (result.isSuccess()) {
					addProvenanceInformation(result.getURI());
					Iterator<NamedGraph> it = result.getResultData().listGraphs();
					while (it.hasNext()) {
						addGraph((NamedGraph) it.next());
					}
					SemanticWebClient.this.retrievedUris.add(result.getURI());
				} else {
					// TODO: URIs get marked unretrievable when the worker thread gets interrupted 
					if ( result.getResultCode() == DereferencingResult.STATUS_REDIRECTED )
						redirectedURIs.put(result.getURI(), result.getRedirectURI());
					else if ( result.getResultCode() == DereferencingResult.STATUS_NEW_URIS_FOUND )
						// ignore GRDDLed documents so far - TODO: better management of results
						// However, the HTML doc. represented by the URI has been retrieved successfully
						retrievedUris.add( result.getURI() );
					else
						unretrievedURIs.put(result.getURI(), result.getException());
				}
			}
		};

		DereferencingTask task = new DereferencingTask( derefURI, step );

		task.attachListener( myListener );
		if ( listener != null ) {
			task.attachListener( listener );
		}

		getDerefQueue().addTask( task );
		return true;
	}

	/**
	 * Initiates a Sindice-based search for the given URI.
	 */
	private void startSearching(String uri, final URISearchListener listener, int step) {
		if ( step > config.getMaxSteps() ) {
			log.debug( "Ignored (maxsteps reached): " + uri );
			return;
		}

		log.debug( "Queued for searching: " + uri );
		URISearchListener myListener = new URISearchListener() {
			public void uriSearchFinished(URISearchResult result) {
				if (isClosed()) {
					return;
				}
				if (result.isSuccess()) {
					successfullySearchedURIs.put( result.getTask().getURI(), result.getMentioningDocs() );
				} else {
					log.debug( "Searching failed (" + result.getException().getMessage() + "): " + result.getTask().getURI(), result.getException() );
					unsuccessfullySearchedURIs.add( result.getTask().getURI() );
				}
				if (listener != null) {
					listener.uriSearchFinished(result);
				}
			}
		};
		getSearchQueue().addTask( new URISearchTask(uri,myListener, step) );
	}
	
	/**
	 * If a URI is successfully retrieved this method is called to add
	 * provenance information about the graph to the SemanticWebClient.
	 * 
	 * @param uri A URI string.
	 */
	private void addProvenanceInformation(String uri) {
		String label = Long.toString(Calendar.getInstance().getTimeInMillis());
		NamedGraph provenanceGraph = getGraph("http://localhost/provenanceInformation");
		provenanceGraph.add(new Triple(Node.createURI(uri), Node
				.createURI("http://www.w3.org/2004/03/trix/swp-2/sourceURL"), Node
				.createURI(uri)));
		provenanceGraph.add(new Triple(Node.createURI(uri), Node
				.createURI("http://www.w3.org/2004/03/trix/swp-2/retrievalTimestamp"), Node
				.createLiteral(label)));
	}

	private DereferencingTaskQueue getDerefQueue() {
		if (this.derefQueue == null) {
			NamedGraphSetFactory ngsFactory = new NamedGraphSetFactory() {
				public NamedGraphSet create () { return new NamedGraphSetImpl(); }
			};
			this.derefQueue = new DereferencingTaskQueue( ngsFactory,
			                                              config.getMaxThreads(),
			                                              config.getMaxFileSize(),
			                                              config.getEnableGRDDL(),
			                                              config.getEnableRDFa(),
			                                              config.getDerefConnectTimeout(),
			                                              config.getDerefReadTimeout() );
		}
		return this.derefQueue;
	}

	private URISearchTaskQueue getSearchQueue() {
		if (searchQueue == null) {
			searchQueue = new URISearchTaskQueue();
			searchQueue.start();
		}
		return searchQueue;
	}

	public String toString ()
	{
		return "SemanticWebClient (queried dataset: SemanticWebClient with " + countQuads() + " quads in " + countGraphs() + " graphs)";
	}
}

/*
 * (c) Copyright 2006 - 2010 Christian Bizer (chris@bizer.de)
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. 2. Redistributions in
 * binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. 3. The name of the author may not
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 */

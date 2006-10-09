/*
 * Created on 23.08.2006
 * 
 */
package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatch;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * <p>
 * The SematicWebClient interface enables applications to access the Semantic
 * Web.
 * </p>
 * 
 * <p>
 * The Semantic Web is represended as a single, global RDF graph. Applications
 * can use the find(TripleMatch pattern) method to retrieve information from the
 * Semantic Web.
 * </p>
 * 
 * <p>
 * During the execution of a find() query, information about all resources that
 * appear in the triple pattern and in the query results is dynamically loaded
 * from the Semantic Web by:
 * <ul>
 * <li>dereferencing the URI of each resource using the HTTP protocol,
 * <li>following all known rdf:seeAlso links for each resource.
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
 * which contains provenance information for each retrieved graph. Each graph is
 * described with a swp:sourceURL and a swp:retrievalTimestamp property.
 * 
 * @author Chris Bizer (chris@bizer.de)
 * @author Richard Cyganiak (richard@cyganiak.de)
 */
public class SemanticWebClient extends NamedGraphSetImpl {
	public String CONFIG_MAXSTEPS = "maxsteps";
	public String CONFIG_MAXTHREADS = "maxthreads";
	public String CONFIG_TIMEOUT = "timeout";
	
	private static final int MAXSTEPS_DEFAULT = 3;

	private static final int MAXTHREADS_DEFAULT = 10;

	private static final long TIMEOUT_DEFAULT = 30000;

	private List retrievedUris;

	private Set markedUris = new HashSet();

	private DereferencingTaskQueue uriQueue = null;

	private List unretrievedURIs;

	private boolean isClosed = false;
	
	private long timeout = TIMEOUT_DEFAULT;
	private int maxsteps = MAXSTEPS_DEFAULT;
	private int maxthreads = MAXTHREADS_DEFAULT;
	
	//dbg
//	private List listenerList = Collections.synchronizedList(new ArrayList());

	private Log log = LogFactory.getLog(SemanticWebClient.class);

	/**
	 * Creates a Semantic Web Client.
	 */
	public SemanticWebClient() {
		super();
		this.createGraph("http://localhost/provenanceInformation");
		this.retrievedUris = Collections.synchronizedList(new ArrayList());
		this.unretrievedURIs = Collections.synchronizedList(new ArrayList());
	}

	/**
	* Finds Triples that match a triple pattern. The argument may contain
	* wildcards ({@link Node#ANY}).
	* 
	* The implementation of the find method uses multithreading, thus the first
	* triples can already be used while there is still information being
	* retrieved in the background.
	* 
	* Returned triples have a sourceGraphs() method which returns an iterator
	* over all graphs names in which contain the triple. The returned iterator
	* has a retrievalFinished() method which returns true when the retrieval
	* process for all URIs and see:Also links is finished.
	* 
	* @param pattern
	* @return
	*/
	public SemWebIterator find(TripleMatch pattern) {
		return new FindQuery(this, pattern.asTriple()).iterator();
	}

	/**
	* Finds Triples that match a triple pattern. The argument may contain
	* wildcards ({@link Node#ANY}).
	* 
	* The implementation of the find method uses multithreading, thus the first
	* triples can already be used while there is still information being
	* retrieved in the background.
	* 
	* Returned triples have a sourceGraphs() method which returns an iterator
	* over all graphs names in which contain the triple. The returned iterator
	* has a retrievalFinished() method which returns true when the retrieval
	* process for all URIs and see:Also links is finished.
	* 
	* @param pattern
	* @return
	*/
	public void find(TripleMatch pattern, TripleListener listener) {
		TripleFinder finder = new TripleFinder(pattern.asTriple(), this, listener);
		finder.start();
	}

	/**
	* Adds a remote graph to the graphset. The graph is retrieved by
	* dereferencing the URI.
	* 
	* @param seconds
	*/	
	public void addRemoteGraph(String uri) {
		requestDereferencing(uri, 0, null);
	}

   /**
	* Reloads a remote graph. The current graph is replaced by the new graph.
	* 
	* @param seconds
	*/
	public synchronized void reloadRemoteGraph(String uri) {
		if (containsGraph(uri)) {
			removeGraph(uri);
			addRemoteGraph(uri);
		}
	}

	/**
	 * Sets a configuration option. Like Timeout or MaxRetrievalSize
	 */
	public void setConfig(String option, String value) {
		if (option.equals(CONFIG_MAXSTEPS)) {
			int val;
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("value '" + value
						+ "' for config " + CONFIG_MAXSTEPS
						+ " is not numeric");
			}
			this.maxsteps = val;
		}
		if (option.equals(CONFIG_MAXTHREADS)) {
			int val;
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("value '" + value
						+ "' for config " + CONFIG_MAXTHREADS
						+ " is not numeric");
			}
			this.maxthreads = val;
		}
		if (option.equals(CONFIG_TIMEOUT)) {
			long val;
			try {
				val = Long.parseLong(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("value '" + value
						+ "' for config " + CONFIG_TIMEOUT
						+ " is not numeric");
			}
			this.timeout = val;
		}
	}

	/**
	 * Get a configuration option. Like Timeout or MaxRetrievalSize MaxThreads
	 */
	public String getConfig(String option) {
		String value = null;
		if (option.toLowerCase().equals(CONFIG_MAXSTEPS))
			value = String.valueOf(this.maxsteps);
		if (option.toLowerCase().equals(CONFIG_MAXTHREADS))
			value = String.valueOf(this.maxthreads);
		if (option.toLowerCase().equals(CONFIG_TIMEOUT))
			value = String.valueOf(this.timeout);

		return value;
	}

	/**
	 * Returns an iterator over all successfully dereferenced URIs.
	 * 
	 * @return
	 */
	public Iterator successfullyDereferencedURIs() {
		return this.retrievedUris.iterator();
	}

	/**
	 * Returns an iterator over all URIs that couldn't be dereferenced. The
	 * elements of the list are pairs (URI, errorcode) describing the problem
	 * that appeared in the retrieval process.
	 * 
	 * @return
	 */
	public Iterator unsuccessfullyDereferencedURIs() {
		return this.unretrievedURIs.iterator();
	}

//	synchronized public void addGraph(NamedGraph graph, ThreadObserver observer) {
//		super.addGraph(graph);

		//	if (this.listener != null) {
		//		this.listener.graphAdded(new GraphAddedEvent(this, graph.getGraphName().getURI()));
		//	}
//		String name = graph.getGraphName().getURI();
		//Iterator it = this.listenerList.iterator();
		
	
//		SemWebIterator l = observer.getSemWebIterator();
//		l.graphAdded(new GraphAddedEvent(this, name));
//	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.NamedGraphSet#close()
	 */
	public synchronized void close() {
		this.log.debug("Closing ...");
		this.isClosed = true;
		if (this.uriQueue != null) {
			this.uriQueue.close();
		}
		this.retrievedUris.clear();
		this.markedUris.clear();
		super.close();
		this.log.debug("Closed");
	}

	public boolean isClosed() {
		return this.isClosed;
	}
	
	/**
	 * Returns a List with already retrieved URIs.
	 * 
	 * @return List
	 */
	public synchronized List getRetrievedUris() {
		return new ArrayList(this.retrievedUris);
	}

	/**
	 * Adds a FindListener to the Semantic Web Client.
	 * 
	 * @param listener
	 *            The FindListener to add.
	 */
//	public void addFindListener(FindListener listener) {
//		this.listenerList.add(listener);
		//this.listener = listener;
//	}

//	public void removeFindListener(FindListener listener) {
//		this.listenerList.remove(listener);
//	}

	/**
	 * Returns a List with unretrievable URIs
	 * 
	 * @return List of unretrieved URIs.
	 */
	public List getUnretrievedURIs() {
		return this.unretrievedURIs;
	}

	public Graph asJenaGraph(Node defaultGraphForAdding) {
		return new SemWebMultiUnion(this);
	}
	
	public synchronized void addGraph(NamedGraph graph){
		super.addGraph(graph);
	}

	public boolean requestDereferencing(String uri, int step,
			final DereferencingListener listener) {
		if (this.markedUris.contains(uri) || containsGraph(uri)) {
			// already retrieved or in queue, don't queue again
			return false;
		}
		if (step > this.maxsteps) {
			this.log.debug("Ignored (maxsteps reached): " + uri);
			return false;
		}
		this.log.debug("Queued (" + step + " steps): " + uri);
		this.markedUris.add(uri);
		DereferencingListener myListener = new DereferencingListener() {
			public void dereferenced(DereferencingResult result) {
				if (isClosed()) {
					return;
				}
				if (result.isSuccess()) {
					addProvenanceInformation(result.getURI());
					Iterator it = result.getResultData().listGraphs();
					while (it.hasNext()) {
						addGraph((NamedGraph) it.next());
					}
					SemanticWebClient.this.retrievedUris.add(result.getURI());
				} else {
					// TODO: URIs get marked unretrievable when the worker thread gets interrupted 
					SemanticWebClient.this.unretrievedURIs.add(result.getURI());
				}
				if (listener != null) {
					listener.dereferenced(result);
				}
			}
		};
		getURIQueue().addTask(new DereferencingTask(myListener, uri, step));
		return true;
	}
	
	/**
	 * If a URI is successfully retrieved this method is called to add
	 * provenance information about the graph to the underlying NamedGraphSet.
	 * 
	 * @param uri
	 *            A URI string.
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

	private DereferencingTaskQueue getURIQueue() {
		if (this.uriQueue == null) {
			this.uriQueue = new DereferencingTaskQueue(this.maxthreads);
		}
		return this.uriQueue;
	}
}

/*
 * (c) Copyright 2006 Christian Bizer (chris@bizer.de) All rights reserved.
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

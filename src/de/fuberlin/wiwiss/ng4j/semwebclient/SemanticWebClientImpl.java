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
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.impl.NamedGraphSetImpl;

/**
 * @author Tobias Gauß
 * 
 */
public class SemanticWebClientImpl extends NamedGraphSetImpl implements
		SemanticWebClient {
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

	private Log log = LogFactory.getLog(SemanticWebClientImpl.class);

	/**
	 * Creates a Semantic Web Client.
	 */
	public SemanticWebClientImpl() {
		super();
		this.createGraph("http://localhost/provenanceInformation");
		this.retrievedUris = Collections.synchronizedList(new ArrayList());
		this.unretrievedURIs = Collections.synchronizedList(new ArrayList());
	}

	synchronized public SemWebIterator find(TripleMatch pattern) {
		return new FindQuery(this, pattern.asTriple()).iterator();
	}

	public void find(TripleMatch pattern, TripleListener listener) {
		//this.retriever.setTriplePattern(pattern);
		//this.retrievalFinished = false;
		
		
		
		
		/*
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
		*/

	}

	public void addRemoteGraph(String uri) {
		final Object lock = new Object();
		DereferencingListener listener = new DereferencingListener() {
			public void dereferencingFailed(DereferencingTask task, int errorCode) {
				synchronized (lock) {
					lock.notify();
				}
			}
			public void dereferencingSuccessful(DereferencingTask task, NamedGraphSet result) {
				synchronized (lock) {
					lock.notify();
				}
			}
		};
		requestDereferencing(uri, 1, listener);
		synchronized (lock) {
			try {
				lock.wait();
			} catch (InterruptedException ex) {
				// We don't know when this happens
				throw new RuntimeException(ex);
			}
		}
	}

	public synchronized void reloadRemoteGraph(String uri) {
		if (containsGraph(uri)) {
			removeGraph(uri);
			addRemoteGraph(uri);
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
				throw new IllegalArgumentException("value '" + value
						+ "' for config " + SemanticWebClient.CONFIG_MAXSTEPS
						+ " is not numeric");
			}
			this.maxsteps = val;
		}
		if (option.equals(SemanticWebClient.CONFIG_MAXTHREADS)) {
			int val;
			try {
				val = Integer.parseInt(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("value '" + value
						+ "' for config " + SemanticWebClient.CONFIG_MAXTHREADS
						+ " is not numeric");
			}
			this.maxthreads = val;
		}
		if (option.equals(SemanticWebClient.CONFIG_TIMEOUT)) {
			long val;
			try {
				val = Long.parseLong(value);
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("value '" + value
						+ "' for config " + SemanticWebClient.CONFIG_TIMEOUT
						+ " is not numeric");
			}
			this.timeout = val;
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
			value = String.valueOf(this.maxsteps);
		if (option.toLowerCase().equals(SemanticWebClient.CONFIG_MAXTHREADS))
			value = String.valueOf(this.maxthreads);
		if (option.toLowerCase().equals(SemanticWebClient.CONFIG_TIMEOUT))
			value = String.valueOf(this.timeout);

		return value;
	}

	public Iterator successfullyDereferencedURIs() {
		return this.retrievedUris.iterator();
	}

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
		this.isClosed = true;
		if (this.uriQueue != null) {
			this.uriQueue.close();
		}
		this.retrievedUris.clear();
		this.markedUris.clear();
		super.close();
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

	public synchronized boolean requestDereferencing(String uri, int step,
			final DereferencingListener listener) {
		if (this.markedUris.contains(uri)) {
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
			public void dereferencingFailed(DereferencingTask task, int errorCode) {
				SemanticWebClientImpl.this.unretrievedURIs.add(task.getURI());
			}
			public void dereferencingSuccessful(DereferencingTask task, NamedGraphSet result) {
				if (isClosed()) {
					return;
				}
				addProvenanceInformation(task.getURI());
				Iterator it = result.listGraphs();
				while (it.hasNext()) {
					addGraph((NamedGraph) it.next());
				}
				SemanticWebClientImpl.this.retrievedUris.add(task.getURI());
				listener.dereferencingSuccessful(task, result);
			}
		};
		getURIQueue().addTask(new DereferencingTask(myListener, uri, step + 1));
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
				.createURI("http://purl.org/net/scutter/source"), Node
				.createURI(uri)));
		provenanceGraph.add(new Triple(Node.createURI(uri), Node
				.createURI("http://purl.org/net/scutter/lastModified"), Node
				.createLiteral(label)));
	}

	private DereferencingTaskQueue getURIQueue() {
		if (this.uriQueue == null) {
			this.uriQueue = new DereferencingTaskQueue(this.maxthreads);
		}
		return this.uriQueue;
	}
}

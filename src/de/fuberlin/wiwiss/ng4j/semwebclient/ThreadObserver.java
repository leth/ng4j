package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;

import de.fuberlin.wiwiss.ng4j.NamedGraph;
import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * If a URI is added to the "to retrieve"- list the ThreadObserver
 * generates a new UriConnector thread to retrieve this URI. It 
 * recurring checks the ThreadList for finished threads
 * fetches the collected data and adds it to the underlying
 * NamedGraphSet.
 * 
 * @author Tobias Gauß
 *
 */
public class ThreadObserver extends Thread {
	/**
	 * The corresponding URIRetriever.
	 */
	private URIRetriever retriever;

	/**
	 * The time when the ThreadObserver was started.
	 */
	private long started;

	/**
	 * True if the ThreadObserver is stopped, false if not.
	 */
	private volatile boolean stopped;

	/**
	 * The ThreadList which contains the running threads.
	 */
	private ThreadList threadlist = new ThreadList();

	/**
	 * A linked list which contains the waiting thraids.
	 */
	private LinkedList waitingThreads = new LinkedList();

	/**
	 * Constructor
	 * 
	 * @param retriever the corresponding URIRetriever.
	 */
	public ThreadObserver(URIRetriever retriever) {
		this.retriever = retriever;
		this.started = Calendar.getInstance().getTimeInMillis();
	}

	/**
	 * If a URI is successfully retrieved this method is called to 
	 * add provenance information about the graph to the underlying
	 * NamedGraphSet.
	 * 
	 * @param uri A URI string.
	 */
	synchronized private void addProvenanceInformation(String uri) {
		String label = Long.toString(Calendar.getInstance().getTimeInMillis());
		NamedGraph provenanceGraph = this.retriever.getClient().getGraph(
				"http://localhost/provenanceInformation");
		provenanceGraph.add(new Triple(Node.createURI(uri), Node
				.createURI("http://purl.org/net/scutter/source"), Node
				.createURI(uri)));
		provenanceGraph.add(new Triple(Node.createURI(uri), Node
				.createURI("http://purl.org/net/scutter/lastModified"), Node
				.createLiteral(label)));
	}

	/**
	 * Adds a Thread to the threadlist if the variable stopped is not true.
	 * 
	 * @param t The thread to add.
	 */
	synchronized public void addThread(Thread t) {
		if (!this.stopped)
			this.waitingThreads.add(t);
	}

	/**
	 * Fetches the retrieved data from a finished URIConnector and
	 * adds it to the underlying NamedGraphSet.
	 * 
	 * @param connector The URIConnector which contains the retrieved data.
	 */
	synchronized private void addToGraphset(UriConnector connector) {
		this.lookupNgs(this.retriever.getClient().getNamedGraphSet(), connector
				.getUriString(), connector.getStep());
		NamedGraphSet ngs = connector.getNgs();

		Iterator it = ngs.listGraphs();
		while (it.hasNext()) {
			NamedGraph g = (NamedGraph) it.next();
			if (g.size() > 0)
				this.retriever.getClient().addGraph(g);
			this.addProvenanceInformation(connector.getUriString());
		}
		this.lookupNgs(ngs, connector.getUriString(), connector.getStep());
		this.inspectNgs(ngs, connector.getStep());

		//System.out.println(connector.uriRetrieved());
	//	if (!connector.uriRetrieved()) {
	//		int response = connector.responseCode();
	//		this.retriever.getClient().getUnretrievedURIs().add(
	//				new RetrieveResult(connector.getUriString(), response));
	//	}else{
	//		
	//	}

	}

	/**
	 *  Checks how long the Observer runs and stops it if the
	 *  timeout is reached.
	 */
	synchronized private void checkTime() {
		long now = Calendar.getInstance().getTimeInMillis();
		if ((now - this.started) > this.retriever.getTimeout())
			this.stopObserver();
	}

	/**
	 * Checks if there are fininshed Threads in the Threadlist calls the
	 * addToGraphset method and removes the finished Threads from the list.
	 */
	synchronized private void clearThreads() {
		if (!this.threadlist.isEmpty()) {
			Iterator iter = this.threadlist.iterator();
			while (iter.hasNext()) {
				UriConnector connector = (UriConnector) iter.next();
				if (connector.isReady()) {
					this.addToGraphset(connector);

					connector.wakeUp();
					this.retriever.getClient().getUrisToRetrieve().remove(
							connector.getUriString());
					if(connector.uriRetrieved()){
						this.retriever.getClient().getRetrievedUris().add(
								connector.getUriString());
					}else{
						this.retriever.getClient().getUnretrievedURIs().add(
								new RetrieveResult(connector.getUriString(), connector.responseCode()));
					}
					this.finishedCheck();
					this.threadlist.remove(connector);
					break;
				}
			}
		}
	}

	/**
	 * If the last thread is removed and there are no waiting threads left
	 * the retrieval is finihed and the Observer stopped.
	 */
	synchronized private void finishedCheck() {
		if (this.threadlist.size() == 1 && (this.waitingThreads.isEmpty())) {
			this.retriever.retrievalFinished();
			this.stopObserver();
		}
	}

	/**
	 * Checks the given NamedGraphSet ngs for uris.
	 * 
	 * @param ngs The NamedgraphSet to inspect
	 * @param step The retrieval step
	 */
	synchronized private void inspectNgs(NamedGraphSet ngs, int step) {
		Iterator iter = ngs.findQuads(Node.ANY, Node.ANY, Node.ANY, Node.ANY);

		while (iter.hasNext()) {
			Quad q = (Quad) iter.next();
			Triple t = q.getTriple();
			this.retriever.getClient().inspectTriple(t, step);
		}
	}

	/**
	 * Checks a given NamedGraphSet ngs for rdfs:seeAlso tags and 
	 * adds the found uris to the "to retrieve" list.
	 * 
	 * @param ngs The NamedGraphSet to inspect.
	 * @param uri The URI.
	 * @param step The retrieval step.
	 */
	synchronized private void lookupNgs(NamedGraphSet ngs, String uri, int step) {
		synchronized (this) {
			Iterator iter = ngs.findQuads(Node.ANY, Node.createURI(uri), Node
					.createURI("http://www.w3.org/2000/01/rdf-schema#seeAlso"),
					Node.ANY);
			while (iter.hasNext()) {
				Quad quad = (Quad) iter.next();
				Node obj = quad.getObject();
				if (obj.isURI()) {
					if (!this.retriever.getClient().getUrisToRetrieve()
							.contains(obj.getURI())
							&& !this.retriever.getClient().getRetrievedUris()
									.contains(obj.getURI()))
						this.retriever.getClient().getUrisToRetrieve().add(
								obj.getURI(), step);
				}
			}
		}
	}

	/**
	 * Checks if there are waiting threads and adds them
	 * to the ThreadList if there are free slots.
	 */
	synchronized private void refillThreadlist() {
		if (!this.waitingThreads.isEmpty()
				&& this.threadlist.size() < this.retriever.getMaxthreads()) {
			Thread t = (Thread) this.waitingThreads.getFirst();
			int i = this.waitingThreads.indexOf(t);
			this.waitingThreads.remove(i);
			this.threadlist.add(t);
		}
	}

	synchronized public void run() {
		while (!stopped) {
			this.clearThreads();
			this.refillThreadlist();
			try {
				this.wait(50);
			} catch (Exception e) {
			}
			this.checkTime();
		}
		this.stopThreads();
	}

	/**
	 * Stops the ThreadObserver
	 */
	synchronized public void stopObserver() {
		this.stopped = true;
	}

	/**
	 * Tries to stop all running Threads and removes them
	 * from the Threadlist.
	 */
	synchronized private void stopThreads() {
		while (!this.threadlist.isEmpty()) {
			Iterator iter = this.threadlist.iterator();
			while (iter.hasNext()) {
				UriConnector connector = (UriConnector) iter.next();
				if (connector.isReady()) {
					connector.wakeUp();
					this.retriever.getClient().getUrisToRetrieve().remove(
							connector.getUriString());
					this.retriever.getClient().getRetrievedUris().add(
							connector.getUriString());
					this.finishedCheck();
					this.threadlist.remove(connector);
					break;
				} else if (!connector.isStopped()) {
					connector.stopConnector();
				}

			}
		}
		this.waitingThreads.clear();
		this.retriever
				.retrievalFinished();
	}

}

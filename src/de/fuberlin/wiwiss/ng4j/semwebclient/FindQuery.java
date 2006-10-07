package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.vocabulary.RDFS;

import de.fuberlin.wiwiss.ng4j.NamedGraphSet;
import de.fuberlin.wiwiss.ng4j.Quad;

/**
 * If a URI is added to the "to retrieve"- list the ThreadObserver generates a
 * new UriConnector thread to retrieve this URI. It recurring checks the
 * ThreadList for finished threads fetches the collected data and adds it to the
 * underlying NamedGraphSet.
 * 
 * @author Tobias Gauß
 * 
 */
public class FindQuery implements DereferencingListener {
	private SemWebIterator iterator;
	private SemanticWebClientImpl client;
	private List urisInProcessing = new LinkedList();
	private TimeoutThread timeoutThread;
	private boolean stopped;
	private Log log = LogFactory.getLog(FindQuery.class);
	
	public FindQuery(SemanticWebClientImpl client, Triple pattern) {
		this.client = client;
		this.iterator = new SemWebIterator(this, pattern);
		this.timeoutThread = new TimeoutThread(this.iterator);
		this.timeoutThread.setName("Timeout");
		synchronized (this.client) {
			this.iterator.queueNamedGraphs(this.client.listGraphs());
		}
		// TODO The inspect operations can be expensive and shouldn't be executed by the application thread
		this.inspectTriple(this.client, pattern, 0);
		this.inspectNgs(this.client, pattern, 0);
		checkIfProcessingFinished();
	}

	public void dereferenced(DereferencingResult result) {
		if (result.isSuccess()) {
			inspectNgs(result.getResultData(), this.iterator.getTriple(), result.getTask().getStep());
			this.iterator.queueNamedGraphs(result.getResultData().listGraphs());
		}
		uriProcessingFinished(result.getURI());
	}

	private synchronized void uriProcessingFinished(String uri) {
		this.urisInProcessing.remove(uri);
		checkIfProcessingFinished();
	}
	
	private void checkIfProcessingFinished() {
		if (!this.urisInProcessing.isEmpty()) {
			return;
		}
		this.iterator.noMoreGraphs();
		close();
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
	private void inspectTriple(NamedGraphSet ngs, Triple t, int step) {
		Node sub = t.getSubject();
		Node pred = t.getPredicate();
		Node obj = t.getObject();
		
		this.inspectNode(ngs, sub, step);
		this.inspectNode(ngs, pred, step);
		this.inspectNode(ngs, obj, step);
	}
	
	private void inspectNode(NamedGraphSet ngs, Node n, int step){
		if (n.isURI()) {
			requestDereferencing(n.getURI(), step + 1);
		}
		if (n.isURI() || n.isBlank()) {
			checkSeeAlso(ngs, n, step);
		}
	}

	/**
	 * Checks the given NamedGraphSet ngs for uris.
	 * 
	 * @param ngs
	 *            The NamedgraphSet to inspect
	 * @param step
	 *            The retrieval step
	 */
	private void inspectNgs(NamedGraphSet ngs, Triple pattern, int step) {
		synchronized (ngs) {
			Iterator iter = ngs.findQuads(Node.ANY, pattern.getSubject(),
					pattern.getPredicate(), pattern.getObject());

			while (iter.hasNext()) {
				Quad q = (Quad) iter.next();
				Triple t = q.getTriple();
				inspectTriple(ngs, t, step);
			}
		}
	}

	/**
	 * Checks a given NamedGraphSet ngs for rdfs:seeAlso tags and adds the found
	 * uris to the "to retrieve" list.
	 * 
	 * @param ngs
	 *            The NamedGraphSet to inspect.
	 * @param uri
	 *            The URI.
	 * @param step
	 *            The retrieval step.
	 */
	private void checkSeeAlso(NamedGraphSet ngs, Node n, int step) {
		synchronized (ngs) {
			Iterator iter = ngs.findQuads(Node.ANY, n, RDFS.seeAlso.asNode(),
					Node.ANY);
			while (iter.hasNext()) {
				Quad quad = (Quad) iter.next();
				Node obj = quad.getObject();
				if (obj.isURI()) {
					requestDereferencing(obj.getURI(), step + 1);
				}
			}
		}
	}

	private void requestDereferencing(String uri, int step) {
		if (this.stopped) {
			return;
		}
		if (!uri.startsWith("http://") && !uri.startsWith("https://")) {
			// Don't try to reference mailto:, file: and other URI schemes
			return;
		}
		if (uri.indexOf("#") >= 0) {
			uri = uri.substring(0, uri.indexOf("#"));
			// TODO: But we have to check for rdfs:seeAlso triples involving the original URI
			// after retrieval has finished! This does not currently happen.
		}
		if (this.client.requestDereferencing(uri, step, this)) {
			this.urisInProcessing.add(uri);
		}
	}
	
	public synchronized void close() {
		this.stopped = true;
		this.timeoutThread.cancel();
	}

	public SemWebIterator iterator(){
		return this.iterator;
	}
	
	private long getTimeout() {
		try {
			return Long.parseLong(
					this.client.getConfig(SemanticWebClient.CONFIG_TIMEOUT));
		} catch (NumberFormatException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private class TimeoutThread extends Thread {
		private SemWebIterator iterator;
		private boolean closeIterator = true;
		TimeoutThread(SemWebIterator iterator) {
			this.iterator = iterator;
			start();
		}
		public synchronized void run() {
			try {
				wait(getTimeout());
			} catch (InterruptedException ex) {
				// We don't know when this happens
				throw new RuntimeException(ex);
			}
			if (this.closeIterator) {
				log.debug("Timeout");
				stopped = true;
				this.iterator.close();
			}
		}
		synchronized void cancel() {
			this.closeIterator = false;
			notify();
		}
	}
}

package de.fuberlin.wiwiss.ng4j.semwebclient;

import com.hp.hpl.jena.graph.TripleMatch;

public class URIRetriever implements ListListener {

	/**
	 * The corresponding SemanticWebClient
	 */
	private SemanticWebClientImpl client;

	/**
	 * Maximum simultaneous executed threads.
	 */
	private int maxthreads;

	/**
	 * Maximum retrieval steps.
	 */
	private int maxsteps;

	/**
	 * Maximum execution time.
	 */
	private long timeout;

	/**
	 * The corresponding ThreadObserver.
	 */
	private ThreadObserver observer;

	/**
	 * The triple to match
	 */
	private TripleMatch triple = null;

	/**
	 * Constructor
	 * 
	 * @param client
	 *            the corresponding SemanticWebClient.
	 */
	public URIRetriever(SemanticWebClientImpl client) {
		this.client = client;
		this.observer = new ThreadObserver(this);
		this.observer.setName("Observer");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.ListListener#retrieveUri(de.fuberlin.wiwiss.ng4j.semWebClient.UriListEvent)
	 */
	public void retrieveUri(UriListEvent e) {
		if (!this.observer.isAlive()) {
			this.observer = new ThreadObserver(this);
			this.observer.start();
		}
		String uri = e.getUri();
		int step = e.getStep();
		this.derefUri(uri, step);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.ListListener#retrievalFinished(de.fuberlin.wiwiss.ng4j.semWebClient.ThreadListEvent)
	 */
	public void retrievalFinished() {
		this.observer.stopObserver();
		this.getClient().retrievalFinished();
	}

	/**
	 * Creates a UriConnector to retrieve the given URI.
	 * 
	 * @param uri
	 *            The URI to retrieve.
	 * @param step
	 *            The retrieval step.
	 */
	private void derefUri(String uri, int step) {
		Thread t = new UriConnector(this, uri, step);
		this.observer.addThread(t);
	}

	/**
	 * @return The corresponding SemanticWebClient.
	 */
	public SemanticWebClientImpl getClient() {
		return this.client;
	}

	/**
	 * Sets the maximum execution time.
	 * 
	 * @param timeout
	 *            The maximum execution time in milisecs.
	 */
	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	/**
	 * @return The maximum execution time in milisecs.
	 */
	public long getTimeout() {
		return this.timeout;
	}

	/**
	 * @return The maximum retrieval steps.
	 */
	public int getMaxsteps() {
		return maxsteps;
	}

	/**
	 * Sets the maximum retrieval steps.
	 * 
	 * @param maxsteps
	 *            The maximum retrieval steps.
	 */
	public void setMaxsteps(int maxsteps) {
		this.maxsteps = maxsteps;
	}

	/**
	 * @return Maximum simultaneous executed threads.
	 */
	public int getMaxthreads() {
		return maxthreads;
	}

	/**
	 * @param maxthreads
	 *            Maximum simultaneous executed threads.
	 */
	public void setMaxthreads(int maxthreads) {
		this.maxthreads = maxthreads;
	}

	public void setTriplePattern(TripleMatch triple) {
		this.triple = triple;
	}

	public TripleMatch getTriplePattern() {
		return this.triple;
	}

	public void close() {
		this.observer.stopObserver();
	}

}

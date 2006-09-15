package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Node;

import de.fuberlin.wiwiss.ng4j.NamedGraph;


public class URIRetriever implements ListListener{
	
	/**
	 * The corresponding SemanticWebClient
	 */
	private SemanticWebClientImpl client;
	
	/**
	 * Maximum simultaneous executed threads.
	 */
	private int maxthreads = 20;

	
	/**
	 * Maximum retrieval steps.
	 */
	private int maxsteps = 3;
	
	/**
	 * Maximum execution time.
	 */
	private long timeout = 300000;
	
	/**
	 * The corresponding ThreadObserver.
	 */
	private ThreadObserver observer;

	/**
	 * Constructor
	 * 
	 * @param client the corresponding SemanticWebClient.
	 */
	public URIRetriever(SemanticWebClientImpl client) {
		this.client = client;
		this.observer = new ThreadObserver(this);
		this.observer.setName("Observer");
		this.observer.start();
	}


	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.ListListener#retrieveUri(de.fuberlin.wiwiss.ng4j.semWebClient.UriListEvent)
	 */
	public void retrieveUri(UriListEvent e) {
		String uri = e.getUri();
		int step = e.getStep();
		this.derefUri(uri, step);
	}

	/* (non-Javadoc)
	 * @see de.fuberlin.wiwiss.ng4j.semWebClient.ListListener#retrievalFinished(de.fuberlin.wiwiss.ng4j.semWebClient.ThreadListEvent)
	 */
	public void retrievalFinished() {
		this.observer.stopObserver();
		this.getClient().retrievalFinished();
		// dbug
		/*
		System.out.println("FINISH!!!. NGS Size: "
				+ this.getClient().getNamedGraphSet().countQuads());

		int count = 0;
		Iterator it1 = this.getClient().getNamedGraphSet().listGraphs();
		while(it1.hasNext()){
			NamedGraph g = (NamedGraph) it1.next();
			if(!g.getGraphName().getURI().equals("http://localhost/provenanceInformation")){
				Iterator iterator = g.find(Node.ANY,Node.createURI("http://xmlns.com/foaf/0.1/knows"),Node.ANY);
				int findcount = 0;
				while(iterator.hasNext()){
			    	iterator.next();
			    	findcount++;
			    	count++;
			    }
			}
		}
	    System.out.println("should find : "+count);
	    */
	}



	/**
	 * Creates a UriConnector to retrieve the given URI.
	 * 
	 * @param uri  The URI to retrieve.
	 * @param step The retrieval step.
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
	 * @param timeout The maximum execution time in milisecs.
	 */
	public void setTimeout(long timeout){
		this.timeout = timeout;
	}
	
	/**
	 * @return The maximum execution time in milisecs.
	 */
	public long getTimeout(){
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
	 * @param maxsteps The maximum retrieval steps.
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
	 * @param maxthreads Maximum simultaneous executed threads.
	 */
	public void setMaxthreads(int maxthreads) {
		this.maxthreads = maxthreads;
	}

}

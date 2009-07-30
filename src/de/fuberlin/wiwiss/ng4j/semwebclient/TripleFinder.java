package de.fuberlin.wiwiss.ng4j.semwebclient;

import com.hp.hpl.jena.graph.Triple;

/**
 * A Thread which starts a retrieval process and reports every found triple to a
 * listener.
 * 
 * @author Tobias Gauß
 */
public class TripleFinder extends Thread {
	private Triple triple;
	
	private SemanticWebClient client;

	private TripleListener listener;

	/**
	 * Constructs a TripleFinder.
	 * 
	 * @param t
	 *            The Node triple.
	 * @param client
	 *            The corresponding SemanticWebClient.
	 * @param listener
	 *            The TripleListener.
	 */
	public TripleFinder(Triple t,
			SemanticWebClient client, TripleListener listener) {
		this.triple = t;
		this.client = client;
		this.listener = listener;
	}

	public void run() {
		SemWebIterator it = new FindQuery(this.client, this.triple).iterator();
		while (it.hasNext()) {
			listener.tripleFound( it.next() );
		}
		this.listener.findFinished();
	}

}

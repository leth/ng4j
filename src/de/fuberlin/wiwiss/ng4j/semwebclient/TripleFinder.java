package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.Iterator;

import com.hp.hpl.jena.graph.Triple;

/**
 * A Thread which starts a retrieval process and reports every found triple to a
 * listener.
 * 
 * @author Tobias Gauﬂ
 */
public class TripleFinder extends Thread {
	private Triple triple;
	
	private SemanticWebClientImpl client;

	private TripleListener listener;

	/**
	 * Constructs a TripleFinder.
	 * 
	 * @param sub
	 *            The subject Node.
	 * @param pred
	 *            The predicate Node.
	 * @param obj
	 *            The object Node.
	 * @param client
	 *            The corresponding SemanticWebClient.
	 * @param listener
	 *            The TripleListener.
	 */
	public TripleFinder(Triple t,
			SemanticWebClientImpl client, TripleListener listener) {
		this.triple = t;
		this.client = client;
		this.listener = listener;
	}

	public void run() {
		Iterator it = new FindQuery(this.client, this.triple).iterator();
		while (it.hasNext()) {
			SemWebTriple triple = (SemWebTriple) it.next();
			listener.tripleFound(triple);
		}
		this.listener.findFinished();
	}

}

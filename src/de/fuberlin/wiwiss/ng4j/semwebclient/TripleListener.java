package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.EventListener;

/**
 * Listener to handle TripleFoundEvents.
 * 
 * @author Tobias Gauﬂ
 */
public interface TripleListener extends EventListener {

	/**
	 * Is performed when a triple is found.
	 * 
	 * @param t
	 */
	public void tripleFound(SemWebTriple t);

	/**
	 * Is performed when the retrieval process is finished.
	 */
	public void findFinished();
}

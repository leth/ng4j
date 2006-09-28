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
	 * @param e
	 */
	public void tripleFound(TripleFoundEvent e);

	/**
	 * Is performed when the retrieval process is finished.
	 * 
	 * @param e
	 */
	public void findFinished(TripleFoundEvent e);
}

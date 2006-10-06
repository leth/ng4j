package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.EventListener;

import com.hp.hpl.jena.graph.Triple;

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
	public void tripleFound(Triple t);

	/**
	 * Is performed when the retrieval process is finished.
	 */
	public void findFinished();
}

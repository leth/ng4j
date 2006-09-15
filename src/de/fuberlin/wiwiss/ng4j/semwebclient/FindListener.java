package de.fuberlin.wiwiss.ng4j.semwebclient;

/**
 * Listener to handle GraphAdded events
 * 
 * @author Tobias Gauﬂ
 *
 */
public interface FindListener {
	/**
	 * Is performed when a Graph is added to the NamedGraphSet.
	 * @param e
	 */
	public void graphAdded(GraphAddedEvent e);
	
	/**
	 * Is performed wenn the retrieval is finished.
	 * 
	 * @param e
	 */
	public void uriRetrievalFininshed(GraphAddedEvent e);

}

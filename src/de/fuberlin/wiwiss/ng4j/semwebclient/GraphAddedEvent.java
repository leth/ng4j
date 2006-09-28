package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.EventObject;

/**
 * Event which is generated when a graph is added to the NamedGraphSet.
 * 
 * @author Tobias Gauﬂ
 * 
 */
public class GraphAddedEvent extends EventObject {
	private String graphuri;

	/**
	 * Constructs a GraphAddedEvent.
	 * 
	 * @param source
	 *            The event source.
	 * @param graphuri
	 *            The graph URI.
	 */
	public GraphAddedEvent(Object source, String graphuri) {
		super(source);
		this.graphuri = graphuri;
	}

	/**
	 * Returns the graph URI.
	 * 
	 * @return String
	 */
	public String getGraphUri() {
		return this.graphuri;
	}

}

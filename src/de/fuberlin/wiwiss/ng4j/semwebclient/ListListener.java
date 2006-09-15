package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.EventListener;


/**
 * Listener to handle UriListEvents
 * 
 * @author Tobias Gauﬂ
 */
public interface ListListener extends EventListener {
	/**
	 * Constructor
	 * 
	 * @param e UriListEvent.
	 */
	public void retrieveUri(UriListEvent e);
}

package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.EventObject;

/**
 * Event which is generated when a URI is added to the UriList.
 * 
 * @author Tobias Gauﬂ
 * 
 */
public class UriListEvent extends EventObject {
	private String uri;

	private int step;

	/**
	 * Generates the UriListEvent.
	 * 
	 * @param src
	 *            The event source.
	 * @param uri
	 *            The URI String.
	 * @param step
	 *            The retrieval step.
	 */
	public UriListEvent(Object src, String uri, int step) {
		super(src);
		this.uri = uri;
		this.step = step;
	}

	/**
	 * Returns the URI String.
	 * 
	 * @return String
	 */
	public String getUri() {
		return this.uri;
	}

	/**
	 * Returns the retrieval step.
	 * 
	 * @return int
	 */
	public int getStep() {
		return this.step;
	}

}

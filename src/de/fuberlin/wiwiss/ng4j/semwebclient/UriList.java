package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.ArrayList;

/**
 * A LinkedList which contains the URIs for retrieval. It has a ListListener
 * which creates a UriListEvent if a new URI is added.
 * 
 * @author Tobias Gauﬂ
 * 
 */
public class UriList extends ArrayList {
	private ListListener listener;
	public int counta = 0;
	public int countb = 0;

	/**
	 * Adds a new URI to the UriList. Returns true if the URI is succsessfully
	 * added false if not.
	 * 
	 * @param uri
	 *            The URI String to add.
	 * @return
	 */
	//synchronized public boolean add(String uri) {
	//	this.listener.retrieveUri(new UriListEvent(this, uri, 0));
	//	return super.add(uri);
	//}

	/**
	 * Adds a new URI to the UriList. Returns true if the URI is succsessfully
	 * added false if not.
	 * 
	 * @param uri
	 *            The URI String to add.
	 * @param step
	 *            The retrieval step.
	 * @return
	 */
	synchronized public boolean add(String uri, int step) {
		this.counta++;
		this.listener.retrieveUri(new UriListEvent(this, uri, step));
		return super.add(uri);
		
	}
	
	/**
	 * Adds a ListListener to the UriList.
	 * 
	 * @param listener
	 *            The ListListener to add.
	 */
	public void addListListener(ListListener listener) {
		this.listener = listener;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	synchronized public boolean remove(Object o) {
		this.countb++;
		return super.remove(o);
	}

	/**
	 * Removes the ListListener.
	 * 
	 * @param listener
	 *            The Listener to remove.
	 */
	public void removeListListener(ListListener listener) {
		this.listener = null;
	}

}

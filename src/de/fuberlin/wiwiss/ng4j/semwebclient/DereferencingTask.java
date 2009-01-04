package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.Task;


/**
 * A DereferencingTask represents a URI which has to be retrieved. 
 *
 * @author Tobias Gauß
 * @author Olaf Hartig
 */
public class DereferencingTask implements Task {
	private List listeners = new Vector ();
	private String uri;
	private int step;
	
	public DereferencingTask(String uri, int step) {
		this.step = step;
		this.uri  = uri;
	}


	// implementation of the Task interface

	public String getIdentifier () {
		return uri;
	}


	// accessor methods

	/**
	 * Appends the given listener to the list of listeners attached to this task.
	 */
	public void attachListener ( DereferencingListener listener ) {
		listeners.add( listener );
	}

	/**
	 * Returns an iterator over all listeners attached to this task.
	 */
	public Iterator getListeners () {
		return listeners.iterator();
	}
	
	public int getStep(){
		return this.step;
	}
	
	public String getURI(){
		return this.uri;
	}
}

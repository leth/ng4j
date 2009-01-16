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
	private List<DereferencingListener> listeners = new Vector<DereferencingListener> ();
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
	synchronized public void attachListener ( DereferencingListener listener ) {
		listeners.add( listener );
	}

	/**
	 * Returns true if the given listeners is already attached to this task.
	 */
	synchronized public boolean isAttached ( DereferencingListener listener ) {
		Iterator<DereferencingListener> it = listeners.iterator();
		while ( it.hasNext() ) {
			if ( listener.equals(it.next()) ) {
				return true;
			}
		}
		return false;
	}
	
	public int getStep(){
		return this.step;
	}
	
	public String getURI(){
		return this.uri;
	}


	// operations

	/**
	 * Returns an iterator over all listeners attached to this task.
	 */
	public void notifyListeners ( DereferencingResult result ) {
		Vector<DereferencingListener> tmp = new Vector<DereferencingListener> ();
		synchronized ( listeners ) {
			tmp.addAll( listeners );
		}
		Iterator<DereferencingListener> it = tmp.iterator();
		while ( it.hasNext() ) {
			it.next().dereferenced( result );
		}
		tmp.clear();
	}

}

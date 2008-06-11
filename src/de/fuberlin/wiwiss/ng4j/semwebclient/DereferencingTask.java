package de.fuberlin.wiwiss.ng4j.semwebclient;

import de.fuberlin.wiwiss.ng4j.semwebclient.threadutils.Task;


/**
 * A DereferencingTask represents a URI which has to be retrieved. 
 *
 * @author Tobias Gauß
 * @author Olaf Hartig
 */
public class DereferencingTask implements Task {
	private DereferencingListener listener;
	private String uri;
	private int step;
	
	public DereferencingTask(DereferencingListener listener, String uri, int step) {
		this.listener = listener;
		this.step = step;
		this.uri  = uri;
	}


	// implementation of the Task interface

	public String getIdentifier () {
		return uri;
	}


	// accessor methods

	public DereferencingListener getListener() {
		return this.listener;
	}
	
	public int getStep(){
		return this.step;
	}
	
	public String getURI(){
		return this.uri;
	}
}

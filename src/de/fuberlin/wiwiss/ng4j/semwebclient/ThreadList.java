package de.fuberlin.wiwiss.ng4j.semwebclient;

import java.util.LinkedList;


/**
 * A LinkedList which starts all added Threads.
 * 
 * @author Tobias Gauß
 *
 */
public class ThreadList extends LinkedList{


	/**
	 * Adds a Thread to the ThreadList and starts it
	 * 
	 * @param thread
	 * @return
	 */
	public boolean add(Thread thread){
		thread.start();
		return super.add(thread);
	}

}

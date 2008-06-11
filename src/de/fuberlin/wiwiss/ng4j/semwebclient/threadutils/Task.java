package de.fuberlin.wiwiss.ng4j.semwebclient.threadutils;


/**
 * A task.
 *
 * @author Olaf Hartig
 */
public interface Task {

	/**
	 * Returns an identifier for this task.
	 * The identifier must be unique among all the tasks of the same type.
	 */
	public String getIdentifier ();

}

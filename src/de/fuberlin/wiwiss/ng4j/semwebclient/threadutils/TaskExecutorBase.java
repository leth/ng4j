package de.fuberlin.wiwiss.ng4j.semwebclient.threadutils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * A base class for threads that execute a specific type of tasks.
 *
 * @author Olaf Hartig
 */
abstract public class TaskExecutorBase extends Thread {

	// members

	private boolean stopped = false;
	private Object currentTask;

	static private Log log = LogFactory.getLog( TaskExecutorBase.class );


	// initialization

	protected TaskExecutorBase () {
		setName( getClass().getSimpleName() + "_" + String.valueOf(getId()) );
	}

	// interface

	/**
	 * Returns the type of the tasks executed by this kind of task execution
	 * threads.
	 */
	abstract public Class getTaskType ();

	/**
	 * Executes the given task (which is guaranteed to be of the type specified
	 * by {@link #getTasktype}).
	 * Note for implementations: if this thread has already been stopped (cf.
	 * {@link #isStopped}) the execution must be stopped as quickly as possible.
	 */
	abstract protected void executeTask ( Object task );


	// accessor methods

	/**
	 * Returns true if this thread is currently busy executing a task.
	 */
	final public boolean hasTask () {
		return ( currentTask != null );
	}

	/**
	 * Returns true if this thread has been stopped already.
	 */
	final public boolean isStopped () {
		return stopped;
	}


	// operations

	/**
	 * Starts to execute the given task.
	 */
	final public synchronized void startTask ( Object task ) {

		if ( stopped )
			throw new IllegalStateException( "This thread '" + getName() + "' (type: " + getClass().getName() + ") has been stopped." );

		if ( currentTask != null )
			throw new IllegalStateException( "This thread '" + getName() + "' (type: " + getClass().getName() + ") is busy." );

		if ( ! getTaskType().isAssignableFrom(task.getClass()) )
			throw new IllegalArgumentException( "The given task (type: " + task.getClass().getName() + ") does not have the expected type (" + getTaskType().getName() + ") for thread '" + getName() + "' (type: " + getClass().getName() + ")." );

		currentTask = task;
		notify();
	}

	final public void run ()
	{
		log.trace( "Thread '" + getName() + "' (type: " + getClass().getName() + ") started." );

		while ( ! stopped ) {
			if ( currentTask != null ) {
				long startTime = 0;
				if ( log.isTraceEnabled() )
					startTime = System.currentTimeMillis();

				try {
					executeTask( currentTask );
				} catch ( RuntimeException e ) {
					log.error( "Executing the current task for thread '" + getName() + "' (type: " + getClass().getName() + ") caused an " + e.getClass().getName() + " (" + e.getMessage() + ")." );
				}

				if ( log.isTraceEnabled() ) {
					long execTimeDiff = System.currentTimeMillis() - startTime;
					log.trace( "Executing the task for thread '" + getName() + "' (type: " + getClass().getName() + ") took " + String.valueOf(execTimeDiff) + " milliseconds." );
				}

				currentTask = null;
			}

			try {
				synchronized ( this ) {
					if ( stopped )
						break;

					wait();
				}
			} catch ( InterruptedException e ) {
				// Do nothing here. An interruption happens when the thread is stopped.
			}
		}

		log.trace( "Thread '" + getName() + "' (type: " + getClass().getName() + ") stopped." );
	}

	/**
	 * Stops this thread.
	 * Sets the stop status of this thread and interrupts the execution.
	 */
	final synchronized public void stopThread () {
		log.trace( "Stopping thread '" + getName() + "' (type: " + getClass().getName() + ")." );
		stopped = true;
		interrupt();
	}

}
